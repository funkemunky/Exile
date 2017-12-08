package anticheat.events;

import anticheat.Exile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class EventInventory implements Listener {

	@EventHandler
	public void onclose(InventoryCloseEvent e) {
		Exile.getAC().getChecks().event(e);
	}

	@EventHandler
	public void onopen(InventoryOpenEvent e) {
		Exile.getAC().getChecks().event(e);
	}

}