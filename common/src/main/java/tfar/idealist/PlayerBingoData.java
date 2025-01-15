package tfar.idealist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record PlayerBingoData(boolean twist, int shot_count, Vec3 return_pos, BlockPos seed_pos) {

    public static final Codec<PlayerBingoData> CODEC = RecordCodecBuilder.create(playerBingoDataInstance ->
            playerBingoDataInstance.group(
                    Codec.BOOL.fieldOf("twist").forGetter(PlayerBingoData::twist),
                    Codec.INT.fieldOf("shot_count").forGetter(PlayerBingoData::shot_count),
                    Vec3.CODEC.fieldOf("return_pos").forGetter(PlayerBingoData::return_pos),
                    BlockPos.CODEC.fieldOf("seed_pos").forGetter(PlayerBingoData::seed_pos)
            ).apply(playerBingoDataInstance,PlayerBingoData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf,PlayerBingoData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,PlayerBingoData::twist,
            ByteBufCodecs.INT,PlayerBingoData::shot_count,
            IdeaList.VEC3_STREAM_CODEC,PlayerBingoData::return_pos,
            BlockPos.STREAM_CODEC,PlayerBingoData::seed_pos,

            PlayerBingoData::new
    );

    public PlayerBingoData() {
        this(true,0,Vec3.ZERO,BlockPos.ZERO);
    }

    public PlayerBingoData withTwist(boolean twist) {
        return new PlayerBingoData(twist,shot_count,return_pos,seed_pos);
    }

    public PlayerBingoData incrementShot() {
        return new PlayerBingoData(twist,shot_count+1,return_pos,seed_pos);
    }

    public PlayerBingoData setReturnPos(Vec3 pos) {
        return new PlayerBingoData(twist,shot_count,pos,seed_pos);
    }

    public PlayerBingoData setSeedPos(BlockPos pos) {
        return new PlayerBingoData(twist,shot_count,return_pos,pos);
    }

}
