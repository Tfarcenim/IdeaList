package tfar.idealist.entity;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class EnhancedPanicGoal extends PanicGoal {
    public EnhancedPanicGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    public EnhancedPanicGoal(PathfinderMob mob, double speedModifier, TagKey<DamageType> panicCausingDamageTypes) {
        super(mob, speedModifier, panicCausingDamageTypes);
    }

    public EnhancedPanicGoal(PathfinderMob mob, double speedModifier, Function<PathfinderMob, TagKey<DamageType>> panicCausingDamageTypes) {
        super(mob, speedModifier, panicCausingDamageTypes);
    }

    @Override
    protected boolean findRandomPosition() {
        Vec3 vec3 = null;
        for (int i = 0; i < 1000;i++){
            vec3 = DefaultRandomPos.getPos(this.mob, 64, 5);
            if (vec3 != null && mob.distanceToSqr(vec3) > 1024) break;
        }
        if (vec3 == null) {
            return false;
        } else {
            this.posX = vec3.x;
            this.posY = vec3.y;
            this.posZ = vec3.z;
            return true;
        }
    }

    @Override
    public boolean canUse() {
        if (isRunning) {
            return mob.position().distanceTo(new Vec3(posX,posY,posZ)) > 8;
        }
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {

        if (mob.getNavigation().isDone()) {
            if (mob.position().distanceTo(new Vec3(posX,posY,posZ)) > 8) {
                mob.getNavigation().moveTo(posX,posY,posZ,speedModifier);
            }
        }
        boolean b = super.canContinueToUse();
        System.out.println("Dist: " + mob.position().distanceTo(new Vec3(posX,posY,posZ)) + "| stopped : "+!b);
        return b;
    }
}
