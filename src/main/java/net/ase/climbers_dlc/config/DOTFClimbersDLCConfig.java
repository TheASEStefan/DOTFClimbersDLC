package net.ase.climbers_dlc.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

public class DOTFClimbersDLCConfig
{
    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static
    {
        Pair<Server, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER = commonPair.getLeft();
        SERVER_SPEC = commonPair.getRight();
    }
    public static class Server
    {
        public final ForgeConfigSpec.ConfigValue<Boolean> canPodInfectorsClimbProperly;

        public Server(ForgeConfigSpec.Builder builder)
        {
            builder.push("Climbers DLC Options");
            this.canPodInfectorsClimbProperly = builder.comment("Default false").define("Should pod infectors also have the ability to climb properly with this dlc? (Turned off by default to avoid pathing issues and lag)",false);
            builder.pop();
        }
    }

    public static void loadConfig(ForgeConfigSpec config, String path)
    {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }
}
