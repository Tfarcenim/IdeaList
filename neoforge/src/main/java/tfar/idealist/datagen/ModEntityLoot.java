package tfar.idealist.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.packs.VanillaEntityLoot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootTable;
import tfar.idealist.init.ModEntityTypes;

import java.util.stream.Stream;

public class ModEntityLoot extends VanillaEntityLoot {

    public ModEntityLoot(HolderLookup.Provider registries) {
        super(registries);
    }

    @Override
    public void generate() {
        nothing(ModEntityTypes.ANT);
        nothing(ModEntityTypes.ANIMATED_BLOCK);
        nothing(ModEntityTypes.SPARROW);
        nothing(ModEntityTypes.TREX_SKELETON);
        nothing(ModEntityTypes.WORM);
    }

    protected void nothing(EntityType<?> type) {
        this.add(type, LootTable.lootTable());
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModDatagen.getKnownEntityTypes().filter(type -> type.getCategory() != MobCategory.MISC);
    }
}
