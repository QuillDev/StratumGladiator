package moe.quilldev.stratumgladiators.Commands.ChallengeCommands;

import org.bukkit.entity.Player;

import java.util.Objects;

public class Challenge {

    private final Player challenger;
    private final Player receiver;

    public Challenge(Player challenger, Player receiver) {
        this.challenger = challenger;
        this.receiver = receiver;
    }

    public Player getChallenger() {
        return challenger;
    }

    public Player getReceiver() {
        return receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return Objects.equals(getChallenger(), challenge.getChallenger()) && Objects.equals(getReceiver(), challenge.getReceiver());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChallenger(), getReceiver());
    }
}
