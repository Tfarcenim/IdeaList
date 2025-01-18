package tfar.idealist.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WormEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);



    public WormEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, SeedEntity.class, false));
    }

    public static AttributeSupplier.Builder attributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE,3)
                .add(Attributes.MAX_HEALTH,20)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.FOLLOW_RANGE, 96);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "idle_controller", 0, event -> {
            boolean isAttacking = this.swinging;
            boolean isDead = this.dead || this.getHealth() < 0.01 || this.isDeadOrDying();
            boolean isFastMoving = getDeltaMovement().lengthSqr() > .01;

            if (event.isMoving() && !isDead && !isAttacking) {
                return event.setAndContinue(DefaultAnimations.WALK);
            }
            return event.setAndContinue(DefaultAnimations.IDLE);
        })
                .triggerableAnim("bite", DefaultAnimations.ATTACK_BITE)
                .triggerableAnim("death",DefaultAnimations.DIE));
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(6);
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
