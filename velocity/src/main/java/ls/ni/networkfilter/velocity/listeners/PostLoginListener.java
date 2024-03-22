package ls.ni.networkfilter.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.NetworkFilterResult;
import ls.ni.networkfilter.velocity.NetworkFilterVelocityPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PostLoginListener {

    private final NetworkFilterVelocityPlugin plugin;

    public PostLoginListener(NetworkFilterVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onEvent(PostLoginEvent event) {
        Player player = event.getPlayer();
        String address = player.getRemoteAddress().getAddress().getHostAddress();

        this.plugin.getServer().getScheduler().buildTask(this.plugin, () -> {
            NetworkFilterResult result = NetworkFilterCommon.getInstance().check(address);

            if (result.blocked()) {
                // TODO: put in config
                player.disconnect(MiniMessage.miniMessage().deserialize("§3§lNetworkFilter §8§l» §7Fehler beim Verbinden. Melde dich beim Support mit der Id §e" + result.asn() + " (" + result.org() + ")"));
            }
        }).schedule();
    }
}
