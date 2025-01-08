package tfar.idealist.world;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import tfar.idealist.IdeaList;

import java.util.ArrayList;
import java.util.List;

public class SlowPasteSavedData extends SavedData {

    private final List<SlowPaste> pastes = new ArrayList<>();

    @Override
    public CompoundTag save(CompoundTag pCompoundTag, HolderLookup.Provider registries) {
        ListTag listTag = new ListTag();
        for (SlowPaste slowPaste : pastes) {
            listTag.add(slowPaste.save());
        }
        pCompoundTag.put("pastes",listTag);
        return pCompoundTag;
    }

    public static SlowPasteSavedData loadStatic(CompoundTag compoundTag, HolderLookup.Provider registries) {
        SlowPasteSavedData SlowPasteSavedData = new SlowPasteSavedData();
        SlowPasteSavedData.load(compoundTag,registries);
        return SlowPasteSavedData;
    }

    public static SlowPasteSavedData loadFromLevel(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(factory(level), IdeaList.MOD_ID +"_"+level.dimension().location());
    }

    public static SavedData.Factory<SlowPasteSavedData> factory(ServerLevel pLevel) {
        return new SavedData.Factory<>(() -> new SlowPasteSavedData(), (tag, p_324123_) -> loadStatic(tag, pLevel.registryAccess()),null);
    }

    public void addSlowPasteData(BlockPos start, Clipboard clipboard, int speed,boolean noAir) {
        SlowPaste slowPaste = SlowPaste.create(start,clipboard,speed,noAir);
        pastes.add(slowPaste);
        setDirty();
    }

    public void tick(ServerLevel level) {
        for (SlowPaste slowPaste : pastes) {
            slowPaste.tick(level);
        }
        pastes.removeIf(SlowPaste::finished);
    }

    protected void load(CompoundTag compoundTag, HolderLookup.Provider registries) {
        ListTag listTag = compoundTag.getList("pastes", Tag.TAG_COMPOUND);
        for (Tag tag : listTag) {
            CompoundTag tag1 = (CompoundTag)tag;
            SlowPaste slowPaste = SlowPaste.loadFromTag(tag1,registries);
            pastes.add(slowPaste);
        }
    }
}
