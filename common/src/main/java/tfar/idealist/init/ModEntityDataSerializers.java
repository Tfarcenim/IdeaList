package tfar.idealist.init;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import tfar.idealist.entity.SeedEntity;
import tfar.idealist.platform.Services;

public class ModEntityDataSerializers {

    public static final EntityDataSerializer<SeedEntity.Stage> STAGE = EntityDataSerializer.forValueType(SeedEntity.Stage.STREAM_CODEC);

    static {
        if (Services.PLATFORM.getPlatformName().equals("Fabric")) {
            EntityDataSerializers.registerSerializer(STAGE);
        }
    }
}
