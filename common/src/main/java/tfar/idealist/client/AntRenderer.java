package tfar.idealist.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.idealist.entity.AntEntity;

public class AntRenderer extends GeoEntityRenderer<AntEntity> {
    public AntRenderer(EntityRendererProvider.Context renderManager, GeoModel<AntEntity> model) {
        super(renderManager, model);
    }

    @Override
    protected float getDeathMaxRotation(AntEntity animatable) {
        return 0;
    }
}
