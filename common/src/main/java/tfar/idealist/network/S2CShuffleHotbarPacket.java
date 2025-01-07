package tfar.idealist.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import tfar.idealist.client.ClientPacketHandler;

public record S2CShuffleHotbarPacket(boolean active) implements S2CModPacket<RegistryFriendlyByteBuf>{

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CShuffleHotbarPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, S2CShuffleHotbarPacket::active,
                    S2CShuffleHotbarPacket::new);

    public static final CustomPacketPayload.Type<S2CShuffleHotbarPacket> TYPE = ModPacket.type(S2CShuffleHotbarPacket.class);

    @Override
    public void handleClient() {
        ClientPacketHandler.handleHotbarShift();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
