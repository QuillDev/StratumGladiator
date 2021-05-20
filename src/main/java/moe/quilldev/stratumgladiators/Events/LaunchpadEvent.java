package moe.quilldev.stratumgladiators.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class LaunchpadEvent implements Listener {

    private final Plugin plugin;

    public LaunchpadEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void launchPlayer(PlayerMoveEvent event) {
        final var player = event.getPlayer();
        final var block = player.getLocation().getBlock();
        if (!block.hasMetadata("launchpad")) return;
        final var magnitude = block.getMetadata("launchpad")
                .stream()
                .findFirst()
                .orElse(new FixedMetadataValue(plugin, 0.))
                .asDouble();
        player.setVelocity(player.getLocation().getDirection().multiply(magnitude));
    }

    @EventHandler
    public void destroyLaunchpad(BlockBreakEvent event) {
        final var block = event.getBlock();
        if (!block.hasMetadata("launchpad")) return;
        System.out.println("DESTROYED LAUNCHPAD");
        block.removeMetadata("launchpad", plugin);
    }
}
