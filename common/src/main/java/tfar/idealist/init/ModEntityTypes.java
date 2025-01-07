package tfar.idealist.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tfar.idealist.entity.AnimatedBlockEntity;

public class ModEntityTypes {
    public static final EntityType<AnimatedBlockEntity> ANIMATED_BLOCK = EntityType.Builder.of(AnimatedBlockEntity::new, MobCategory.MISC).sized(1,1).build("");
}
