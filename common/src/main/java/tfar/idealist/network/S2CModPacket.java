package tfar.idealist.network;


import net.minecraft.network.FriendlyByteBuf;

public interface S2CModPacket<T extends FriendlyByteBuf> extends ModPacket<T> {
    void handleClient();
}
