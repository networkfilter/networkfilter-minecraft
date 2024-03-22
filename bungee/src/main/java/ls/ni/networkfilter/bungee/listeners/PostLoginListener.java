/*
 * MIT License
 *
 * Copyright (c) 2019 Nils Müller
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ls.ni.networkfilter.bungee.listeners;

import ls.ni.networkfilter.bungee.NetworkFilterBungeePlugin;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.NetworkFilterResult;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener {

    private final NetworkFilterBungeePlugin plugin;

    public PostLoginListener(NetworkFilterBungeePlugin plugin) {
        this.plugin = plugin;
    }

    // TODO: make non-destructive and available in config?
//    @EventHandler
//    public void onJoin(PreLoginEvent event) {
//        int connected = 0;
//
//        for (UUID uuid : RedisBungee.getApi().getPlayersOnline()) {
//            InetAddress playerIp = RedisBungee.getApi().getPlayerIp(uuid);
//
//            if (event.getConnection().getAddress().getAddress().toString().equalsIgnoreCase(playerIp.toString()))
//                connected++;
//        }
//
//        if (connected >= 3) {
//            event.setCancelled(true);
//            event.setCancelReason(TextComponent.fromLegacyText("§3§lNetworkFilter §8§l» §7Fehler beim Verbinden. Über deine IP sind bereits 3 Accounts online!"));
//        }
//    }

    @EventHandler
    public void onEvent(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String address = player.getAddress().getAddress().getHostAddress();

        if (player.hasPermission("networkfilter.ignore")) {
            return;
        }

        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            NetworkFilterResult result = NetworkFilterCommon.getInstance().check(address);

            if (result.blocked()) {
                // TODO: put in config
                player.disconnect(TextComponent.fromLegacyText("§3§lNetworkFilter §8§l» §7Fehler beim Verbinden. Melde dich beim Support mit der Id §e" + result.asn() + " (" + result.org() + ")"));
            }
        });
    }
}
