package anticheat.commands.implemented;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import anticheat.Exile;
import anticheat.commands.Command;
import anticheat.detections.Checks;
import anticheat.utils.Color;

public class ToggleCommand extends Command {

	public ToggleCommand() {
		super("Exile");
	}

	public void onCommand(CommandSender sender, String[] args) {
			if (args.length > 1) {
				String subCommand = args[0];
				String CheckName = args[1];
				if (subCommand.equalsIgnoreCase("toggle")) {
					if(sender.hasPermission("Exile.admin")) {
						Checks check = Exile.getAC().getChecks().getCheckByName(CheckName);
						if (check == null) {
							sender.sendMessage(Exile.getAC().getPrefix() + ChatColor.RED + " Check ' " + CheckName + " ' not found.");
							ArrayList<String> list = new ArrayList<String>();
							for(Checks check1 : Exile.getAC().getChecks().getDetections()) {
								list.add(Color.Gray + (check1.getState() ? Color.Green + check1.getName() : Color.Red + check1.getName()).toString() + Color.Gray);
							}
							sender.sendMessage(Exile.getAC().getPrefix() + ChatColor.RED + " Available checks: " + Color.Gray + list.toString());
							return;
						}
						check.toggle();
						sender.sendMessage(Exile.getAC().getPrefix() + ChatColor.RED + check.getName()
						+ " state has been set to " + (check.isBannable() ? Color.Green + check.getState() : check.getState()));
					} else {
						sender.sendMessage(Color.Red + "No permission.");
					}
				}
			} else {
				if(args[0].equalsIgnoreCase("toggle")) {
					sender.sendMessage(Exile.getAC().getPrefix() + ChatColor.RED + " Invalid usage, use /Exile toggle CheckName.");
				}
			}
	}
}