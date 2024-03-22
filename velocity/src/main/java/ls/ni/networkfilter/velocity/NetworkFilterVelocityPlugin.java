package ls.ni.networkfilter.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.config.Config;
import ls.ni.networkfilter.velocity.listeners.PostLoginListener;
import org.slf4j.Logger;

//@Plugin(
//        id = "networkfiltervelocity",
//        name = "${project.name}",
//        version = "${project.version}#${git.commit.id.describe}",
//        description = "${project.description}",
//        authors = {"nidotls"},
//        url = "https://github.com/nidotls/${project.parent.name}"
//)
public class NetworkFilterVelocityPlugin {

    @Getter
    private final ProxyServer server;

    @Getter
    private final Logger logger;

    @Inject
    public NetworkFilterVelocityPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onEvent(ProxyInitializeEvent event) {
        // TODO: config

        // ---

        // incomplete
        // NetworkFilterCommon.init(this.getLogger(), new Config());

        // ---

        this.server.getEventManager().register(this, new PostLoginListener(this));
    }
}
