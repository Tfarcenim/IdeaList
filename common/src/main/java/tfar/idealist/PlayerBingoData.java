package tfar.idealist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public record PlayerBingoData(boolean twist, int shot_count, Vec3 seed_return_pos, BlockPos seed_pos, QuizData quiz_data,Vec3 piglin_parkour_return_pos) {

    public record QuizData(BlockPos deferred_end_portal, int question, UUID enderman) {
        public static final Codec<QuizData> CODEC = RecordCodecBuilder.create(playerBingoDataInstance ->
                playerBingoDataInstance.group(
                        BlockPos.CODEC.fieldOf("deferred_end_portal").forGetter(QuizData::deferred_end_portal),
                        Codec.INT.fieldOf("question").forGetter(QuizData::question),
                        UUIDUtil.CODEC.fieldOf("enderman").forGetter(QuizData::enderman)
                ).apply(playerBingoDataInstance,QuizData::new));
        public static final StreamCodec<RegistryFriendlyByteBuf,QuizData> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC,QuizData::deferred_end_portal,
                ByteBufCodecs.INT,QuizData::question,
                UUIDUtil.STREAM_CODEC,QuizData::enderman,
                QuizData::new
        );

        public QuizData setDeferredEndPortalPos(BlockPos pos) {
            return new QuizData(pos, question,enderman);
        }

        public QuizData incrementQuestion() {
            return new QuizData(deferred_end_portal, question +1,enderman);
        }

        public QuizData resetQuestions() {
            return new QuizData(deferred_end_portal, -1, Util.NIL_UUID);
        }

        public QuizData setEnderman(UUID uuid) {
            return new QuizData(deferred_end_portal, question, uuid);
        }
    }

    public static final Codec<PlayerBingoData> CODEC = RecordCodecBuilder.create(playerBingoDataInstance ->
            playerBingoDataInstance.group(
                    Codec.BOOL.fieldOf("twist").forGetter(PlayerBingoData::twist),
                    Codec.INT.fieldOf("shot_count").forGetter(PlayerBingoData::shot_count),
                    Vec3.CODEC.fieldOf("seed_return_pos").forGetter(PlayerBingoData::seed_return_pos),
                    BlockPos.CODEC.fieldOf("seed_pos").forGetter(PlayerBingoData::seed_pos),
                    QuizData.CODEC.fieldOf("quiz_data").forGetter(PlayerBingoData::quiz_data),
                    Vec3.CODEC.fieldOf("piglin_parkour_return_pos").forGetter(PlayerBingoData::piglin_parkour_return_pos)
            ).apply(playerBingoDataInstance,PlayerBingoData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf,PlayerBingoData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,PlayerBingoData::twist,
            ByteBufCodecs.INT,PlayerBingoData::shot_count,
            IdeaList.VEC3_STREAM_CODEC,PlayerBingoData::seed_return_pos,
            BlockPos.STREAM_CODEC,PlayerBingoData::seed_pos,
            QuizData.STREAM_CODEC,PlayerBingoData::quiz_data,
            IdeaList.VEC3_STREAM_CODEC,PlayerBingoData::piglin_parkour_return_pos,
            PlayerBingoData::new
    );

    public PlayerBingoData() {
        this(true,0,Vec3.ZERO,BlockPos.ZERO,new QuizData(BlockPos.ZERO,-1, Util.NIL_UUID),Vec3.ZERO);
    }

    public PlayerBingoData withTwist(boolean twist) {
        return new PlayerBingoData(twist,shot_count, seed_return_pos,seed_pos,quiz_data,piglin_parkour_return_pos);
    }

    public PlayerBingoData incrementShot() {
        return new PlayerBingoData(twist,shot_count+1, seed_return_pos,seed_pos,quiz_data,piglin_parkour_return_pos);
    }

    public PlayerBingoData setSeedReturnPos(Vec3 pos) {
        return new PlayerBingoData(twist,shot_count,pos,seed_pos,quiz_data,piglin_parkour_return_pos);
    }

    public PlayerBingoData setSeedPos(BlockPos pos) {
        return new PlayerBingoData(twist,shot_count, seed_return_pos,pos,quiz_data,piglin_parkour_return_pos);
    }

    public PlayerBingoData setDeferredEndPortalPos(BlockPos pos) {
        return new PlayerBingoData(twist,shot_count, seed_return_pos,seed_pos,quiz_data.setDeferredEndPortalPos(pos),piglin_parkour_return_pos);
    }

    public PlayerBingoData incrementQuestion() {
        return new PlayerBingoData(twist,shot_count, seed_return_pos,seed_pos,quiz_data.incrementQuestion(),piglin_parkour_return_pos);
    }

    public PlayerBingoData resetQuestions() {
        return new PlayerBingoData(twist,shot_count, seed_return_pos,seed_pos,quiz_data.resetQuestions(),piglin_parkour_return_pos);
    }

    public PlayerBingoData setEnderman(UUID uuid) {
        return new PlayerBingoData(twist,shot_count, seed_return_pos,seed_pos,quiz_data.setEnderman(uuid),piglin_parkour_return_pos);
    }

    public PlayerBingoData setPiglinParkourReturnPos(Vec3 pos) {
        return new PlayerBingoData(twist,shot_count, seed_return_pos,seed_pos,quiz_data,pos);
    }

}
