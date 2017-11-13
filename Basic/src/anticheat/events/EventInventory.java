package anticheat.events;

import anticheat.Exile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class EventInventory implements Listener {

	@EventHandler
	public void onclose(InventoryCloseEvent e) {
		Exile.getAC().getchecksmanager().event(e);
		Player p = (Player) e.getPlayer();
	}

	@EventHandler
	public void onopen(InventoryOpenEvent e) {
		Exile.getAC().getchecksmanager().event(e);
		Player p = (Player) e.getPlayer();
	}

}