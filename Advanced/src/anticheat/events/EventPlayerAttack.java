package anticheat.events;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Fiona;
import anticheat.user.User;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventPlayerAttack implements Listener {

	public static ConcurrentHashMap<Player, String> hasAttacked = new ConcurrentHashMap<>();

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Fiona.getAC();
			if (!EventPlayerAttack.hasAttacked.contains(e.getDamager())) {
				Fiona.getAC();
				EventPlayerAttack.hasAttacked.put((Player) e.getDamager(), "Has Attacked Entity");
				Bukkit.getScheduler().runTaskLater(Fiona.getAC(), new Runnable() {
					@Override
					public void run() {
						Fiona.getAC();
						hasAttacked.remove(e.getDamager());
					}
				}, 100);
			}
			Fiona.getAC().getChecks().event(e);
			if(e.getEntity() instanceof Player) {
				Player player = (Player) e.getEntity();
				User user = Fiona.getUserManager().getUser(player.getUniqueId());
				if(user != null) {
					user.setIsHit(System.currentTimeMillis());
				}
			}
		}
	}

}
