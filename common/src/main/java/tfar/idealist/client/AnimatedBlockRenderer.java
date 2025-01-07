package tfar.idealist.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import tfar.idealist.entity.AnimatedBlockEntity;

public class AnimatedBlockRenderer extends HumanoidMobRenderer<AnimatedBlockEntity,AnimatedBlockModel> {
    private final BlockRenderDispatcher dispatcher;

    public AnimatedBlockRenderer(EntityRendererProvider.Context context) {
        this(context, ModelLayers.SKELETON);
    }

    public AnimatedBlockRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer) {
        super(context,new AnimatedBlockModel(context.bakeLayer(layer)),.5f);
        this.shadowRadius = 0.5F;
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(AnimatedBlockEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockState blockState = entity.getBlockState();
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            Level level = entity.level();
            if (blockState != level.getBlockState(entity.blockPosition()) && blockState.getRenderShape() != RenderShape.INVISIBLE) {
                poseStack.pushPose();
                BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
                poseStack.translate(-0.5, 0.5, -0.5);
                this.dispatcher.getModelRenderer().tesselateBlock(level, this.dispatcher.getBlockModel(blockState), blockState, blockPos, poseStack, buffer.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(blockState)), false, RandomSource.create(), blockState.getSeed(entity.getStartPos()), OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
                super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            }
        }
    }

    /**
     * Returns the location of an entity's texture.
     */

    private static final ResourceLocation SKELETON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png");

    public ResourceLocation getTextureLocation(AnimatedBlockEntity entity) {
        return SKELETON_LOCATION;
    }
}
