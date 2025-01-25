package tfar.idealist;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

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
        public static final ResourceLocation BRUSH_SUSPICIOUS_SAND = IdeaList.id("brush_sand");//IdeaList.id("brush_suspicious_sand");
        public static final ResourceLocation ENTER_THE_END = IdeaList.id("enter_the_end");

        public static final ResourceLocation SHOOT_A_TARGET = IdeaList.id("shoot_a_target");
        public static final ResourceLocation COMPLETE_A_RAID = IdeaList.id("complete_a_raid");
        public static final ResourceLocation GROW_WHEAT = IdeaList.id("grow_wheat");

        public static final QandA Q0 = new QandA("What color is a Minecraft pig?", Sets.newHashSet("pink"));



        public static final QandA Q1 = new QandA("Which mineral can generate naturally in both the Overworld and the Nether?",
                Sets.newHashSet("gold","au"));

        public static final QandA Q2 = new QandA("Which of these mobs has the most health points?",Sets.newHashSet("warden"));


        public static final QandA Q3 = new QandA("How many music discs are in the game?",Sets.newHashSet("fifteen","15"));

        public static final QandA Q4 = new QandA("What is Walter white's age in season 3 of breaking bad?",Sets.newHashSet("fifty","50"));

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

        public static ConfigHelper.ConfigObject<QandA> QUESTION_0;
        public static ConfigHelper.ConfigObject<QandA> QUESTION_1;
        public static ConfigHelper.ConfigObject<QandA> QUESTION_2;
        public static ConfigHelper.ConfigObject<QandA> QUESTION_3;
        public static ConfigHelper.ConfigObject<QandA> QUESTION_4;

        public static ConfigHelper.ConfigObject<Vec3> SEED_TELEPORT;
        public static ConfigHelper.ConfigObject<Vec3> PIGLIN_PARKOUR_PLAYER_TELEPORT;
        public static ConfigHelper.ConfigObject<Vec3> PIGLIN_PARKOUR_PIGLIN_TELEPORT;

        public Server(ModConfigSpec.Builder builder) {
            builder.push("general");
            BINGO_ADVANCEMENT_0 = builder.define("bingo_advancement_0",Defaults.KILL_COW.toString());
            BINGO_ADVANCEMENT_1 = builder.define("bingo_advancement_1",Defaults.MINE_DIAMOND.toString());
            BINGO_ADVANCEMENT_2 = builder.define("bingo_advancement_2",Defaults.KILL_PLAYER.toString());
            BINGO_ADVANCEMENT_3 = builder.define("bingo_advancement_3",Defaults.TRADE_PIGLIN.toString());
            BINGO_ADVANCEMENT_4 = builder.define("bingo_advancement_4",Defaults.BRUSH_SUSPICIOUS_SAND.toString());
            BINGO_ADVANCEMENT_5 = builder.define("bingo_advancement_5", Defaults.ENTER_THE_END.toString());
            BINGO_ADVANCEMENT_6 = builder.define("bingo_advancement_6", Defaults.SHOOT_A_TARGET.toString());
            BINGO_ADVANCEMENT_7 = builder.define("bingo_advancement_7", Defaults.COMPLETE_A_RAID.toString());
            BINGO_ADVANCEMENT_8 = builder.define("bingo_advancement_8", Defaults.GROW_WHEAT.toString());

            SEED_TELEPORT = ConfigHelper.defineObject(builder,"seed_teleport",Vec3.CODEC,new Vec3(-1, 64, 1));

            PIGLIN_PARKOUR_PLAYER_TELEPORT = ConfigHelper.defineObject(builder,"piglin_parkour_player_teleport",Vec3.CODEC,new Vec3(.5, 64, .5));
            PIGLIN_PARKOUR_PIGLIN_TELEPORT = ConfigHelper.defineObject(builder,"piglin_parkour_piglin_teleport",Vec3.CODEC,new Vec3(47, 62, 1));

            QUESTION_0 = ConfigHelper.defineObject(builder,"question_0",QandA.CODEC,Defaults.Q0);
            QUESTION_1 = ConfigHelper.defineObject(builder,"question_1",QandA.CODEC,Defaults.Q1);
            QUESTION_2 = ConfigHelper.defineObject(builder,"question_2",QandA.CODEC,Defaults.Q2);
            QUESTION_3 = ConfigHelper.defineObject(builder,"question_3",QandA.CODEC,Defaults.Q3);
            QUESTION_4 = ConfigHelper.defineObject(builder,"question_4",QandA.CODEC,Defaults.Q4);
            builder.pop();
        }

        public static Set<ResourceLocation> bingoAdvancements;

        public static void cache() {
            bingoAdvancements = new HashSet<>(9);
            bingoAdvancements.add(ResourceLocation.parse(BINGO_ADVANCEMENT_0.get()));
            bingoAdvancements.add(ResourceLocation.parse(BINGO_ADVANCEMENT_1.get()));
            bingoAdvancements.add(ResourceLocation.parse(BINGO_ADVANCEMENT_2.get()));
            bingoAdvancements.add(ResourceLocation.parse(BINGO_ADVANCEMENT_3.get()));
            bingoAdvancements.add(ResourceLocation.parse(BINGO_ADVANCEMENT_4.get()));
            bingoAdvancements.add(ResourceLocation.parse(BINGO_ADVANCEMENT_5.get()));
            bingoAdvancements.add(ResourceLocation.parse(BINGO_ADVANCEMENT_6.get()));
            bingoAdvancements.add(ResourceLocation.parse(BINGO_ADVANCEMENT_7.get()));
            bingoAdvancements.add(ResourceLocation.parse(BINGO_ADVANCEMENT_8.get()));
        }

    }

    public static class Client {
        public Client(ModConfigSpec.Builder builder) {

        }
    }

}
