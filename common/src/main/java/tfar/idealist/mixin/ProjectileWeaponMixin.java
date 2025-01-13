package tfar.idealist.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import tfar.idealist.IdeaList;

@Mixin(ProjectileWeaponItem.class)
public class ProjectileWeaponMixin {
    @ModifyVariable(method = "shoot", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private float modifyAcc(float original,
                            ServerLevel level,
                            LivingEntity shooter) {
        return IdeaList.modifyInaccuracy(original, shooter);
    }
}
