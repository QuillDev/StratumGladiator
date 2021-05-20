package moe.quilldev.stratumgladiators.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SpawnLaunchpad implements CommandExecutor {

    Plugin plugin;

    public SpawnLaunchpad(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        final var player = ((Player) sender).getPlayer();
        if (player == null) return true;
        if (args.length != 1) {
            player.sendMessage("Incorrent syntax! Syntax please supply a magnitude!");
            return true;
        }
        final var magnitude = Double.parseDouble(args[0]);
        if (Double.isNaN(magnitude)) {
            player.sendMessage("You entered an invalid magnitude!");
            return true;
        }

        final var location = player.getLocation();
        final var block = location.getBlock();
        System.out.println(magnitude);
        block.setType(Material.CYAN_CARPET);
        block.setMetadata("launchpad", new FixedMetadataValue(plugin, magnitude));
        return false;
    }
}
