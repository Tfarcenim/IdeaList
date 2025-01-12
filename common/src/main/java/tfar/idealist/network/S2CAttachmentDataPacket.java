package tfar.idealist.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import tfar.idealist.PlayerBingoData;
import tfar.idealist.client.ModClient;

public record S2CAttachmentDataPacket(PlayerBingoData data) implements S2CModPacket<RegistryFriendlyByteBuf> {

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CAttachmentDataPacket> STREAM_CODEC =
            StreamCodec.composite(
                    PlayerBingoData.STREAM_CODEC, S2CAttachmentDataPacket::data,
                    S2CAttachmentDataPacket::new);

    public static final CustomPacketPayload.Type<S2CAttachmentDataPacket> TYPE = ModPacket.type(S2CAttachmentDataPacket.class);
    @Override
    public void handleClient() {
        ModClient.handle(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
