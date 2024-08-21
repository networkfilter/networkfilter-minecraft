package ls.ni.networkfilter.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.velocity.listeners.PostLoginListener;

import java.nio.file.Path;
import java.util.logging.Logger;

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

    private final Path dataFolder;

    @Inject
    public NetworkFilterVelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    @Subscribe
    public void onEvent(ProxyInitializeEvent event) {
        NetworkFilterCommon.init(this.logger, this.dataFolder.toFile());

        // ---

        this.server.getEventManager().register(this, new PostLoginListener(this));
    }
}
