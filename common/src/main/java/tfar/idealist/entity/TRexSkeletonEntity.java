package tfar.idealist.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class TRexSkeletonEntity extends Monster implements GeoEntity, SmartBrainOwner<TRexSkeletonEntity> {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public TRexSkeletonEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static final RawAnimation DIE_ALT = RawAnimation.begin().thenPlayAndHold("misc.die");


    public static AttributeSupplier.Builder attributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE,8)
                .add(Attributes.MAX_HEALTH,200)
                .add(Attributes.MOVEMENT_SPEED, 0.5F)
                .add(Attributes.STEP_HEIGHT, 2)
                .add(Attributes.FOLLOW_RANGE, 64);
    }

    @Override
    public List<? extends ExtendedSensor<? extends TRexSkeletonEntity>> getSensors() {
        NearbyLivingEntitySensor<? extends TRexSkeletonEntity> nearbyLivingEntitySensor = new NearbyLivingEntitySensor<>();
        nearbyLivingEntitySensor.setPredicate((target, entity) -> canTarget(target));
        return List.of(nearbyLivingEntitySensor, // This tracks nearby entities
                new HurtBySensor<>());
    }

    @Override
    public BrainActivityGroup<? extends TRexSkeletonEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new FloatToSurfaceOfFluid<>(),
                new MoveToWalkTarget<>());
    }

    @Override
    public BrainActivityGroup<? extends TRexSkeletonEntity> getIdleTasks() {


        // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(      // Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new TargetOrRetaliate<>()
                                .attackablePredicate(entity -> entity.isAlive() && (!(entity instanceof Player player) || !player.isCreative())),            // Set the attack target and walk target based on nearby entities
                        new SetPlayerLookTarget<>(),          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()),         // Set a random look target
                new OneRandomBehaviour<>(                 // Run a random task from the below options
                        new SetRandomWalkTarget<>(),          // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60)))); // Do nothing for 1.5->3 seconds
    }

    @Override
    public BrainActivityGroup<? extends TRexSkeletonEntity> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>()
                        .invalidateIf((entity, target) -> target instanceof Player pl && (pl.isCreative() || pl.isSpectator())), // Cancel fighting if the target is no longer valid,
                new SetWalkTargetToAttackTarget<>().speedMod((owner, target) -> 1.5f).closeEnoughDist((mob, living) -> {
                    return 7;
                }),      // Set the walk target to the attack target


                new AnimatableMeleeAttack<>(13) { // Melee attack the target if close enough
                            @Override
                            protected void start(Mob entity) {
                                BehaviorUtils.lookAtEntity(entity, this.target);
                                triggerAnim("controller", "bite");
                            }
                        }.attackInterval(mob -> 24)
        );
    }

    public boolean canTarget(LivingEntity target) {
        return target instanceof Player player && !(player.isCreative() || player.isSpectator());
    }

    private static final double ATTACK_REACH = 3.5F;

    @Override
    protected AABB getAttackBoundingBox() {
        Entity entity = this.getVehicle();
        AABB aabb;
        if (entity != null) {
            AABB aabb1 = entity.getBoundingBox();
            AABB aabb2 = this.getBoundingBox();
            aabb = new AABB(
                    Math.min(aabb2.minX, aabb1.minX),
                    aabb2.minY,
                    Math.min(aabb2.minZ, aabb1.minZ),
                    Math.max(aabb2.maxX, aabb1.maxX),
                    aabb2.maxY,
                    Math.max(aabb2.maxZ, aabb1.maxZ)
            );
        } else {
            aabb = this.getBoundingBox();
        }

        return aabb.inflate(ATTACK_REACH, .5, ATTACK_REACH);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 5, event -> {
            boolean isAttacking = this.swinging;
            boolean isDead = this.dead || this.isDeadOrDying();

            if (event.isMoving() && !isDead && !isAttacking) {
                return event.setAndContinue(DefaultAnimations.WALK);
            }
            return event.setAndContinue(DefaultAnimations.IDLE);
        })
                .triggerableAnim("bite", DefaultAnimations.ATTACK_BITE)
                .triggerableAnim("death",DIE_ALT));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        tickBrain(this);
    }

    @Override
    protected Brain.Provider<? extends TRexSkeletonEntity> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(6);
    }


    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (deathTime == 1) {
            this.triggerAnim("controller", "death");
        }
        if (this.deathTime >= 80 && !isRemoved() && !level().isClientSide) {
            this.level().broadcastEntityEvent(this, EntityEvent.POOF);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }
}
