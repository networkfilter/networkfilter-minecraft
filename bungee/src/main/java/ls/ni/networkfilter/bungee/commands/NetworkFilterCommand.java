package ls.ni.networkfilter.bungee.commands;

import com.google.common.primitives.Ints;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.config.Config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

public class NetworkFilterCommand extends Command {

    private static final String PREFIX = "§e§lNetworkFilter §8» §r";

    public NetworkFilterCommand() {
        super("networkfilter", "networkfilter.admin", "nf");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 3 ||
                (!args[0].equalsIgnoreCase("whitelist") && !args[0].equalsIgnoreCase("blacklist")) ||
                (!args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("remove"))) {
            commandSender.sendMessage(TextComponent.fromLegacy(PREFIX + "§cUsage: /networkfilter whitelist add|remove <asn>"));
            commandSender.sendMessage(TextComponent.fromLegacy(PREFIX + "§cUsage: /networkfilter blacklist add|remove <asn>"));
        } else {
            Integer asn = Ints.tryParse(args[2]);

            if (asn == null) {
                commandSender.sendMessage(TextComponent.fromLegacy("§cInvalid ASN: " + args[2]));
                return;
            }

            Config config = NetworkFilterCommon.getConfig();

            if (args[0].equalsIgnoreCase("whitelist")) {
                List<Integer> asnWhitelist = config.getAsnWhitelist();

                if (args[1].equalsIgnoreCase("add")) {
                    if (asnWhitelist.contains(asn)) {
                        commandSender.sendMessage(TextComponent.fromLegacy(PREFIX + "§cASN " + asn + " is already in the whitelist"));
                    } else {
                        asnWhitelist.add(asn);
                        commandSender.sendMessage(TextComponent.fromLegacy(PREFIX + "§aAdded ASN " + asn + " to whitelist"));
                    }
                } else {
                    if (asnWhitelist.contains(asn)) {
                        asnWhitelist.remove(asn);
                        commandSender.sendMessage(TextComponent.fromLegacy(PREFIX + "§aRemoved ASN " + asn + " from whitelist"));
                    } else {
                        commandSender.sendMessage(TextComponent.fromLegacy(PREFIX + "§cASN " + asn + " is not in the whitelist"));
                    }
                }
            } else {
                List<Integer> asnBlacklist = config.getAsnBlacklist();

                if (args[1].equalsIgnoreCase("add")) {
                    if (asnBlacklist.contains(asn)) {
                        commandSender.sendMessage(TextComponent.fromLegacy(PREFIX + "§cASN " + asn + " is already in the blacklist"));
                    } else {
                        asnBlacklist.add(asn);
                        commandSender.sendMessage(TextComponent.fromLegacy(PREFIX + "§aAdded ASN " + asn + " to blacklist"));
                    }
                } else {
                    if (asnBlacklist.contains(asn)) {
                        asnBlacklist.remove(asn);
                        commandSender.sendMessage(TextComponent.fromLegacy(PREFIX + "§aRemoved ASN " + asn + " from blacklist"));
                    } else {
                        commandSender.sendMessage(TextComponent.fromLegacy(PREFIX + "§cASN " + asn + " is not in the blacklist"));
                    }
                }
            }
        }
    }

}
