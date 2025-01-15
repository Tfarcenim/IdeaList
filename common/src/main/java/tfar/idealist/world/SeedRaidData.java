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
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tfar.idealist.IdeaList;
import tfar.idealist.PlayerBingoData;
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
    List<LivingEntity> waveEntities = new ArrayList<>();

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
                    spawnNextWave();
                }
            }
        }
    }

    void end() {
        active = false;
        PlayerBingoData data = Services.PLATFORM.getData(cause);
        cause.changeDimension(new DimensionTransition(cause.server.overworld(),
                data.return_pos(), Vec3.ZERO,cause.getXRot(),cause.getYRot(),false,SEED_LEAVE_TRANSITION_WIN));
    }

    public static final DimensionTransition.PostDimensionTransition SEED_LEAVE_TRANSITION_WIN = entity -> {
        if (entity instanceof ServerPlayer player) {
            PlayerBingoData data = Services.PLATFORM.getData(player);
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
        //raidEvent.addPlayer(player);
        waves.add(Wave.WAVE_1);
        spawnNextWave();
    }

    void spawnNextWave() {
        activeWave++;
        Wave wave = waves.get(activeWave-1);

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

    record Wave(Object2IntMap<EntityType<? extends LivingEntity>> mobs) {
        public static final Wave WAVE_1 = makeWave();
    }

    static Wave makeWave() {
        Object2IntMap<EntityType<? extends LivingEntity>> mobs = new Object2IntOpenHashMap<>(3);
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
