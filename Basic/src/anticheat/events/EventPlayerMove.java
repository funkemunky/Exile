package anticheat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Exile;
import anticheat.user.User;
import anticheat.utils.PlayerUtils;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventPlayerMove implements Listener {

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Exile.getAC().getChecks().event(event);
		Player p = event.getPlayer();
		User user = Exile.getAC().getUserManager().getUser(p.getUniqueId());

		if (PlayerUtils.isReallyOnground(p)) {
			user.setGroundTicks(user.getGroundTicks() + 1);
			user.setAirTicks(0);
		} else {
			user.setGroundTicks(0);
			user.setAirTicks(user.getAirTicks() + 1);
		}
		
		if(event.isCancelled()) {
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
}