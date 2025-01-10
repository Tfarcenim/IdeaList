package tfar.idealist.mixin;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.idealist.IdeaList;

@Mixin(EnderEyeItem.class)
public class EnderEyeItemMixin {
    @Inject(method = "useOn",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/pattern/BlockPattern$BlockPatternMatch;getFrontTopLeft()Lnet/minecraft/core/BlockPos;"),cancellable = true)
    private void onPortalCompleted(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        IdeaList.onEndPortalCompleted(context,cir);
    }
}
