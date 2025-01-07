package tfar.idealist;


import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import tfar.idealist.client.ClientPacketHandler;
import tfar.idealist.client.ModClient;
import tfar.idealist.client.ModClientNeoForge;
import tfar.idealist.entity.AnimatedBlockEntity;
import tfar.idealist.init.ModEntityTypes;
import tfar.idealist.network.PacketHandler;
import tfar.idealist.network.S2CShuffleHotbarPacket;
import tfar.idealist.platform.PacketHandlerNeoForge;
import tfar.idealist.platform.Services;

import java.util.*;
import java.util.function.Supplier;

@Mod(IdeaList.MOD_ID)
public class IdeaListNeoForge {
    public static Map<Registry<?>, List<Pair<ResourceLocation, Supplier<?>>>> registerLater = new HashMap<>();

    public IdeaListNeoForge(IEventBus eventBus, Dist dist) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.
        eventBus.addListener(this::setup);
        eventBus.addListener(this::registerObjs);
        eventBus.addListener(this::createAttr);
        eventBus.addListener(PacketHandlerNeoForge::register);
        if (dist.isClient()) {
            ModClientNeoForge.init(eventBus);
        }
        // Use NeoForge to bootstrap the Common mod.
        ((MappedRegistry<?>)BuiltInRegistries.ENTITY_TYPE).unfreeze();
        IdeaList.init();

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

    void setup(FMLCommonSetupEvent event) {
        registerLater.clear();
        if (IdeaConfig.COW_REVENGE) {
            NeoForge.EVENT_BUS.addListener(this::cowRevenge);
        }
        if (IdeaConfig.RUNNING_BLOCKS) {
            NeoForge.EVENT_BUS.addListener(this::leftClickBlock);
        }

        if (IdeaConfig.INVENTORY_SHUFFLE) {
            NeoForge.EVENT_BUS.addListener(this::attackerShuffle);
        }


    }

    void createAttr(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.ANIMATED_BLOCK, AnimatedBlockEntity.create().build());
    }

    //- When a player tries to kill a cow, the cow stops moving and looks at the player,
    // then the cow along with a bunch of other cows in the “area” bunch up together to form a giant cow mech
    // (the other cows don’t have to be there already, have it so they spawn nearby when the cow is hit then they all rush towards the cow that was hit).
    // The cow mech can shoot lasers out of its eyes for 10 seconds at a time (cooldown 10 seconds) causing half a heart of damage a hit. The cow mech has 150 health.

    void cowRevenge(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        if (attacker instanceof Player playerAttacker && target instanceof Cow cow) {
            cow.getNavigation().stop();
            cow.getLookControl().setLookAt(playerAttacker);
            event.setCanceled(true);
             for (int i = 0; i < 9;i++) {

             }
        }
    }

    //When a player tries to kill another player, their hotbar starts randomly shifting around
    void attackerShuffle(AttackEntityEvent event) {
        Entity target = event.getTarget();
        Player playerAttacker = event.getEntity();
        if (target instanceof Player) {
            Inventory inventory = playerAttacker.getInventory();
            List<ItemStack> shuffled = new ArrayList<>();
            int size = 9;
            for (int i = 0; i < size;i++) {
                shuffled.add(inventory.items.get(i));
                inventory.items.set(i,ItemStack.EMPTY);
            }
            Collections.shuffle(shuffled);
            for (int i = 0; i < size;i++) {
                inventory.items.set(i,shuffled.get(i));
            }
            if (!(playerAttacker instanceof ServerPlayer)) {
                ClientPacketHandler.handleHotbarShift();
            }
        }
    }

    void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        BlockState state = player.level().getBlockState(event.getPos());
        if (!player.isCreative() && !state.isAir()) {
            event.setCanceled(true);
            AnimatedBlockEntity run = AnimatedBlockEntity.run(player.level(), pos, state);
            run.hurt(player.damageSources().mobAttack(player),0);
        }
    }
}