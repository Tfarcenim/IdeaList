package tfar.idealist.client;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

import java.util.Random;

public class ClientPacketHandler {
    static Random random = new Random();
    public static void handleHotbarShift() {
        ModClient.shifted = true;

        Window window = Minecraft.getInstance().getWindow();
        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();

        if (width > 182) {
        ModClient.shiftedX = random.nextInt(width - 182);
        }
        if (height > 23) {
            ModClient.shiftedY = random.nextInt(height - 23);
        }
    }

//                int xPos = 0;// max is guiGraphics.guiWidth() -182, min is 0
//                int yPos = guiGraphics.guiHeight() - 23;
}
