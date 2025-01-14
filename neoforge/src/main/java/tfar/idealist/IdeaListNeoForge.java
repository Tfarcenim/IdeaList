package tfar.idealist;


import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import tfar.idealist.client.ModClientNeoForge;
import tfar.idealist.datagen.ModDatagen;
import tfar.idealist.entity.AnimatedBlockEntity;
import tfar.idealist.entity.WormEntity;
import tfar.idealist.init.AttachmentTypes;
import tfar.idealist.init.ModEntityTypes;
import tfar.idealist.network.PacketHandler;
import tfar.idealist.network.S2CAttachmentDataPacket;
import tfar.idealist.platform.PacketHandlerNeoForge;
import tfar.idealist.platform.Services;
import tfar.idealist.world.SeedRaidData;

import java.util.*;
import java.util.function.Supplier;

@Mod(IdeaList.MOD_ID)
public class IdeaListNeoForge {
    public static Map<Registry<?>, List<Pair<ResourceLocation, Supplier<?>>>> registerLater = new HashMap<>();

    public IdeaListNeoForge(IEventBus eventBus, Dist dist, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER,IdeaConfig.SERVER_SPEC);
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.
        eventBus.addListener(this::setup);
        eventBus.addListener(this::registerObjs);
        eventBus.addListener(this::createAttr);
        eventBus.addListener(PacketHandlerNeoForge::register);
        eventBus.addListener(ModDatagen::gather);
        eventBus.addListener(this::configChange);
        eventBus.addListener(this::config);
        if (dist.isClient()) {
            ModClientNeoForge.init(eventBus);
        }
        // Use NeoForge to bootstrap the Common mod.
        Services.PLATFORM.registerAll(AttachmentTypes.class, NeoForgeRegistries.ATTACHMENT_TYPES, IdeaList.cast(AttachmentType.class));
        IdeaList.init();
    }

    void pickup(ItemEntityPickupEvent.Post event) {
        ItemEntity entity = event.getItemEntity();
        Entity thrower = entity.getOwner();
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        if (thrower instanceof Piglin) {
            AdvancementHolder advancement = player.server.getAdvancements().get(IdeaConfig.Defaults.TRADE_PIGLIN);
            if (advancement != null) {
                AdvancementCommands.Action.GRANT.perform(player,List.of(advancement));
            }
        }
    }

    public void registerObjs(RegisterEvent event) {
        Registry<?> registry = event.getRegistry();
        List<Pair<ResourceLocation,Supplier<?>>> list = registerLater.get(registry);
        if (list != null) {
            for (Pair<ResourceLocation,Supplier<?>> pair : list) {
                event.register((ResourceKey<? extends Registry<Object>>)registry.key(),pair.getLeft(),(Supplier<Object>)pair.getValue());
            }
        }
    }

    void configChange(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(IdeaList.MOD_ID)) {
            IdeaConfig.Server.cache();
        }
    }

    void config(ModConfigEvent.Loading event) {
        if (event.getConfig().getModId().equals(IdeaList.MOD_ID)) {
            IdeaConfig.Server.cache();
        }
    }

    void login(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        PacketHandler.sendTo(new S2CAttachmentDataPacket(player.getData(AttachmentTypes.PLAYER_BINGO_DATA)), (ServerPlayer) player);
    }


    void setup(FMLCommonSetupEvent event) {
        registerLater.clear();
        NeoForge.EVENT_BUS.addListener(this::cowRevenge);
        NeoForge.EVENT_BUS.addListener(this::leftClickBlock);
        NeoForge.EVENT_BUS.addListener(this::attackerShuffle);
        NeoForge.EVENT_BUS.addListener(this::advEarn);
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class,e -> {
            ModCommands.register(e.getDispatcher());
            //SlowLoadSchematicCommand.register(e.getDispatcher());
        });
        NeoForge.EVENT_BUS.addListener(this::login);
        NeoForge.EVENT_BUS.addListener(this::spawn);
        NeoForge.EVENT_BUS.addListener(this::pickup);
        NeoForge.EVENT_BUS.addListener(this::plantSeed);
        NeoForge.EVENT_BUS.addListener(this::levelTick);
    }

    void spawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        PacketHandler.sendTo(new S2CAttachmentDataPacket(player.getData(AttachmentTypes.PLAYER_BINGO_DATA)), (ServerPlayer) player);
    }

    void advEarn(AdvancementEvent.AdvancementEarnEvent event) {
        AdvancementHolder holder  = event.getAdvancement();
        Player player = event.getEntity();
        if (IdeaConfig.Server.bingoAdvancements.contains(holder.id())) {
            Vec3 pos = player.position().add(0,2,0);
            ItemStack stack = Items.FIREWORK_ROCKET.getDefaultInstance();

            FireworkExplosion fireworkExplosion = new FireworkExplosion(FireworkExplosion.Shape.CREEPER, IntList.of(0x00ff00),IntList.of(0x00ff00),true,true);
            Fireworks fireworks = new Fireworks(4,List.of(fireworkExplosion));
            stack.set(DataComponents.FIREWORKS,fireworks);

            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(
                    player.level(),
                    player,
                    pos.x,
                    pos.y,
                    pos.z,
                    stack
            );
            player.level().addFreshEntity(fireworkrocketentity);
        }
    }

    void createAttr(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.ANIMATED_BLOCK, AnimatedBlockEntity.create().build());
        event.put(ModEntityTypes.WORM, WormEntity.attributes().build());
    }

    //- When a player tries to kill a cow, the cow stops moving and looks at the player,
    // then the cow along with a bunch of other cows in the "area" bunch up together to form a giant cow mech
    // (the other cows don’t have to be there already, have it so they spawn nearby when the cow is hit then they all rush towards the cow that was hit).
    // The cow mech can shoot lasers out of its eyes for 10 seconds at a time (cooldown 10 seconds) causing half a heart of damage a hit. The cow mech has 150 health.

    void cowRevenge(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        if (attacker instanceof Player playerAttacker && target instanceof Cow cow) {
            if (playerAttacker.getData(AttachmentTypes.PLAYER_BINGO_DATA).twist()) {
                cow.getNavigation().stop();
                cow.getLookControl().setLookAt(playerAttacker);
                event.setCanceled(true);
                for (int i = 0; i < 9; i++) {

                }
            }
        }
    }

    //When a player tries to kill another player, their hotbar starts randomly shifting around
    void attackerShuffle(AttackEntityEvent event) {
        Entity target = event.getTarget();
        Player playerAttacker = event.getEntity();
        if (Services.PLATFORM.getData(playerAttacker).twist()) {
            if (target instanceof Player) {
                Inventory inventory = playerAttacker.getInventory();
                List<ItemStack> shuffled = new ArrayList<>();
                int size = 9;
                for (int i = 0; i < size; i++) {
                    shuffled.add(inventory.items.get(i));
                    inventory.items.set(i, ItemStack.EMPTY);
                }
                Collections.shuffle(shuffled);
                for (int i = 0; i < size; i++) {
                    inventory.items.set(i, shuffled.get(i));
                }
                ///   if (!(playerAttacker instanceof ServerPlayer)) {
                //      ClientPacketHandler.handleHotbarShift();
                //  }
            }
        }
    }

    //- When a player tries to trade with a piglin,
    // the piglin takes the gold and runs away really fast building a complex parkour course with lava under it and waits at the end of it.
    // The player has to complete the parkour course and make it to the piglin in order for the piglin to complete the trade.
    // (the player cannot place blocks while this challenge is active).

    void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        BlockState state = player.level().getBlockState(event.getPos());
        if (player.getData(AttachmentTypes.PLAYER_BINGO_DATA).twist() && !player.isCreative() && !state.isAir() && state.is(Tags.Blocks.ORES_DIAMOND)) {
            event.setCanceled(true);
            AnimatedBlockEntity run = AnimatedBlockEntity.run(player.level(), pos, state);
            run.hurt(player.damageSources().mobAttack(player),0);
        }
    }

    void plantSeed(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        BlockPos pos = event.getPos();
        BlockState state = player.level().getBlockState(pos);
        if (Services.PLATFORM.getData(player).twist()) {
            if (stack.is(Items.WHEAT_SEEDS) && state.is(Blocks.FARMLAND)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                if (!player.level().isClientSide) {
                    ServerLevel newLevel = player.getServer().getLevel(IdeaList.SEED_DIM);
                    player.changeDimension(new DimensionTransition(newLevel,IdeaConfig.Server.SEED_TELEPORT.get(),Vec3.ZERO,player.getXRot(),player.getYRot(),false,SEED_ENTER_TRANSITION));
                }
            }
        }
    }

    public static final DimensionTransition.PostDimensionTransition SEED_ENTER_TRANSITION = entity -> {
        Level level = entity.level();
        if (level.dimension() == IdeaList.SEED_DIM && entity instanceof ServerPlayer player) {
            SeedRaidData seedRaidData = SeedRaidData.getOrLoad((ServerLevel) level);
            seedRaidData.start(player);
        }
    };

    void levelTick(LevelTickEvent.Pre event) {
        Level level = event.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            SeedRaidData seedRaidData = SeedRaidData.get(serverLevel);
            if (seedRaidData != null) {
                seedRaidData.tick();
            }
        }
    }
}