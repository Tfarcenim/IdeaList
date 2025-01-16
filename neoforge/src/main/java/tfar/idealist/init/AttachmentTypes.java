package tfar.idealist.init;

import net.neoforged.neoforge.attachment.AttachmentType;
import tfar.idealist.ExtraPiglinData;
import tfar.idealist.PlayerBingoData;

public class AttachmentTypes {
    public static final AttachmentType<PlayerBingoData> PLAYER_BINGO_DATA = AttachmentType.builder(() -> new PlayerBingoData())
            .serialize(PlayerBingoData.CODEC)
            .copyOnDeath()
            .build();

    public static final AttachmentType<ExtraPiglinData> EXTRA_PIGLIN_DATA = AttachmentType.builder(() -> new ExtraPiglinData())
            .serialize(ExtraPiglinData.CODEC)
            .build();

}
