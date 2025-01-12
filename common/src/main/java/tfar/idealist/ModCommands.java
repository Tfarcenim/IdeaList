package tfar.idealist;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import tfar.idealist.platform.Services;

import java.util.Collection;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("bingo")
                .then(Commands.literal("twist")
                        .then(Commands.argument("twist", BoolArgumentType.bool())
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes(ModCommands::setTwist)
                                )
                        )
                )
        );
    }

    private static int setTwist(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        boolean twist = BoolArgumentType.getBool(context,"twist");
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context,"players");

        for (ServerPlayer player : players) {
            PlayerBingoData playerBingoData = Services.PLATFORM.getData(player);
            Services.PLATFORM.setData(player,playerBingoData.withTwist(twist));
        }

        return players.size();
    }

}
