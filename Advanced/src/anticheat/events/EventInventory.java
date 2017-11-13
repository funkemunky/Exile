package anticheat.events;

import anticheat.Fiona;
import anticheat.checks.movement.NoSlowDown;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventInventory implements Listener {

	@EventHandler
	public void onclose(InventoryCloseEvent e) {
		Fiona.getAC().getchecksmanager().event(e);
		Player p = (Player) e.getPlayer();
		if (NoSlowDown.isininv.contains(p)) {
			NoSlowDown.isininv.remove(p);
		}
	}

	@EventHandler
	public void onopen(InventoryOpenEvent e) {
		Fiona.getAC().getchecksmanager().event(e);
		Player p = (Player) e.getPlayer();
		if (p.getOpenInventory() != null) {
			if (!NoSlowDown.isininv.contains(p)) {
				NoSlowDown.isininv.add(p);
			}
		}
	}

}