package tfar.idealist.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.idealist.entity.WormEntity;

public class WormRenderer extends GeoEntityRenderer<WormEntity> {
    public WormRenderer(EntityRendererProvider.Context renderManager, GeoModel<WormEntity> model) {
        super(renderManager, model);
    }

    @Override
    protected float getDeathMaxRotation(WormEntity animatable) {
        return 0;
    }
}
