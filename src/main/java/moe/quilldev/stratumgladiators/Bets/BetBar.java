package moe.quilldev.stratumgladiators.Bets;

import moe.quilldev.stratumgladiators.Commands.ChallengeCommands.Challenge;
import moe.quilldev.stratumgladiators.Commands.ChallengeCommands.ChallengeManger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BetBar {


    private ArrayList<Bet> bets = new ArrayList<>();
    private final BossBar bossBar;
    private ChallengeManger challengeManger;
    private Economy economy;

    public BetBar(ChallengeManger challengeManger) {
        this.bossBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
        this.bossBar.setProgress(0.);
        this.challengeManger = challengeManger;
        this.economy = challengeManger.getEconomy();
    }

    public void create() {
        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
        bossBar.setVisible(true);
        update();
    }

    public void update() {
        final var currentChallenge = challengeManger.getActiveChallenge();
        if (currentChallenge == null) return;

        double challengerBetAmount = getSumForPlayer(currentChallenge.getChallenger());
        double receiverBetAmount = getSumForPlayer(currentChallenge.getReceiver());
        double betTotal = challengerBetAmount + receiverBetAmount;
        var ratio = " [1:1] ";
        if (challengerBetAmount > 0 && receiverBetAmount > 0) {
            final var leftRatio = Math.round((betTotal / challengerBetAmount * 10)) / 10;
            final var rightRatio = Math.round((betTotal / receiverBetAmount * 10)) / 10;
            ratio = " [" + leftRatio + ":" + rightRatio + "] ";
        }

        bossBar.setTitle(currentChallenge.getChallenger().getName() + ratio + currentChallenge.getReceiver().getName());
        final var progress = challengerBetAmount / betTotal;
        if (Double.isNaN(progress)) {
            bossBar.setProgress(.5);
            return;
        }
        bossBar.setProgress(progress);
    }

    public void payout(Player winner, Challenge challenge) {
        double challengerBetAmount = getSumForPlayer(challenge.getChallenger());
        double receiverBetAmount = getSumForPlayer(challenge.getReceiver());
        double betTotal = challengerBetAmount + receiverBetAmount;

        var challengerRatio = 1.;
        var receiverRatio = 1.;
        if (challengerBetAmount > 0 || receiverBetAmount > 0) {
            challengerRatio = Math.round((betTotal / challengerBetAmount * 10.)) / 10.;
            receiverRatio = Math.round((betTotal / receiverBetAmount * 10.)) / 10.;
        }

        //if the bet ratio is 1:1 then just return bets
        if (challengerRatio / receiverRatio == 1.) {
            for (final var bet : bets) {
                payPlayer(bet.getPlayer(), bet.getAmount());
            }
            return;
        }

        for (final var bet : bets) {
            if (winner.equals(challenge.getChallenger())) {
                if (bet.getBetTarget().equals(challenge.getChallenger())) {
                    final var amount = bet.getAmount() * challengerRatio - bet.getAmount();
                    payPlayer(bet.getPlayer(), amount);
                    continue;
                }
            }

            if (winner.equals(challenge.getReceiver())) {
                if (bet.getBetTarget().equals(challenge.getReceiver())) {
                    final var amount = bet.getAmount() * receiverRatio - bet.getAmount();
                    payPlayer(bet.getPlayer(), amount);
                }
            }
        }

    }

    private void payPlayer(Player player, double amount) {
        economy.depositPlayer(player, amount);
        player.sendMessage(
                Component.text("You won")
                        .append(Component.space())
                        .append(Component.text(amount))
                        .append(Component.space())
                        .append(Component.text("for your bet!"))
                        .color(TextColor.color(0x4CC75C))
        );
    }

    private double getSumForPlayer(Player player) {
        return bets.stream()
                .filter(bet -> bet.getBetTarget().equals(player))
                .mapToDouble(Bet::getAmount)
                .sum();
    }

    public void addBet(Bet bet) {
        bets.add(bet);
    }

    public void reset() {
        bossBar.removeAll();
        bets.clear();
        bossBar.setTitle("");
    }
}
