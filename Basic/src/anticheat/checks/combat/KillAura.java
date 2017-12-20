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
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.wrappers.EnumWrappers;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketKillauraEvent;
import anticheat.packets.events.PacketPlayerType;
import anticheat.packets.events.PacketUseEntityEvent;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {PacketUseEntityEvent.class, PlayerMoveEvent.class, PacketKillauraEvent.class
		, PlayerQuitEvent.class})
public class KillAura extends Checks {
	
	private Map<Player, Long> directionHit;
	public Map<UUID, List<Long>> Clicks;
	public Map<UUID, Map.Entry<Integer, Long>> ClickTicks;
	public Map<UUID, Long> LastMS;
	public Map<UUID, Integer> heuristicVerbose;
	
	public KillAura() {
		super("KillAura", ChecksType.COMBAT, Exile.getAC(), 10, true, true);
		
		this.LastMS = new HashMap<UUID, Long>();
		this.Clicks = new HashMap<UUID, List<Long>>();
		this.ClickTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
		directionHit = new WeakHashMap<Player, Long>();
		heuristicVerbose = new HashMap<UUID, Integer>();
	}
	
	@Override
	protected void onEvent(Event event) {
		if(!this.getState()) {
			return;
		}	
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			
			if(directionHit.containsKey(e.getPlayer())) {
				directionHit.remove(e.getPlayer());
			}
		}
		if(event instanceof PacketKillauraEvent) {
			PacketKillauraEvent e = (PacketKillauraEvent) event;
			
			if(Exile.getAC().getPing().getTPS() < 17) {
				return;
			}
			Player player = e.getPlayer();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			if(e.getType() == PacketPlayerType.ARM_SWING) {
				user.addSwingPackets();
			}
			if(e.getType() == PacketPlayerType.USE) {
				user.addUsePackets();
			}
			
			if(user.getUsePackets() > user.getSwingPackets() && user.getSwingPackets() == 0) {
				user.setVL(this, user.getVL(this) + 1);
				alert(player, Color.Gray + "Reason: " + Color.White + "Invalid Swing");
			}
			if(user.getSwingPackets() > 0 && user.getUsePackets() > 0) {
				user.resetSwingPackets();
				user.resetUsePackets();
			}
		}
		if(event instanceof PacketUseEntityEvent) {
			PacketUseEntityEvent e = (PacketUseEntityEvent) event;
			Player player = e.getAttacker();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			if(!TimerUtils.elapsed(directionHit.getOrDefault(player, 0L), 51L) && e.getAttacked().getLocation().toVector().subtract(player.getLocation().toVector()).normalize().dot(player.getLocation().getDirection()) > 0.97) {
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player, Color.Gray + "Reason: " + Color.White + "Direction");
			}
		}
		if(event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			
			if(getYawDifference(e.getFrom(), e.getTo()) < 77.5) {
				return;
			}
			
			directionHit.put(e.getPlayer(), System.currentTimeMillis());
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
			if ((Count > 0 && Exile.getAC().getPing().getPing(damager) < 100) || (Count > 2 && Exile.getAC().getPing().getPing(damager) < 200)
					|| (Count > 4 && Exile.getAC().getPing().getPing(damager) > 200)) {
				Count = 0;
				alert(damager, Color.Gray + "Reason: " + Color.White + "Ogre");
				User user = Exile.getAC().getUserManager().getUser(damager.getUniqueId());
				
				user.setVL(this, user.getVL(this) + 1);
				ClickTicks.remove(damager.getUniqueId());
			}
			this.LastMS.put(damager.getUniqueId(), TimerUtils.nowlong());
			this.ClickTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
		}
	}
	
    public static float getYawDifference(Location location, Location location2) {
        float f = getYaw(location);
        float f2 = getYaw(location2);
        float f3 = Math.abs(f - f2);
        if (f < 90.0f && f2 > 270.0f || f2 < 90.0f && f > 270.0f) {
            f3 -= 360.0f;
        }
        return Math.abs(f3);
    }
    
    public static float getYaw(Location location) {
        float f = (location.getYaw() - 90.0f) % 360.0f;
        if (f < 0.0f) {
            f += 360.0f;
        }
        return f;
    }

}
