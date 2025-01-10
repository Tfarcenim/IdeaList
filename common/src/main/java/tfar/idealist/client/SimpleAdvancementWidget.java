package tfar.idealist.client;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class SimpleAdvancementWidget extends AbstractWidget {

    private final AdvancementHolder holder;

    public SimpleAdvancementWidget(int x, int y, int width, int height, Component message, AdvancementHolder holder) {
        super(x, y, width, height, message);
        this.holder = holder;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (holder == null) return;
        AdvancementProgress progress = Minecraft.getInstance().getConnection().getAdvancements().progress.get(holder);
        Optional<DisplayInfo> info = holder.value().display();

        info.ifPresent(displayInfo -> {
            float f = progress == null ? 0.0F : progress.getPercent();
            AdvancementWidgetType advancementwidgettype;
            if (f >= 1.0F) {
                advancementwidgettype = AdvancementWidgetType.OBTAINED;
            } else {
                advancementwidgettype = AdvancementWidgetType.UNOBTAINED;
            }

            guiGraphics.blitSprite(advancementwidgettype.frameSprite(displayInfo.getType()), this.getX() + 3, this.getY(), 26, 26);
            guiGraphics.renderFakeItem(displayInfo.getIcon(), this.getX() + 8, this.getY() + 5);
        });
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
