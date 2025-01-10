package tfar.idealist;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextComponents {

    public static final MutableComponent ROOT = Component.translatable("advancements." + IdeaList.MOD_ID + ".root.title");
    public static final MutableComponent ROOT_DESC = Component.translatable("advancements." + IdeaList.MOD_ID + ".root.description");

    public static final MutableComponent KILL_COW = Component.translatable("advancements."+ IdeaList.MOD_ID+".kill_cow.title");
    public static final MutableComponent KILL_COW_DESC = Component.translatable("advancements."+ IdeaList.MOD_ID+".kill_cow.description");

    public static final MutableComponent KILL_PLAYER = Component.translatable("advancements."+ IdeaList.MOD_ID+".kill_player.title");
    public static final MutableComponent KILL_PLAYER_DESC = Component.translatable("advancements."+ IdeaList.MOD_ID+".kill_player.description");

    public static final MutableComponent ADV_BULLSEYE = Component.translatable("advancements."+IdeaList.MOD_ID+".bullseye.title");
    public static final MutableComponent ADV_BULLSEYE_DESC = Component.translatable("advancements."+IdeaList.MOD_ID+".bullseye.description");

    public static final MutableComponent PLANT_WHEAT = Component.translatable("advancements."+IdeaList.MOD_ID+".plant_wheat.title");
    public static final MutableComponent PLANT_WHEAT_DESC = Component.translatable("advancements."+IdeaList.MOD_ID+".plant_wheat.description");
    
    public static final MutableComponent TAB_TITLE = Component.translatable("itemGroup."+ IdeaList.MOD_ID);
}
