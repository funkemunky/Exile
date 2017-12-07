package anticheat.commands.implemented;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import anticheat.Exile;
import anticheat.commands.Command;
import anticheat.user.User;
import anticheat.utils.Color;

public class ToggleAlertCommand extends Command {

	public ToggleAlertCommand() {
		super("Exile");
	}

	public void onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Color.Red + "You must be a player to run this command.");
			return;
		}
		Player p = (Player) sender;
		User user = Exile.getAC().getUserManager().getUser(p.getUniqueId());
			if (args.length > 0) {
				String subCommand = args[0];
				if (subCommand.equalsIgnoreCase("Alerts")) {
					if(user.isStaff()) {
						if(user.isHasAlerts()) {
							user.setHasAlerts(false);
							p.sendMessage(Exile.getAC().getPrefix() + ChatColor.RED + " Alerts state set to " + ChatColor.DARK_RED
									+ "false");
						} else {
							user.setHasAlerts(true);
							p.sendMessage(
									Exile.getAC().getPrefix() + ChatColor.RED + " Alerts state set to " + ChatColor.GREEN + "true");
						}
					} else {
						sender.sendMessage(Color.Red + "No permission.");
					}
				}
			} else {

				p.sendMessage(Exile.getAC().getPrefix() + ChatColor.RED + " Invalid argument!");
			}
	}
}
