package tfar.idealist.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tfar.idealist.IdeaConfig;
import tfar.idealist.IdeaList;
import tfar.idealist.PlayerBingoData;
import tfar.idealist.entity.SeedEntity;
import tfar.idealist.entity.SparrowEntity;
import tfar.idealist.entity.WormEntity;
import tfar.idealist.init.ModEntityTypes;
import tfar.idealist.platform.Services;

import java.util.ArrayList;
import java.util.List;

public class SeedRaidData extends SavedData {

    // Some more specifics on how the seed defense idea will work: The ants, worms, and birds will have the same health and damage as zombies and spawn in 4 waves.
    // Each wave their health will increase by 1.5 hearts.
    // Ants and worms movement speed will also slightly increase every wave so that in the 4th wave their movement speed will be 2x the standard speed.
    // In order for the seed to grow to the next phase, the players must kill all the ants, birds, and worms for that specific wave.
    // The seed will be able to take 10 hits before it breaks (please add a comment for this part so that we can change it later on if needed).

    private final ServerBossEvent raidEvent = new ServerBossEvent(SEED_RAID_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    public static final MutableComponent SEED_RAID_NAME_COMPONENT = Component.translatable("event.idealist.seed_raid");
    public static final MutableComponent SEED_RAID_BAR_VICTORY_COMPONENT = Component.translatable("event.idealist.seed_raid.victory.full");
    public static final MutableComponent SEED_RAID_BAR_DEFEAT_COMPONENT = Component.translatable("event.minecraft.seed_raid.defeat.full");
    private final ServerLevel level;
    private int tick;
    public static final int TIME = 20 * 60;
    int activeWave;
    List<Mob> waveEntities = new ArrayList<>();

    SeedEntity seedEntity;

    final List<Wave> waves = new ArrayList<>();
    boolean active;

    //aprox 100x90x100 hemisphere

    protected ServerPlayer cause;

    public SeedRaidData(ServerLevel level) {
        this.level = level;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return tag;
    }

    public static SavedData.Factory<SeedRaidData> factory(ServerLevel pLevel) {
        return new SavedData.Factory<>(() -> new SeedRaidData(pLevel), (tag, p_324123_) -> loadStatic(tag,pLevel, pLevel.registryAccess()),null);
    }

    public static SeedRaidData loadStatic(CompoundTag compoundTag, ServerLevel pLevel, HolderLookup.Provider registries) {
        SeedRaidData seedRaidData = new SeedRaidData(pLevel);
        seedRaidData.load(compoundTag,registries);
        return seedRaidData;
    }

    public void tick() {
        if (active) {
            tick++;
            if (waveEntities.isEmpty()) {
                if (activeWave >= waves.size()) {
                    end();
                } else {
                    spawnNextWave(false);
                }
            } else {
                waveEntities.removeIf(Entity::isRemoved);
            }
            if (active && seedEntity != null && !seedEntity.isAlive()) {
                end();
            }
        }
    }

    void end() {
        active = false;
        PlayerBingoData data = Services.PLATFORM.getPlayerData(cause);
        boolean win = seedEntity.isAlive();
        if (win) {
            seedEntity.discard();
        } else {
            for (Mob mob : waveEntities) {
                mob.discard();
            }
        }
        cause.changeDimension(new DimensionTransition(cause.server.overworld(),
                data.seed_return_pos(), Vec3.ZERO,cause.getXRot(),cause.getYRot(),false,win ? SEED_LEAVE_TRANSITION_WIN:SEED_LEAVE_TRANSITION_LOSS ));
    }

    public static final DimensionTransition.PostDimensionTransition SEED_LEAVE_TRANSITION_LOSS = entity -> {
        if (entity instanceof ServerPlayer player) {
            PlayerBingoData data = Services.PLATFORM.getPlayerData(player);
            BlockPos pos = data.seed_pos();
            ModSavedData.getOrLoad(player.serverLevel()).removePos(pos);
            BlockState state = player.level().getBlockState(pos);
            if (state.is(Blocks.WHEAT)) {
                player.level().destroyBlock(pos,true);
            }
        }
    };

    public static final DimensionTransition.PostDimensionTransition SEED_LEAVE_TRANSITION_WIN = entity -> {
        if (entity instanceof ServerPlayer player) {
            PlayerBingoData data = Services.PLATFORM.getPlayerData(player);
            BlockPos pos = data.seed_pos();
            ModSavedData.getOrLoad(player.serverLevel()).removePos(pos);
            BlockState state = player.level().getBlockState(pos);
            if (state.is(Blocks.WHEAT)) {
                player.level().setBlock(pos,state.setValue(CropBlock.AGE,7),3);
            }
        }
    };

    public void start(ServerPlayer player) {
        cause = player;
        active = true;
        activeWave = 0;
        //raidEvent.addPlayer(player);
        waves.clear();
        waves.add(Wave.WAVE_1);
        waves.add(Wave.WAVE_2);
        waves.add(Wave.WAVE_3);
        waves.add(Wave.WAVE_4);
        seedEntity = ModEntityTypes.SEED_1.spawn(level,BlockPos.containing(IdeaConfig.Server.SEED_TELEPORT.get()),MobSpawnType.EVENT);
        level.addFreshEntity(seedEntity);
        spawnNextWave(true);
    }

    void spawnNextWave(boolean first) {
    if (!first) {
        seedEntity.grow();
    }
        Wave wave = waves.get(activeWave);
        activeWave++;
        for (Object2IntMap.Entry<EntityType<? extends Mob>> entry :  wave.mobs().object2IntEntrySet()) {
            int count = entry.getIntValue();
            for (int i = 0; i < count;i++) {
                Mob entity = entry.getKey().spawn(level,randomPos(), MobSpawnType.EVENT);
                if (entity != null) {
                    entity.setPersistenceRequired();
                    entity.setTarget(cause);
                    entity.addEffect(new MobEffectInstance(MobEffects.GLOWING,MobEffectInstance.INFINITE_DURATION));
                    if (entity instanceof SparrowEntity sparrowEntity) {
                        sparrowEntity.setAnchorPoint(cause.blockPosition().above(10));
                    }

                    if (entity instanceof WormEntity) {
                        entity.setTarget(seedEntity);
                    } else {
                        entity.setTarget(cause);
                    }

                    waveEntities.add(entity);
                }
            }
        }
    }

    BlockPos randomPos() {
        Vec3 center = IdeaConfig.Server.SEED_TELEPORT.get();
        float angleX = level.getRandom().nextFloat() * 360;
        float x = Mth.sin((float) (Math.PI / 180 * angleX));
        float z = Mth.cos((float) (Math.PI / 180 *angleX));

        Vec3 pos = new Vec3(center.x + x * 64,center.y,center.z + z * 64);
        BlockPos containing = BlockPos.containing(pos);
        return getHeightIgnoringBarriersAndLight(level,containing.above(10)).above();
    }

    public static BlockPos getHeightIgnoringBarriersAndLight(Level level,BlockPos pos) {
        BlockPos containing = pos;

        int y =containing.getY();
        while (y > level.getMinBuildHeight()) {
            containing = new BlockPos(containing.getX(),y,containing.getZ());
            BlockState state = level.getBlockState(containing);
            if (!state.isAir() && !state.is(Blocks.BARRIER) && !state.is(Blocks.LIGHT)) {
                break;
            }
            y--;
        }
        return containing;
    }

    @Nullable
    public static SeedRaidData get(ServerLevel level) {
        return level.getDataStorage()
                .get(factory(level), name(level));
    }

    public static SeedRaidData getOrLoad(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(factory(level), name(level));
    }

    static String name(ServerLevel level) {
        return IdeaList.MOD_ID +"_"+level.dimension().location().getPath()+"_seed_raid";
    }

    public void updateSeedEntity(SeedEntity seedEntity) {
        this.seedEntity = seedEntity;
    }

    record Wave(Object2IntMap<EntityType<? extends Mob>> mobs) {
        public static final Wave WAVE_1 = makeWave();
        public static final Wave WAVE_2 = makeWave();
        public static final Wave WAVE_3 = makeWave();
        public static final Wave WAVE_4 = makeWave();
    }

    static Wave makeWave() {
        Object2IntMap<EntityType<? extends Mob>> mobs = new Object2IntOpenHashMap<>(3);
        mobs.put(ModEntityTypes.ANT,4);
        mobs.put(ModEntityTypes.WORM,4);
        mobs.put(ModEntityTypes.SPARROW,4);
        return new Wave(mobs);
    }

    protected void load(CompoundTag compoundTag, HolderLookup.Provider registries) {
        ListTag listTag = compoundTag.getList("seed_raid", Tag.TAG_COMPOUND);
        for (Tag tag : listTag) {
            CompoundTag tag1 = (CompoundTag)tag;
        }
    }

}
