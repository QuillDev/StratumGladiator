package moe.quilldev.stratumgladiators.Commands.ArenaCommands;

import moe.quilldev.stratumgladiators.Arenas.Arena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChangeArenaTabs implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Arrays.stream(Arena.values())
                .map(Enum::name)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
