package tfar.idealist.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModDatagen {

    public static void gather(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(true,new ModLangProvider(output));
        generator.addProvider(true,new ModDamageTypeTagsProvider(output,lookupProvider,existingFileHelper));
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(output,lookupProvider,existingFileHelper);
        generator.addProvider(true,blockTagsProvider);
        generator.addProvider(true,new ModItemTagsProvider(output,lookupProvider,blockTagsProvider.contentsGetter()));
        generator.addProvider(event.includeClient(),new ModItemModelProvider(output,existingFileHelper));
        generator.addProvider(true,new AdvancementProvider(output,lookupProvider,existingFileHelper, List.of(new BingoAdvancements())));
        generator.addProvider(true,new ModDataPackProvider(output,lookupProvider));
    }

}
