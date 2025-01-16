package tfar.idealist.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import tfar.idealist.ExtraPiglinData;
import tfar.idealist.PlayerBingoData;
import tfar.idealist.network.C2SModPacket;
import tfar.idealist.network.S2CAttachmentDataPacket;
import tfar.idealist.network.S2CModPacket;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    default void unfreeze(Registry<?> registry) {

    }

    default  <F> void registerAll(Class<?> clazz, Registry<F> registry, Class<? extends F> filter) {
        unfreeze(registry);
        Map<String,F> map = new HashMap<>();
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    map.put(field.getName().toLowerCase(Locale.ROOT),(F)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
        registerAll(map,registry,filter);
    }

    <F> void registerAll(Map<String,? extends F> map, Registry<F> registry, Class<? extends F> filter);

    <MSG extends S2CModPacket<?>> void registerClientPlayPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf,MSG> streamCodec);
    <MSG extends C2SModPacket<?>> void registerServerPlayPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf,MSG> streamCodec);

    void sendToClient(S2CModPacket<?> msg, ServerPlayer player);
    void sendToServer(C2SModPacket<?> msg);

    void setPlayerData(Player player, PlayerBingoData twist);
    default void setAndSyncPlayerData(ServerPlayer player, PlayerBingoData data) {
        setPlayerData(player, data);
        sendToClient(new S2CAttachmentDataPacket(data), player);
    }

    PlayerBingoData getPlayerData(Player player);

    ExtraPiglinData getPiglinData(Piglin piglin);
    void setPiglinData(Piglin piglin,ExtraPiglinData extraPiglinData);
}