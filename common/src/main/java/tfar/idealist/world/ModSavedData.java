package tfar.idealist.world;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import tfar.idealist.IdeaList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModSavedData extends SavedData {

    private final ServerLevel level;
    protected List<BlockPos> dont_grow = new ArrayList<>();

    public ModSavedData(ServerLevel level) {
        this.level = level;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        Tag tag1 = BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE,dont_grow).resultOrPartial(IdeaList.LOG::error).orElseThrow();
        tag.put("dont_grow", tag1);
        return tag;
    }



    public void addPos(BlockPos pos) {
        dont_grow.add(pos);
        setDirty();
    }

    public void removePos(BlockPos  pos) {
        dont_grow.remove(pos);
        setDirty();
    }

    public boolean growthBlocked(BlockPos pos) {
        return dont_grow.contains(pos);
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
        ModSavedData seedRaidData = new ModSavedData(pLevel);
        seedRaidData.load(compoundTag,registries);
        return seedRaidData;
    }

    protected void load(CompoundTag compoundTag, HolderLookup.Provider registries) {
        ListTag listTag = compoundTag.getList("dont_grow", Tag.TAG_COMPOUND);
        dont_grow = new ArrayList<>(BlockPos.CODEC.listOf().parse(new Dynamic<>(NbtOps.INSTANCE, listTag)).resultOrPartial(IdeaList.LOG::error).get());
    }
}
