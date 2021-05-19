package moe.quilldev.stratumgladiators.Commands.BetCommands;

import moe.quilldev.stratumgladiators.Bets.BetManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BetCommand implements CommandExecutor {
    private final BetManager betManager;

    public BetCommand(BetManager betManager) {
        this.betManager = betManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        final var player = ((Player) sender).getPlayer();
        if (player == null) return true;

        if (args.length != 2) {
            player.sendMessage("Invalid arguments, use /bet <player> <amount>");
            return true;
        }

        final var amount = Double.parseDouble(args[0]);
        if (Double.isNaN(amount)) {
            player.sendMessage("You entered an invalid amount!");
            return true;
        }
        final var targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            player.sendMessage("Invalid player target!");
            return true;
        }

        betManager.bet(player, targetPlayer, amount);
        return true;
    }
}
