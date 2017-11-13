package anticheat.commands.implemented;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import anticheat.Fiona;
import anticheat.commands.Command;
import anticheat.detections.Checks;
import anticheat.detections.ChecksManager;
import anticheat.utils.Color;

public class ToggleBansCommand extends Command {
	
	public ToggleBansCommand() {
		super("Fiona");
	}
	
	public void onCommand(CommandSender sender, String[] args) {
		if(args.length > 1) {
			String subCommand = args[0];
			String checkName = args[1];
			
			if(subCommand.equalsIgnoreCase("bannable")) {
				if(!sender.hasPermission("fiona.admin")) {
					sender.sendMessage(Color.Red + "No permission.");
					return;
				}
				Checks check = ChecksManager.getCheckByName(checkName);
				if (check == null) {
					sender.sendMessage(Fiona.getAC().getPrefix() + ChatColor.RED + " Check ' " + checkName + " ' not found.");
					ArrayList<String> list = new ArrayList<String>();
					for(Checks check1 : ChecksManager.getDetections()) {
						list.add(Color.Gray + (check1.isBannable() ? Color.Green + check1.getName() : Color.Red + check1.getName()).toString() + Color.Gray);
					}
					sender.sendMessage(Fiona.getAC().getPrefix() + ChatColor.RED + " Bannable Status: " + Color.Gray + list.toString());
					return;
				}
				check.toggleBans();
				sender.sendMessage(Fiona.getAC().getPrefix() + ChatColor.RED + check.getName()
						+ " bannable state has been set to " + (check.isBannable() ? Color.Green + check.isBannable() : check.isBannable()));
			}
			return;
		}
	}

}
