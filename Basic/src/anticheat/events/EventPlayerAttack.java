package anticheat.events;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Exile;
import anticheat.packets.events.PacketKillauraEvent;
import anticheat.packets.events.PacketPlayerType;
import anticheat.user.User;

public class EventPlayerAttack implements Listener {

	public static ConcurrentHashMap<Player, String> hasAttacked = new ConcurrentHashMap<>();

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
		}
	}

	@EventHandler
	public void onKillaura(PacketKillauraEvent event) {
		Exile.getAC().getChecks().event(event);
	}

}
