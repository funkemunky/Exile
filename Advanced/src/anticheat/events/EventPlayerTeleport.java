package anticheat.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Exile;
import anticheat.user.User;

public class EventPlayerTeleport implements Listener {
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Exile.getAC().getchecksmanager().event(event);
		
		User user = Exile.getUserManager().getUser(event.getPlayer().getUniqueId());
		user.setTeleported(true);
		
		new BukkitRunnable() {
			public void run() {
				if(user.isTeleported()) {
					user.setTeleported(false);
				}
			}
		}.runTaskLaterAsynchronously(Exile.getAC(), 20L);
	}

}
