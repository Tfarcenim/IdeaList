package tfar.idealist.world;

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
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import tfar.idealist.IdeaList;

public class SeedRaidData extends SavedData {

    //I will get you the growing seed model by Tuesday.
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
        tick++;
        raidEvent.setProgress((float)tick/TIME);
    }

    public void start(ServerPlayer player) {
        cause = player;
        raidEvent.addPlayer(player);
    }

    @Nullable
    public static SeedRaidData get(ServerLevel level) {
        return level.getDataStorage()
                .get(factory(level), IdeaList.MOD_ID +"_"+level.dimension().location());
    }

    public static SeedRaidData getOrLoad(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(factory(level), IdeaList.MOD_ID +"_"+level.dimension().location());
    }

    protected void load(CompoundTag compoundTag, HolderLookup.Provider registries) {
        ListTag listTag = compoundTag.getList("seed_raid", Tag.TAG_COMPOUND);
        for (Tag tag : listTag) {
            CompoundTag tag1 = (CompoundTag)tag;
        }
    }

}
