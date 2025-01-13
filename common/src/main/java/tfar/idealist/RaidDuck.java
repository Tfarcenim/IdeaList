package tfar.idealist;

import net.minecraft.server.level.ServerPlayer;

public interface RaidDuck {

    void setCausingPlayer(ServerPlayer player);
    ServerPlayer getCausingPlayer();

}
