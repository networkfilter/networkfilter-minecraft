package ls.ni.networkfilter.bukkit.listeners;

import ls.ni.networkfilter.bukkit.NetworkFilterBukkitPlugin;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.NetworkFilterResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class PlayerJoinListener implements Listener {

    private final NetworkFilterBukkitPlugin plugin;

    public PlayerJoinListener(NetworkFilterBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String address = player.getAddress().getAddress().getHostAddress();

        if (player.hasPermission("networkfilter.ignore")) {
            return;
        }

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            NetworkFilterResult result = NetworkFilterCommon.getInstance().check(address);

            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                if (result.blocked()) {
                    // TODO: put in config
                    player.kickPlayer("§3§lNetworkFilter §8§l» §7Fehler beim Verbinden. Melde dich beim Support mit der Id §e" + result.asn() + " (" + result.org() + ")");
                }
            });
        });
    }
}
