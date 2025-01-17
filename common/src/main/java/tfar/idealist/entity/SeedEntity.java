package tfar.idealist.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tfar.idealist.init.ModEntityDataSerializers;
import tfar.idealist.network.PacketHandler;

public class SeedEntity extends Mob implements GeoEntity {

    private static final EntityDataAccessor<Stage> DATA_STAGE = SynchedEntityData.defineId(SeedEntity.class, ModEntityDataSerializers.STAGE);

    public enum Stage {
        ONE(-1),ONE_TO_TWO(29),TWO(-1);

        private final int transition;
        Stage(int transition) {
            this.transition = transition;
        }

        public static final StreamCodec<FriendlyByteBuf, Stage> STREAM_CODEC = PacketHandler.enumCodec(Stage.class);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected SeedEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    public void grow() {
        Stage stage = getStage();

    }

    public Stage getStage() {
        return entityData.get(DATA_STAGE);
    }

    public void setStage(Stage stage) {
        entityData.set(DATA_STAGE,stage);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STAGE,Stage.ONE);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,20);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
