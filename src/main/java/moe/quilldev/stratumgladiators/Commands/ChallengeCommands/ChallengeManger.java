package moe.quilldev.stratumgladiators.Commands.ChallengeCommands;

import moe.quilldev.stratumgladiators.Arenas.Arena;
import moe.quilldev.stratumgladiators.Arenas.ArenaLoader;
import moe.quilldev.stratumgladiators.Arenas.ArenaMapCycler;
import moe.quilldev.stratumgladiators.Bets.BetBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;

public class ChallengeManger implements Listener {

    private final Plugin plugin;

    private final ArrayList<Challenge> challenges;
    private final PotionEffect slowEffect = new PotionEffect(PotionEffectType.SLOW, 60, 9999);

    //Locations
    private final Location challengerLocation;
    private final Location receiverLocation;
    private final Location standLocation;


    //Set the active challenge to the given one.
    private Challenge activeChallenge = null;

    //Boss bar data
    private final BetBar betBar;
    private final Economy economy;

    //The id for the map change task
    private final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
    private final ArenaMapCycler arenaMapCycler;

    /**
     * Create the challenge manager
     *
     * @param plugin to manage challenges for
     */
    public ChallengeManger(Plugin plugin, Economy economy) {
        final var world = Bukkit.getServer().getWorld("world");
        this.challenges = new ArrayList<>();
        this.challengerLocation = new Location(world, 4, 69, 1686);
        this.receiverLocation = new Location(world, -30, 69, 1686);
        this.standLocation = new Location(world, -14, 81, 1666);
        this.plugin = plugin;
        this.economy = economy;
        this.betBar = new BetBar(this);
        this.arenaMapCycler = new ArenaMapCycler(plugin);
        this.arenaMapCycler.startRotation(true);
    }

    /**
     * Accept the challenge from the other player
     *
     * @param challenge to accept
     */
    public void acceptChallenge(Challenge challenge) {
        final var receiver = challenge.getReceiver();
        final var challenger = challenge.getChallenger();

        //If they do not have a challenge from ths player tell them that and return
        if (!challenges.contains(challenge)) {
            challenge.getReceiver().sendMessage("There is no challenge from this player!");
            return;
        }

        //Send challenger + receiver messages
        receiver.sendMessage("You have accepted " + challenger.getName() + "'s challenge!");
        challenger.sendMessage(receiver.getName() + " has accepted your challenge!");


        //TODO: Add a queue step between this and the start of the battle
        //Set the active challenge to this one
        this.activeChallenge = challenge;

        //Set the game mode of the challenger + receiver
        receiver.setGameMode(GameMode.SURVIVAL);
        challenger.setGameMode(GameMode.SURVIVAL);

        //Teleport them to the arena
        receiver.teleport(receiverLocation);
        challenger.teleport(challengerLocation);

        Bukkit.getServer().sendMessage(
                Component.text("A duel has begun between")
                        .append(Component.space())
                        .append(Component.text(challenger.getName()))
                        .append(Component.space())
                        .append(Component.text("and"))
                        .append(Component.space())
                        .append(Component.text(receiver.getName()))
                        .append(Component.text("!"))
                        .color(TextColor.color(0xFF513E))
        );

        challenges.remove(challenge); //remove the challenge from the challenges

        createBetBar();
        receiver.addPotionEffect(slowEffect);
        challenger.addPotionEffect(slowEffect);
        sendCountdown(receiver, challenger);
        this.arenaMapCycler.stopRotation();
    }

    /**
     * Create the betting bar for the current arena
     */
    public void createBetBar() {
        betBar.create();
    }


    /**
     * Send the countdowns to hte players
     *
     * @param receiver   to receive the countdown
     * @param challenger to receive the countdown
     */
    public void sendCountdown(Player receiver, Player challenger) {
        final var threeText = Component.text("3").color(TextColor.color(0xD2C709));
        final var twoText = Component.text("2").color(TextColor.color(0xFF8B3C));
        final var oneText = Component.text("1").color(TextColor.color(0xFF544D));
        final var goText = Component.text("GO!").color(TextColor.color(0x35FF57));

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            receiver.showTitle(Title.title(threeText, Component.empty()));
            challenger.showTitle(Title.title(threeText, Component.empty()));
        }, 0L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            receiver.showTitle(Title.title(twoText, Component.empty()));
            challenger.showTitle(Title.title(twoText, Component.empty()));
        }, 20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            receiver.showTitle(Title.title(oneText, Component.empty()));
            challenger.showTitle(Title.title(oneText, Component.empty()));
        }, 40L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            receiver.showTitle(Title.title(goText, Component.empty()));
            challenger.showTitle(Title.title(goText, Component.empty()));
        }, 60L);

    }

    public void addChallenge(Challenge challenge) {
        if (this.challenges.contains(challenge)) return;
        System.out.println("Added challenge between " + challenge.getChallenger().getName() + " " + challenge.getReceiver().getName());
        this.challenges.add(challenge);
    }

    /**
     * Process the ending of the duel
     *
     * @param winner of the duel
     * @param loser  of the duel
     */
    private void processDuelEnd(Player winner, Player loser) {
        announceWinner(winner, loser);
        winner.teleport(standLocation);
        loser.teleport(standLocation);
        betBar.payout(winner, activeChallenge);
        betBar.reset();
        arenaMapCycler.startRotation(false);
        activeChallenge = null;
    }

    /**
     * Announce the winner of the battle
     *
     * @param winner of the battle
     * @param loser  of the battle
     */
    private void announceWinner(Player winner, Player loser) {
        final var winnerComponent = Component.text(winner.getName()).color(TextColor.color(0xC5BE6F));
        final var loserComponent = Component.text(loser.getName()).color(TextColor.color(0xC6635F));
        Bukkit.getServer().sendMessage(
                Component.empty()
                        .append(winnerComponent)
                        .append(Component.space())
                        .append(Component.text("slaughtered"))
                        .append(Component.space())
                        .append(loserComponent)
                        .append(Component.space())
                        .append(Component.text("and won the duel!"))
                        .color(TextColor.color(Color.WHITE.asRGB()))
        );
    }

    /**
     * Get the active challenge to place bets for
     *
     * @return the active challenge
     */
    public Challenge getActiveChallenge() {
        return activeChallenge;
    }

    /**
     * Whenever a player dies, add a death event for them
     *
     * @param event death event
     */
    @EventHandler
    public void playerDeathEvent(EntityDeathEvent event) {
        if (activeChallenge == null) return;
        final var entity = event.getEntity();
        if (!(entity instanceof Player)) return;
        final var player = ((Player) entity).getPlayer();
        if (player == null) return;
        //Get challenge players
        final var challenger = activeChallenge.getChallenger();
        final var receiver = activeChallenge.getReceiver();

        //If the player is the challenger, the receiver wins
        if (player.equals(challenger)) {
            processDuelEnd(receiver, challenger);
        }

        //If the player is the receiver, the challenger wins
        else if (player.equals(receiver)) {
            processDuelEnd(challenger, receiver);
        }
    }

    /**
     * If one of the players quits, let the other one win the duel
     *
     * @param event of player quitting
     */
    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        if (activeChallenge == null) return;
        //Get challenge players
        final var challenger = activeChallenge.getChallenger();
        final var receiver = activeChallenge.getReceiver();
        final var quitter = event.getPlayer();

        //If the person who left was the challenger or the receiver, end the duel as well
        if (quitter.equals(challenger)) {
            processDuelEnd(receiver, challenger);
        } else if (quitter.equals(receiver)) {
            processDuelEnd(challenger, receiver);
        }
    }

    public BetBar getBetBar() {
        return betBar;
    }

    public Economy getEconomy() {
        return economy;
    }
}
