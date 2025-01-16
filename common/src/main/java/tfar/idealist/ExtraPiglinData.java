package tfar.idealist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record ExtraPiglinData(UUID lastTraded) {

    public ExtraPiglinData() {
        this(Util.NIL_UUID);
    }

    public static final Codec<ExtraPiglinData> CODEC = RecordCodecBuilder.create(dataInstance ->
            dataInstance.group(
                    UUIDUtil.CODEC.fieldOf("last_traded").forGetter(ExtraPiglinData::lastTraded)
            ).apply(dataInstance,ExtraPiglinData::new));

}
