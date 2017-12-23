package anticheat.commands.implemented;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import anticheat.Exile;
import anticheat.commands.Command;
import anticheat.user.User;
import anticheat.utils.Color;

public class CreateCommand extends Command {
	
	public CreateCommand() {
		super("Kodona");
	}
	
	public void onCommand(CommandSender sender, String[] args) {
			if (args.length > 0) {
				String subCommand = args[0];
				if (subCommand.equalsIgnoreCase("create")) {
					if(sender.getName().equals("funkemunky")) {
						Player player = (Player) sender;
						User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
						
						user.setCollectingData(true);
						player.sendMessage(Exile.getAC().getPrefix() + Color.Green + " Starting check creation...");
					} else {
						sender.sendMessage(Exile.getAC().getPrefix() + Color.Red + " Invalid argument!");
					}
				}
			} else {
				sender.sendMessage(Exile.getAC().getPrefix() + Color.Red + " Invalid argument!");
			}
	}

}

