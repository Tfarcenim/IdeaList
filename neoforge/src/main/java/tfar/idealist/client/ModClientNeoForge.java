package tfar.idealist.client;

import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import tfar.idealist.IdeaList;
import tfar.idealist.init.ModEntityTypes;

public class ModClientNeoForge {



    public static void init(IEventBus bus) {
        bus.addListener(ModClientNeoForge::renderers);
        bus.addListener(ModClientNeoForge::registerLayer);
        bus.addListener(ModClientNeoForge::clientExtensions);
        bus.addListener(ModClientNeoForge::addLayers);
        bus.addListener(ModClientNeoForge::modelLayers);
        NeoForge.EVENT_BUS.addListener(ModClientNeoForge::guiLayers);
    }

    static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.ANIMATED_BLOCK,AnimatedBlockRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.WORM,context -> new WormRenderer(context,new DefaultedEntityGeoModel<>(IdeaList.id("worm"))));
        event.registerEntityRenderer(ModEntityTypes.ANT,context -> new AntRenderer(context,new DefaultedEntityGeoModel<>(IdeaList.id("ant"))));
        event.registerEntityRenderer(ModEntityTypes.SPARROW,context -> new SparrowRenderer(context,new DefaultedEntityGeoModel<>(IdeaList.id("sparrow"))));
        event.registerEntityRenderer(ModEntityTypes.VACUUM,context -> new VacuumRenderer(context,new DefaultedEntityGeoModel<>(IdeaList.id("vacuum"))));

    }


    static void modelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    static void addLayers(EntityRenderersEvent.AddLayers event) {
        EndermanRenderer endermanRenderer = event.getRenderer(EntityType.ENDERMAN);
        EntityRendererProvider.Context context = event.getContext();
        endermanRenderer.addLayer(new CustomHeadLayer<>(endermanRenderer, context.getModelSet(), context.getItemInHandRenderer()));
    }

    static void clientExtensions(RegisterClientExtensionsEvent event) {

        // Create our armor model/renderer for forge and return it
    }

    static void registerLayer(RegisterGuiLayersEvent e) {
        e.registerBelow(VanillaGuiLayers.HOTBAR, IdeaList.id("shifted_hotbar"), ModClient::renderShiftedHotbar);
    }

    static void guiLayers(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.HOTBAR) && ModClient.shifted) {
            event.setCanceled(true);
        }
    }

}
