package anticheat.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import anticheat.Exile;

public class EventPacketMoveEvent implements Listener {
	
	Map<Player, Location> locations;
	
	public EventPacketMoveEvent() {
		this.locations = new HashMap<Player, Location>();
	}
	
	@EventHandler
	public void onTick(TickEvent event) {
		if(event.getType() != TickType.FASTEST) {
			return;
		}
		for(Player online : Bukkit.getOnlinePlayers()) {
			Location location = online.getLocation();
			Location lastLocation = online.getLocation();
			if(this.locations.containsKey(online)) {
				lastLocation = this.locations.get(online);
			} else {
				this.locations.put(online, location);
			}
			
			Bukkit.getPluginManager().callEvent(new PacketedMovementEvent(online, lastLocation, location));
			this.locations.put(online, location);
		}
	}
	
	@EventHandler
	public void onMove(PacketedMovementEvent event) {
		Exile.getAC().getchecksmanager().event(event);
	}

}
