package anticheat.checks.combat;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
	
	public KillAura() {
		super("KillAura", ChecksType.COMBAT, Exile.getAC(), 10, true, true);
		
		directionHit = new WeakHashMap<Player, Long>();
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
