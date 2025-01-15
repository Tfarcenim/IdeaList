package tfar.idealist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record PlayerBingoData(boolean twist, int shot_count, Vec3 return_pos, BlockPos seed_pos,BlockPos deferred_end_portal,int question) {

    public static final Codec<PlayerBingoData> CODEC = RecordCodecBuilder.create(playerBingoDataInstance ->
            playerBingoDataInstance.group(
                    Codec.BOOL.fieldOf("twist").forGetter(PlayerBingoData::twist),
                    Codec.INT.fieldOf("shot_count").forGetter(PlayerBingoData::shot_count),
                    Vec3.CODEC.fieldOf("return_pos").forGetter(PlayerBingoData::return_pos),
                    BlockPos.CODEC.fieldOf("seed_pos").forGetter(PlayerBingoData::seed_pos),
                    BlockPos.CODEC.fieldOf("deferred_end_portal").forGetter(PlayerBingoData::deferred_end_portal),
                    Codec.INT.fieldOf("question").forGetter(PlayerBingoData::question)

            ).apply(playerBingoDataInstance,PlayerBingoData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf,PlayerBingoData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,PlayerBingoData::twist,
            ByteBufCodecs.INT,PlayerBingoData::shot_count,
            IdeaList.VEC3_STREAM_CODEC,PlayerBingoData::return_pos,
            BlockPos.STREAM_CODEC,PlayerBingoData::seed_pos,
            BlockPos.STREAM_CODEC,PlayerBingoData::deferred_end_portal,
            ByteBufCodecs.INT,PlayerBingoData::question,
            PlayerBingoData::new
    );

    public PlayerBingoData() {
        this(true,0,Vec3.ZERO,BlockPos.ZERO,BlockPos.ZERO,-1);
    }

    public PlayerBingoData withTwist(boolean twist) {
        return new PlayerBingoData(twist,shot_count,return_pos,seed_pos,deferred_end_portal,question);
    }

    public PlayerBingoData incrementShot() {
        return new PlayerBingoData(twist,shot_count+1,return_pos,seed_pos,deferred_end_portal,question);
    }

    public PlayerBingoData setReturnPos(Vec3 pos) {
        return new PlayerBingoData(twist,shot_count,pos,seed_pos,deferred_end_portal,question);
    }

    public PlayerBingoData setSeedPos(BlockPos pos) {
        return new PlayerBingoData(twist,shot_count,return_pos,pos,deferred_end_portal,question);
    }

    public PlayerBingoData setDeferredEndPortalPos(BlockPos pos) {
        return new PlayerBingoData(twist,shot_count,return_pos,seed_pos,pos,question);
    }

    public PlayerBingoData incrementQuestion() {
        return new PlayerBingoData(twist,shot_count,return_pos,seed_pos,deferred_end_portal,question+1);
    }

    public PlayerBingoData resetQuestions() {
        return new PlayerBingoData(twist,shot_count,return_pos,seed_pos,deferred_end_portal,-1);
    }

}
