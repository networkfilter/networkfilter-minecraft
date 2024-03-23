package ls.ni.networkfilter.bukkit;

import ls.ni.networkfilter.bukkit.listeners.PlayerJoinListener;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import org.bukkit.plugin.java.JavaPlugin;

public class NetworkFilterBukkitPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        NetworkFilterCommon.init(this.getLogger(), this.getDataFolder());

        // ---

        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }
}
