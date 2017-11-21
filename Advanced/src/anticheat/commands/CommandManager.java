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
						sender.sendMessage(Exile.getAC().getPrefix() + Color.Gray + "Invalid usage.");
						sender.sendMessage(Exile.getAC().getPrefix() + Color.Gray
								+ "Use /exile toggle <CheckName> to enable/disable checks.");
						sender.sendMessage(Exile.getAC().getPrefix() + Color.Gray
								+ "Use /Exile Alerts on/off to enable/disable alerts.");
						sender.sendMessage(Exile.getAC().getPrefix() + Color.Gray
								+ "Use /Exile bannable <CheckName> to make a check bannable/silent.");
						sender.sendMessage(Exile.getAC().getPrefix() + Color.Gray
								+ "Use /Exile status to check the current Exile status.");
					} else {
						sender.sendMessage(Color.Red + "This server is using Exile " + Exile.getAC().getDescription().getVersion() + " by funkemunky.");
					}
					return;
				}
				cmd.onCommand(sender, args);
			}
		}
	}
}
