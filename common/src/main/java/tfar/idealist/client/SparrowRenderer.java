package tfar.idealist.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.idealist.entity.SparrowEntity;
import tfar.idealist.entity.WormEntity;

public class SparrowRenderer extends GeoEntityRenderer<SparrowEntity> {
    public SparrowRenderer(EntityRendererProvider.Context renderManager, GeoModel<SparrowEntity> model) {
        super(renderManager, model);
    }

    @Override
    protected float getDeathMaxRotation(SparrowEntity animatable) {
        return 0;
    }
}
