package moe.quilldev.stratumgladiators;

import moe.quilldev.stratumgladiators.Bets.BetManager;
import moe.quilldev.stratumgladiators.Commands.ArenaCommands.ChangeArena;
import moe.quilldev.stratumgladiators.Commands.ArenaCommands.ChangeArenaTabs;
import moe.quilldev.stratumgladiators.Commands.BetCommands.BetCommand;
import moe.quilldev.stratumgladiators.Commands.ChallengeCommands.Challenge;
import moe.quilldev.stratumgladiators.Commands.ChallengeCommands.ChallengeAcceptCommand;
import moe.quilldev.stratumgladiators.Commands.ChallengeCommands.ChallengeCommand;
import moe.quilldev.stratumgladiators.Commands.ChallengeCommands.ChallengeManger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

public final class StratumGladiators extends JavaPlugin {

    private ChallengeManger challengeManager = null;
    Economy economy = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        setupEconomy();

        challengeManager = new ChallengeManger(this, economy);
        final var betManager = new BetManager(challengeManager);
        final var pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(challengeManager, this);
        getCommand("challenge").setExecutor(new ChallengeCommand(challengeManager));
        getCommand("challengeaccept").setExecutor(new ChallengeAcceptCommand(challengeManager));
        getCommand("bet").setExecutor(new BetCommand(betManager));

        final var changeArenaCommand = getCommand("changearena");
        if (changeArenaCommand != null) {
            changeArenaCommand.setExecutor(new ChangeArena());
            changeArenaCommand.setTabCompleter(new ChangeArenaTabs());
        }
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        var rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }

    @Override
    public void onDisable() {
    }
}
