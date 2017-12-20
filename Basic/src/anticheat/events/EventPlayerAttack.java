package anticheat.events;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Exile;
import anticheat.packets.events.PacketKillauraEvent;
import anticheat.user.User;

public class EventPlayerAttack implements Listener {

	public static ConcurrentHashMap<Player, String> hasAttacked = new ConcurrentHashMap<>();
	private Map<Player, Long> lastHit = new WeakHashMap<Player, Long>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCombat(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player player = (Player) e.getDamager();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			user.setLastPlayer(player);
		}
	}
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (!EventPlayerAttack.hasAttacked.contains(e.getDamager())) {

				EventPlayerAttack.hasAttacked.put((Player) e.getDamager(), "Has Attacked Entity");
				new BukkitRunnable() {
					public void run() {
						hasAttacked.remove(e.getDamager());
					}
				}.runTaskLaterAsynchronously(Exile.getAC(), 100L);
			}
			Exile.getAC().getChecks().event(e);
			if (e.getEntity() instanceof Player) {
				Player player = (Player) e.getEntity();
				User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
				if (user != null) {
					user.setIsHit(System.currentTimeMillis());
				}
			}
			User user = Exile.getAC().getUserManager().getUser(((Player) e.getDamager()).getUniqueId());

			if ((System.currentTimeMillis() - lastHit.getOrDefault((Player) e.getDamager(), 0L)) > 1000L) {
				user.resetHits();
			} else {
				user.addHit();
			}
			lastHit.put((Player) e.getDamager(), System.currentTimeMillis());
			user.setAttackTime(System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onKillaura(PacketKillauraEvent event) {
		Exile.getAC().getChecks().event(event);
	}

}