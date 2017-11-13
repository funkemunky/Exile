package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import anticheat.Fiona;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketUseEntityEvent;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.Ping;
import anticheat.utils.PlayerUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { PlayerMoveEvent.class, EntityDamageByEntityEvent.class, PacketUseEntityEvent.class,
		PlayerQuitEvent.class })
public class Reach extends Checks {

	public Map<Player, Integer> count;
	public Map<Player, Map.Entry<Double, Double>> offsets;
	public Map<Player, Long> reachTicks;
	public Map<Player, ArrayList<Double>> reachs;
	private ArrayList<Player> projectileHit;

	public Reach() {
		super("Reach", ChecksType.COMBAT, Fiona.getAC(), 11, true, true);

		this.count = new HashMap<Player, Integer>();
		this.offsets = new WeakHashMap<Player, Map.Entry<Double, Double>>();
		this.reachTicks = new HashMap<Player, Long>();
		this.projectileHit = new ArrayList<Player>();
		this.reachs = new HashMap<Player, ArrayList<Double>>();
	}

	private int getPotionEffectLevel(Player p, PotionEffectType pet) {
		for (PotionEffect pe : p.getActivePotionEffects()) {
			if (pe.getType().getName().equals(pet.getName())) {
				return pe.getAmplifier() + 1;
			}
		}
		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}
		if (event instanceof EntityDamageByEntityEvent) {

			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

			//THIS WILL FIX AN ERROR THAT WAS IN CONSOLE
			if (!(e.getEntity() instanceof Player)) {
				return;
			}
			Player damager = (Player) e.getDamager();

			Player damaged = (Player) e.getEntity();

			Ping ping = Fiona.getAC().getPing();
			/**
			 * Checks if the damager and the damage player are players (if they
			 * are not mobs or something else...)
			 */
			if (!(damager instanceof Player)) {
				return;
			}
			if (!(damaged instanceof Player)) {
				return;
			}

			/**
			 * If the damager is in gamemode creative return, since creative
			 * reach is higher then survival reach.
			 */

			if (damager.getGameMode() == GameMode.CREATIVE) {
				return;
			}
			/**
			 * Gets the velocity length of the damaged entity.
			 */
			double velocity = damaged.getVelocity().length() + damager.getVelocity().length();
			/**
			 * The maximum range player can get, 3.0 is the default reach, plus
			 * the velocity multiplied by 4 (in combos).
			 */
			double rangeThreshold = 3.0f + (velocity * 5.0);
			/**
			 * The distance between the damager and the damaged entity.
			 */
			double yDif = Math.abs(PlayerUtils.getEyeLocation(damager).getY() - PlayerUtils.getEyeLocation(damaged).getY());
			double range = MathUtils.trim(2,
					((PlayerUtils.getEyeLocation(damager).distance(damaged.getEyeLocation())) - 0.32) - yDif);

			/**
			 * Checks if the damager have a speed effect.
			 */
			double YawDifference = Math.abs(damager.getEyeLocation().getYaw() - damaged.getEyeLocation().getYaw());
			rangeThreshold += (YawDifference * 0.001);
			;
			if (damager.hasPotionEffect(PotionEffectType.SPEED)) {
				int level = this.getPotionEffectLevel(damager, PotionEffectType.SPEED);
				if (level == 1) {
					/**
					 * Increase the rangeThreshold by 0.1 if the speed level is
					 * 1.
					 */
					rangeThreshold += 0.1;
				} else {
					/**
					 * Increase the rangeThreshold by 0.2 for all other values,
					 * in mc speed level can be either 1 or 2 if you use pots.
					 */
					rangeThreshold += 0.3;
				}
			}

			/**
			 * If the damaged person velocity length is greater then 0.2 add 0.2
			 * to the rangeThreshold.
			 */

			if (damaged.getVelocity().length() >= 0.2D || damager.getVelocity().length() >= 0.2D) {
				rangeThreshold += 0.2;
			}

			/**
			 * If the damaged player is sprinting (Not walking backwards) or if
			 * the damager is 1.7ing the damaged player increase the
			 * rangeThreshold by 2.
			 */

			if ((!damaged.isSprinting())
					|| Math.abs(damaged.getLocation().getYaw() - damager.getLocation().getYaw()) < 90) {
				rangeThreshold += 2;
			}

			/**
			 * Increases the rangeThreshold depending on the player ping to
			 * avoid few false flags.
			 */

			if (ping.getPing(damager) <= 50) {
				rangeThreshold += 0.1;
			} else if (ping.getPing(damager) > 50 && ping.getPing(damager) < 100) {
				rangeThreshold += 0.2;
			} else if (ping.getPing(damager) >= 100 && ping.getPing(damager) < 150) {
				rangeThreshold += 0.3;
			} else if (ping.getPing(damager) >= 150 && ping.getPing(damager) < 200) {
				rangeThreshold += 0.4;
			} else if (ping.getPing(damager) >= 200 && ping.getPing(damager) < 250) {
				rangeThreshold += 0.5;
			} else if (ping.getPing(damager) >= 250 && ping.getPing(damager) < 300) {
				rangeThreshold += 0.6;
			} else if (ping.getPing(damager) >= 300 && ping.getPing(damager) < 350) {
				rangeThreshold += 0.7;
			} else if (ping.getPing(damager) >= 350 && ping.getPing(damager) < 400) {
				rangeThreshold += 0.8;
			} else {
				rangeThreshold += 2.0;
			}

			if (ping.getPing(damaged) <= 50) {
				rangeThreshold += 0.1;
			} else if (ping.getPing(damaged) > 50 && ping.getPing(damaged) < 100) {
				rangeThreshold += 0.2;
			} else if (ping.getPing(damaged) >= 100 && ping.getPing(damaged) < 150) {
				rangeThreshold += 0.3;
			} else if (ping.getPing(damaged) >= 150 && ping.getPing(damaged) < 200) {
				rangeThreshold += 0.4;
			} else if (ping.getPing(damaged) >= 200 && ping.getPing(damaged) < 250) {
				rangeThreshold += 0.5;
			} else if (ping.getPing(damaged) >= 250 && ping.getPing(damaged) < 300) {
				rangeThreshold += 0.6;
			} else if (ping.getPing(damaged) >= 300 && ping.getPing(damaged) < 350) {
				rangeThreshold += 0.7;
			} else if (ping.getPing(damaged) >= 350 && ping.getPing(damaged) < 400) {
				rangeThreshold += 0.8;
			} else {
				rangeThreshold += 2.0;
			}

			/**
			 * To get the damager profile.
			 */
			User user = Fiona.getUserManager().getUser(damager.getUniqueId());

			/**
			 * Get the damager violations.
			 */

			int vl = user.getVL(this);
			if (range >= rangeThreshold) {
				/**
				 * If the range is grater then the rangeThreshold increase the
				 * violation.
				 */
				/**
				 * If the violations is above or equal to 5 violations starts
				 * alerting staff and if he needs a ban, ban him.
				 */
					Alert(damager, Color.Gray + "Reason: " + Color.Green + "Experimental " + Color.Gray + "Ping: " + Color.White + Fiona.getAC().getPing().getPing(damager) +  Color.Gray + " Reach: " + Color.Green
							+ range + Color.Gray + " > " + Color.Green + rangeThreshold);

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
			double yDif = Math.abs(PlayerUtils.getEyeLocation(damager).getY() - PlayerUtils.getEyeLocation(player).getY());
			double Reach = MathUtils.trim(2,
					(PlayerUtils.getEyeLocation(damager).distance(player.getEyeLocation()) - 0.32) - yDif);

			double Difference;

			if (Fiona.getAC().getPing().getTPS() < 17D) {
				return;
			}
			if (damager.getAllowFlight()) {
				return;
			}
			if (player.getAllowFlight()) {
				return;
			}

			if (Fiona.getAC().getPing().getPing(player) > 550 || Fiona.getAC().getPing().getPing(damager) > 550) {
				return;
			}

			if (!count.containsKey(damager)) {
				count.put(damager, 0);
			}

			int Count = count.get(damager);
			long Time = System.currentTimeMillis();
			double MaxReach = 3.0;
			double YawDifference = Math.abs(damager.getEyeLocation().getYaw() - player.getEyeLocation().getYaw());
			double lastHorizontal = 0.0D;
			if (this.offsets.containsKey(damager)) {
				lastHorizontal = ((Double) (this.offsets.get(damager)).getValue()).doubleValue();
			}
			MaxReach += (YawDifference * 0.001);
			MaxReach += lastHorizontal * 1.1;
			if (damager.getLocation().getY() > player.getLocation().getY()) {
				Difference = damager.getLocation().getY() - player.getLocation().getY();
				MaxReach += Difference / 2.5;
			} else if (player.getLocation().getY() > damager.getLocation().getY()) {
				Difference = player.getLocation().getY() - damager.getLocation().getY();
				MaxReach += Difference / 2.5;
			}
			MaxReach += damager.getWalkSpeed() <= 0.2 ? 0 : damager.getWalkSpeed() - 0.2;
			int PingD = Fiona.getAC().getPing().getPing(damager);
			int PingP = Fiona.getAC().getPing().getPing(player);
			MaxReach += ((PingD + PingP) / 2) * 0.0024;
			if (TimerUtils.elapsed(Time, 30000)) {
				count.remove(damager);
				Time = System.currentTimeMillis();
			}
			if (Reach > MaxReach) {
				count.put(damager, Count + 1);
			} else {
				if (Count >= -2) {
					count.put(damager, Count - 1);
				}
			}
			User user = Fiona.getUserManager().getUser(damager.getUniqueId());
			if (Count >= 2 && Reach > MaxReach && Reach < 20.0) {
				count.remove(damager);
				this.Alert(damager, Color.Gray + "Reason: " + Color.Green + "Heuristic " + Color.Gray + "Ping: " + Color.White + Fiona.getAC().getPing().getPing(damager) +  Color.Gray + " Reach: " + Color.Green
						+ Reach + Color.Gray + " > " + Color.Green + MaxReach);
				user.setVL(this, user.getVL(this) + 1);
				return;
			}
		}
		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			if (offsets.containsKey(e.getPlayer())) {
				offsets.remove(e.getPlayer());
			}
			if (count.containsKey(e.getPlayer())) {
				count.remove(e.getPlayer());
			}
			if (reachTicks.containsKey(e.getPlayer())) {
				reachTicks.remove(e.getPlayer());
			}
			if (this.projectileHit.contains(e.getPlayer())) {
				this.projectileHit.remove(e.getPlayer());
			}
		}
		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) {
				return;
			}
			double OffsetXZ = MathUtils.offset(MathUtils.getHorizontalVector(e.getFrom().toVector()),
					MathUtils.getHorizontalVector(e.getTo().toVector()));
			double horizontal = Math.sqrt(Math.pow(e.getTo().getX() - e.getFrom().getX(), 2.0)
					+ Math.pow(e.getTo().getZ() - e.getFrom().getZ(), 2.0));
			this.offsets.put(e.getPlayer(),
					new AbstractMap.SimpleEntry(Double.valueOf(OffsetXZ), Double.valueOf(horizontal)));
		}
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (!(e.getDamager() instanceof Player)) {
				return;
			}
			if (e.getCause() != DamageCause.PROJECTILE) {
				return;
			}

			Player player = (Player) e.getDamager();

			this.projectileHit.add(player);
		}
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if(!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
				return;
			}
			
			if(Fiona.getAC().getPing().getTPS() < 17) {
				return;
			}
			
			Player player = (Player) e.getDamager();
			Player damaged = (Player) e.getEntity();
			
			if(Fiona.getAC().getPing().getPing(player) > 500 || Fiona.getAC().getPing().getPing(damaged) > 500) {
				return;
			}
			
			double Reach = MathUtils.trim(2,
					PlayerUtils.getEyeLocation(player).distance(damaged.getEyeLocation()) - 0.32);
			long lastHit = this.reachTicks.getOrDefault(player, 0L);
			
			ArrayList<Double> reach = new ArrayList<Double>();
			if(this.reachs.containsKey(player)) {
				reach = this.reachs.get(player);
			}
			
			if(TimerUtils.elapsed(lastHit, 1080L)) {
				reach.add(Reach);
			}
			
			if(reach.size() > 7) {
				double Reach2 = 0D;
				
				for(double dub : reach) {
					Reach2+= dub;
				}
				
				Reach2 = Reach2/reach.size();
				
				if(Reach2 > 3.08) {
					User user = Fiona.getUserManager().getUser(player.getUniqueId());
					user.setVL(this, user.getVL(this) + 1);
					
					this.Alert(player, Color.Gray + "Reason: " + Color.Green + "Average " + Color.Gray + "Ping: " + Color.White + Fiona.getAC().getPing().getPing(player) +  Color.Gray + " Reach: " + Color.Green
						+ Reach + Color.Gray + " > " + Color.Green + "3.08");
				}
				reach.clear();
			}
			this.reachTicks.put(player, System.currentTimeMillis());
			this.reachs.put(player, reach);
		}
	}
}
