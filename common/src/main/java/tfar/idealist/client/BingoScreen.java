package tfar.idealist.client;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tfar.idealist.IdeaConfig;
import tfar.idealist.IdeaList;

public class BingoScreen extends Screen {

    protected final ClientAdvancements clientAdvancements;

    protected BingoScreen(Component title) {
        super(title);
        clientAdvancements = Minecraft.getInstance().player.connection.getAdvancements();

    }

    public static final ResourceLocation BINGO = IdeaList.id("bingo");


    @Override
    protected void init() {
        super.init();
        int x = (this.width - 90) / 2;
        int y = (this.height - 86) / 2;
        addRenderableWidget(new SimpleAdvancementWidget(x,y,20,20,Component.empty(),
                clientAdvancements.get(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_0.get()))));

        addRenderableWidget(new SimpleAdvancementWidget(x+30,y,20,20,Component.empty(),
                clientAdvancements.get(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_1.get()))));

        addRenderableWidget(new SimpleAdvancementWidget(x+60,y,20,20,Component.empty(),
                clientAdvancements.get(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_2.get()))));

        y+=30;

        addRenderableWidget(new SimpleAdvancementWidget(x,y,20,20,Component.empty(),
                clientAdvancements.get(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_3.get()))));

        addRenderableWidget(new SimpleAdvancementWidget(x+30,y,20,20,Component.empty(),
                clientAdvancements.get(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_4.get()))));

        addRenderableWidget(new SimpleAdvancementWidget(x+60,y,20,20,Component.empty(),
                clientAdvancements.get(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_5.get()))));

        y+=30;

        addRenderableWidget(new SimpleAdvancementWidget(x,y,20,20,Component.empty(),
                clientAdvancements.get(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_6.get()))));

        addRenderableWidget(new SimpleAdvancementWidget(x+30,y,20,20,Component.empty(),
                clientAdvancements.get(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_7.get()))));

        addRenderableWidget(new SimpleAdvancementWidget(x+60,y,20,20,Component.empty(),
                clientAdvancements.get(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_8.get()))));


    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

    }

        public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int size = 96;
        int x = (this.width - size) / 2;
        int y = (this.height - size) / 2;
        guiGraphics.blitSprite(BINGO,x,y,size,size);


    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
