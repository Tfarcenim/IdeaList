package tfar.idealist.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TRexSkeletonEntity extends Monster implements GeoEntity {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public TRexSkeletonEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }


    public static AttributeSupplier.Builder attributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE,8)
                .add(Attributes.MAX_HEALTH,200)
                .add(Attributes.MOVEMENT_SPEED, 0.5F)
                .add(Attributes.FOLLOW_RANGE, 64);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "idle_controller", 0, event -> {
            boolean isAttacking = this.swinging;
            boolean isDead = this.dead || this.getHealth() < 0.01 || this.isDeadOrDying();

            if (event.isMoving() && !isDead && !isAttacking) {
                return event.setAndContinue(DefaultAnimations.WALK);
            }
            return event.setAndContinue(DefaultAnimations.IDLE);
        })
                .triggerableAnim("bite", DefaultAnimations.ATTACK_BITE)
                .triggerableAnim("death",DefaultAnimations.DIE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (deathTime == 1) {
            this.triggerAnim("idle_controller", "death");
        }
        if (this.deathTime >= 30 && !isRemoved() && !level().isClientSide) {
            this.level().broadcastEntityEvent(this, EntityEvent.POOF);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }
}
