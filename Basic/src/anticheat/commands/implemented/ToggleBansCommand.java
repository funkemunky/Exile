package anticheat.commands.implemented;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import anticheat.Exile;
import anticheat.commands.Command;
import anticheat.detections.Checks;
import anticheat.utils.Color;

public class ToggleBansCommand extends Command {
	
	public ToggleBansCommand() {
		super("Kodona");
	}
	
	public void onCommand(CommandSender sender, String[] args) {
		if(args.length > 1) {
			String subCommand = args[0];
			String checkName = args[1];
			
			if(subCommand.equalsIgnoreCase("bans")) {
				if(!sender.hasPermission("Exile.admin")) {
					sender.sendMessage(Color.Red + "No permission.");
					return;
				}
				Checks check = Exile.getAC().getChecks().getCheckByName(checkName);
				if (check == null) {
					sender.sendMessage(Exile.getAC().getPrefix() + ChatColor.RED + " Check ' " + checkName + " ' not found.");
					ArrayList<String> list = new ArrayList<String>();
					for(Checks check1 : Exile.getAC().getChecks().getDetections()) {
						list.add(Color.Gray + (check1.isBannable() ? Color.Green + check1.getName() : Color.Red + check1.getName()).toString() + Color.Gray);
					}
					sender.sendMessage(Exile.getAC().getPrefix() + ChatColor.RED + " Bannable Status: " + Color.Gray + list.toString());
					return;
				}
				check.toggleBans();
				sender.sendMessage(Exile.getAC().getPrefix() + ChatColor.RED + check.getName()
						+ " bannable state has been set to " + (check.isBannable() ? Color.Green + check.isBannable() : check.isBannable()));
			}
			return;
		}
	}

}
