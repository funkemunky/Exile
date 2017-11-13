package anticheat.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import anticheat.Fiona;
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
					if(sender.hasPermission("fiona.staff")) {
						sender.sendMessage(Fiona.getAC().getPrefix() + ChatColor.RED + "Invalid usage.");
						sender.sendMessage(Fiona.getAC().getPrefix() + ChatColor.RED
								+ "Use /Fiona toggle <CheckName> to enable/disable checks.");
						sender.sendMessage(Fiona.getAC().getPrefix() + ChatColor.RED
								+ "Use /Fiona Alerts on/off to enable/disable alerts.");
						sender.sendMessage(Fiona.getAC().getPrefix() + ChatColor.RED
								+ "Use /Fiona bannable <CheckName> to make a check bannable/silent.");
						sender.sendMessage(Fiona.getAC().getPrefix() + ChatColor.RED
								+ "Use /Fiona status to check the current Fiona status.");
					} else {
						sender.sendMessage(Color.Red + "This server is using Fiona " + Fiona.getAC().getDescription().getVersion() + " by funkemunky and XTasyCode");
					}
					return;
				}
				cmd.onCommand(sender, args);
			}
		}
	}
}
