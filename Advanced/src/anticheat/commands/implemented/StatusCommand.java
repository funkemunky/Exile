package anticheat.commands.implemented;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import anticheat.Fiona;
import anticheat.commands.Command;
import anticheat.detections.Checks;
import anticheat.detections.ChecksManager;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import net.md_5.bungee.api.ChatColor;

public class StatusCommand extends Command {
	
	public StatusCommand() {
		super("fiona");
	}
	
	public void onCommand(CommandSender sender, String[] args) {
			if (args.length > 0) {
				String subCommand = args[0];
				if (subCommand.equalsIgnoreCase("status")) {
					if(sender.hasPermission("fiona.admin")) {
						sender.sendMessage(Color.Gray + "Loading status...");
						ArrayList<String> bannable = new ArrayList<String>();
						for(Checks check1 : ChecksManager.getDetections()) {
							if(check1.isBannable()) {
								bannable.add(Color.Gray + (check1.getState() ? Color.Green + check1.getName() : Color.Red + check1.getName()).toString() + Color.Gray);
							}
						}
						ArrayList<String> notbannable = new ArrayList<String>();
						for(Checks check1 : ChecksManager.getDetections()) {
							if(!check1.isBannable()) {
								notbannable.add(Color.Gray + (check1.getState() ? Color.Green + check1.getName() : Color.Red + check1.getName()).toString() + Color.Gray);
							}
						}
						sender.sendMessage("");
						sender.sendMessage("");
						sender.sendMessage("");
						sender.sendMessage(Color.Dark_Gray  + Color.Strikethrough + Color.Bold + "----------------------------------");
						sender.sendMessage(Color.Gold + Color.Bold + "Fiona Status:");
						sender.sendMessage("");
						sender.sendMessage(Color.Yellow + "TPS: " + Color.Red + MathUtils.trim(1, Fiona.getAC().getPing().getTPS()));
						sender.sendMessage("");
						sender.sendMessage(Color.Yellow + " Silent Checks: " + Color.Gray + notbannable.toString());
						sender.sendMessage(Color.Yellow + " Bannable Checks: " + Color.Gray + bannable.toString());
						sender.sendMessage(Color.Dark_Gray + Color.Strikethrough +  Color.Bold + "----------------------------------");
						sender.sendMessage("");
					} else {
						sender.sendMessage(Color.Red + "No permission.");
					}
				}
			} else {

				sender.sendMessage(Fiona.getAC().getPrefix() + Color.Red + " Invalid argument!");
			}
	}

}
