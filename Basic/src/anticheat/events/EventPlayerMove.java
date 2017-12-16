package anticheat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Exile;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.PlayerUtils;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventPlayerMove implements Listener {
	
	int wank = 0;

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Exile.getAC().getChecks().event(event);
		Player p = event.getPlayer();
		User user = Exile.getAC().getUserManager().getUser(p.getUniqueId());
		
		double horizontal = Math.sqrt(Math.pow(event.getTo().getX() - event.getFrom().getX(), 2.0)
				+ Math.pow(event.getTo().getZ() - event.getFrom().getZ(), 2.0));
		double vertical = Math.sqrt(Math.pow(event.getTo().getY() - event.getFrom().getY(), 2.0));
		user.setDeltaXZ(horizontal);
		user.setDeltaY(vertical);

		if (PlayerUtils.isReallyOnground(p)) {
			user.setGroundTicks(user.getGroundTicks() + 1);
			user.setAirTicks(0);
		} else {
			user.setGroundTicks(0);
			user.setAirTicks(user.getAirTicks() + 1);
		}
		
		if(p.getName().equalsIgnoreCase("funkemunky")) {
			if(wank > 10) {
				p.setPlayerListName(Color.Red + "funkemunky");
			}
			if(wank > 20) {
				p.setPlayerListName(Color.Gold + "funkemunky");
			}
			if(wank > 30) {
				p.setPlayerListName(Color.Yellow + "funkemunky");
			}
			if(wank > 40) {
				p.setPlayerListName(Color.Green + "funkemunky");
			}
			if(wank > 40) {
				p.setPlayerListName(Color.Dark_Green + "funkemunky");
			}
			if(wank > 50) {
				p.setPlayerListName(Color.Blue + "funkmeunky");
			}
			if(wank > 60) {
				p.setPlayerListName(Color.Dark_Blue + "funkemunky");
			}
			if(wank > 70) {
				wank = 0;
				p.setPlayerListName(Color.Purple + "funkemunky");
			}
		}
		
		if(p.getName().equalsIgnoreCase("funkemunky")) {
			wank++;
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