package moe.quilldev.stratumgladiators.Bets;

import moe.quilldev.stratumgladiators.Commands.ChallengeCommands.ChallengeManger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class BetManager {

    private final ChallengeManger challengeManger;
    private final Economy economy;
    private final BetBar betBar;

    public BetManager(ChallengeManger challengeManger) {
        this.challengeManger = challengeManger;
        this.economy = challengeManger.getEconomy();
        this.betBar = challengeManger.getBetBar();
    }

    public void bet(Player gambler, Player betTarget, double amount) {

        //Get the active challenge
        final var activeChallenge = challengeManger.getActiveChallenge();
        if (activeChallenge == null) {
            gambler.sendMessage("There is no challenge currently happening!");
            return;
        }

        //Check if they gave a valid betting target
        if (!activeChallenge.getChallenger().equals(betTarget) && !activeChallenge.getReceiver().equals(betTarget)) {
            gambler.sendMessage("You did not supply a valid bet target!");
            return;
        }

        //Check if they have enough money to place the bet
        final var gamblerBalance = economy.getBalance(gambler);
        if (gamblerBalance < amount) {
            gambler.sendMessage("You're too broke to make this bet!");
            return;
        }

        //Take the money off the players
        economy.withdrawPlayer(gambler, amount);
        gambler.sendMessage("You have placed a bet of $" + amount + " on " + betTarget.getName() + "!");
        betBar.addBet(new Bet(gambler, amount, betTarget));
        betBar.update();
    }
}
