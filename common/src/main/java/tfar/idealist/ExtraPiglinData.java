package tfar.idealist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record ExtraPiglinData(UUID lastTraded) {
    public static final Codec<ExtraPiglinData> CODEC = RecordCodecBuilder.create(playerBingoDataInstance ->
            playerBingoDataInstance.group(
                    UUIDUtil.CODEC.fieldOf("last_traded").forGetter(ExtraPiglinData::lastTraded)
            ).apply(playerBingoDataInstance,ExtraPiglinData::new));

}
