package tfar.idealist.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;
import tfar.idealist.IdeaList;
import tfar.idealist.init.ModEntityTypes;
import tfar.idealist.network.PacketHandler;
import tfar.idealist.world.SeedRaidData;

public class SeedEntity extends Mob implements GeoEntity {

    public final Stage stage;

    public static final RawAnimation GROW = RawAnimation.begin().thenPlayAndHold("grow");

    protected int transition_time;

    public enum Stage {
        SEED_1(-1, ModEntityTypes.SEED_1),SEED_1_TO_2(29,ModEntityTypes.SEED_1_TO_2),SEED_2(-1,ModEntityTypes.SEED_2);

        private final int transition;
        private final EntityType<? extends SeedEntity> type;

        Stage(int transition,EntityType<? extends SeedEntity> type) {
            this.transition = transition;
            this.type = type;
        }

        boolean isTransition() {
            return transition >-1;
        }

        public Stage next() {
            return IdeaList.cycle(this);
        }

        public static final StreamCodec<FriendlyByteBuf, Stage> STREAM_CODEC = PacketHandler.enumCodec(Stage.class);
        public static final Stage LAST = values()[values().length-1];
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected SeedEntity(EntityType<? extends Mob> entityType, Level level,Stage stage) {
        super(entityType, level);
        this.stage = stage;
        transition_time = stage.transition;
    }

    public static SeedEntity createSeed1(EntityType<? extends Mob> entityType, Level level) {
        return new SeedEntity(entityType,level,Stage.SEED_1);
    }

    public static SeedEntity createSeed1to2(EntityType<? extends Mob> entityEntityType, Level level) {
        return new SeedEntity(entityEntityType,level,Stage.SEED_1_TO_2);
    }

    public static SeedEntity createSeed2(EntityType<? extends Mob> entityType, Level level) {
        return new SeedEntity(entityType,level,Stage.SEED_2);
    }

    public void grow() {
        if (stage != Stage.LAST) {
            discard();
            Stage next = stage.next();
            SeedEntity seedEntity = next.type.spawn((ServerLevel) level(), blockPosition(), MobSpawnType.EVENT);
            updateSeedRaid(seedEntity);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity entity= source.getEntity();
        if (entity instanceof Player) {
            return false;
        }
        return super.hurt(source, amount);
    }

    static void updateSeedRaid(SeedEntity seedEntity) {
        ServerLevel level = (ServerLevel) seedEntity.level();
        if (level.dimension() == IdeaList.SEED_DIM) {
            SeedRaidData seedRaidData = SeedRaidData.get(level);
            if (seedRaidData != null) {
                seedRaidData.updateSeedEntity(seedEntity);
            }
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (stage.isTransition()) {
            if (transition_time > 0) {
                transition_time--;
            } else {
                grow();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (stage.isTransition() && tickCount == 1) {
            triggerAnim("controller","grow");
        }
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        if (stage.isTransition()) {
            controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 0, event -> event.setAndContinue(DefaultAnimations.IDLE))
                    .triggerableAnim("grow", GROW));
        }
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,20)
                .add(Attributes.KNOCKBACK_RESISTANCE,1);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
