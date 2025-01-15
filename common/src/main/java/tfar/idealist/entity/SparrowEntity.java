package tfar.idealist.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;
import tfar.idealist.world.SeedRaidData;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class SparrowEntity extends FlyingMob implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final RawAnimation GLIDE = RawAnimation.begin().thenLoop("move.glide");


    Vec3 moveTargetPoint = Vec3.ZERO;
    BlockPos anchorPoint = BlockPos.ZERO;
    AttackPhase attackPhase = AttackPhase.CIRCLE;

    public SparrowEntity(EntityType<? extends FlyingMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new PhantomMoveControl(this);
        this.lookControl = new PhantomLookControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PhantomAttackStrategyGoal());
        this.goalSelector.addGoal(2, new PhantomSweepAttackGoal());
        this.goalSelector.addGoal(3, new PhantomCircleAroundAnchorGoal());
        this.targetSelector.addGoal(1, new PhantomAttackPlayerTargetGoal());
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
            boolean isDead = this.dead || this.getHealth() < 0.01 || this.isDeadOrDying();
            boolean moving_down = getDeltaMovement().y <0;
            if ( !onGround()) {
                return event.setAndContinue(moving_down ? GLIDE :DefaultAnimations.FLY);
            }
            return event.setAndContinue(DefaultAnimations.IDLE);
        })
                .triggerableAnim("bite", DefaultAnimations.ATTACK_BITE)
                .triggerableAnim("death",DefaultAnimations.DIE));
    }

    public void setAnchorPoint(BlockPos anchorPoint) {
        this.anchorPoint = anchorPoint;
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
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new PhantomBodyRotationControl(this);
    }


    enum AttackPhase {
        CIRCLE,
        SWOOP
    }

    class PhantomAttackPlayerTargetGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(96);
        private int nextScanTick = reducedTickDelay(20);

        @Override
        public boolean canUse() {
            if (this.nextScanTick > 0) {
                this.nextScanTick--;
                return false;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                List<Player> list = SparrowEntity.this.level()
                        .getNearbyPlayers(this.attackTargeting, SparrowEntity.this, SparrowEntity.this.getBoundingBox().inflate(96.0, 64.0, 96.0));
                if (!list.isEmpty()) {
                    list.sort(Comparator.<Player, Double>comparing(Entity::getY).reversed());

                    for (Player player : list) {
                        if (SparrowEntity.this.canAttack(player, TargetingConditions.DEFAULT)) {
                            SparrowEntity.this.setTarget(player);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity = SparrowEntity.this.getTarget();
            return livingentity != null && SparrowEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT);
        }
    }

    class PhantomAttackStrategyGoal extends Goal {
        private int nextSweepTick;

        @Override
        public boolean canUse() {
            LivingEntity livingentity = SparrowEntity.this.getTarget();
            return livingentity != null && SparrowEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT);
        }

        @Override
        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(10);
            SparrowEntity.this.attackPhase = AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        @Override
        public void stop() {
            SparrowEntity.this.anchorPoint = SeedRaidData.getHeightIgnoringBarriersAndLight(level(),anchorPoint)
                    .above(10 + SparrowEntity.this.random.nextInt(20));
        }

        @Override
        public void tick() {
            if (SparrowEntity.this.attackPhase == AttackPhase.CIRCLE) {
                this.nextSweepTick--;
                if (this.nextSweepTick <= 0) {
                    SparrowEntity.this.attackPhase = AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((8 + SparrowEntity.this.random.nextInt(4)) * 20);
                    SparrowEntity.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + SparrowEntity.this.random.nextFloat() * 0.1F);
                }
            }
        }

        private void setAnchorAboveTarget() {
            SparrowEntity.this.anchorPoint = SparrowEntity.this.getTarget().blockPosition().above(20 + SparrowEntity.this.random.nextInt(20));
            if (SparrowEntity.this.anchorPoint.getY() < SparrowEntity.this.level().getSeaLevel()) {
                SparrowEntity.this.anchorPoint = new BlockPos(
                        SparrowEntity.this.anchorPoint.getX(), SparrowEntity.this.level().getSeaLevel() + 1, SparrowEntity.this.anchorPoint.getZ()
                );
            }
        }
    }

    class PhantomBodyRotationControl extends BodyRotationControl {
        public PhantomBodyRotationControl(Mob mob) {
            super(mob);
        }

        @Override
        public void clientTick() {
            SparrowEntity.this.yHeadRot = SparrowEntity.this.yBodyRot;
            SparrowEntity.this.yBodyRot = SparrowEntity.this.getYRot();
        }
    }

    class PhantomCircleAroundAnchorGoal extends PhantomMoveTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        @Override
        public boolean canUse() {
            return SparrowEntity.this.getTarget() == null || SparrowEntity.this.attackPhase == AttackPhase.CIRCLE;
        }

        @Override
        public void start() {
            this.distance = 5.0F + SparrowEntity.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + SparrowEntity.this.random.nextFloat() * 9.0F;
            this.clockwise = SparrowEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        @Override
        public void tick() {
            if (SparrowEntity.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + SparrowEntity.this.random.nextFloat() * 9.0F;
            }

            if (SparrowEntity.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                this.distance++;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (SparrowEntity.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = SparrowEntity.this.random.nextFloat() * 2.0F * (float) Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (SparrowEntity.this.moveTargetPoint.y < SparrowEntity.this.getY() && !SparrowEntity.this.level().isEmptyBlock(SparrowEntity.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (SparrowEntity.this.moveTargetPoint.y > SparrowEntity.this.getY() && !SparrowEntity.this.level().isEmptyBlock(SparrowEntity.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }
        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(SparrowEntity.this.anchorPoint)) {
                SparrowEntity.this.anchorPoint = SparrowEntity.this.blockPosition();
            }

            this.angle = this.angle + this.clockwise * 15.0F * (float) (Math.PI / 180.0);
            SparrowEntity.this.moveTargetPoint = Vec3.atLowerCornerOf(SparrowEntity.this.anchorPoint)
                    .add(this.distance * Mth.cos(this.angle), -4.0F + this.height, this.distance * Mth.sin(this.angle));
        }
    }

    static class PhantomLookControl extends LookControl {
        public PhantomLookControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
        }
    }

    class PhantomMoveControl extends MoveControl {
        private float speed = 0.1F;

        public PhantomMoveControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
            if (SparrowEntity.this.horizontalCollision) {
                SparrowEntity.this.setYRot(SparrowEntity.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            double d0 = SparrowEntity.this.moveTargetPoint.x - SparrowEntity.this.getX();
            double d1 = SparrowEntity.this.moveTargetPoint.y - SparrowEntity.this.getY();
            double d2 = SparrowEntity.this.moveTargetPoint.z - SparrowEntity.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > 1.0E-5F) {
                double d4 = 1.0 - Math.abs(d1 * 0.7F) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = SparrowEntity.this.getYRot();
                float f1 = (float)Mth.atan2(d2, d0);
                float f2 = Mth.wrapDegrees(SparrowEntity.this.getYRot() + 90.0F);
                float f3 = Mth.wrapDegrees(f1 * (180.0F / (float)Math.PI));
                SparrowEntity.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                SparrowEntity.this.yBodyRot = SparrowEntity.this.getYRot();
                if (Mth.degreesDifferenceAbs(f, SparrowEntity.this.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
                }

                float f4 = (float)(-(Mth.atan2(-d1, d3) * 180.0F / (float)Math.PI));
                SparrowEntity.this.setXRot(f4);
                float f5 = SparrowEntity.this.getYRot() + 90.0F;
                double d6 = (double)(this.speed * Mth.cos(f5 * (float) (Math.PI / 180.0))) * Math.abs(d0 / d5);
                double d7 = (double)(this.speed * Mth.sin(f5 * (float) (Math.PI / 180.0))) * Math.abs(d2 / d5);
                double d8 = (double)(this.speed * Mth.sin(f4 * (float) (Math.PI / 180.0))) * Math.abs(d1 / d5);
                Vec3 vec3 = SparrowEntity.this.getDeltaMovement();
                SparrowEntity.this.setDeltaMovement(vec3.add(new Vec3(d6, d8, d7).subtract(vec3).scale(0.2)));
            }
        }
    }

    abstract class PhantomMoveTargetGoal extends Goal {
        public PhantomMoveTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget() {
            return SparrowEntity.this.moveTargetPoint.distanceToSqr(SparrowEntity.this.getX(), SparrowEntity.this.getY(), SparrowEntity.this.getZ()) < 4.0;
        }
    }

    class PhantomSweepAttackGoal extends PhantomMoveTargetGoal {
        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private boolean isScaredOfCat;
        private int catSearchTick;

        @Override
        public boolean canUse() {
            return SparrowEntity.this.getTarget() != null && SparrowEntity.this.attackPhase == AttackPhase.SWOOP;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity = SparrowEntity.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (livingentity instanceof Player player && (livingentity.isSpectator() || player.isCreative())) {
                    return false;
                }

                if (!this.canUse()) {
                    return false;
                } else {
                    if (SparrowEntity.this.tickCount > this.catSearchTick) {
                        this.catSearchTick = SparrowEntity.this.tickCount + 20;
                        List<Cat> list = SparrowEntity.this.level()
                                .getEntitiesOfClass(Cat.class, SparrowEntity.this.getBoundingBox().inflate(16.0), EntitySelector.ENTITY_STILL_ALIVE);

                        for (Cat cat : list) {
                            cat.hiss();
                        }

                        this.isScaredOfCat = !list.isEmpty();
                    }

                    return !this.isScaredOfCat;
                }
            }
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
            SparrowEntity.this.setTarget(null);
            SparrowEntity.this.attackPhase = AttackPhase.CIRCLE;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = SparrowEntity.this.getTarget();
            if (livingentity != null) {
                SparrowEntity.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5), livingentity.getZ());
                if (SparrowEntity.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox())) {
                    SparrowEntity.this.doHurtTarget(livingentity);
                    SparrowEntity.this.attackPhase = AttackPhase.CIRCLE;
                    if (!SparrowEntity.this.isSilent()) {
                        SparrowEntity.this.level().levelEvent(LevelEvent.SOUND_PHANTOM_BITE, SparrowEntity.this.blockPosition(), 0);
                    }
                } else if (SparrowEntity.this.horizontalCollision || SparrowEntity.this.hurtTime > 0) {
                    SparrowEntity.this.attackPhase = AttackPhase.CIRCLE;
                }
            }
        }
    }

}
