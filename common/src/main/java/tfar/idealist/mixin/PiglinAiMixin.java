package tfar.idealist.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.idealist.IdeaList;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {

    @Inject(method = "pickUpItem",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;admireGoldItem(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private static void addExtraBehavior(Piglin piglin, ItemEntity itemEntity, CallbackInfo ci, @Local ItemStack stack) {
        IdeaList.onPiglinGoldPickup(piglin,itemEntity,stack);
    }

    @Inject(method = "stopHoldingOffHandItem",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;throwItems(Lnet/minecraft/world/entity/monster/piglin/Piglin;Ljava/util/List;)V",ordinal = 0),cancellable = true)
    private static void finishTrade(Piglin piglin, boolean shouldBarter, CallbackInfo ci) {
        IdeaList.finishTrade(piglin,shouldBarter,ci);
    }
}
