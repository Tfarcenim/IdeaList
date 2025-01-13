package tfar.idealist.client;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Optional;

public class SimpleAdvancementWidget extends AbstractWidget {

    private final ClientAdvancements clientAdvancements;
    private final AdvancementHolder holder;

    private static final ResourceLocation TITLE_BOX_SPRITE = ResourceLocation.withDefaultNamespace("advancements/title_box");
    private final List<FormattedCharSequence> description;
    private final FormattedCharSequence title;
    private final Font font = Minecraft.getInstance().font;
    private final DisplayInfo display;
    private int tooltipWidth;

    public SimpleAdvancementWidget(int x, int y, int width, int height, Component message, ClientAdvancements clientAdvancements, AdvancementHolder holder) {
        super(x, y, width, height, message);
        this.clientAdvancements = clientAdvancements;
        this.holder = holder;

        display = holder.value().display().get();

        this.title = Language.getInstance().getVisualOrder(font.substrByWidth(display.getTitle(), 163));
        int i = this.getMaxProgressWidth();
        int j = 29 + Minecraft.getInstance().font.width(this.title) + i;

        this.description = Language.getInstance()
                .getVisualOrder(
                        this.findOptimalLines(ComponentUtils.mergeStyles(display.getDescription().copy(), Style.EMPTY.withColor(display.getType().getChatColor())), j)
                );

        this.tooltipWidth = j + 3 + 5;
    }

    private int getMaxProgressWidth() {
        int i = holder.value().requirements().size();
        if (i <= 1) {
            return 0;
        } else {
            int j = 8;
            Component component = Component.translatable("advancements.progress", i, i);
            return Minecraft.getInstance().font.width(component) + 8;
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (holder == null) return;
        AdvancementProgress progress = clientAdvancements.progress.get(holder);
        Optional<DisplayInfo> info = holder.value().display();
        if (info.isEmpty()) return;
        DisplayInfo displayInfo = info.get();

            float f = progress == null ? 0.0F : progress.getPercent();
            AdvancementWidgetType advancementwidgettype;
            if (f >= 1.0F) {
                advancementwidgettype = AdvancementWidgetType.OBTAINED;
            } else {
                advancementwidgettype = AdvancementWidgetType.UNOBTAINED;
            }

            guiGraphics.blitSprite(advancementwidgettype.frameSprite(displayInfo.getType()), this.getX() + 3, this.getY(), 26, 26);
            guiGraphics.renderFakeItem(displayInfo.getIcon(), this.getX() + 8, this.getY() + 5);
            if (isMouseOver(mouseX,mouseY)) {
                drawHover(guiGraphics,mouseX,mouseY,displayInfo,progress);
            }

    }

    public void drawHover(GuiGraphics guiGraphics,int mouseX,int mouseY,DisplayInfo info,AdvancementProgress progress) {
        /*boolean flag = width + x + this.x + this.width + 26 >= this.tab.getScreen().width;
        Component component = this.progress == null ? null : this.progress.getProgressText();
        int i = component == null ? 0 : this.minecraft.font.width(component);
        boolean flag1 = 113 - y - this.y - 26 <= 6 + this.description.size() * 9;
        float f = this.progress == null ? 0.0F : this.progress.getPercent();
        int j = Mth.floor(f * (float)this.width);
        AdvancementWidgetType advancementwidgettype;
        AdvancementWidgetType advancementwidgettype1;
        AdvancementWidgetType advancementwidgettype2;
        if (f >= 1.0F) {
            j = this.width / 2;
            advancementwidgettype = AdvancementWidgetType.OBTAINED;
            advancementwidgettype1 = AdvancementWidgetType.OBTAINED;
            advancementwidgettype2 = AdvancementWidgetType.OBTAINED;
        } else if (j < 2) {
            j = this.width / 2;
            advancementwidgettype = AdvancementWidgetType.UNOBTAINED;
            advancementwidgettype1 = AdvancementWidgetType.UNOBTAINED;
            advancementwidgettype2 = AdvancementWidgetType.UNOBTAINED;
        } else if (j > this.width - 2) {
            j = this.width / 2;
            advancementwidgettype = AdvancementWidgetType.OBTAINED;
            advancementwidgettype1 = AdvancementWidgetType.OBTAINED;
            advancementwidgettype2 = AdvancementWidgetType.UNOBTAINED;
        } else {
            advancementwidgettype = AdvancementWidgetType.OBTAINED;
            advancementwidgettype1 = AdvancementWidgetType.UNOBTAINED;
            advancementwidgettype2 = AdvancementWidgetType.UNOBTAINED;
        }

        int k = this.width - j;
        RenderSystem.enableBlend();
        int l = y + this.y;
        int i1;
        if (flag) {
            i1 = x + this.x - this.width + 26 + 6;
        } else {
            i1 = x + this.x;
        }

        int j1 = 32 + this.description.size() * 9;
        if (!this.description.isEmpty()) {
            if (flag1) {
                guiGraphics.blitSprite(TITLE_BOX_SPRITE, i1, l + 26 - j1, this.width, j1);
            } else {
                guiGraphics.blitSprite(TITLE_BOX_SPRITE, i1, l, this.width, j1);
            }
        }

        guiGraphics.blitSprite(advancementwidgettype.boxSprite(), 200, 26, 0, 0, i1, l, j, 26);
        guiGraphics.blitSprite(advancementwidgettype1.boxSprite(), 200, 26, 200 - k, 0, i1 + j, l, k, 26);
        guiGraphics.blitSprite(advancementwidgettype2.frameSprite(this.display.getType()), x + this.x + 3, y + this.y, 26, 26);
        if (flag) {
            guiGraphics.drawString(this.minecraft.font, this.title, i1 + 5, y + this.y + 9, -1);
            if (component != null) {
                guiGraphics.drawString(this.minecraft.font, component, x + this.x - i, y + this.y + 9, -1);
            }
        } else {
            guiGraphics.drawString(this.minecraft.font, this.title, x + this.x + 32, y + this.y + 9, -1);
            if (component != null) {
                guiGraphics.drawString(this.minecraft.font, component, x + this.x + this.width - i - 5, y + this.y + 9, -1);
            }
        }

        if (flag1) {
            for (int k1 = 0; k1 < this.description.size(); k1++) {
                guiGraphics.drawString(this.minecraft.font, this.description.get(k1), i1 + 5, l + 26 - j1 + 7 + k1 * 9, -5592406, false);
            }
        } else {
            for (int l1 = 0; l1 < this.description.size(); l1++) {
                guiGraphics.drawString(this.minecraft.font, this.description.get(l1), i1 + 5, y + this.y + 9 + 17 + l1 * 9, -5592406, false);
            }
        }

        guiGraphics.renderFakeItem(this.display.getIcon(), x + this.x + 8, y + this.y + 5);*/
    }

    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};


    private List<FormattedText> findOptimalLines(Component component, int maxWidth) {
        StringSplitter stringsplitter = Minecraft.getInstance().font.getSplitter();
        List<FormattedText> list = null;
        float f = Float.MAX_VALUE;

        for (int i : TEST_SPLIT_OFFSETS) {
            List<FormattedText> list1 = stringsplitter.splitLines(component, maxWidth - i, Style.EMPTY);
            float f1 = Math.abs(getMaxWidth(stringsplitter, list1) - (float)maxWidth);
            if (f1 <= 10.0F) {
                return list1;
            }

            if (f1 < f) {
                f = f1;
                list = list1;
            }
        }

        return list;
    }

    private static float getMaxWidth(StringSplitter manager, List<FormattedText> text) {
        return (float)text.stream().mapToDouble(manager::stringWidth).max().orElse(0.0);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
