package anticheat.commands.implemented;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import anticheat.Exile;
import anticheat.commands.Command;
import anticheat.detections.Checks;
import anticheat.detections.ChecksManager;
import anticheat.utils.Color;

public class StatusCommand extends Command {
	
	public StatusCommand() {
		super("Exile");
	}
	
	public void onCommand(CommandSender sender, String[] args) {
			if (args.length > 0) {
				String subCommand = args[0];
				if (subCommand.equalsIgnoreCase("info")) {
					if(sender.hasPermission("Exile.admin")) {
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
						sender.sendMessage(Color.Dark_Gray  + Color.Strikethrough + Color.Bold + "----------------------------------");
						sender.sendMessage(Color.Gold + Color.Bold + "Exile Information:");
						sender.sendMessage("");
						sender.sendMessage(Color.Gray + " Silent Checks: " + Color.White + notbannable.toString());
						sender.sendMessage(Color.Gray + " Bannable Checks: " + Color.White + bannable.toString());
						sender.sendMessage(Color.Dark_Gray + Color.Strikethrough +  Color.Bold + "----------------------------------");
						sender.sendMessage("");
					} else {
						sender.sendMessage(Color.Red + "No permission.");
					}
				}
			} else {

				sender.sendMessage(Exile.getAC().getPrefix() + Color.Red + " Invalid argument!");
			}
	}

}
