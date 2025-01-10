package tfar.idealist.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.packs.VanillaNetherAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import tfar.idealist.IdeaConfig;
import tfar.idealist.IdeaList;
import tfar.idealist.TextComponents;
import tfar.idealist.init.ModItems;

import java.util.Optional;
import java.util.function.Consumer;

public class BingoAdvancements implements AdvancementProvider.AdvancementGenerator {

    //- Kill a cow
    //- Mine a diamond
    //- Kill another player
    //- Trade with a piglin
    //- Brush suspicious sand
    //- Enter the end
    //- Shoot a target block with an arrow from 50 blocks away
    //- Complete a raid
    //- Grow wheat from a seed

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(ModItems.BINGO_CARD, TextComponents.ROOT, TextComponents.ROOT_DESC,
                        ResourceLocation.parse("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementType.TASK, true, true, false)
                .requirements(AdvancementRequirements.Strategy.OR)
                .addCriterion("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
                .save(saver, IdeaList.id("root").toString());

        EntityPredicate.Builder cowPredicate = EntityPredicate.Builder.entity().of(EntityType.COW);

        AdvancementHolder killCow = Advancement.Builder.advancement().parent(root)
                .display(Items.COW_SPAWN_EGG,TextComponents.KILL_COW,TextComponents.KILL_COW_DESC, null, AdvancementType.TASK, true, true, false)
                .addCriterion("player_killed_entity", KilledTrigger.TriggerInstance.playerKilledEntity(cowPredicate))
                .save(saver, IdeaConfig.Defaults.KILL_COW.toString());

        AdvancementHolder getDiamond = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.DIAMOND,
                        Component.translatable("advancements.story.mine_diamond.title"),
                        Component.translatable("advancements.story.mine_diamond.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("diamond", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND))
                .save(saver,IdeaConfig.Defaults.MINE_DIAMOND.toString());

        EntityPredicate.Builder playerPredicate = EntityPredicate.Builder.entity().of(EntityType.PLAYER);

        AdvancementHolder killPlayer = Advancement.Builder.advancement().parent(root)
                .display(Items.PLAYER_HEAD,TextComponents.KILL_PLAYER,TextComponents.KILL_PLAYER_DESC, null, AdvancementType.TASK, true, true, false)
                .addCriterion("player_killed_entity", KilledTrigger.TriggerInstance.playerKilledEntity(playerPredicate))
                .save(saver, IdeaConfig.Defaults.KILL_PLAYER.toString());

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.GOLD_INGOT,
                        Component.translatable("advancements.nether.distract_piglin.title"),
                        Component.translatable("advancements.nether.distract_piglin.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "distract_piglin_directly",
                        PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                Optional.of(VanillaNetherAdvancements.DISTRACT_PIGLIN_PLAYER_ARMOR_PREDICATE),
                                ItemPredicate.Builder.item().of(PiglinAi.BARTERING_ITEM),
                                Optional.of(
                                        EntityPredicate.wrap(
                                                EntityPredicate.Builder.entity().of(EntityType.PIGLIN).flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false))
                                        )
                                )
                        )
                )
                .save(saver, IdeaConfig.Defaults.TRADE_PIGLIN.toString());


    }
}
