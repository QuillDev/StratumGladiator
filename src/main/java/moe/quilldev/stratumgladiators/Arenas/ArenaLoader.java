package moe.quilldev.stratumgladiators.Arenas;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ArenaLoader {

    private final Random random = new Random();

    public void loadRandomArena() {
        final var arenas = new ArrayList<>(Arrays.asList(Arena.values()));
        arenas.remove(Arena.ARENA_EMPTY);
        final var nextArena = arenas.get(random.nextInt(arenas.size()));
        loadArena(nextArena);
        Bukkit.getServer().sendMessage(Component.text("The arena has changed to " + nextArena.name()));
    }

    public void loadArena(Arena arena) {
        final var file = new File("./plugins/WorldEdit/schematics/" + arena.name() + ".schem");

        Clipboard clipboard = null;
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) return;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (clipboard == null) return;
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(Bukkit.getWorld("world")))) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(-14, 68, 1686))
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }
}
