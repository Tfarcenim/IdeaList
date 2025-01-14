package tfar.idealist.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tfar.idealist.entity.AnimatedBlockEntity;
import tfar.idealist.entity.AntEntity;
import tfar.idealist.entity.WormEntity;

public class ModEntityTypes {
    public static final EntityType<AnimatedBlockEntity> ANIMATED_BLOCK = EntityType.Builder.of(AnimatedBlockEntity::new, MobCategory.MISC).sized(1,1).build("");
    public static final EntityType<WormEntity> WORM = EntityType.Builder.of(WormEntity::new,MobCategory.MONSTER)
            .sized(.9375f,.625f)
            .clientTrackingRange(8).build("");
    public static final EntityType<AntEntity> ANT = EntityType.Builder.of(AntEntity::new,MobCategory.MONSTER).sized(1.4f,.9f).clientTrackingRange(8).build("");

}
