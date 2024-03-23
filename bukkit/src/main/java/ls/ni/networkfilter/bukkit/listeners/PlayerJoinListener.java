package ls.ni.networkfilter.bukkit.listeners;

import ls.ni.networkfilter.bukkit.NetworkFilterBukkitPlugin;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.NetworkFilterResult;
import ls.ni.networkfilter.common.util.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetSocketAddress;
import java.util.Optional;


public class PlayerJoinListener implements Listener {

    private final NetworkFilterBukkitPlugin plugin;

    public PlayerJoinListener(NetworkFilterBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        InetSocketAddress address = player.getAddress();

        if (player.hasPermission(NetworkFilterCommon.getConfig().getIgnore().getPermission())) {
            NetworkFilterCommon.getInstance().debug("{0} has permission, skip check", player.getName());
            return;
        }

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            NetworkFilterResult result = NetworkFilterCommon.getInstance().check(address);

            if (!result.blocked()) {
                return;
            }

            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                if (NetworkFilterCommon.getConfig().getConsequences().getKick().getEnabled()) {
                    String rawMessage = NetworkFilterCommon.getConfig().getConsequences().getKick().getMessage();
                    String message = PlaceholderUtil.replace(rawMessage, result, player.getName(), player.getUniqueId());

                    player.kickPlayer(message);
                }

                for (String rawCommand : NetworkFilterCommon.getConfig().getConsequences().getCommands()) {
                    if (rawCommand.isBlank()) {
                        continue;
                    }

                    String command = PlaceholderUtil.replace(rawCommand, result, player.getName(), player.getUniqueId());

                    this.plugin.getServer().dispatchCommand(
                            this.plugin.getServer().getConsoleSender(), command);
                }
            });
        });
    }
}
