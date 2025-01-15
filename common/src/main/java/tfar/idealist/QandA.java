package tfar.idealist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.HashSet;

public record QandA(String question, HashSet<String> acceptable_answers) {

    public static final Codec<QandA> CODEC = RecordCodecBuilder.create(
            qandAInstance -> qandAInstance.group(Codec.STRING.fieldOf("question").forGetter(QandA::question),
                          Codec.list(Codec.STRING).xmap(HashSet::new, ArrayList::new).fieldOf("acceptable_answers").forGetter(QandA::acceptable_answers)
                          ).apply(qandAInstance,QandA::new)
    );

}
