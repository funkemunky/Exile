package anticheat.checks.movement;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import anticheat.Fiona;
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
	public Map<UUID, Double> velocity;

	public Fly() {
		super("FlyA", ChecksType.MOVEMENT, Fiona.getAC(), 9, true, true);
		this.AscensionTicks = new HashMap<UUID, Map.Entry<Long, Double>>();
		this.velocity = new WeakHashMap<UUID, Double>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}

		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			Player p = e.getPlayer();
			UUID uuid = p.getUniqueId();

			if (this.AscensionTicks.containsKey(uuid)) {
				this.AscensionTicks.remove(uuid);
			}
			if (this.velocity.containsKey(uuid)) {
				this.velocity.remove(uuid);
			}
		}

		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player player = e.getPlayer();
			if (e.getFrom().getY() >= e.getTo().getY()) {
				return;
			}
			if (player.getAllowFlight()) {
				return;
			}
			if (player.getVehicle() != null) {
				return;
			}
			
			User user = Fiona.getUserManager().getUser(player.getUniqueId());

			if (PlayerUtils.isAir(player) && user.getAirTicks() > 20 && Math.abs(e.getFrom().getY() - e.getTo().getY()) < 0.05
					&& player.getNoDamageTicks() == 0.0 && user.getGroundTicks() == 0.0 && !player.hasPotionEffect(PotionEffectType.JUMP)) {
				Alert(player, "Invalid");
				user.setVL(this, user.getVL(this) + 1);
			}

			if (player.getVelocity().length() < velocity.getOrDefault(player.getUniqueId(), -1.0D)) {
				return;
			}

			long Time = System.currentTimeMillis();
			double TotalBlocks = 0.0D;
			if (this.AscensionTicks.containsKey(player.getUniqueId())) {
				Time = ((Long) ((Map.Entry) this.AscensionTicks.get(player.getUniqueId())).getKey()).longValue();
				TotalBlocks = Double.valueOf(
						((Double) ((Map.Entry) this.AscensionTicks.get(player.getUniqueId())).getValue()).doubleValue())
						.doubleValue();
			}
			long MS = System.currentTimeMillis() - Time;
			double OffsetY = MathUtils.offset(MathUtils.getVerticalVector(e.getFrom().toVector()),
					MathUtils.getVerticalVector(e.getTo().toVector()));
			if (OffsetY > 0.0D) {
				TotalBlocks += OffsetY;
			}
			if (MiscUtils.blocksNear(player)) {
				TotalBlocks = 0.0D;
			}
			Location a = player.getLocation().subtract(0.0D, 1.0D, 0.0D);
			if (MiscUtils.blocksNear(a)) {
				TotalBlocks = 0.0D;
			}
			double Limit = 0.5D;
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
						this.Alert(player, ChatColor.GREEN + "Flew up " + MathUtils.trim(2, TotalBlocks) + " blocks.");
					}
					Time = System.currentTimeMillis();
				}
			} else {
				Time = System.currentTimeMillis();
			}
			this.AscensionTicks.put(player.getUniqueId(),
					new AbstractMap.SimpleEntry(Long.valueOf(Time), Double.valueOf(TotalBlocks)));
			if (!player.isOnGround() && !player.getActivePotionEffects().contains(PotionEffectType.POISON)) {
				this.velocity.put(player.getUniqueId(), player.getVelocity().length());
			} else {
				this.velocity.put(player.getUniqueId(), -1.0D);
			}
		}
	}
}