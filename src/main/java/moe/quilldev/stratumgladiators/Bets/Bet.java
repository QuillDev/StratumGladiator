package moe.quilldev.stratumgladiators.Bets;

import org.bukkit.entity.Player;

public class Bet {

    private final double amount;
    private final Player player;
    private final Player betTarget;

    public Bet(Player player, double amount, Player betTarget) {
        this.player = player;
        this.amount = amount;
        this.betTarget = betTarget;
    }

    public Player getBetTarget() {
        return betTarget;
    }

    public Player getPlayer() {
        return player;
    }

    public double getAmount() {
        return amount;
    }
}
