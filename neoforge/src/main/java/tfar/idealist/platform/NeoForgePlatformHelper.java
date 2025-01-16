package tfar.idealist.platform;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.commons.lang3.tuple.Pair;
import tfar.idealist.ExtraPiglinData;
import tfar.idealist.IdeaList;
import tfar.idealist.IdeaListNeoForge;
import tfar.idealist.PlayerBingoData;
import tfar.idealist.init.AttachmentTypes;
import tfar.idealist.network.C2SModPacket;
import tfar.idealist.network.S2CModPacket;
import tfar.idealist.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public <F> void registerAll(Map<String, ? extends F> map, Registry<F> registry, Class<? extends F> filter) {
        List<Pair<ResourceLocation, Supplier<?>>> list = IdeaListNeoForge.registerLater.computeIfAbsent(registry, k -> new ArrayList<>());
        for (Map.Entry<String, ? extends F> entry : map.entrySet()) {
            list.add(Pair.of(IdeaList.id(entry.getKey()), entry::getValue));
        }
    }

    @Override
    public void unfreeze(Registry<?> registry) {
        ((MappedRegistry<?>)registry).unfreeze();
    }

    public static PayloadRegistrar registrar;

    @Override
    public <MSG extends S2CModPacket<?>> void registerClientPlayPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf,MSG> streamCodec) {
        registrar.playToClient(type, streamCodec, (p, t) -> p.handleClient());
    }

    @Override
    public <MSG extends C2SModPacket<?>> void registerServerPlayPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf, MSG> streamCodec) {
        registrar.playToServer(type, streamCodec, (p, t) -> p.handleServer((ServerPlayer) t.player()));
    }


    @Override
    public void sendToClient(S2CModPacket<?> msg, ServerPlayer player) {
        PacketHandlerNeoForge.sendToClient(msg, player);
    }

    @Override
    public void sendToServer(C2SModPacket<?> msg) {
        PacketHandlerNeoForge.sendToServer(msg);
    }

    @Override
    public void setPlayerData(Player player, PlayerBingoData data) {
        player.setData(AttachmentTypes.PLAYER_BINGO_DATA,data);
    }

    @Override
    public PlayerBingoData getPlayerData(Player player) {
        return player.getData(AttachmentTypes.PLAYER_BINGO_DATA);
    }

    @Override
    public ExtraPiglinData getPiglinData(Piglin piglin) {
        return piglin.getData(AttachmentTypes.EXTRA_PIGLIN_DATA);
    }

    @Override
    public void setPiglinData(Piglin piglin, ExtraPiglinData extraPiglinData) {
        piglin.setData(AttachmentTypes.EXTRA_PIGLIN_DATA,extraPiglinData);
    }
}