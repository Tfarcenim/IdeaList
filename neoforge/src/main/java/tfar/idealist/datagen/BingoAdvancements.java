package tfar.idealist.datagen;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
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
                        AdvancementType.TASK, true, false, false)
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

     /*   Advancement.Builder.advancement()
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
                                ItemPredicate.Builder.item().of(PiglinAi.BARTERING_ITEM),
                                Optional.of(
                                        EntityPredicate.wrap(
                                                EntityPredicate.Builder.entity().of(EntityType.PIGLIN).flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false))
                                        )
                                )
                        )
                )
                .save(saver, IdeaConfig.Defaults.TRADE_PIGLIN.toString());*/

        AdvancementHolder tradePiglin = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.GOLD_INGOT,
                        Component.translatable("advancements.nether.distract_piglin.title"),
                        Component.translatable("advancements.nether.distract_piglin.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("trade_piglin",CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(saver,IdeaConfig.Defaults.TRADE_PIGLIN.toString());

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Blocks.END_STONE,
                        Component.translatable("advancements.story.enter_the_end.title"),
                        Component.translatable("advancements.story.enter_the_end.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("entered_end", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END))
                .save(saver, IdeaConfig.Defaults.ENTER_THE_END.toString());

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Blocks.TARGET.asItem(),
                        TextComponents.ADV_BULLSEYE,
                        TextComponents.ADV_BULLSEYE_DESC,
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "bullseye",
                        TargetBlockTrigger.TriggerInstance.targetHit(
                                MinMaxBounds.Ints.atLeast(2),
                                Optional.of(
                                        EntityPredicate.wrap(EntityPredicate.Builder.entity().distance(DistancePredicate.horizontal(MinMaxBounds.Doubles.atLeast(50))))
                                )
                        )
                )
                .save(saver, IdeaConfig.Defaults.SHOOT_A_TARGET.toString());

        HolderLookup.RegistryLookup<BannerPattern> registrylookup = registries.lookupOrThrow(Registries.BANNER_PATTERN);

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Raid.getLeaderBannerInstance(registrylookup),
                        Component.translatable("advancements.adventure.hero_of_the_village.title"),
                        Component.translatable("advancements.adventure.hero_of_the_village.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("hero_of_the_village", PlayerTrigger.TriggerInstance.raidWon())
                .save(saver, IdeaConfig.Defaults.COMPLETE_A_RAID.toString());

        AdvancementHolder advancementholder7 = Advancement.Builder.advancement()
                .parent(root)
                .addCriterion(
                        "plant_wheat_seeds",
                        ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                                LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.FARMLAND)),
                                ItemPredicate.Builder.item().of(Items.WHEAT_SEEDS)
                        )
                )
                .display(
                        Items.WHEAT,
                        TextComponents.PLANT_WHEAT,
                        TextComponents.PLANT_WHEAT_DESC,
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .save(saver,IdeaConfig.Defaults.GROW_WHEAT.toString());

     /*   AdvancementHolder advancementholder10 = VanillaAdventureAdvancements.respectingTheRemnantsCriterions(Advancement.Builder.advancement())
                .parent(root)
                .display(
                        Items.BRUSH,
                        Component.translatable("advancements.adventure.salvage_sherd.title"),
                        Component.translatable("advancements.adventure.salvage_sherd.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .save(saver, IdeaConfig.Defaults.BRUSH_SUSPICIOUS_SAND.toString());*/


        AdvancementHolder brushSand = Advancement.Builder.advancement()
                .parent(root)
                .display(Items.BRUSH,
                        Component.translatable("advancements.adventure.salvage_sherd.title"),
                        Component.translatable("advancements.adventure.salvage_sherd.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("brush_suspicious_sand",CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(saver,IdeaConfig.Defaults.BRUSH_SUSPICIOUS_SAND.toString());
    }
}
