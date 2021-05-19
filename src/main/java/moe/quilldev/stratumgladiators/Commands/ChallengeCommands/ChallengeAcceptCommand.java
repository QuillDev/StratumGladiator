package moe.quilldev.stratumgladiators.Commands.ChallengeCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChallengeAcceptCommand implements CommandExecutor {

    private final ChallengeManger challengeManger;

    public ChallengeAcceptCommand(ChallengeManger challengeManger) {
        this.challengeManger = challengeManger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        final var receiver = ((Player) sender).getPlayer();
        if (receiver == null) return true;
        final var challenger = sender.getServer().getPlayer(args[0]);
        if (challenger == null) return true;

        final var challenge = new Challenge(challenger, receiver);
        challengeManger.acceptChallenge(challenge);

        //TODO: make shit actually happen
        return true;
    }
}
