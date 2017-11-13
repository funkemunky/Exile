package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import anticheat.Fiona;

public class EventProjectileLaunch implements Listener {
	
	@EventHandler
	public void onLaunch(ProjectileLaunchEvent event)  {
		Fiona.getAC().getchecksmanager().event(event);
	}

}
