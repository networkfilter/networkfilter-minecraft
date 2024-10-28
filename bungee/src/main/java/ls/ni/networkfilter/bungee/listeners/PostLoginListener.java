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
import ls.ni.networkfilter.common.util.PlaceholderUtil;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.SocketAddress;

public class PostLoginListener implements Listener {

    private final NetworkFilterBungeePlugin plugin;

    public PostLoginListener(NetworkFilterBungeePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        SocketAddress address = player.getSocketAddress();

        if (player.hasPermission(NetworkFilterCommon.getConfig().getIgnore().getPermission())) {
            NetworkFilterCommon.getInstance().debug("{0} has permission, skip check", player.getName());
            return;
        }

        event.registerIntent(this.plugin);

        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            try {
                NetworkFilterResult result = NetworkFilterCommon.getInstance().check(address);

                if (!result.blocked()) {
                    return;
                }

                if (NetworkFilterCommon.getConfig().getConsequences().getKick().getEnabled()) {
                    String rawMessage = NetworkFilterCommon.getConfig().getConsequences().getKick().getMessage();
                    String message = PlaceholderUtil.replace(rawMessage, result, player.getName(), player.getUniqueId());

                    player.disconnect(TextComponent.fromLegacyText(message));
                }

                for (String rawCommand : NetworkFilterCommon.getConfig().getConsequences().getCommands()) {
                    if (rawCommand.isBlank()) {
                        continue;
                    }

                    String command = PlaceholderUtil.replace(rawCommand, result, player.getName(), player.getUniqueId());
                    this.plugin.getProxy().getPluginManager().dispatchCommand(
                            this.plugin.getProxy().getConsole(), command);
                }

                NetworkFilterCommon.getInstance().sendNotify(result, player.getName(), player.getUniqueId());
            } finally {
                event.completeIntent(this.plugin);
            }
        });
    }
}
