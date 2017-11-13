package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import anticheat.Fiona;

public class EventPlayerRespawn implements Listener {
	
	@EventHandler
	public void respawn(PlayerRespawnEvent event) {
		Fiona.getAC().getchecksmanager().event(event);
	}

}
