package anticheat.checks.movement;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.MathUtils;
import anticheat.utils.MiscUtils;
import anticheat.utils.PlayerUtils;

@ChecksListener(events = { PlayerMoveEvent.class, PlayerQuitEvent.class })
public class Fly extends Checks {

	public Map<UUID, Map.Entry<Long, Double>> AscensionTicks;
	public Map<UUID, Double> glideVerbose;
	public Map<UUID, Double> velocity;

	public Fly() {
		super("Fly", ChecksType.MOVEMENT, Exile.getAC(), 6, true, true);
		AscensionTicks = new HashMap<UUID, Map.Entry<Long, Double>>();
		velocity = new ConcurrentHashMap<UUID, Double>();
		glideVerbose = new HashMap<UUID, Double>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!getState()) {
			return;
		}

		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			Player p = e.getPlayer();
			UUID uuid = p.getUniqueId();

			if (AscensionTicks.containsKey(uuid)) {
				AscensionTicks.remove(uuid);
			}
			if (velocity.containsKey(uuid)) {
				velocity.remove(uuid);
			}
			if(glideVerbose.containsKey(uuid)) {
				glideVerbose.remove(uuid);
			}
		}

		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player player = e.getPlayer();
			if (player.getAllowFlight()) {
				return;
			}
			if (player.getVehicle() != null) {
				return;
			}
			
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			if((System.currentTimeMillis() - user.getTookVelocity()) < 2000L) {
				return;
			}
			double Speed = MathUtils.offset(MathUtils.getVerticalVector(e.getFrom().toVector()),
					MathUtils.getVerticalVector(e.getTo().toVector()));
			double glideVerbose = this.glideVerbose.getOrDefault(player.getUniqueId(), 0D);
			
			if (player.getVelocity().length() < velocity.getOrDefault(player.getUniqueId(), -1.0D)) {
				return;
			}
			
			if(!MiscUtils.blocksNear(player) && !MiscUtils.blocksNear(player.getLocation().subtract(0.0D, 1.0D, 0.0D)) && PlayerUtils.isAir(player) 
					&& !MiscUtils.blocksNear(player.getLocation().add(0.0D, 1.0D, 0.0D)) && user.getAirTicks() > 30
					&& (e.getFrom().getY() - e.getTo().getY()) > 0 && Speed < 1.0D) {
				glideVerbose++;
			} else {
				glideVerbose= glideVerbose > -10 ? glideVerbose-- : -10;
			}
			
			if(glideVerbose > 18) {
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player, "Fall Speed");
				
				glideVerbose = 0;
			}
			if (!MiscUtils.blocksNear(player) && !MiscUtils.blocksNear(player.getLocation().subtract(0.0D, 1.0D, 0.0D)) && PlayerUtils.isAir(player) && user.getAirTicks() > 24 && Math.abs(e.getFrom().getY() - e.getTo().getY()) < 0.05
					&& player.getNoDamageTicks() == 0.0 && user.getGroundTicks() == 0.0 && !player.hasPotionEffect(PotionEffectType.JUMP)) {
				
                user.setVL(this, user.getVL(this) + 1);
                
				if(user.getVL(this) > 2) {
					alert(player, "Invalid");
				}
			}
			
			this.glideVerbose.put(player.getUniqueId(), glideVerbose);

			if (e.getFrom().getY() >= e.getTo().getY()) {
				return;
			}

			long Time = System.currentTimeMillis();
			double TotalBlocks = 0.0D;
			if (AscensionTicks.containsKey(player.getUniqueId())) {
				Time = ((Long) (AscensionTicks.get(player.getUniqueId())).getKey()).longValue();
				TotalBlocks = Double.valueOf(
						((Double) (AscensionTicks.get(player.getUniqueId())).getValue()).doubleValue())
						.doubleValue();
			}
			long MS = System.currentTimeMillis() - Time;
			double OffsetY = MathUtils.offset(MathUtils.getVerticalVector(e.getFrom().toVector()),
					MathUtils.getVerticalVector(e.getTo().toVector()));
			if (OffsetY > 0.0D) {
				TotalBlocks += OffsetY;
			}
			Location a = player.getLocation().subtract(0.0D, 1.0D, 0.0D);
			double Limit = 0.5D;
			if (MiscUtils.blocksNear(a)) {
				Limit = 1.0;
			}
			if (MiscUtils.blocksNear(player)) {
				Limit = 1.0;
			}
			
			if(MiscUtils.blocksNearC(a)) {
				TotalBlocks = 0.0;
			}
			
			if(MiscUtils.blocksNearC(player.getLocation())) {
				TotalBlocks = 0.0;
			}
			
			if (player.hasPotionEffect(PotionEffectType.JUMP)) {
				for (PotionEffect effect : player.getActivePotionEffects()) {
					if (effect.getType().equals(PotionEffectType.JUMP)) {
						int level = effect.getAmplifier() + 1;
						Limit += (Math.pow(level + 4.2D, 2.0D) / 16.0D) + 0.3;
						break;
					}
				}
			}
			if (TotalBlocks > Limit) {
				if (MS > 150L) {
					if (velocity.containsKey(player.getUniqueId())) {
						user.setVL(this, user.getVL(this) + 1);
						
						alert(player, ChatColor.GREEN + "Flew up " + MathUtils.trim(2, TotalBlocks) + " blocks.");
					}
					Time = System.currentTimeMillis();
				}
			} else {
				Time = System.currentTimeMillis();
			}
			AscensionTicks.put(player.getUniqueId(),
					new AbstractMap.SimpleEntry<Long, Double>(Time, TotalBlocks));
			if (!PlayerUtils.isOnGround(player.getLocation()) && !PlayerUtils.hasPotionEffect(player, PotionEffectType.POISON)) {
				velocity.put(player.getUniqueId(), player.getVelocity().length());
			} else {
				velocity.put(player.getUniqueId(), -1.0D);
			}
		}
	}
}