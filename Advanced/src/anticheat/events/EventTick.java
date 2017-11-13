package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import anticheat.Fiona;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventTick implements Listener {

	@EventHandler
	public void onEvent(TickEvent event) {
		Fiona.getAC().getChecks().event(event);
		
	}
}