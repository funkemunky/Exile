package anticheat.commands.implemented;

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
		super("Kodona");
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
					sender.sendMessage(Color.Dark_Gray + Color.Strikethrough + "--------------------------------------------");
					User user = Exile.getAC().getUserManager().getUser(target.getUniqueId());
					if(user.getVLs().size() > 0) {
						sender.sendMessage(Color.Gold + Color.Bold + target.getName() + "'s Violations/Info");
						sender.sendMessage("");
						sender.sendMessage(Color.Gray + "Ping: " + Color.White + Exile.getAC().getPing().getPing(target));
						sender.sendMessage("");
						sender.sendMessage(Color.Red + Color.Bold + "Set off:");
						sender.sendMessage("");
						
						for(Checks check : user.getVLs().keySet()) {
							sender.sendMessage(Color.White + "- " + check.getName() + " VL: " + user.getVL(check));
						}
						
					} else {
						sender.sendMessage(Color.Red + "This player set of no checks!");
					}
					sender.sendMessage(Color.Dark_Gray + Color.Strikethrough + "--------------------------------------------");
				} else {
					sender.sendMessage(Color.Red + "No permission.");
				}
			}
		}
	}
 
}
