package ls.ni.networkfilter.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.NetworkFilterResult;
import ls.ni.networkfilter.common.util.PlaceholderUtil;
import ls.ni.networkfilter.velocity.NetworkFilterVelocityPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.net.InetSocketAddress;
import java.util.Optional;

public class PostLoginListener {

    private final NetworkFilterVelocityPlugin plugin;

    public PostLoginListener(NetworkFilterVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onEvent(PostLoginEvent event) {
        Player player = event.getPlayer();
        InetSocketAddress address = player.getRemoteAddress();

        if (player.hasPermission(NetworkFilterCommon.getConfig().getIgnore().getPermission())) {
            NetworkFilterCommon.getInstance().debug("{0} has permission, skip check", player.getUsername());
            return;
        }

        this.plugin.getServer().getScheduler().buildTask(this.plugin, () -> {
            NetworkFilterResult result = NetworkFilterCommon.getInstance().check(address);

            if (!result.blocked()) {
                return;
            }

            if (NetworkFilterCommon.getConfig().getConsequences().getKick().getEnabled()) {
                String rawMessage = NetworkFilterCommon.getConfig().getConsequences().getKick().getMessage();
                String message = PlaceholderUtil.replace(rawMessage, result, player.getUsername(), player.getUniqueId());

                player.disconnect(MiniMessage.miniMessage().deserialize(message));
            }

            for (String rawCommand : NetworkFilterCommon.getConfig().getConsequences().getCommands()) {
                if (rawCommand.isBlank()) {
                    continue;
                }

                String command = PlaceholderUtil.replace(rawCommand, result, player.getUsername(), player.getUniqueId());
                this.plugin.getServer().getCommandManager().executeAsync(
                        this.plugin.getServer().getConsoleCommandSource(), command);
            }
        }).schedule();
    }
}
