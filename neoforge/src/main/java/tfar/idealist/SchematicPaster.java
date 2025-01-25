package tfar.idealist;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.PlatformManager;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.neoforge.NeoForgeCommandSender;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.world.World;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SchematicPaster {



    public static void init(MinecraftServer server) {
        long start = System.nanoTime();


        placeSchem(server,IdeaList.SEED_DIM,"seed_no_light");
        placeSchem(server,IdeaList.PIGLIN_PARKOUR_DIM,"parkour2");


        long end = System.nanoTime();
        System.out.println((end - start) / 10000000 +" ms");
    }
    static void placeSchem(MinecraftServer server,ResourceKey<Level> key, String name) {
        WorldEdit worldEdit = WorldEdit.getInstance();
        LocalConfiguration config = worldEdit.getConfiguration();
        File schematicFolder = worldEdit.getWorkingDirectoryPath(config.saveDir).toFile();

        schematicFolder.mkdirs();
        PlatformManager platformManager = worldEdit.getPlatformManager();
        Actor actor = platformManager.createProxyActor(new NeoForgeCommandSender(server.createCommandSourceStack()));
        try {
            File f = worldEdit.getSafeOpenFile(actor, schematicFolder, name,
                    BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getPrimaryFileExtension(),
                    ClipboardFormats.getFileExtensionArray());
            ServerLevel seedLevel = server.getLevel(key);
            pasteSchematic(NeoForgeAdapter.adapt(seedLevel), f);
        } catch (FilenameException e) {
            throw new RuntimeException(e);
        }
    }


    public static void pasteSchematic(@NotNull World world, @NotNull File file) {

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            final Clipboard clipboard = reader.read();

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(new BlockVector3(0,64,0))
                        .ignoreAirBlocks(true)
                        .copyBiomes(false)
                        .copyEntities(false)
                        .build();

                Operations.complete(operation);

            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
