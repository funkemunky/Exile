package anticheat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.user.User;
import anticheat.utils.Color;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventJoinQuit implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Exile.getAC();
		Exile.getAC().getUserManager().add(new User(p));
		User user = Exile.getAC().getUserManager().getUser(p.getUniqueId());
		user.setLoginMillis(System.currentTimeMillis());
		if(user.isStaff()) {
			if(!user.isHasAlerts()) {
				user.setHasAlerts(true);
				p.sendMessage(Exile.getAC().getPrefix() + Color.Gray + Color.Italics + " Turns on your cheat alerts automatically. Do " + Color.Green + "/Exile alerts " + Color.Gray + Color.Italics + "to toggle them.");
			}
		}
		Exile.getAC().getChecks().event(e);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Exile.getAC();
		Exile.getAC().getUserManager().remove(Exile.getAC().getUserManager().getUser(p.getUniqueId()));
		Exile.getAC().getChecks().event(e);
	}

}
