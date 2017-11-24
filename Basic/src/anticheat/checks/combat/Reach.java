package anticheat.checks.combat;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.PlayerUtils;

@ChecksListener(events = { EntityDamageByEntityEvent.class })
public class Reach extends Checks {


	public Reach() {
		super("Reach", ChecksType.COMBAT, Exile.getAC(), 11, true, true);

	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
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
				MaxReach += 1.5;
			}
			
			/**
			 * Combined velocity of both players.
			 */
			double velocity = damager.getVelocity().length() + player.getVelocity().length();
			
			
			/**
			 * Adds leniency to reach check based on the combined velocities of both players.
			 */
			MaxReach += velocity * 4.5;
			
			/**
			 * Checks if Reach measured above is greater than the leniency given, then adds a ban VL and flags.
			 */
			
			
			if(Reach > MaxReach) {
				User user = Exile.getUserManager().getUser(damager.getUniqueId());
				user.setVL(this, user.getVL(this) + 1);
				
				this.Alert(damager, Color.Green + Reach + Color.Gray + " > " + Color.Green + MaxReach);
			}
		}
	}
}
