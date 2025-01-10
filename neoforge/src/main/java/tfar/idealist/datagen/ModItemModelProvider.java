package tfar.idealist.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import tfar.idealist.IdeaList;
import tfar.idealist.init.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, IdeaList.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        makeOneLayerItem(ModItems.BINGO_CARD);
    }

    protected ModelFile.ExistingModelFile generated = getExistingFile(mcLoc("item/generated"));
    protected ModelFile.ExistingModelFile handheld = getExistingFile(mcLoc("item/handheld"));

    protected void makeOneLayerItem(Item item, ResourceLocation texture) {
        makeOneLayerItem(item,texture,generated);
    }

    protected void makeOneLayerItem(Item item, ResourceLocation texture,ModelFile parent) {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        if (existingFileHelper.exists(texture, PackType.CLIENT_RESOURCES, ".png", "textures")) {
            getBuilder(path).parent(parent)
                    .texture("layer0",texture);
        } else {
            System.out.println("no texture for " + item + " found, skipping");
        }
    }


    protected void makeOneLayerItem(Item item) {
        makeOneLayerItem(item,generated);
    }

    protected void makeOneLayerItem(Item item,ModelFile parent) {
        ResourceLocation texture = BuiltInRegistries.ITEM.getKey(item);
        makeOneLayerItem(item, texture.withPrefix("item/"),parent);
    }


}
