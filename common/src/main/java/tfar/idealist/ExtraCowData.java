package tfar.idealist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ExtraCowData(int morph_countdown) {

    public static final Codec<ExtraCowData> CODEC = RecordCodecBuilder.create(dataInstance ->
            dataInstance.group(
                    Codec.INT.fieldOf("morph_countdown").forGetter(ExtraCowData::morph_countdown)
            ).apply(dataInstance,ExtraCowData::new));

    public ExtraCowData() {
        this(-1);
    }

    public ExtraCowData tick() {
        return new ExtraCowData(morph_countdown-1);
    }


}
