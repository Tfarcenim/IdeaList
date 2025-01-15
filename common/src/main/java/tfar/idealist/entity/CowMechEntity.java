package tfar.idealist.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class CowMechEntity extends PathfinderMob {
    public CowMechEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }
}
