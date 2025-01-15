package tfar.idealist.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tfar.idealist.IdeaConfig;
import tfar.idealist.IdeaList;
import tfar.idealist.world.SeedRaidData;

public class VacuumEntity extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public VacuumEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();
        Player nearestPlayer = level().getNearestPlayer(this, 8);
        if (nearestPlayer != null) {
            Vec3 direction = position().subtract(nearestPlayer.position()).normalize().scale(.25);
            nearestPlayer.setDeltaMovement(direction);
        }
    }

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);
        if (!level().isClientSide) {
            discard();
            ServerLevel newLevel = player.getServer().getLevel(IdeaList.SEED_DIM);
            player.changeDimension(new DimensionTransition(newLevel, IdeaConfig.Server.SEED_TELEPORT.get(),Vec3.ZERO,player.getXRot(),player.getYRot(),false,SEED_ENTER_TRANSITION));
        }
    }

    public static final DimensionTransition.PostDimensionTransition SEED_ENTER_TRANSITION = entity -> {
        Level level = entity.level();
        if (level.dimension() == IdeaList.SEED_DIM && entity instanceof ServerPlayer player) {
            SeedRaidData seedRaidData = SeedRaidData.getOrLoad((ServerLevel) level);
            seedRaidData.start(player);
        }
    };

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
