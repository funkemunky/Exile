package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import anticheat.Exile;

public class EventProjectileLaunch implements Listener {
	
	@EventHandler
	public void onLaunch(ProjectileLaunchEvent event)  {
		Exile.getAC().getChecks().event(event);
	}

}
