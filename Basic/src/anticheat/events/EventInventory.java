package anticheat.events;

import org.bukkit.Achievement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import anticheat.Exile;
import anticheat.user.User;

public class EventInventory implements Listener {

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Exile.getAC().getChecks().event(e);
		
		User user = Exile.getAC().getUserManager().getUser(e.getPlayer().getUniqueId());
		
		if(user != null) {
			user.setInventoryOpen(false);
		}
	}
	
	@EventHandler
	public void onPlayerInvOpen(PlayerAchievementAwardedEvent e) {
		if(e.getAchievement() == Achievement.OPEN_INVENTORY) {
			User user = Exile.getAC().getUserManager().getUser(e.getPlayer().getUniqueId());
			e.setCancelled(true);
			user.setInventoryOpen(true);
		}
	}
	
	@EventHandler
	public void onOpen(InventoryOpenEvent e) {
		Exile.getAC().getChecks().event(e);
		
		Exile.getAC().getUserManager().getUser(e.getPlayer().getUniqueId()).setInventoryOpen(true);
	}

}