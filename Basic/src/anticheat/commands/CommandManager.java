package anticheat.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import anticheat.Exile;
import anticheat.commands.implemented.HelpCommand;
import anticheat.commands.implemented.StatusCommand;
import anticheat.commands.implemented.ToggleAlertCommand;
import anticheat.commands.implemented.ToggleBansCommand;
import anticheat.commands.implemented.ToggleCommand;
import anticheat.commands.implemented.ViolationsCommand;
import anticheat.utils.Color;

public class CommandManager {
	private List<Command> commands = new ArrayList<Command>();

	public void init() {
		addCommand(new ToggleCommand());
		addCommand(new ToggleAlertCommand());
		addCommand(new ToggleBansCommand());
		addCommand(new StatusCommand());
		addCommand(new ViolationsCommand());
		addCommand(new HelpCommand());
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
					if (!(sender instanceof Player)) {
						sender.sendMessage(Color.Red
								+ "This is for players only! Do /exile help to show the commands you can do in console.");
						return;
					}
					Player player = (Player) sender;
					if (player.hasPermission("exile.admin")) {
						Exile.getAC().getGUIManager().openMainGUI(player);
						player.sendMessage(Color.Green + "Opened GUI.");
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
