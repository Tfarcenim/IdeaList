package tfar.idealist.datagen;

import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;
import tfar.idealist.IdeaList;
import tfar.idealist.TextComponents;
import tfar.idealist.init.ModEntityTypes;
import tfar.idealist.init.ModItems;
import tfar.idealist.world.SeedRaidData;

import java.util.function.Supplier;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output) {
        super(output, IdeaList.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addDefaultItem(() -> ModItems.BINGO_CARD);
        add("death.attack.rose",  "%1$s was pricked to death");
        add( "death.attack.rose.player", "%1$s walked into a rose bush while trying to escape %2$s");
        add("sleep.too_big","Too big to sleep in this bed");

        addTextComponent(TextComponents.KILL_COW,"Kill Cow");
        addTextComponent(TextComponents.KILL_COW_DESC,"Kill Cow");

        addTextComponent(TextComponents.KILL_PLAYER,"Kill Player");
        addTextComponent(TextComponents.KILL_PLAYER_DESC,"Kill Player");

        addTextComponent(TextComponents.ADV_BULLSEYE,"Bullseye+");
        addTextComponent(TextComponents.ADV_BULLSEYE_DESC,"Hit the bullseye of a Target block from at least 50 meters away");

        addTextComponent(TextComponents.PLANT_WHEAT,"Green Thumb");
        addTextComponent(TextComponents.PLANT_WHEAT_DESC,"Plant wheat seeds on farmland");

         addTextComponent(SeedRaidData.SEED_RAID_NAME_COMPONENT,"Seed Raid");
        addTextComponent(SeedRaidData.SEED_RAID_BAR_VICTORY_COMPONENT,"Seed Raid - Victory");
        addTextComponent(SeedRaidData.SEED_RAID_BAR_DEFEAT_COMPONENT,"Seed Raid - Loss");

        addDefaultEntityType(() -> ModEntityTypes.WORM);
        addDefaultEntityType(() -> ModEntityTypes.SPARROW);
        addDefaultEntityType(() -> ModEntityTypes.ANT);
        addDefaultEntityType(() -> ModEntityTypes.SEED);
        addDefaultEntityType(() -> ModEntityTypes.VACUUM);
    }

    protected void addPotion(Holder<Potion> potion, String name) {
        ItemStack stack = PotionContents.createItemStack(Items.POTION,potion);
        add(stack.getDescriptionId(), name);
        
        ItemStack splashStack = PotionContents.createItemStack(Items.SPLASH_POTION,potion);
        add(splashStack.getDescriptionId(), "Splash "+name);

        ItemStack lingeringStack = PotionContents.createItemStack(Items.LINGERING_POTION,potion);
        add(lingeringStack.getDescriptionId(), "Lingering "+name);
    }

    protected void addDefaultMobEffect(Supplier<? extends MobEffect> supplier) {
        addEffect(supplier,getNameFromEffect(supplier.get()));
    }

    protected void addDefaultItem(Supplier<? extends Item> supplier) {
        addItem(supplier,getNameFromItem(supplier.get()));
    }

    protected void addDefaultBlock(Supplier<? extends Block> supplier) {
        addBlock(supplier,getNameFromBlock(supplier.get()));
    }

    protected void addDefaultEntityType(Supplier<EntityType<?>> supplier) {
        addEntityType(supplier,getNameFromEntity(supplier.get()));
    }

    public static String getNameFromItem(Item item) {
        return StringUtils.capitaliseAllWords(item.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromBlock(Block block) {
        return StringUtils.capitaliseAllWords(block.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromEffect(MobEffect effect) {
        return StringUtils.capitaliseAllWords(effect.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromEntity(EntityType<?> entity) {
        return StringUtils.capitaliseAllWords(entity.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    protected void addTextComponent(MutableComponent component, String text) {
        ComponentContents contents = component.getContents();
        if (contents instanceof TranslatableContents translatableContents) {
            add(translatableContents.getKey(),text);
        } else {
            throw new UnsupportedOperationException(component +" is not translatable");
        }
    }

}
