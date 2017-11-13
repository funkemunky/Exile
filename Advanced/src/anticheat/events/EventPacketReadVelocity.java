package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import anticheat.Fiona;
import anticheat.packets.events.PacketReadVelocityEvent;

public class EventPacketReadVelocity implements Listener {
	
	@EventHandler
	public void onRead(PacketReadVelocityEvent event) {
		Fiona.getAC().getchecksmanager().event(event);
	}

}
