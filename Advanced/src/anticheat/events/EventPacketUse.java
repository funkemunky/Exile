package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import anticheat.Exile;
import anticheat.packets.events.PacketUseEntityEvent;

public class EventPacketUse implements Listener {
	
	@EventHandler
	public void onUse(PacketUseEntityEvent e) {
		Exile.getAC().getChecks().event(e);
	}

}
