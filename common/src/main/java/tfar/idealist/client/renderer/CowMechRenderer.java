package tfar.idealist.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.idealist.entity.CowMechEntity;

public class CowMechRenderer extends GeoEntityRenderer<CowMechEntity> {
    public CowMechRenderer(EntityRendererProvider.Context renderManager, GeoModel<CowMechEntity> model) {
        super(renderManager, model);
    }

    @Override
    public void render(CowMechEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        if (entity.isLaserActive()) {
            Vector3f laserPos = entity.getLaserTarget();

            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.END_ROD,laserPos.x,laserPos.y,laserPos.z,0,0,0);

          //  LevelRenderer.renderLineBox(consumer,laserPos.x-.5,laserPos.y-.5,laserPos.z-.5,
          //          laserPos.x+.5,laserPos.y+.5,laserPos.z+.5,1,1,0,1);

            long gameTime = entity.level().getGameTime();
            int yOffset = 0;
            int color = 0xffff0000;
            double eyeHeight = entity.getEyeHeight();
            poseStack.pushPose();
            poseStack.translate(0,eyeHeight,0);
            Vec3 vec3 = new Vec3(laserPos);
            Vec3 vec31 = entity.getEyePosition(partialTick);
            Vec3 direction = vec3.subtract(vec31);
            int height = (int) Math.ceil(direction.length()+2);
            direction = direction.normalize();
            float pitch = (float)Math.acos(direction.y);
            float pitchDegrees = pitch * (180.0F / (float)Math.PI);
            float yaw = (float)Math.atan2(direction.z, direction.x);
            poseStack.mulPose(Axis.YP.rotationDegrees(((float) (Math.PI / 2) - yaw) * (180.0F / (float)Math.PI)));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitchDegrees));


            BeaconRenderer.renderBeaconBeam(poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION, partialTick, 1.0F,
                    gameTime, yOffset, height, color, 0.05F, 0.0625F);
            poseStack.popPose();
        }
    }

    @Override
    protected float getDeathMaxRotation(CowMechEntity animatable) {
        return 0;
    }
}
