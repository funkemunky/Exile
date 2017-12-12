package anticheat.commands.implemented;

import org.bukkit.command.CommandSender;

import anticheat.Exile;
import anticheat.commands.Command;
import anticheat.utils.Color;

public class HelpCommand extends Command {
	
	public HelpCommand() {
		super("exile");
	}
	
	public void onCommand(CommandSender sender, String[] args) {
		if (args.length > 0) {
			String subCommand = args[0];
			if (subCommand.equalsIgnoreCase("help")) {
				if(sender.hasPermission("exile.staff")) {
					sender.sendMessage(Color.Dark_Gray + Color.Strikethrough + "--------------------------------------------");
					sender.sendMessage(Color.Gold + Color.Bold + "Exile Help:");
					sender.sendMessage("");
					sender.sendMessage(Color.White + "/exile alerts               - " + Color.Gray + "Toggles alerts on/off.");
					sender.sendMessage(Color.White + "/exile toggle <check>  -  " + Color.Gray + "Toggle checks.");
					sender.sendMessage(Color.White + "/exile bans <check>    -" + Color.Gray + "Toggle banning for a check.");
					sender.sendMessage(Color.White + "/exile info                  - " + Color.Gray + "View info on Exile.");
					sender.sendMessage(Color.White + "/exile violations <player> - " + Color.Gray + "See a player's violations.");
					sender.sendMessage(Color.Dark_Gray + Color.Strikethrough + "--------------------------------------------");
				} else {
					sender.sendMessage(Color.Red + "No permission.");
				}
			}
		} else {

			sender.sendMessage(Exile.getAC().getPrefix() + Color.Red + " Invalid argument!");
		}
}
}
