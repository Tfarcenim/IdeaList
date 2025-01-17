package tfar.idealist.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.idealist.entity.VacuumEntity;

public class VacuumRenderer extends GeoEntityRenderer<VacuumEntity> {
    public VacuumRenderer(EntityRendererProvider.Context renderManager, GeoModel<VacuumEntity> model) {
        super(renderManager, model);
    }

}
