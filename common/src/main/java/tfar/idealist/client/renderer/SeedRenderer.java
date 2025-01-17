package tfar.idealist.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.idealist.entity.SeedEntity;

public class SeedRenderer extends GeoEntityRenderer<SeedEntity> {
    public SeedRenderer(EntityRendererProvider.Context renderManager, GeoModel<SeedEntity> model) {
        super(renderManager, model);
    }
}
