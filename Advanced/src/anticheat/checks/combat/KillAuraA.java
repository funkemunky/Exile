package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.wrappers.EnumWrappers;

import anticheat.Fiona;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketUseEntityEvent;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MiscUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {EntityDamageByEntityEvent.class, PacketUseEntityEvent.class, PlayerMoveEvent.class, PlayerDeathEvent.class, PlayerQuitEvent.class})
public class KillAuraA extends Checks {
	public Map<UUID, Long> LastMS;
	public Map<UUID, Player> lastHit;
	public Map<UUID, List<Long>> Clicks;
	public Map<UUID, Map.Entry<Integer, Long>> ClickTicks;
	public Map<UUID, Map.Entry<Integer, Long>> AimbotTicks;
	public Map<UUID, Double> Differences;
	public Map<UUID, Location> LastLocation;
	public Map<UUID, Integer> count;
	public Map<UUID, Double> yawDif;

	public KillAuraA() {
		super("KillAura", ChecksType.COMBAT,  Fiona.getAC(), 12, true, false);
		this.LastMS = new HashMap<UUID, Long>();
		this.Clicks = new HashMap<UUID, List<Long>>();
		this.ClickTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.AimbotTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.Differences = new HashMap<UUID, Double>();
		this.LastLocation = new HashMap<UUID, Location>();
		this.lastHit = new WeakHashMap<UUID, Player>();
		this.count = new HashMap<UUID, Integer>();
		this.yawDif = new HashMap<UUID, Double>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (e.getCause() != DamageCause.ENTITY_ATTACK) {
				return;
			}
			if (!((e.getEntity() instanceof Player)) || !(e.getDamager() instanceof Player)) {
				return;
			}
			Player player = (Player) e.getDamager();
			Player attacked = (Player) e.getEntity();
			
			if(Fiona.getAC().getPing().getPing(player) > 220 || Fiona.getAC().getPing().getPing(attacked) > 350) {
				return;
			}

			int Count = 0;
			double yawDif = 0;
			Player lastPlayer = attacked;

			if (this.lastHit.containsKey(player.getUniqueId())) {
				lastPlayer = this.lastHit.get(player.getUniqueId());
			}

			if (count.containsKey(player.getUniqueId())) {
				Count = count.get(player.getUniqueId());
			}
			if (this.yawDif.containsKey(player.getUniqueId())) {
				yawDif = this.yawDif.get(player.getUniqueId());
			}

			if (lastPlayer == attacked) {
				double offset = MiscUtils.getOffsetOffCursor(player, attacked);
				double Limit = 104D;
				double distance = player.getLocation().distance(attacked.getLocation());
				Limit += distance > 4.0 ? distance * 90 : distance * 40;
				Limit += (attacked.getVelocity().length() + player.getVelocity().length()) * 66;
				Limit += Math.abs(yawDif - Math.abs(player.getLocation().getY() - attacked.getLocation().getY())) * 10;

				if (offset > Limit) {
					Count++;
				} else {
					Count = Count >= -3 ? Count-- : 0;
				}

				if (Count > 2) {
					this.Alert(player, Color.Gray + "Reason: " + Color.Green + "Hitboxes " + Color.Gray + "Ping: " + Fiona.getAC().getPing().getPing(player));
					User user = Fiona.getUserManager().getUser(player.getUniqueId());
					user.setVL(this, user.getVL(this) + 1);
					Count = 0;
				}

				this.count.put(player.getUniqueId(), Count);
				this.lastHit.put(player.getUniqueId(), attacked);
			}
		}
		if(event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			double yawDif = Math.abs(e.getFrom().getYaw() - e.getTo().getYaw()); 
			this.yawDif.put(e.getPlayer().getUniqueId(), yawDif);
		}
		if (event instanceof PacketUseEntityEvent) {
			PacketUseEntityEvent e = (PacketUseEntityEvent) event;
			if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
				return;
			}
			final Player damager = e.getAttacker();
			int Count = 0;
			long Time = System.currentTimeMillis();
			if (this.ClickTicks.containsKey(damager.getUniqueId())) {
				Count = this.ClickTicks.get(damager.getUniqueId()).getKey();
				Time = this.ClickTicks.get(damager.getUniqueId()).getValue();
			}
			if (this.LastMS.containsKey(damager.getUniqueId())) {
				final long MS = TimerUtils.nowlong() - this.LastMS.get(damager.getUniqueId());
				if (MS > 500L || MS < 5L) {
					this.LastMS.put(damager.getUniqueId(), TimerUtils.nowlong());
					return;
				}
				if (this.Clicks.containsKey(damager.getUniqueId())) {
					final List<Long> Clicks = this.Clicks.get(damager.getUniqueId());
					if (Clicks.size() == 10) {
						this.Clicks.remove(damager.getUniqueId());
						Collections.sort(Clicks);
						final long Range = Clicks.get(Clicks.size() - 1) - Clicks.get(0);
						if (Range < 30L) {
							++Count;
							Time = System.currentTimeMillis();
						}
					} else {
						Clicks.add(MS);
						this.Clicks.put(damager.getUniqueId(), Clicks);
					}
				} else {
					final List<Long> Clicks = new ArrayList<Long>();
					Clicks.add(MS);
					this.Clicks.put(damager.getUniqueId(), Clicks);
				}
			}
			if (this.ClickTicks.containsKey(damager.getUniqueId()) && TimerUtils.elapsed(Time, 5000L)) {
				Count = 0;
				Time = TimerUtils.nowlong();
			}
			if ((Count > 0 && Fiona.getAC().getPing().getPing(damager) < 100) || (Count > 2 && Fiona.getAC().getPing().getPing(damager) < 200)
					|| (Count > 4 && Fiona.getAC().getPing().getPing(damager) > 200)) {
				Count = 0;
				this.Alert(damager, Color.Gray + "Reason: " + Color.Green + "Patterns " + Color.Gray + "Ping: " + Fiona.getAC().getPing().getPing(damager));
				User user = Fiona.getUserManager().getUser(damager.getUniqueId());
				user.setVL(KillAuraA.this, user.getVL(KillAuraA.this) + 1);
				ClickTicks.remove(damager.getUniqueId());
			}
			this.LastMS.put(damager.getUniqueId(), TimerUtils.nowlong());
			this.ClickTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
		}
		if (event instanceof PacketUseEntityEvent) {
			PacketUseEntityEvent e = (PacketUseEntityEvent) event;
			if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
				return;
			}
			Player damager = e.getAttacker();
			if (damager.getAllowFlight()) {
				return;
			}
			if (!((e.getAttacked()) instanceof Player)) {
				return;
			}
			Location from = null;
			Location to = damager.getLocation();
			if (this.LastLocation.containsKey(damager.getUniqueId())) {
				from = this.LastLocation.get(damager.getUniqueId());
			}
			this.LastLocation.put(damager.getUniqueId(), damager.getLocation());
			double Count = 0;
			long Time = System.currentTimeMillis();
			double LastDifference = -111111.0;
			if (this.Differences.containsKey(damager.getUniqueId())) {
				LastDifference = this.Differences.get(damager.getUniqueId());
			}
			if (this.AimbotTicks.containsKey(damager.getUniqueId())) {
				Count = this.AimbotTicks.get(damager.getUniqueId()).getKey();
				Time = this.AimbotTicks.get(damager.getUniqueId()).getValue();
			}
			if (from == null || (to.getX() == from.getX() && to.getZ() == from.getZ())) {
				return;
			}
			double Difference = Math.abs(to.getYaw() - from.getYaw());
			if (Difference == 0.0) {
				return;
			}

			if (Difference > 2.4) {
				double diff = Math.abs(LastDifference - Difference);
				if (e.getAttacked().getVelocity().length() < 0.2) {
					if (diff < 1.2) {
						Count += 1;
					} else {
						Count = 0;
					}
				} else {
					if (diff < 1.6) {
						Count += 1;
					} else {
						Count = 0;
					}
				}
			}
			this.Differences.put(damager.getUniqueId(), Difference);
			if (this.AimbotTicks.containsKey(damager.getUniqueId()) && TimerUtils.elapsed(Time, 5000L)) {
				Count = 0;
				Time = TimerUtils.nowlong();
			}
			if (Count >= 4) {
				Count = 0;
				this.Alert(damager, Color.Gray + "Reason: " + Color.Green + "Aimbot " + Color.Gray + "Ping: " + Fiona.getAC().getPing().getPing(damager));
			}
			this.AimbotTicks.put(damager.getUniqueId(),
					new AbstractMap.SimpleEntry<Integer, Long>((int) Math.round(Count), Time));
		}
		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			Player p = e.getPlayer();
			UUID uuid = p.getUniqueId();

			if (LastMS.containsKey(uuid)) {
				LastMS.remove(uuid);
			}
			if (Clicks.containsKey(uuid)) {
				Clicks.remove(uuid);
			}
			if (ClickTicks.containsKey(uuid)) {
				ClickTicks.remove(uuid);
			}
			if (AimbotTicks.containsKey(uuid)) {
				AimbotTicks.remove(e.getPlayer().getUniqueId());
			}
			if (Differences.containsKey(uuid)) {
				Differences.remove(e.getPlayer().getUniqueId());
			}
			if (LastLocation.containsKey(uuid)) {
				LastLocation.remove(e.getPlayer().getUniqueId());
			}
			if(this.count.containsKey(uuid)) {
				this.count.remove(uuid);
			}
			if(this.lastHit.containsKey(uuid)) {
				this.lastHit.remove(uuid);
			}
		}
		if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			Player p = e.getEntity();
			UUID uuid = p.getUniqueId();
			if (LastMS.containsKey(uuid)) {
				LastMS.remove(uuid);
			}
			if (Clicks.containsKey(uuid)) {
				Clicks.remove(uuid);
			}
			if (ClickTicks.containsKey(uuid)) {
				ClickTicks.remove(uuid);
			}
			if (AimbotTicks.containsKey(uuid)) {
				AimbotTicks.remove(uuid);
			}
			if (Differences.containsKey(uuid)) {
				Differences.remove(uuid);
			}
			if (LastLocation.containsKey(uuid)) {
				LastLocation.remove(uuid);
			}
		}
	}
}