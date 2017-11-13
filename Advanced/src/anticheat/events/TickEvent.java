package anticheat.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class TickEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private TickType type;

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public TickEvent(TickType type) {
		this.type = type;
	}
	
	public TickType getType() {
		return this.type;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
