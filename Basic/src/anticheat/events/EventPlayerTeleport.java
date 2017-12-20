package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import anticheat.Exile;
import anticheat.user.User;

public class EventPlayerTeleport implements Listener {
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Exile.getAC().getChecks().event(event);
		
		User user = Exile.getAC().getUserManager().getUser(event.getPlayer().getUniqueId());
		if(user != null) {
			user.setTeleported(System.currentTimeMillis());
		}
	}

}
