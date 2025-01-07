package tfar.idealist.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import tfar.idealist.IdeaList;
import tfar.idealist.init.ModEntityTypes;

public class ModClientNeoForge {


    public static void init(IEventBus bus) {
        bus.addListener(ModClientNeoForge::renderers);
        bus.addListener(ModClientNeoForge::registerLayer);
        NeoForge.EVENT_BUS.addListener(ModClientNeoForge::guiLayers);
    }

    static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.ANIMATED_BLOCK,AnimatedBlockRenderer::new);
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
