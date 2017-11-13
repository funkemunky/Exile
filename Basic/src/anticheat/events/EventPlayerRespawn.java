package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import anticheat.Exile;

public class EventPlayerRespawn implements Listener {
	
	@EventHandler
	public void respawn(PlayerRespawnEvent event) {
		Exile.getAC().getchecksmanager().event(event);
	}

}
