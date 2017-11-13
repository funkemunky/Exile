package anticheat.events;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Exile;
import anticheat.user.User;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventPlayerAttack implements Listener {

	public static ConcurrentHashMap<Player, String> hasAttacked = new ConcurrentHashMap<>();

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Exile.getAC();
			if (!EventPlayerAttack.hasAttacked.contains(e.getDamager())) {
				Exile.getAC();
				EventPlayerAttack.hasAttacked.put((Player) e.getDamager(), "Has Attacked Entity");
				Bukkit.getScheduler().runTaskLater(Exile.getAC(), new Runnable() {
					@Override
					public void run() {
						Exile.getAC();
						hasAttacked.remove(e.getDamager());
					}
				}, 100);
			}
			Exile.getAC().getChecks().event(e);
			if(e.getEntity() instanceof Player) {
				Player player = (Player) e.getEntity();
				User user = Exile.getUserManager().getUser(player.getUniqueId());
				if(user != null) {
					user.setIsHit(System.currentTimeMillis());
				}
			}
		}
	}

}
