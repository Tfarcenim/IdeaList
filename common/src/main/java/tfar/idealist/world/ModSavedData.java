package tfar.idealist.world;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import tfar.idealist.IdeaList;

import java.util.*;

public class ModSavedData extends SavedData {

    private final ServerLevel level;
    protected List<BlockPos> dont_grow = new ArrayList<>();

    protected Set<UUID> joinedBefore = new HashSet<>();

    protected boolean firstStart = true;

    public ModSavedData(ServerLevel level) {
        this.level = level;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        Tag tag1 = BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE,dont_grow).resultOrPartial(IdeaList.LOG::error).orElseThrow();
        tag.put("dont_grow", tag1);
        Tag tag2 = UUIDUtil.CODEC_SET.encodeStart(NbtOps.INSTANCE,joinedBefore).resultOrPartial(IdeaList.LOG::error).orElseThrow();
        tag.put("joined_before",tag2);
        tag.putBoolean("first_start",firstStart);
        return tag;
    }



    public void addPos(BlockPos pos) {
        dont_grow.add(pos);
        setDirty();
    }

    public void addPlayer(ServerPlayer player) {
        joinedBefore.add(player.getUUID());
        setDirty();
    }

    public boolean hasJoinedBefore(ServerPlayer player) {
        return joinedBefore.contains(player.getUUID());
    }

    public void removePos(BlockPos  pos) {
        dont_grow.remove(pos);
        setDirty();
    }

    public boolean growthBlocked(BlockPos pos) {
        return dont_grow.contains(pos);
    }

    public boolean isFirstStart() {
        return firstStart;
    }

    public void setFirstStart() {
        firstStart = false;
        setDirty();
    }

    @Nullable
    public static ModSavedData get(ServerLevel level) {
        return level.getDataStorage()
                .get(factory(level), name(level));
    }

    public static ModSavedData getOrLoad(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(factory(level), name(level));
    }

    static String name(ServerLevel level) {
        return IdeaList.MOD_ID +"_"+level.dimension().location().getPath()+"_mod_data";
    }


    public static Factory<ModSavedData> factory(ServerLevel pLevel) {
        return new Factory<>(() -> new ModSavedData(pLevel), (tag, p_324123_) -> loadStatic(tag,pLevel, pLevel.registryAccess()),null);
    }

    public static ModSavedData loadStatic(CompoundTag compoundTag, ServerLevel pLevel, HolderLookup.Provider registries) {
        ModSavedData modSavedData = new ModSavedData(pLevel);
        modSavedData.load(compoundTag,registries);
        return modSavedData;
    }

    protected void load(CompoundTag compoundTag, HolderLookup.Provider registries) {
        ListTag listTag = compoundTag.getList("dont_grow", Tag.TAG_COMPOUND);
        dont_grow = new ArrayList<>(BlockPos.CODEC.listOf().parse(new Dynamic<>(NbtOps.INSTANCE, listTag)).resultOrPartial(IdeaList.LOG::error).orElseThrow());
        joinedBefore = UUIDUtil.CODEC_SET.parse(new Dynamic<>(NbtOps.INSTANCE,compoundTag.get("joined_before"))).resultOrPartial(IdeaList.LOG::error).orElseThrow();
        firstStart = compoundTag.getBoolean("first_start");
    }
}
