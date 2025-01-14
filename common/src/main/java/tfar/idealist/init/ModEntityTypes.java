package tfar.idealist.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tfar.idealist.entity.*;

public class ModEntityTypes {
    public static final EntityType<AnimatedBlockEntity> ANIMATED_BLOCK = EntityType.Builder.of(AnimatedBlockEntity::new, MobCategory.MISC).sized(1,1).build("");
    public static final EntityType<WormEntity> WORM = EntityType.Builder.of(WormEntity::new,MobCategory.MONSTER)
            .sized(.9375f,.625f)
            .clientTrackingRange(8).build("");
    public static final EntityType<AntEntity> ANT = EntityType.Builder.of(AntEntity::new,MobCategory.MONSTER).sized(1.4f,.9f).clientTrackingRange(8).build("");
    public static final EntityType<SparrowEntity> SPARROW = EntityType.Builder.of(SparrowEntity::new,MobCategory.MONSTER).sized(2.5f,2.5f).clientTrackingRange(8).build("");
    public static final EntityType<SeedEntity> SEED = EntityType.Builder.of(SeedEntity::new,MobCategory.MISC)
            .sized(1,5f)
            .clientTrackingRange(8).build("");

}
