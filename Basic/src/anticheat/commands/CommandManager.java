package anticheat.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import anticheat.Exile;
import anticheat.commands.implemented.StatusCommand;
import anticheat.commands.implemented.ToggleAlertCommand;
import anticheat.commands.implemented.ToggleBansCommand;
import anticheat.commands.implemented.ToggleCommand;
import anticheat.utils.Color;

public class CommandManager {
	private List<Command> commands = new ArrayList<Command>();

	public void init() {
		addCommand(new ToggleCommand());
		addCommand(new ToggleAlertCommand());
		addCommand(new ToggleBansCommand());
		addCommand(new StatusCommand());
	}

	private List<Command> getCommands() {
		return this.commands;
	}

	private void addCommand(Command command) {
		this.getCommands().add(command);
	}

	public void CmdHandler(CommandSender sender, String label, String[] args) {
		for (Command cmd : getCommands()) {
			if (cmd.getString().equalsIgnoreCase(label)) {
				if (args.length < 1) {
					if(sender.hasPermission("exile.staff")) {
						sender.sendMessage(Color.Dark_Gray + Color.Italics + "----------------------------------");
						sender.sendMessage(Color.Gold + Color.Bold + "Exile Help:");
						sender.sendMessage("");
						sender.sendMessage(Color.White + "/exile alerts           - " + Color.Gray + "Toggles alerts on/off.");
						sender.sendMessage(Color.White + "/exile toggle <check> -  " + Color.Gray + "Toggle checks.");
						sender.sendMessage(Color.White + "/exile bans <check>    -" + Color.Gray + "Toggle banning for a check.");
						sender.sendMessage(Color.White + "/exile info            - " + Color.Gray + "View info on Exile.");
						sender.sendMessage(Color.White + "/exile violations <player> - " + Color.Gray + "See a player's violations.");
						sender.sendMessage(Color.Dark_Gray + Color.Italics + "----------------------------------");
					} else {
						sender.sendMessage(Color.Red + "This server is using Exile v" + Exile.getAC().getDescription().getVersion() + " by funkemunky.");
					}
					return;
				}
				cmd.onCommand(sender, args);
			}
		}
	}
}
