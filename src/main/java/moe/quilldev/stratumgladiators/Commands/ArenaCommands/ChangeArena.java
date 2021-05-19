package moe.quilldev.stratumgladiators.Commands.ArenaCommands;

import moe.quilldev.stratumgladiators.Arenas.Arena;
import moe.quilldev.stratumgladiators.Arenas.ArenaLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ChangeArena implements CommandExecutor {

    private static final ArenaLoader arenaLoader = new ArenaLoader();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("You did not enter an arena name");
            return true;
        }

        final var arena = Arena.valueOf(args[0]);
        arenaLoader.loadArena(arena);
        return true;
    }
}
