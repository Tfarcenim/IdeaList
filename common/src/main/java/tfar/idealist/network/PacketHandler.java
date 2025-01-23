package tfar.idealist.network;

import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import tfar.idealist.IdeaList;
import tfar.idealist.platform.Services;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class PacketHandler {

    public static void registerPackets() {

        Services.PLATFORM.registerClientPlayPacket(S2CShuffleHotbarPacket.TYPE, S2CShuffleHotbarPacket.STREAM_CODEC);
        Services.PLATFORM.registerClientPlayPacket(S2CAttachmentDataPacket.TYPE, S2CAttachmentDataPacket.STREAM_CODEC);


    }

    public static <B extends FriendlyByteBuf, V extends Enum<V>> StreamCodec<B, V> enumCodec(final Class<V> enumClass) {
        return new StreamCodec<>() {
            @Override
            public V decode(B buf) {
                return buf.readEnum(enumClass);
            }

            @Override
            public void encode(B buf, V value) {
                buf.writeEnum(value);
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6,T7> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> codec1,
            final Function<C, T1> getter1,
            final StreamCodec<? super B, T2> codec2,
            final Function<C, T2> getter2,
            final StreamCodec<? super B, T3> codec3,
            final Function<C, T3> getter3,
            final StreamCodec<? super B, T4> codec4,
            final Function<C, T4> getter4,
            final StreamCodec<? super B, T5> codec5,
            final Function<C, T5> getter5,
            final StreamCodec<? super B, T6> codec6,
            final Function<C, T6> getter6,
            final StreamCodec<? super B, T7> codec7,
            final Function<C, T7> getter7,
            final Function7<T1, T2, T3, T4, T5, T6,T7, C> factory
    ) {
        return new StreamCodec<>() {
            @Override
            public C decode(B b) {
                T1 t1 = codec1.decode(b);
                T2 t2 = codec2.decode(b);
                T3 t3 = codec3.decode(b);
                T4 t4 = codec4.decode(b);
                T5 t5 = codec5.decode(b);
                T6 t6 = codec6.decode(b);
                T7 t7 = codec7.decode(b);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            @Override
            public void encode(B b, C c) {
                codec1.encode(b, getter1.apply(c));
                codec2.encode(b, getter2.apply(c));
                codec3.encode(b, getter3.apply(c));
                codec4.encode(b, getter4.apply(c));
                codec5.encode(b, getter5.apply(c));
                codec6.encode(b, getter6.apply(c));
                codec7.encode(b, getter7.apply(c));
            }
        };
    }

    public static void sendToServer(C2SModPacket<?> packet) {
        Services.PLATFORM.sendToServer(packet);
    }

    public static void sendTo(S2CModPacket<?> packet, ServerPlayer player) {//todo check for fake players
            Services.PLATFORM.sendToClient(packet, player);
    }

    public static void sendPacketToAllInArea(ServerLevel level,S2CModPacket<?> packet, BlockPos center, int rangesqr) {
        List<ServerPlayer> playerList = level.players();
        for (ServerPlayer player : playerList)
        {
            if (player.distanceToSqr(center.getX(), center.getY(), center.getZ()) < rangesqr)
            {
                sendTo(packet, player);
            }
        }
    }

    public static void sendPacketToAll(MinecraftServer server,S2CModPacket<?> packet)
    {
        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            sendTo(packet, player);
        }
    }


    public static ResourceLocation packet(Class<?> clazz) {
        return IdeaList.id(clazz.getName().toLowerCase(Locale.ROOT));
    }


}
