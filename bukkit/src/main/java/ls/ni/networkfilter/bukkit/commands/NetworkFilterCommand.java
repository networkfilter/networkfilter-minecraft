package ls.ni.networkfilter.bukkit.commands;

import com.google.common.primitives.Ints;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
                List<Integer> asnWhitelist = config.getAsnWhitelist();

                if (args[1].equalsIgnoreCase("add")) {
                    if (asnWhitelist.contains(asn)) {
                        commandSender.sendMessage("§cASN " + asn + " is already in the whitelist");
                    } else {
                        asnWhitelist.add(asn);
                        commandSender.sendMessage("§aAdded ASN " + asn + " to whitelist");
                    }
                } else {
                    if (asnWhitelist.contains(asn)) {
                        asnWhitelist.remove(asn);
                        commandSender.sendMessage("§aRemoved ASN " + asn + " from whitelist");
                    } else {
                        commandSender.sendMessage("§cASN " + asn + " is not in the whitelist");
                    }
                }
            } else {
                List<Integer> asnBlacklist = config.getAsnBlacklist();

                if (args[1].equalsIgnoreCase("add")) {
                    if (asnBlacklist.contains(asn)) {
                        commandSender.sendMessage("§cASN " + asn + " is already in the blacklist");
                    } else {
                        asnBlacklist.add(asn);
                        commandSender.sendMessage("§aAdded ASN " + asn + " to blacklist");
                    }
                } else {
                    if (asnBlacklist.contains(asn)) {
                        asnBlacklist.remove(asn);
                        commandSender.sendMessage("§aRemoved ASN " + asn + " from blacklist");
                    } else {
                        commandSender.sendMessage("§cASN " + asn + " is not in the blacklist");
                    }
                }
            }
        }

        return true;
    }
}
