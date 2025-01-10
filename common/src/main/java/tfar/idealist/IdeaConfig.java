package tfar.idealist;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class IdeaConfig {

    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    public static final Server SERVER;
    public static final ModConfigSpec SERVER_SPEC;

    static {
        final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
        final Pair<Server, ModConfigSpec> specPair2 = new ModConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }

    public static class Defaults {
        public static final ResourceLocation KILL_COW = IdeaList.id("kill_cow");
        public static final ResourceLocation MINE_DIAMOND = IdeaList.id("mine_diamond");
        public static final ResourceLocation KILL_PLAYER = IdeaList.id("kill_player");
        public static final ResourceLocation TRADE_PIGLIN = IdeaList.id("trade_piglin");
    }

    public static class Server {

        public static ModConfigSpec.ConfigValue<String> BINGO_ADVANCEMENT_0;
        public static ModConfigSpec.ConfigValue<String> BINGO_ADVANCEMENT_1;
        public static ModConfigSpec.ConfigValue<String> BINGO_ADVANCEMENT_2;
        public static ModConfigSpec.ConfigValue<String> BINGO_ADVANCEMENT_3;
        public static ModConfigSpec.ConfigValue<String> BINGO_ADVANCEMENT_4;
        public static ModConfigSpec.ConfigValue<String> BINGO_ADVANCEMENT_5;
        public static ModConfigSpec.ConfigValue<String> BINGO_ADVANCEMENT_6;
        public static ModConfigSpec.ConfigValue<String> BINGO_ADVANCEMENT_7;
        public static ModConfigSpec.ConfigValue<String> BINGO_ADVANCEMENT_8;
        public Server(ModConfigSpec.Builder builder) {
            builder.push("general");
            BINGO_ADVANCEMENT_0 = builder.define("bingo_advancement_0",Defaults.KILL_COW.toString());
            BINGO_ADVANCEMENT_1 = builder.define("bingo_advancement_1",Defaults.MINE_DIAMOND.toString());
            BINGO_ADVANCEMENT_2 = builder.define("bingo_advancement_2",Defaults.KILL_PLAYER.toString());
            BINGO_ADVANCEMENT_3 = builder.define("bingo_advancement_3",Defaults.TRADE_PIGLIN.toString());
            BINGO_ADVANCEMENT_4 = builder.define("bingo_advancement_4","adventure/salvage_sherd");
            BINGO_ADVANCEMENT_5 = builder.define("bingo_advancement_5","story/root");
            BINGO_ADVANCEMENT_6 = builder.define("bingo_advancement_6","story/root");
            BINGO_ADVANCEMENT_7 = builder.define("bingo_advancement_7","story/root");
            BINGO_ADVANCEMENT_8 = builder.define("bingo_advancement_8","story/root");
            builder.pop();
        }
    }

    public static class Client {
        public Client(ModConfigSpec.Builder builder) {

        }
    }

    public static boolean COW_REVENGE = true;
    public static boolean RUNNING_BLOCKS = true;
    public static boolean INVENTORY_SHUFFLE = true;
    public static boolean T_REX = true;
    public static boolean PORTAL_QUIZ = true;
}
