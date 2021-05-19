package moe.quilldev.stratumgladiators.Arenas;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ArenaMapCycler {

    final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
    final static ArenaLoader arenaLoader = new ArenaLoader();
    final Plugin plugin;

    //Interval times
    long delay;
    final long interval;

    //Keep track of ticks
    int startingTick = Bukkit.getCurrentTick();
    int lastStartingTick = Bukkit.getCurrentTick();
    //Process of the task
    int processId = -1;

    public ArenaMapCycler(Plugin plugin) {
        this.delay = 0L;
        this.interval = 20 * 60 * 30L; //20 Ticks * 60 Seconds * 30 Minutes
        this.plugin = plugin;
    }

    public void startRotation(boolean ignoreFirstTick) {
        this.startingTick = Bukkit.getCurrentTick();

        if (!ignoreFirstTick) {
            final var dif = this.startingTick - this.lastStartingTick;

            //if the delay is more than the interval set the delay to be zero
            if (dif >= interval) {
                this.delay = 0;
            } else {
                this.delay = dif % interval;
            }
        }

        this.processId = this.bukkitScheduler.scheduleSyncRepeatingTask(plugin, arenaLoader::loadRandomArena, this.delay, this.interval);
    }

    public void stopRotation() {
        this.delay = (Bukkit.getCurrentTick() - startingTick) % interval;
        this.bukkitScheduler.cancelTask(processId);
    }
}
