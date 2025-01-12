package tfar.idealist;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.idealist.init.ModEntityTypes;
import tfar.idealist.init.ModItems;
import tfar.idealist.platform.Services;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class IdeaList {

    public static final String MOD_ID = "idealist";
    public static final String MOD_NAME = "IdeaList";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        Services.PLATFORM.registerAll(ModEntityTypes.class, BuiltInRegistries.ENTITY_TYPE,cast(EntityType.class));
        Services.PLATFORM.registerAll(ModItems.class, BuiltInRegistries.ITEM, Item.class);

    }

    public static void onBrushingCompleted(Player player, BlockPos pos, Level level) {
        if (Services.PLATFORM.getData(player).twist()) {
            EntityType.WARDEN.spawn((ServerLevel) level, pos, MobSpawnType.EVENT);
        }
    }

    public static void onEndPortalCompleted(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = context.getPlayer();
        if (Services.PLATFORM.getData(player).twist()) {
            Level level = context.getLevel();
            if (player != null) {
                cir.setReturnValue(InteractionResult.CONSUME);//don't construct the portal yet
                EnderMan enderMan = EntityType.ENDERMAN.spawn((ServerLevel) level, player.blockPosition(), MobSpawnType.EVENT);
                enderMan.setItemSlot(EquipmentSlot.HEAD,ModItems.PURPLE_GLASSES.getDefaultInstance());
            }
        }
    }

    @SuppressWarnings("unchecked")
    static <T> Class<T> cast(Class<?> clazz) {
        return (Class<T>) clazz;
    }

    public static ResourceLocation id(String key) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,key);
    }
}

//- When a player tries to kill a cow, the cow stops moving and looks at the player, then the cow along with a bunch of other cows in the "area" bunch up together to form a giant cow mech (the other cows don’t have to be there already, have it so they spawn nearby when the cow is hit then they all rush towards the cow that was hit). The cow mech can shoot lasers out of its eyes for 10 seconds at a time (cooldown 10 seconds) causing half a heart of damage a hit. The cow mech has 150 health.
//
//
//
//- When a player tries to mine a diamond, the diamond ore block grows arms and legs and starts running away. To get the diamond the player has to chase the diamond ore block and kill it. The diamond ore block has 20 health and runs at 1.5x the speed of a player. Once you kill the diamond ore block the objective is complete.
//
//
//
//- When a player tries to kill another player, their hotbar starts randomly shifting around
//
//
//
//- When a player tries to trade with a piglin, the piglin takes the gold and runs away really fast building a complex parkour course with lava under it and waits at the end of it. The player has to complete the parkour course and make it to the piglin in order for the piglin to complete the trade. (the player cannot place blocks while this challenge is active).
//
//
//
//- When a player brushes suspicious sand, they complete the objective. Although as soon as the player finishes, a dinosaur skeleton comes alive out of the sand and attacks the player. The dinosaur skeleton does 4 hearts of damage a hit, and can run faster than a player. (Make the dinosaur a T-rex)
//
//
//
//- When a player puts the last ender eye in the last available portal block slot, an enderman walks into the room towards the player and tells the player he needs to pass a test to see if the player is allowed in the end (make the enderman have glasses). The test is 5 questions and if the player gets one wrong they die (the enderman lets the player know the rules before the test begins). To answer a question, the player has to write it in chat.
//
//
//
//- When a player tries to shoot an arrow, the arrow goes flying in a random direction, changing the flight path of the arrow and making it fly around randomly. Every 10th shot the player makes acts like a normal arrow
//
//
//
//- When a player starts a raid, every pillager and mob that is a part of the raid is a ravager with speed 2
//
//
//
//- When a player plants a seed, the player gets sucked into the seed and taken to another dimension called the Seed Dimension. In the seed dimension everything looks green almost like you are inside a plant. There is a mound in the middle, where the player spawns in. In the middle of the mound there is a block with a seed growing and the player has to defend the seed and let it grow as 3 different types of seed monsters attempt to break and ruin the seed. The monsters are: worms, giant birds and ants.