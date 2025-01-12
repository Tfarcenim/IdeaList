package tfar.idealist.init;

import net.neoforged.neoforge.attachment.AttachmentType;
import tfar.idealist.PlayerBingoData;

public class AttachmentTypes {
    public static final AttachmentType<PlayerBingoData> PLAYER_BINGO_DATA = AttachmentType.builder(() -> new PlayerBingoData())
            .serialize(PlayerBingoData.CODEC)
            .copyOnDeath()
            .build();
}
