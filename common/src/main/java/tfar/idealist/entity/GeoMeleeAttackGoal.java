package tfar.idealist.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import software.bernie.geckolib.animatable.GeoEntity;

public class GeoMeleeAttackGoal<G extends PathfinderMob & GeoEntity> extends MeleeAttackGoal {
    public GeoMeleeAttackGoal(G mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.canPerformAttack(target)) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(target);
            ((G)mob).triggerAnim("controller","bite");
        }
    }
}
