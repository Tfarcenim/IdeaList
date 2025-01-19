package tfar.idealist.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.idealist.entity.CowMechEntity;

public class CowMechRenderer extends GeoEntityRenderer<CowMechEntity> {
    public CowMechRenderer(EntityRendererProvider.Context renderManager, GeoModel<CowMechEntity> model) {
        super(renderManager, model);
    }

    @Override
    protected float getDeathMaxRotation(CowMechEntity animatable) {
        return 0;
    }
}
