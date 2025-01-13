package tfar.idealist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PlayerBingoData(boolean twist,int shot_count) {

    public static final Codec<PlayerBingoData> CODEC = RecordCodecBuilder.create(playerBingoDataInstance ->
            playerBingoDataInstance.group(
                    Codec.BOOL.fieldOf("twist").forGetter(PlayerBingoData::twist),
                    Codec.INT.fieldOf("shot_count").forGetter(PlayerBingoData::shot_count)
            ).apply(playerBingoDataInstance,PlayerBingoData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf,PlayerBingoData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,PlayerBingoData::twist,
            ByteBufCodecs.INT,PlayerBingoData::shot_count,
            PlayerBingoData::new
    );

    public PlayerBingoData() {
        this(true,0);
    }

    public PlayerBingoData withTwist(boolean twist) {
        return new PlayerBingoData(twist,shot_count);
    }

    public PlayerBingoData incrementShot() {
        return new PlayerBingoData(twist,shot_count+1);
    }

    public Tag save() {
        Tag tag = CODEC.encodeStart(NbtOps.INSTANCE, this).resultOrPartial(IdeaList.LOG::error).orElseThrow();
        return tag;
    }

}
