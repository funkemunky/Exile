package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import anticheat.Exile;
import anticheat.packets.events.PacketReadVelocityEvent;

public class EventPacketReadVelocity implements Listener {
	
	@EventHandler
	public void onRead(PacketReadVelocityEvent event) {
		Exile.getAC().getChecks().event(event);
	}

}
