package anticheat.checks.combat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;

@ChecksListener(events = { ProjectileLaunchEvent.class, PlayerInteractEvent.class, PlayerQuitEvent.class })
public class Fastbow extends Checks {

	public Map<Player, Long> bowPull;
	public Map<Player, Integer> count;

	public Fastbow() {
		super("Fastbow", ChecksType.COMBAT, Exile.getAC(), 5, true, true);

		this.bowPull = new HashMap<Player, Long>();
		this.count = new HashMap<Player, Integer>();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}

		if (event instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent) event;
			Player Player = e.getPlayer();
			if (Player.getItemInHand() != null && Player.getItemInHand().getType().equals(Material.BOW)) {
				this.bowPull.put(Player, System.currentTimeMillis());
			}
		}

		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			if (bowPull.containsKey(e.getPlayer())) {
				bowPull.remove(e.getPlayer());
			}

			if (count.containsKey(e.getPlayer())) {
				count.remove(e.getPlayer());
			}
		}

		if (event instanceof ProjectileLaunchEvent) {
			ProjectileLaunchEvent e = (ProjectileLaunchEvent) event;
			if (e.getEntity() instanceof Arrow) {
				final Arrow arrow = (Arrow) e.getEntity();
				if (arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
					final Player player = (Player) arrow.getShooter();
					if (this.bowPull.containsKey(player)) {
						Long time = System.currentTimeMillis() - this.bowPull.get(player);
						double power = arrow.getVelocity().length();
						Long timeLimit = 300L;
						int Count = 0;
						if (count.containsKey(player)) {
							Count = count.get(player);
						}
						if (power > 2.5 && time < timeLimit) {
							count.put(player, Count + 1);
						} else {
							count.put(player, Count - 1);
						}
						if (Count > 8) {
							User user = Exile.getUserManager().getUser(player.getUniqueId());
							user.setVL(this, user.getVL(this) + 1);
							this.Alert(player, "*");
							count.remove(player);
						}
					}
				}
			}
		}
	}

}
