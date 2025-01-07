package tfar.idealist.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import tfar.idealist.init.ModEntityTypes;

public class ModClientNeoForge {

    public static void init(IEventBus bus) {
        bus.addListener(ModClientNeoForge::renderers);
    }

    static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.ANIMATED_BLOCK,AnimatedBlockRenderer::new);
    }

}
