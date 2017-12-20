package anticheat.checks.combat;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketUseEntityEvent;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.PlayerUtils;

@ChecksListener(events = { EntityDamageByEntityEvent.class, PlayerQuitEvent.class })
public class Reach extends Checks {

	public Map<Player, Integer> bCount;
	public Map<Player, Integer> useCount;

	public Reach() {
		super("Reach", ChecksType.COMBAT, Exile.getAC(), 3, true, true);

		this.bCount = new WeakHashMap<Player, Integer>();
		this.useCount = new WeakHashMap<Player, Integer>();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}
		
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
	        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player))
	            return;

	        Player attacker = (Player) e.getDamager();

	        Player attacked = (Player) e.getEntity();
	        if (attacker.getGameMode() == GameMode.CREATIVE) return;

	        float attackerPing = Exile.getAC().getPing().getPing(attacker);
	        float attackedPing = Exile.getAC().getPing().getPing(attacked);

	        double distance = MathUtils.trim(3, attacker.getLocation().distance(attacked.getLocation()));

	        double YawDifference = Math.abs(180 - Math.abs(attacker.getLocation().getYaw() - attacked.getLocation().getYaw()));

	        float max = 3.0F;


	        max += ((attackedPing + attackerPing) / 2) * 0.0024;

	        User attackerData = Exile.getAC().getUserManager().getUser(attacker.getUniqueId());
	        User attackedData = Exile.getAC().getUserManager().getUser(attacked.getUniqueId());

	        double deltaXCombined = attackedData != null ? Math.abs(attackedData.getDeltaXZ2()) + Math.abs(attackerData.getDeltaXZ2()) : Math.abs(attackerData.getDeltaXZ2());
	        double deltaY = attackedData != null ? attackedData.getDeltaY2() : 0;
	        double combinedDeltas = (deltaXCombined + deltaY) * 2.0;

	        max += combinedDeltas;
	        max += YawDifference > 100  && deltaY < 0.1 ? YawDifference * 0.01 : YawDifference * 0.001;
	        max += deltaXCombined > 0.2 ? (attackerData.getHits() < 4 ? (attackerData.getHits() <= 2 && attackerData.getHits() > 0 ? attackerData.getHits() * 0.36 : attackerData.getHits() * 0.105) : 4 * 0.105) : 0;

	        for (PotionEffect potionEffect : attacked.getActivePotionEffects()) {
	            if (potionEffect.getType().getId() == PotionEffectType.SPEED.getId()) {
	                int amplifier = potionEffect.getAmplifier() + 1;
	                max += 0.15 * amplifier;
	                break;
	            }
	        }

	        for (PotionEffect potionEffect : attacker.getActivePotionEffects()) {
	            if (potionEffect.getType().getId() == PotionEffectType.SPEED.getId()) {
	                int amplifier = potionEffect.getAmplifier() + 1;
	                max += 0.15 * amplifier;
	                break;
	            }
	        }
	        attackerData.setReachVL(distance >= max ? attackerData.getReachVL() + 1 : attackerData.getReachVL() > 0 ? (distance >= 3.0 ? attackerData.getReachVL() - 0.25D : attackerData.getReachVL()) : attackerData.getReachVL());
	        //if(distance >= max) { 
	        	    //debug("Damager: " + attacker.getName() + " Count: " + attackerData.getReachVL() + " Hits: " + attackerData.getHits() + " Reach: " + distance + " Max: " + max);
	       // }
	        if(attackerData.getReachVL() > 5) {
	        	    attackerData.setVL(this, attackerData.getVL(this) + 1);
	        	    attackerData.setReachVL(0);
	        	    alert(attacker,
							Color.Gray + "Reason: " + Color.White + "Counted " + Color.Gray + "Ping: " + Color.White
									+ attackerPing + Color.Gray + " Reach: " + Color.White
									+ distance + Color.Gray + " > " + Color.White + max);
	        }
		}
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

			if (e.getCause() != DamageCause.ENTITY_ATTACK) {
				return;
			}
			if (!(e.getDamager() instanceof Player)) {
				return;
			}
			if (!(e.getEntity() instanceof Player)) {
				return;
			}

			Player damager = (Player) e.getDamager();
			Player player = (Player) e.getEntity();
			double yDif = Math
					.abs(PlayerUtils.getEyeLocation(damager).getY() - PlayerUtils.getEyeLocation(player).getY());
			double Reach = MathUtils.trim(2,
					(PlayerUtils.getEyeLocation(damager).distance(player.getEyeLocation()) - 0.32) - yDif);

			if (Exile.getAC().getPing().getTPS() < 17D) {
				return;
			}
			if (damager.getAllowFlight()) {
				return;
			}
			if (player.getAllowFlight()) {
				return;
			}

			if (Exile.getAC().getPing().getPing(player) > 550 || Exile.getAC().getPing().getPing(damager) > 550) {
				return;
			}

			if (!bCount.containsKey(damager)) {
				bCount.put(damager, 0);
			}

			int Count = bCount.get(damager);

			double MaxReach = 3.0D;
			double YawDifference = Math
					.abs(180 - Math.abs(damager.getLocation().getYaw() - player.getLocation().getYaw()));
			User user = Exile.getAC().getUserManager().getUser(damager.getUniqueId());
			double lastHorizontal = user.getDeltaXZ2();

			MaxReach += YawDifference > 100 && player.getVelocity().getY() < 0.2 ? YawDifference * 0.01
					: YawDifference * 0.001;
			MaxReach += lastHorizontal * 1.1;

			MaxReach += damager.getWalkSpeed() <= 0.2 ? 0 : damager.getWalkSpeed() - 0.2;
			int PingD = Exile.getAC().getPing().getPing(damager);
			int PingP = Exile.getAC().getPing().getPing(player);
			MaxReach += ((PingD + PingP) / 2) * 0.0024;

			if (Reach > MaxReach) {
				bCount.put(damager, Count + 1);
			} else {
				if (Count > -2) {
					bCount.put(damager, Count - 1);
				}
			}
			if (Count > 6 && Reach > MaxReach) {
				bCount.remove(damager);
				alert(damager,
						Color.Gray + "Reason: " + Color.White + "Ogre " + Color.Gray + "Ping: " + Color.White
								+ Exile.getAC().getPing().getPing(damager) + Color.Gray + " Reach: " + Color.White
								+ Reach + Color.Gray + " > " + Color.White + MaxReach);
				user.setVL(this, user.getVL(this) + 1);
				return;
			}
		}
		if (event instanceof PacketUseEntityEvent) {
			PacketUseEntityEvent e = (PacketUseEntityEvent) event;

			if (e.getAction() != EntityUseAction.ATTACK) {
				return;
			}

			Player player = e.getAttacker();
			Entity damaged = e.getAttacked();
			double yDif = Math.abs(PlayerUtils.getEyeLocation(player).getY()
					- damaged.getLocation().clone().add(0.0D, 1.0D, 0.0D).getY());
			double Reach = MathUtils.trim(2,
					(PlayerUtils.getEyeLocation(player).distance(damaged.getLocation().clone().add(0.0D, 1.0D, 0.0D))
							- 0.32) - yDif);

			if (Exile.getAC().getPing().getPing(player) > 425) {
				return;
			}
			
			if(e.getAttacked() instanceof Player) {
				if(Exile.getAC().getPing().getPing((Player) e.getAttacked()) > 425) {
					return;
				}
			}
			
			if(Exile.getAC().getPing().getTPS() < 17) {
				return;
			}
			
			int verbose = this.useCount.getOrDefault(player, 0);
			
			if(Reach > 6.0) {
				verbose++;
			}
			
			if(verbose > 5) {
				User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
				
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player,
						Color.Gray + "Reason: " + Color.White + "Blatant " + Color.Gray + "Ping: " + Color.White
								+ Exile.getAC().getPing().getPing(player) + Color.Gray + " Reach: " + Color.White
								+ Reach + Color.Gray + " > " + Color.White + "6.0");
			}

		}
		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			if (bCount.containsKey(e.getPlayer())) {
				bCount.remove(e.getPlayer());
			}
			if(useCount.containsKey(e.getPlayer())) {
				useCount.remove(e.getPlayer());
			}
		}
	}
}