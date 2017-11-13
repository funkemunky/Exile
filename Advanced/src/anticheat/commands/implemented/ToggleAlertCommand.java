package anticheat.commands.implemented;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import anticheat.Fiona;
import anticheat.commands.Command;
import anticheat.user.User;
import anticheat.utils.Color;

public class ToggleAlertCommand extends Command {

	public ToggleAlertCommand() {
		super("Fiona");
	}

	public void onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Color.Red + "Only players may use this command.");
			return;
		}
		Player p = (Player) sender;
		User user = Fiona.getUserManager().getUser(p.getUniqueId());
			if (args.length > 0) {
				String subCommand = args[0];
				if (subCommand.equalsIgnoreCase("Alerts")) {
					if(user.isStaff()) {
						if(user.isHasAlerts()) {
							user.setHasAlerts(false);
							p.sendMessage(Fiona.getAC().getPrefix() + ChatColor.RED + " Alerts state set to " + ChatColor.DARK_RED
									+ "false");
						} else {
							user.setHasAlerts(true);
							p.sendMessage(
									Fiona.getAC().getPrefix() + ChatColor.RED + " Alerts state set to " + ChatColor.GREEN + "true");
						}
					} else {
						sender.sendMessage(Color.Red + "No permission.");
					}
				}
			} else {

				p.sendMessage(Fiona.getAC().getPrefix() + ChatColor.RED + " Invalid argument!");
			}
	}
}
