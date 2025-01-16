package tfar.idealist;

import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.idealist.init.ModEntityTypes;
import tfar.idealist.init.ModItems;
import tfar.idealist.platform.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class IdeaList {

    public static final String MOD_ID = "idealist";
    public static final String MOD_NAME = "IdeaList";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final ResourceKey<DimensionType> SEED_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,id("seed"));
    public static final ResourceKey<Level> SEED_DIM = ResourceKey.create(Registries.DIMENSION,id("seed"));
    public static final ResourceKey<Level> PIGLIN_PARKOUR_DIM = ResourceKey.create(Registries.DIMENSION,id("piglin_parkour"));

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        Services.PLATFORM.registerAll(ModEntityTypes.class, BuiltInRegistries.ENTITY_TYPE,cast(EntityType.class));
        Services.PLATFORM.registerAll(ModItems.class, BuiltInRegistries.ITEM, Item.class);

    }

    public static void onBrushingCompleted(ServerPlayer player, BlockPos pos, Level level) {
        AdvancementHolder advancement = player.server.getAdvancements().get(IdeaConfig.Defaults.BRUSH_SUSPICIOUS_SAND);
        if (advancement != null) {
            AdvancementCommands.Action.GRANT.perform(player, List.of(advancement));
        }
        if (Services.PLATFORM.getPlayerData(player).twist()) {
            ModEntityTypes.TREX_SKELETON.spawn((ServerLevel) level, pos, MobSpawnType.EVENT);
        }
    }

    public static void onEndPortalCompleted(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, BlockPattern.BlockPatternMatch match) {
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        if (Services.PLATFORM.getPlayerData(player).twist()) {
            Level level = context.getLevel();
                cir.setReturnValue(InteractionResult.CONSUME);//don't construct the portal yet
                EnderMan enderMan = EntityType.ENDERMAN.spawn((ServerLevel) level, player.blockPosition().south(5), MobSpawnType.EVENT);
                enderMan.setInvulnerable(true);
                enderMan.setNoAi(true);
                enderMan.setAggressive(false);
                enderMan.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(
                        new AttributeModifier(id("enderman_quiz"),100, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                enderMan.setItemSlot(EquipmentSlot.HEAD,ModItems.PURPLE_GLASSES.getDefaultInstance());
                Services.PLATFORM.setPlayerData(player,Services.PLATFORM.getPlayerData(player).incrementQuestion().setDeferredEndPortalPos(match.getFrontTopLeft()).setEnderman(enderMan.getUUID()));
                askQuestion(player);
        }
    }

    public static void askQuestion(ServerPlayer player) {
        int question = Services.PLATFORM.getPlayerData(player).quiz_data().question();
        QandA qandA = getQuestion(question);
        player.displayClientMessage(Component.literal(qandA.question()),false);

    }

    public static void spawnPortal(ServerLevel level,BlockPos pos) {
            BlockPos blockpos1 = pos.offset(-3, 0, -3);

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    level.setBlock(blockpos1.offset(i, 0, j), Blocks.END_PORTAL.defaultBlockState(), 2);
                }
            }

            level.globalLevelEvent(1038, blockpos1.offset(1, 0, 1), 0);
        }

    public static QandA getQuestion(int i) {
        return switch (i){
            case 0->IdeaConfig.Server.QUESTION_0.get();
            case 1->IdeaConfig.Server.QUESTION_1.get();
            case 2->IdeaConfig.Server.QUESTION_2.get();
            case 3->IdeaConfig.Server.QUESTION_3.get();
            case 4->IdeaConfig.Server.QUESTION_4.get();
            default->
                    throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public static float modifyInaccuracy(float original, LivingEntity entity) {
        if (entity instanceof Player player && Services.PLATFORM.getPlayerData(player).twist()) {
            Services.PLATFORM.setPlayerData(player,Services.PLATFORM.getPlayerData(player).incrementShot());
            int shots = Services.PLATFORM.getPlayerData(player).shot_count();
            if (shots %10 == 0) {
                return original;
            }
            return 25;
        }
        return original;
    }

    public static void overrideRaidSpawns(Raid raid, BlockPos pos, CallbackInfo ci, boolean flag, int i, DifficultyInstance difficultyinstance) {
        boolean flag1 = raid.shouldSpawnBonusGroup();
        for (int i1 = 0; i1< Raid.RaiderType.values().length;i1++) {
            Raid.RaiderType raid$raidertype = Raid.RaiderType.RAVAGER;
            int j = raid.getDefaultNumSpawns(raid$raidertype, i, flag1)
                    + raid.getPotentialBonusSpawns(raid$raidertype, raid.random, i, difficultyinstance, flag1);
            int k = 0;

            for (int l = 0; l < j; l++) {
                Raider raider = raid$raidertype.entityType.create(raid.getLevel());
                if (raider == null) {
                    break;
                }

                raider.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,MobEffectInstance.INFINITE_DURATION, 1));

                if (!flag && raider.canBeLeader()) {
                    raider.setPatrolLeader(true);
                    raid.setLeader(i, raider);
                    flag = true;
                }

                raid.joinRaid(i, raider, pos, false);
                if (raid$raidertype.entityType == EntityType.RAVAGER) {
                    Raider raider1 = null;
                    if (i == raid.getNumGroups(Difficulty.NORMAL)) {
                        raider1 = EntityType.PILLAGER.create(raid.getLevel());
                    } else if (i >= raid.getNumGroups(Difficulty.HARD)) {
                        if (k == 0) {
                            raider1 = EntityType.EVOKER.create(raid.getLevel());
                        } else {
                            raider1 = EntityType.VINDICATOR.create(raid.getLevel());
                        }
                    }

                    k++;
                    if (raider1 != null) {
                        raid.joinRaid(i, raider1, pos, false);
                        raider1.moveTo(pos, 0.0F, 0.0F);
                        raider1.startRiding(raider);
                    }
                }
            }
        }

        raid.waveSpawnPos = Optional.empty();
        raid.groupsSpawned++;
        raid.updateBossbar();
        raid.setDirty();
    }

    public static void onPiglinGoldPickup(Piglin piglin, ItemEntity itemEntity, ItemStack stack) {
        if (stack.is(Items.GOLD_INGOT)) {
            Entity owner = itemEntity.getOwner();
            if (owner instanceof Player player && Services.PLATFORM.getPlayerData(player).twist()) {
                piglin.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(randomPosInCircle(piglin.blockPosition(),100), 2, 1));
                Services.PLATFORM.setPiglinData(piglin,new ExtraPiglinData(owner.getUUID()));
            }
        }
    }

    static BlockPos randomPosInCircle(BlockPos pos,double r) {
        float angle = (float) (Math.random() * 360);
        float x = Mth.sin((float) (angle * Math.PI /180));
        float z = Mth.cos((float) (angle * Math.PI /180));
        return BlockPos.containing(pos.getX() + x * r,pos.getY(),pos.getZ() + z * r);
    }

    public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, Vec3::x,
            ByteBufCodecs.DOUBLE, Vec3::y,
            ByteBufCodecs.DOUBLE, Vec3::z,
            Vec3::new);

    @SuppressWarnings("unchecked")
    static <T> Class<T> cast(Class<?> clazz) {
        return (Class<T>) clazz;
    }

    public static <V> Stream<V> getKnown(Registry<V> registry) {
        return registry.stream().filter(o -> registry.getKey(o).getNamespace().equals(MOD_ID));
    }

    public static ResourceLocation id(String key) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,key);
    }

    public static void finishTrade(Piglin piglin, boolean shouldBarter, CallbackInfo ci) {
        ExtraPiglinData data = Services.PLATFORM.getPiglinData(piglin);
        ServerPlayer player = piglin.getServer().getPlayerList().getPlayer(data.lastTraded());

        if (piglin.level().dimension() == Level.NETHER) {
            if (shouldBarter) {
                if (player != null) {
                    if (Services.PLATFORM.getPlayerData(player).twist()) {
                        piglin.discard();
                        Services.PLATFORM.setPlayerData(player,Services.PLATFORM.getPlayerData(player).setPiglinParkourReturnPos(player.position()));
                        ServerLevel piglinParkour = player.server.getLevel(PIGLIN_PARKOUR_DIM);
                        player.changeDimension(new DimensionTransition(piglinParkour, IdeaConfig.Server.PIGLIN_PARKOUR_PLAYER_TELEPORT.get(), Vec3.ZERO, player.getXRot(), player.getYRot(), entity -> {
                            Piglin newPiglin = EntityType.PIGLIN.spawn(piglinParkour, BlockPos.containing(IdeaConfig.Server.PIGLIN_PARKOUR_PIGLIN_TELEPORT.get()), MobSpawnType.EVENT);
                            piglin.setPersistenceRequired();

                            ItemEntity item = new ItemEntity(piglinParkour, newPiglin.getX(), newPiglin.getY(), newPiglin.getZ(), Items.GOLD_INGOT.getDefaultInstance());
                            piglinParkour.addFreshEntity(item);
                            player.setGameMode(GameType.ADVENTURE);
                        }));
                    }
                } else {
                    //todo handle offline players
                }
                ci.cancel();
            }
        } else {
            List<ItemStack> barterItems = PiglinAi.getBarterResponseItems(piglin);
            PiglinAi.throwItemsTowardPos(piglin,barterItems,IdeaConfig.Server.PIGLIN_PARKOUR_PIGLIN_TELEPORT.get());
            ci.cancel();
        }
    }
}

//- When a player tries to kill a cow, the cow stops moving and looks at the player, then the cow along with a bunch of other cows in the "area" bunch up together to form a giant cow mech (the other cows don’t have to be there already, have it so they spawn nearby when the cow is hit then they all rush towards the cow that was hit). The cow mech can shoot lasers out of its eyes for 10 seconds at a time (cooldown 10 seconds) causing half a heart of damage a hit. The cow mech has 150 health.
//
//
//
//- When a player tries to mine a diamond, the diamond ore block grows arms and legs and starts running away. To get the diamond the player has to chase the diamond ore block and kill it. The diamond ore block has 20 health and runs at 1.5x the speed of a player. Once you kill the diamond ore block the objective is complete.
//
//
//
//- When a player tries to kill another player, their hotbar starts randomly shifting around
//
//
//
//- When a player tries to trade with a piglin, the piglin takes the gold and runs away really fast building a complex parkour course with lava under it and waits at the end of it. The player has to complete the parkour course and make it to the piglin in order for the piglin to complete the trade. (the player cannot place blocks while this challenge is active).
//
//
//
//- When a player brushes suspicious sand, they complete the objective. Although as soon as the player finishes, a dinosaur skeleton comes alive out of the sand and attacks the player. The dinosaur skeleton does 4 hearts of damage a hit, and can run faster than a player. (Make the dinosaur a T-rex)
//
//
//
//- When a player puts the last ender eye in the last available portal block slot, an enderman walks into the room towards the player and tells the player he needs to pass a test to see if the player is allowed in the end (make the enderman have glasses). The test is 5 questions and if the player gets one wrong they die (the enderman lets the player know the rules before the test begins). To answer a question, the player has to write it in chat.
//
//
//
//- When a player tries to shoot an arrow, the arrow goes flying in a random direction, changing the flight path of the arrow and making it fly around randomly. Every 10th shot the player makes acts like a normal arrow
//
//
//
//- When a player starts a raid, every pillager and mob that is a part of the raid is a ravager with speed 2
//
//
//
//- When a player plants a seed, the player gets sucked into the seed and taken to another dimension called the Seed Dimension. In the seed dimension everything looks green almost like you are inside a plant. There is a mound in the middle, where the player spawns in. In the middle of the mound there is a block with a seed growing and the player has to defend the seed and let it grow as 3 different types of seed monsters attempt to break and ruin the seed. The monsters are: worms, giant birds and ants.