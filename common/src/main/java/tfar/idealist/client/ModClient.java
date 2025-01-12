package tfar.idealist.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.idealist.network.S2CAttachmentDataPacket;
import tfar.idealist.platform.Services;

public class ModClient {

    public static boolean shifted;

    public static int shiftedX;
    public static int shiftedY;

    //91,-22 are default
    public static void renderShiftedHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!shifted)return;
        Gui gui = Minecraft.getInstance().gui;
            Player player = gui.getCameraPlayer();
            if (player != null) {
                ItemStack itemstack = player.getOffhandItem();
                HumanoidArm humanoidarm = player.getMainArm().getOpposite();
                int xPos = shiftedX;
                int yPos = shiftedY;
                RenderSystem.enableBlend();
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0.0F, 0.0F, -90.0F);
                guiGraphics.blitSprite(Gui.HOTBAR_SPRITE, xPos, yPos, 182, 22);
                guiGraphics.blitSprite(Gui.HOTBAR_SELECTION_SPRITE, xPos - 1 + player.getInventory().selected * 20,yPos - 1, 24, 24);
                if (!itemstack.isEmpty()) {
                    if (humanoidarm == HumanoidArm.LEFT) {
                        guiGraphics.blitSprite(Gui.HOTBAR_OFFHAND_LEFT_SPRITE, xPos - 29, yPos - 1, 29, 24);
                    } else {
                        guiGraphics.blitSprite(Gui.HOTBAR_OFFHAND_RIGHT_SPRITE, xPos, yPos - 1, 29, 24);
                    }
                }

                guiGraphics.pose().popPose();
                RenderSystem.disableBlend();
                int l = 1;

                for (int slot = 0; slot < 9; slot++) {
                    int j1 = xPos - (-1) + slot * 20 + 2;
                    int k1 =  - 16 - 3 + yPos + 22;
                    gui.renderSlot(guiGraphics, j1, k1, deltaTracker, player, player.getInventory().items.get(slot), l++);
                }

                if (!itemstack.isEmpty()) {
                    int i2 = guiGraphics.guiHeight() - 16 - 3;
                    if (humanoidarm == HumanoidArm.LEFT) {
                        gui.renderSlot(guiGraphics, xPos - 26, i2, deltaTracker, player, itemstack, l++);
                    } else {
                        gui.renderSlot(guiGraphics, xPos + 10, i2, deltaTracker, player, itemstack, l++);
                    }
                }

                if (gui.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                    RenderSystem.enableBlend();
                    float attackStrength = gui.minecraft.player.getAttackStrengthScale(0.0F);
                    if (attackStrength < 1.0F) {
                        int j2 = guiGraphics.guiHeight() - 20;
                        int k2 = xPos + 6;
                        if (humanoidarm == HumanoidArm.RIGHT) {
                            k2 = xPos - 22;
                        }

                        int atkWidth = (int)(attackStrength * 19.0F);
                        guiGraphics.blitSprite(Gui.HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k2, j2, 18, 18);
                        guiGraphics.blitSprite(Gui.HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - atkWidth, k2, j2 + 18 - atkWidth, 18, atkWidth);
                    }

                    RenderSystem.disableBlend();
                }
        }
    }

    public static void openBingoScreen() {
        Minecraft.getInstance().setScreen(new BingoScreen(Component.literal("Bingo")));
    }

    public static void handle(S2CAttachmentDataPacket s2CAttachmentDataPacket) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            Services.PLATFORM.setData(player,s2CAttachmentDataPacket.data());
        }
    }
}
