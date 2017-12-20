package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerVelocityEvent;

import anticheat.Exile;
import anticheat.user.User;

public class EventPlayerVelocity implements Listener {

	@EventHandler
	public void onMove(PlayerVelocityEvent event) {
		Exile.getAC().getChecks().event(event);
		
		User user = Exile.getAC().getUserManager().getUser(event.getPlayer().getUniqueId());
		if(user != null) {
			user.setTookVelocity(System.currentTimeMillis());
		}
	}

}
