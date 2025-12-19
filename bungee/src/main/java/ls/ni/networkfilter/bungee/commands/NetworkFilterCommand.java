package ls.ni.networkfilter.bungee.commands;

import com.google.common.primitives.Ints;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.config.Config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class NetworkFilterCommand extends Command {

    public NetworkFilterCommand() {
        super("networkfilter", "networkfilter.admin", "nf");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 3 ||
                (!args[0].equalsIgnoreCase("whitelist") && !args[0].equalsIgnoreCase("blacklist")) ||
                (!args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("remove"))) {
            commandSender.sendMessage(TextComponent.fromLegacy("§cUsage: /networkfilter whitelist add|remove <asn>"));
            commandSender.sendMessage(TextComponent.fromLegacy("§cUsage: /networkfilter blacklist add|remove <asn>"));
        } else {
            Integer asn = Ints.tryParse(args[2]);

            if (asn == null) {
                commandSender.sendMessage(TextComponent.fromLegacy("§cInvalid ASN: " + args[2]));
                return;
            }

            Config config = NetworkFilterCommon.getConfig();

            if (args[0].equalsIgnoreCase("whitelist")) {
                if (args[1].equalsIgnoreCase("add")) {
                    config.getAsnWhitelist().add(asn);
                    commandSender.sendMessage(TextComponent.fromLegacy("§aAdded ASN " + asn + " to whitelist"));
                } else {
                    config.getAsnWhitelist().remove(asn);
                    commandSender.sendMessage(TextComponent.fromLegacy("§aRemoved ASN " + asn + " from whitelist"));
                }
            } else {
                if (args[1].equalsIgnoreCase("add")) {
                    config.getAsnBlacklist().add(asn);
                    commandSender.sendMessage(TextComponent.fromLegacy("§aAdded ASN " + asn + " to blacklist"));
                } else {
                    config.getAsnBlacklist().remove(asn);
                    commandSender.sendMessage(TextComponent.fromLegacy("§aRemoved ASN " + asn + " from blacklist"));
                }
            }
        }
    }

}
