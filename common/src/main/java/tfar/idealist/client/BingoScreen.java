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
        int x = (this.width - 92) / 2;
        int y = (this.height - 86) / 2;
        maybeAdd(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_0.get()),0,x,y);
        maybeAdd(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_1.get()),1,x,y);
        maybeAdd(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_2.get()),2,x,y);
        maybeAdd(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_3.get()),3,x,y);
        maybeAdd(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_4.get()),4,x,y);
        maybeAdd(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_5.get()),5,x,y);
        maybeAdd(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_6.get()),6,x,y);
        maybeAdd(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_7.get()),7,x,y);
        maybeAdd(ResourceLocation.parse(IdeaConfig.Server.BINGO_ADVANCEMENT_8.get()),8,x,y);
    }

    protected void maybeAdd(ResourceLocation id, int index,int xStart,int yStart) {
        AdvancementHolder holder = clientAdvancements.get(id);
        if (holder == null) return;
        int x = xStart + (index % 3) * 30;
        int y = yStart + (index / 3) * 30;
        SimpleAdvancementWidget widget = new SimpleAdvancementWidget(x, y, 24, 24, Component.empty(),
                clientAdvancements, holder);
        addRenderableWidget(widget);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBlurredBackground(partialTick);
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

    }

        public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int size = 100;
        int x = (this.width - size) / 2;
        int y = (this.height - size) / 2;
        guiGraphics.blitSprite(BINGO,x,y,size,size);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
