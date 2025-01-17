package tfar.idealist.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.idealist.entity.TRexSkeletonEntity;

public class TRexSkeletonRenderer extends GeoEntityRenderer<TRexSkeletonEntity> {
    public TRexSkeletonRenderer(EntityRendererProvider.Context renderManager, GeoModel<TRexSkeletonEntity> model) {
        super(renderManager, model);
    }

    @Override
    protected float getDeathMaxRotation(TRexSkeletonEntity animatable) {
        return 0;
    }
}
