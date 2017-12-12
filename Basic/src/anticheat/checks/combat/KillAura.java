package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import com.comphenix.protocol.wrappers.EnumWrappers;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.events.TickEvent;
import anticheat.events.TickType;
import anticheat.packets.events.PacketKillauraEvent;
import anticheat.packets.events.PacketPlayerType;
import anticheat.packets.events.PacketUseEntityEvent;
import anticheat.user.User;
import anticheat.utils.MiscUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {PacketUseEntityEvent.class, TickEvent.class, PacketKillauraEvent.class})
public class KillAura extends Checks {
	
	public Map<UUID, Map.Entry<Integer, Long>> AimbotTicks;
	public Map<UUID, Double> Differences;
	public Map<UUID, Location> LastLocation;
    public Map<UUID, Location> lastMovementLocation;
	
	public KillAura() {
		super("KillAura", ChecksType.COMBAT, Exile.getAC(), 10, true, true);
		
		this.AimbotTicks = new WeakHashMap<UUID, Map.Entry<Integer, Long>>();
		this.Differences = new WeakHashMap<UUID, Double>();
		this.LastLocation = new WeakHashMap<UUID, Location>();
		this.lastMovementLocation = new WeakHashMap<UUID, Location>();
	}
	
	@Override
	protected void onEvent(Event event) {
		if(!this.getState()) {
			return;
		}
		if(event instanceof PacketUseEntityEvent) {
			PacketUseEntityEvent e = (PacketUseEntityEvent) event;
			
			if(!(e.getAttacked() instanceof Player)) {
				return;
			}
			Player player = e.getAttacker();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			if(MiscUtils.getYawDifference(player.getLocation(), lastMovementLocation.getOrDefault(player.getUniqueId(), player.getLocation())) > 90) {
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player, "Direction");
			}
			
		}
		
		if(event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			if(e.getFrom().getYaw() == e.getTo().getYaw()) {
				return;
			}
			lastMovementLocation.put(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
		}
		if(event instanceof TickEvent) {
			TickEvent e = (TickEvent) event;
			
			if(e.getType() != TickType.SECOND) {
				return;
			}
			
			if(Exile.getAC().getPing().getTPS() < 17) {
				return;
			}
			
			for(Player player : Bukkit.getOnlinePlayers()) {
				User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
				
				if(user.getUsePackets() > user.getSwingPackets()) {
					user.setVL(this, user.getVL(this) + 1);
					
					alert(player, "Invalid Swing");
				}
				user.resetSwingPackets();
				user.resetUsePackets();
			}
		}
		if(event instanceof PacketKillauraEvent) {
			PacketKillauraEvent e = (PacketKillauraEvent) event;
			
			if(Exile.getAC().getPing().getTPS() < 17) {
				return;
			}
			Player player = e.getPlayer();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			if(e.getType() == PacketPlayerType.USE) {
				user.addUsePackets();
			}
			if(e.getType() == PacketPlayerType.ARM_SWING) {
				user.addSwingPackets();
			}
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
			if (!((e.getAttacked()) instanceof Player) && !(e.getAttacked() instanceof Zombie)) {
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
					if (diff < 2.0) {
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
				User user = Exile.getAC().getUserManager().getUser(damager.getUniqueId());
				Count = 0;
				user.setVL(this, user.getVL(this) + 1);
				if(user.getVL(this) > 2) {
					alert(damager, "Aimbot");
					this.advancedalert(damager, 84.3);
				}
			}
			this.AimbotTicks.put(damager.getUniqueId(),
					new AbstractMap.SimpleEntry<Integer, Long>((int) Math.round(Count), Time));
		}
	}

}
