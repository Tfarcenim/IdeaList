package tfar.idealist.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import tfar.idealist.entity.AnimatedBlockEntity;

import java.util.function.Function;

public class AnimatedBlockModel extends HumanoidModel<AnimatedBlockEntity> {
    public AnimatedBlockModel(ModelPart root) {
        super(root);
        head.visible = false;
        body.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        createDefaultSkeletonMesh(partdefinition);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    protected static void createDefaultSkeletonMesh(PartDefinition partDefinition) {
        partDefinition.addOrReplaceChild(
                "right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 2.0F, 0.0F)
        );
        partDefinition.addOrReplaceChild(
                "left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F)
        );
        partDefinition.addOrReplaceChild(
                "right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-2.0F, 12.0F, 0.0F)
        );
        partDefinition.addOrReplaceChild(
                "left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(2.0F, 12.0F, 0.0F)
        );
    }

    public AnimatedBlockModel(ModelPart root, Function<ResourceLocation, RenderType> renderType) {
        super(root, renderType);
    }
}
