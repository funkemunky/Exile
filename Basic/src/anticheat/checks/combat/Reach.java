package anticheat.checks.combat;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.PlayerUtils;

@ChecksListener(events = { EntityDamageByEntityEvent.class, PlayerQuitEvent.class })
public class Reach extends Checks {

	private Map<UUID, Integer> verbose;

	public Reach() {
		super("Reach", ChecksType.COMBAT, Exile.getAC(), 5, true, true);

		this.verbose = new WeakHashMap<UUID, Integer>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!getState()) {
			return;
		}
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			
			if(verbose.containsKey(e.getPlayer().getUniqueId())) {
				verbose.remove(e.getPlayer().getUniqueId());
			}
		}
		
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (e.getCause() != DamageCause.ENTITY_ATTACK) {
				return;
			}
			if (!(e.getEntity() instanceof Player)) {
				return;
			}
			if (!(e.getDamager() instanceof Player)) {
				return;
			}
			Player damager = (Player) e.getDamager();
			Player player = (Player) e.getEntity();
			
			/**
			 * Gets the verbose count if contains. If not, return 0.
			 */
			int verbose = this.verbose.getOrDefault(damager.getUniqueId(), 0);
			
			/**
			 * Measures reach minus Y Coordinate Difference. Only substracts y difference if greater than 0.5.
			 */
			double yDif = Math.abs(PlayerUtils.getEyeLocation(damager).getY() - PlayerUtils.getEyeLocation(player).getY()) > 0.5 
					? Math.abs(PlayerUtils.getEyeLocation(damager).getY() - PlayerUtils.getEyeLocation(player).getY()) : 0;
			double Reach = MathUtils.trim(2,
					(PlayerUtils.getEyeLocation(damager).distance(player.getEyeLocation()) - 0.32) - yDif);
			double MaxReach = 3.0;

			/**
			 * Checks if server is lagging.
			 */
			if (Exile.getAC().getPing().getTPS() < 17D) {
				return;
			}
			
			/**
			 * Checks if player has flight to prevent false flags.
			 */
			if (damager.getAllowFlight()) {
				return;
			}
			if (player.getAllowFlight()) {
				return;
			}
			
			/**
			 * Adds latency of both players in combat.
			 */
			int Ping = Exile.getAC().getPing().getPing(damager) + Exile.getAC().getPing().getPing(player);
			
			
			/**
			 * Gives leniency based on the combined latency of both players.
			 */
			if(Ping > 50 && Ping <= 100) {
				MaxReach += 0.2;
			} else if(Ping > 100 && Ping <= 150) {
				MaxReach += 0.42;
			} else if(Ping > 150 && Ping <= 200) {
				MaxReach += 0.54;
			} else if(Ping > 200 && Ping <= 250) {
				MaxReach += 0.65;
			} else if(Ping > 250 && Ping <= 350) {
				MaxReach += 0.8;
			} else if(Ping > 350 && Ping <= 400) {
				MaxReach += 1.0;
			} else if(Ping > 400 && Ping <= 550) {
				MaxReach += 1.28;
			} else if(Ping > 550) {
				MaxReach += 1.5 * (Ping * 0.00093);
			}
			
			/**
			 * Combined velocity of both players.
			 */
			double velocity = damager.getVelocity().length() + player.getVelocity().length();
			
			
			/**
			 * Adds leniency to reach check based on the combined velocities of both players.
			 */
			MaxReach += velocity * 2.5;
			Reach-= velocity * 2.0;
			
			/**
			 * Checks if Reach measured above is greater than the leniency given, then adds a ban VL and flags.
			 */
			
			
			if(Reach > MaxReach) {
				verbose+= 2;
				this.advancedalert(player, verbose * 24);
			} else {
				verbose = verbose > 0 ? verbose-- : 0;
			}
			
			if(verbose > 5) {
				User user = Exile.getAC().getUserManager().getUser(damager.getUniqueId());
				user.setVL(this, user.getVL(this) + 1);
				
				alert(damager, Color.Green + Reach + Color.Gray + " > " + Color.Green + MaxReach);
				this.advancedalert(player, 100);
				
				verbose = 0;
			}
			
			this.verbose.put(damager.getUniqueId(), verbose);
		}
	}
}
