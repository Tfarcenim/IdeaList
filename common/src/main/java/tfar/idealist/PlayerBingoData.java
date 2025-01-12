package tfar.idealist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PlayerBingoData(boolean twist) {

    public static final Codec<PlayerBingoData> CODEC = RecordCodecBuilder.create(playerBingoDataInstance ->
            playerBingoDataInstance.group(
                    Codec.BOOL.fieldOf("twist").forGetter(PlayerBingoData::twist)
            ).apply(playerBingoDataInstance,PlayerBingoData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf,PlayerBingoData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,PlayerBingoData::twist,
            PlayerBingoData::new
    );

    public PlayerBingoData() {
        this(true);
    }

    public PlayerBingoData withTwist(boolean twist) {
        return new PlayerBingoData(twist);
    }

    public Tag save() {
        Tag tag = CODEC.encodeStart(NbtOps.INSTANCE, this).resultOrPartial(IdeaList.LOG::error).orElseThrow();
        return tag;
    }

}
