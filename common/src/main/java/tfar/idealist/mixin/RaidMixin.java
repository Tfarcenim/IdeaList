package tfar.idealist.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.idealist.IdeaList;
import tfar.idealist.RaidDuck;
import tfar.idealist.platform.Services;

@Mixin(Raid.class)
public class RaidMixin implements RaidDuck {

    ServerPlayer causingPlayer;

    @Override
    public void setCausingPlayer(ServerPlayer player) {
        causingPlayer = player;
    }

    @Override
    public ServerPlayer getCausingPlayer() {
        return causingPlayer;
    }

    @Inject(method = "absorbRaidOmen",at = @At("RETURN"))
    private void setCause(ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        causingPlayer = player;
    }

    @Inject(method = "spawnGroup",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;shouldSpawnBonusGroup()Z",shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD,cancellable = true)
    private void overrideSpawns(BlockPos pos, CallbackInfo ci, boolean flag, int i, DifficultyInstance difficultyinstance) {
        if (causingPlayer != null && Services.PLATFORM.getData(causingPlayer).twist()) {
            IdeaList.overrideRaidSpawns((Raid)(Object)this,pos, ci, flag, i, difficultyinstance);
            ci.cancel();
        }
    }
}
