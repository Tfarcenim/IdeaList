package tfar.idealist.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
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
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class CowMechEntity extends PathfinderMob implements GeoEntity, RangedAttackMob,SmartBrainOwner<CowMechEntity> {

    protected Player aggro;

    private static final EntityDataAccessor<Vector3f> DATA_LASER_POS = SynchedEntityData.defineId(CowMechEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Boolean> DATA_LASER_ACTIVE = SynchedEntityData.defineId(CowMechEntity.class,EntityDataSerializers.BOOLEAN);

    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public CowMechEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.triggerAnim("controller", "spawn");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 5, event -> {
            boolean isDead = this.dead || this.isDeadOrDying();

            if (event.isMoving() && !isDead) {
                return event.setAndContinue(DefaultAnimations.WALK);
            }
            return event.setAndContinue(DefaultAnimations.IDLE);
        })
                .triggerableAnim("slam", DefaultAnimations.ATTACK_SLAM)
                .triggerableAnim("spawn", DefaultAnimations.SPAWN)
                .triggerableAnim("death",DefaultAnimations.DIE));
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE,5)
                .add(Attributes.MAX_HEALTH,150)
                .add(Attributes.MOVEMENT_SPEED,.2)
                .add(Attributes.STEP_HEIGHT,2)
                .add(Attributes.KNOCKBACK_RESISTANCE,1);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_LASER_ACTIVE,false);
        builder.define(DATA_LASER_POS,new Vector3f());
    }

    public void setLaserActive(boolean laserActive) {
        entityData.set(DATA_LASER_ACTIVE,laserActive);
    }

    public boolean isLaserActive() {
        return entityData.get(DATA_LASER_ACTIVE);
    }

    public void setLaserTarget(Vector3f vector3f) {
        entityData.set(DATA_LASER_POS,vector3f);
    }

    public Vector3f getLaserTarget() {
        return entityData.get(DATA_LASER_POS);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (deathTime == 1) {
            this.triggerAnim("controller", "death");
        }
        if (this.deathTime >= 32 && !isRemoved() && !level().isClientSide) {
            this.level().broadcastEntityEvent(this, EntityEvent.POOF);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void setAggro(Player aggro) {
        this.aggro = aggro;
    }

    public boolean canTarget(LivingEntity target) {
        return target instanceof Player player && !(player.isCreative() || player.isSpectator());
    }


    @Override
    public List<? extends ExtendedSensor<? extends CowMechEntity>> getSensors() {
        NearbyLivingEntitySensor<? extends CowMechEntity> nearbyLivingEntitySensor = new NearbyLivingEntitySensor<>();
        nearbyLivingEntitySensor.setPredicate((target, entity) -> canTarget(target));
        return List.of(nearbyLivingEntitySensor, // This tracks nearby entities
                new HurtBySensor<>());
    }

    @Override
    public BrainActivityGroup<? extends CowMechEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new FloatToSurfaceOfFluid<>(),
                new MoveToWalkTarget<>());
    }

    @Override
    public BrainActivityGroup<? extends CowMechEntity> getIdleTasks() {


        // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(      // Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new TargetOrRetaliate<>()
                                .attackablePredicate(entity -> tickCount > 120 && entity.isAlive() && (!(entity instanceof Player player) || !player.isCreative())),            // Set the attack target and walk target based on nearby entities
                        new SetPlayerLookTarget<>(),          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()),         // Set a random look target
                new OneRandomBehaviour<>(                 // Run a random task from the below options
                        new SetRandomWalkTarget<>(),          // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60)))); // Do nothing for 1.5->3 seconds
    }

    @Override
    public BrainActivityGroup<? extends CowMechEntity> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>()
                        .invalidateIf((entity, target) -> target instanceof Player pl && (pl.isCreative() || pl.isSpectator())), // Cancel fighting if the target is no longer valid,
                new SetWalkTargetToAttackTarget<>().speedMod((owner, target) -> 1f).closeEnoughDist((mob, living) -> {
                    return 3;
                }),      // Set the walk target to the attack target


                new OneRandomBehaviour<>(
                        Pair.of(new AnimatableMeleeAttack<>(25) { // Melee attack the target if close enough
                            @Override
                            protected void start(Mob entity) {
                                BehaviorUtils.lookAtEntity(entity, this.target);
                                triggerAnim("controller", "slam");
                            }
                        }.attackInterval(mob -> 400), 9),
                        Pair.of(new LaserAttackBehavior(200),3)
                )
        );
    }

    @Override
    protected Brain.Provider<? extends CowMechEntity> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        tickBrain(this);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        Vector3f laserPos = getLaserTarget();
        double distSqr = target.distanceToSqr(new Vec3(laserPos));
        if (distSqr < 1) {
            target.hurt(damageSources().indirectMagic(this, this), 1);
            doHurtTarget(target);

        }
    }

    public class LaserAttackBehavior extends AnimatableRangedAttack<CowMechEntity> {
        public LaserAttackBehavior(int delayTicks) {
            super(delayTicks);
            attackRadius(24);
        }

        @Override
        protected void start(CowMechEntity entity) {
            super.start(entity);
            setLaserActive(true);
        }

        @Override
        protected void tick(CowMechEntity entity) {
            super.tick(entity);
            if (target != null) {
                setLaserTarget(target.position().add(0,target.getBbHeight()/2,0).toVector3f());
                if (entity.tickCount%20 == 0) {
                    performRangedAttack(target,1);
                }
            }
        }

        @Override
        protected void stop(CowMechEntity entity) {
            super.stop(entity);
            setLaserActive(false);
        }
    }
}
