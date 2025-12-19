package ls.ni.networkfilter.bukkit.commands;

import com.google.common.primitives.Ints;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class NetworkFilterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 3 ||
                (!args[0].equalsIgnoreCase("whitelist") && !args[0].equalsIgnoreCase("blacklist")) ||
                (!args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("remove"))) {
            commandSender.sendMessage("§cUsage: /networkfilter whitelist add|remove <asn>");
            commandSender.sendMessage("§cUsage: /networkfilter blacklist add|remove <asn>");
        } else {
            Integer asn = Ints.tryParse(args[2]);

            if (asn == null) {
                commandSender.sendMessage("§cInvalid ASN: " + args[2]);
                return true;
            }

            Config config = NetworkFilterCommon.getConfig();

            if (args[0].equalsIgnoreCase("whitelist")) {
                if (args[1].equalsIgnoreCase("add")) {
                    config.getAsnWhitelist().add(asn);
                    commandSender.sendMessage("§aAdded ASN " + asn + " to whitelist");
                } else {
                    config.getAsnWhitelist().remove(asn);
                    commandSender.sendMessage("§aRemoved ASN " + asn + " from whitelist");
                }
            } else {
                if (args[1].equalsIgnoreCase("add")) {
                    config.getAsnBlacklist().add(asn);
                    commandSender.sendMessage("§aAdded ASN " + asn + " to blacklist");
                } else {
                    config.getAsnBlacklist().remove(asn);
                    commandSender.sendMessage("§aRemoved ASN " + asn + " from blacklist");
                }
            }
        }

        return true;
    }
}
