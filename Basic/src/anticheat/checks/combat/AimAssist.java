package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketPlayerEvent;
import anticheat.packets.events.PacketPlayerType;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {PacketPlayerEvent.class, PlayerQuitEvent.class, PlayerMoveEvent.class})
public class AimAssist extends Checks {
	
	public Map<UUID, Map.Entry<Integer, Long>> aimAVerbose;
	public Map<UUID, Map.Entry<Integer, Long>> aimBVerbose;
	public Map<UUID, Integer> aimCVerbose;
	
	public AimAssist() {
		super("AimPattern", ChecksType.COMBAT, Exile.getAC(), 1, true, true);
		
		this.aimAVerbose = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.aimBVerbose = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.aimCVerbose = new HashMap<UUID, Integer>();
	}
	
	@Override
	protected void onEvent(Event event) {
		if(!getState()) {
			return;
		}
		
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;

			UUID uuid = e.getPlayer().getUniqueId();
			
			if(aimAVerbose.containsKey(uuid)) {
				aimAVerbose.remove(uuid);
			}
			
			if(aimBVerbose.containsKey(uuid)) {
				aimBVerbose.remove(uuid);
			}
		}
		
		if(event instanceof PacketPlayerEvent) {
			PacketPlayerEvent e = (PacketPlayerEvent) event;
			
			if(e.getType() != PacketPlayerType.POSLOOK) {
				return;
			}
			
			Player player = e.getPlayer();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			int verbose = 0;
			long Time = TimerUtils.nowlong();
			if (this.aimAVerbose.containsKey(player.getUniqueId())) {
				verbose = ((Integer) ( this.aimAVerbose.get(player.getUniqueId())).getKey()).intValue();
				Time = ((Long) ( this.aimAVerbose.get(player.getUniqueId())).getValue()).longValue();
			}
			
			if(TimerUtils.elapsed(Time, 10000L)) {
				verbose = 0;
				Time = TimerUtils.nowlong();
				
			}
			
			if(user.getLastYawDifference() == Math.abs(user.getLastYaw() - e.getYaw()) && (Math.abs(user.getLastYaw() - e.getYaw()) != 0.0D || user.getLastYawDifference() != 0.0D)) {
				verbose++;
			}
			
			if(verbose > 33) {
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player, Color.Gray + "Reason: " + Color.White + "Yaw Patterns");
				this.advancedalert(player, 99.999);
				verbose = 0;
			}
			user.setLastYawDifference(Math.abs(user.getLastYaw() - e.getYaw()));
			user.setLastYaw(e.getYaw());
			
			this.aimAVerbose.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(verbose, Time));
		}
	    if(event instanceof PlayerMoveEvent) {
            PlayerMoveEvent e = (PlayerMoveEvent) event;

            if(e.getFrom().getYaw() == e.getTo().getYaw()) {
            	    return;
            }
            
            double yawDelta = Math.abs(e.getFrom().getYaw() - e.getTo().getYaw());
            
            if(yawDelta > 0 && yawDelta < 360) {
                if (yawDelta % 1 == 0 && yawDelta >= 5) {
                   	alert(e.getPlayer(), Color.Gray + "Reason: " + Color.Red + "Experimental");
                }
            }
        }
		if(event instanceof PacketPlayerEvent) {
            PacketPlayerEvent e = (PacketPlayerEvent) event;
			
			if(e.getType() != PacketPlayerType.POSLOOK) {
				return;
			}
			
			Player player = e.getPlayer();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			int verbose = 0;
			long Time = TimerUtils.nowlong();
			if (this.aimBVerbose.containsKey(player.getUniqueId())) {
				verbose = ((Integer) ( this.aimBVerbose.get(player.getUniqueId())).getKey()).intValue();
				Time = ((Long) ( this.aimBVerbose.get(player.getUniqueId())).getValue()).longValue();
			}
			
			if(TimerUtils.elapsed(Time, 17000L)) {
				verbose = 0;
				Time = TimerUtils.nowlong();
				
			}
			
			if(MathUtils.elapsed(user.getLastAimB()) > 500L && user.getLastYawDifference() / 2 > user.getLastPitchDifference() / 3 && Math.abs(user.getLastYawDifference() - Math.abs(e.getYaw() - user.getLastYaw())) > 1.0D && user.getLastPitchDifference() != Math.abs(user.getLastPitch() - e.getPitch()) && Math.abs(user.getLastPitch() - e.getPitch()) > 0.004D && Math.abs(user.getLastPitchDifference() - Math.abs(e.getPitch() - user.getLastPitch())) < 0.008) {
				verbose++;
				user.setLastAimB(System.currentTimeMillis());
			} else if((MathUtils.elapsed(user.getLastAimB()) <= 500L) && user.getLastYawDifference() / 2 > user.getLastPitchDifference() / 3 && Math.abs(user.getLastYawDifference() - Math.abs(e.getYaw() - user.getLastYaw())) > 1.0D && user.getLastPitchDifference() != Math.abs(user.getLastPitch() - e.getPitch()) && Math.abs(user.getLastPitch() - e.getPitch()) > 0.004D && Math.abs(user.getLastPitchDifference() - Math.abs(e.getPitch() - user.getLastPitch())) < 0.008) {
				user.setLastAimB(System.currentTimeMillis());
			}
	        
			
			if(verbose > 8) {
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player, Color.Gray + "Reason: " + Color.White + "Pitch Patterns");
				this.advancedalert(player, 98.69);
				verbose = 0;
			}
			
			user.setLastPitchDifference(Math.abs(user.getLastPitch() - e.getPitch()));
			user.setLastPitch(e.getPitch());
			
			this.aimBVerbose.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(verbose, Time));
		}
		if(event instanceof PacketPlayerEvent) {
			PacketPlayerEvent e = (PacketPlayerEvent) event;
			
			if(e.getType() != PacketPlayerType.POSLOOK) {
				return;
			}
			
			Player player = e.getPlayer();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			int verbose = this.aimCVerbose.getOrDefault(player.getUniqueId(), 0);
			double pitchDifference = Math.abs(e.getPitch() - user.getLastPitchAimC());
			
			if(Math.abs(pitchDifference - user.getLastPitchDifferenceAimC()) < 0.000001) {
				verbose++;
			} else {
				verbose = 0;
			}
			
			if(verbose > 10) {
				user.setVL(this, user.getVL(this) + 1);
				
				verbose = 0;
				
				alert(player, Color.Gray + "Reason: " + Color.White + "Impossible Pitch Movement");
			}
			
			this.aimCVerbose.put(player.getUniqueId(), verbose);
			user.setLastPitchAimC(e.getPitch());
			user.setLastPitchDifferenceAimC(pitchDifference);
		}
	}

}