package moe.quilldev.stratumgladiators.Commands.ChallengeCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChallengeCommand implements CommandExecutor {

    private final ChallengeManger challengeManger;

    public ChallengeCommand(ChallengeManger challengeManger) {
        this.challengeManger = challengeManger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        final var player = ((Player) sender).getPlayer();
        if (player == null) return true;
        final var opponent = player.getServer().getPlayer(args[0]);
        if (opponent == null) return true;

        opponent.sendMessage(player.getName() + " has challenged you to a duel!");
        challengeManger.addChallenge(new Challenge(player, opponent));
        return true;
    }

}
