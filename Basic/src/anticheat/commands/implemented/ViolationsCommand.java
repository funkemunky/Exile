package anticheat.commands.implemented;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import anticheat.Exile;
import anticheat.commands.Command;
import anticheat.detections.Checks;
import anticheat.user.User;
import anticheat.utils.Color;

public class ViolationsCommand extends Command {
	
	public ViolationsCommand() {
		super("Exile");
	}
	
	public void onCommand(CommandSender sender, String[] args) {
		if(args.length > 0) {
			String subCommand = args[0];
			if(subCommand.equalsIgnoreCase("violations")) {
				if(sender.hasPermission("exile.staff")) {
					if(args.length != 2) {
						sender.sendMessage(Color.Red + "Invalid usage. Usage: /exile violations <player>");
						return;
					}
					
					Player target = Bukkit.getPlayer(args[1]);
					
					if(target == null) {
						sender.sendMessage(Color.Red + "That player is not online!");
						return;
					}
					sender.sendMessage(Color.Dark_Gray + Color.Italics + "----------------------------------");
					User user = Exile.getUserManager().getUser(target.getUniqueId());
					if(user.getVLs().size() > 0) {
						sender.sendMessage(Color.Red + Color.Bold + "Set off:");
						sender.sendMessage("");
						
						for(Checks check : user.getVLs().keySet()) {
							sender.sendMessage(Color.White + "- " + check.getName());
						}
						
					} else {
						sender.sendMessage(Color.Red + "This player set of no checks!");
					}
					sender.sendMessage(Color.Dark_Gray + Color.Italics + "----------------------------------");
				} else {
					sender.sendMessage(Color.Red + "No permission.");
				}
			}
		}
	}
 
}
