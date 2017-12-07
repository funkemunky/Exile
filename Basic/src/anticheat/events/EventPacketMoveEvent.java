package anticheat.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Exile;

public class EventPacketMoveEvent implements Listener {
	
	Map<Player, Location> locations;
	
	public EventPacketMoveEvent() {
		locations = new HashMap<Player, Location>();
		
		new BukkitRunnable() {
			public void run() {
				for(Player online : Bukkit.getOnlinePlayers()) {
					Location location = online.getLocation();
					Location lastLocation = online.getLocation();
					if(locations.containsKey(online)) {
						lastLocation = locations.get(online);
					} else {
						locations.put(online, location);
					}
					
					Bukkit.getPluginManager().callEvent(new PacketedMovementEvent(online, lastLocation, location));
					locations.put(online, location);
				}
			}
		}.runTaskTimer(Exile.getAC(), 0L, 1L);
	}
	
	@EventHandler
	public void onTick(TickEvent event) {
		if(event.getType() != TickType.FASTEST) {
			return;
		}
		for(Player online : Bukkit.getOnlinePlayers()) {
			Location location = online.getLocation();
			Location lastLocation = online.getLocation();
			if(locations.containsKey(online)) {
				lastLocation = EventPacketMoveEvent.this.locations.get(online);
			} else {
				locations.put(online, location);
			}
			
			Bukkit.getPluginManager().callEvent(new PacketedMovementEvent(online, lastLocation, location));
			locations.put(online, location);
		}
	}
	
	@EventHandler
	public void onMove(PacketedMovementEvent event) {
		Exile.getAC().getChecks().event(event);
	}

}
