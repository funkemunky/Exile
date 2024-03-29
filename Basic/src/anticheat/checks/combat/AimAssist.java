package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

@ChecksListener(events = {PacketPlayerEvent.class, PlayerQuitEvent.class, PlayerMoveEvent.class, EntityDamageByEntityEvent.class})
public class AimAssist extends Checks {
	
	public Map<UUID, Map.Entry<Integer, Long>> aimAVerbose;
	public Map<UUID, Integer> aimABVerbose;
	public Map<UUID, Map.Entry<Integer, Long>> aimBVerbose;
	public Map<UUID, Integer> aimCVerbose;
	public Map<UUID, Integer> aimDVerbose;
	
	public AimAssist() {
		super("AimPattern", ChecksType.COMBAT, Exile.getAC(), 1, true, true);
		
		this.aimAVerbose = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.aimABVerbose = new HashMap<UUID, Integer>();
		this.aimBVerbose = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.aimCVerbose = new HashMap<UUID, Integer>();
		this.aimDVerbose = new HashMap<UUID, Integer>();
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
			if(aimABVerbose.containsKey(uuid)) {
				aimABVerbose.remove(uuid);
			}
			if(aimBVerbose.containsKey(uuid)) {
				aimBVerbose.remove(uuid);
			}
			if(aimCVerbose.containsKey(uuid)) {
				aimCVerbose.remove(uuid);
			}
		}
		
		if(event instanceof PacketPlayerEvent) {
			PacketPlayerEvent e = (PacketPlayerEvent) event;
			
			if(e.getType() != PacketPlayerType.POSLOOK) {
				return;
			}
			
			Player player = e.getPlayer();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			int verboseA = 0;
			int verboseB = this.aimABVerbose.getOrDefault(player.getUniqueId(), 0);
			
			long Time = TimerUtils.nowlong();
			if (this.aimAVerbose.containsKey(player.getUniqueId())) {
				verboseA = this.aimAVerbose.get(player.getUniqueId()).getKey().intValue();
				Time = this.aimAVerbose.get(player.getUniqueId()).getValue().longValue();
			}
			
			if(TimerUtils.elapsed(Time, 12000L)) {
				verboseA = 0;
				Time = TimerUtils.nowlong();
				
			}
			double yawDif = Math.abs(user.getLastYaw() - e.getYaw());
			if(yawDif > 1.0D && yawDif == user.getLastYawDifference() && user.getLastLastYawDifference() != user.getLastYawDifference() && yawDif != 0.0D) {
				verboseA++;
				//debug("Player: " + player.getName() + " Verbose(+1): " + verboseA + " YawDif: " + Math.abs(user.getLastYaw() - e.getYaw()) + " LastYawDif: " + user.getLastYawDifference());
			}
			
			if(user.getLastYawDifference() == Math.abs(user.getLastYaw() - e.getYaw()) && (Math.abs(user.getLastYaw() - e.getYaw()) != 0.0D || user.getLastYawDifference() != 0.0D)) {
				verboseB+= 1;
				//debug("Player: " + player.getName() + " Verbose(+1): " + verboseB + " YawDif: " + Math.abs(user.getLastYaw() - e.getYaw()) + " LastYawDif: " + user.getLastYawDifference());
			} else {
				verboseB = 0;
			}
			
			if((verboseA > 25 && (System.currentTimeMillis() - user.getAttackTime()) > 1000) || (verboseA > 15 && (System.currentTimeMillis() - user.getAttackTime()) < 1000)) {
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player, Color.Gray + "Reason: " + Color.White + "Yaw Patterns " + Color.Gray + " Type: " + Color.White + "Overall");
				this.advancedalert(player, 99.999);
				verboseA = 0;
			}
			if(verboseB > 9) {
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player, Color.Gray + "Reason: " + Color.White + "Yaw Patterns " + Color.Gray + " Type: " + Color.White + "Short");
				verboseB = 0;
			}
				int verboseD = this.aimDVerbose.getOrDefault(player.getUniqueId(), 0);
				double outcome = Math.sqrt((player.getEyeLocation().getYaw() * player.getEyeLocation().getYaw()) + (player.getEyeLocation().getPitch() * player.getEyeLocation().getPitch()));
				//debug("Pitch Difference: " + user.getLastPitchDifference() + " Yaw Difference: " + user.getLastYawDifference());
				if(outcome != user.getLastYawOffset()) {
					if(MathUtils.round(Math.abs(outcome - user.getLastYawOffset()), 0) == user.getLastDifference()) {
						verboseD++;
						//debug("Verbose (+1): " + verboseD);
					}
				} else {
					verboseD = 0;
				}
				user.setLastDifference(MathUtils.round(Math.abs(outcome - user.getLastYawOffset()), 0));
				user.setLastYawOffset(outcome);
				this.aimDVerbose.put(player.getUniqueId(), verboseD);
			user.setLastLastYawDifference(user.getLastYawDifference());
			user.setLastYawDifference(Math.abs(user.getLastYaw() - e.getYaw()));
			user.setLastYaw(e.getYaw());
			
			this.aimAVerbose.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(verboseA, Time));
			this.aimABVerbose.put(player.getUniqueId(), verboseB);
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
				verbose = this.aimBVerbose.get(player.getUniqueId()).getKey().intValue();
				Time = this.aimBVerbose.get(player.getUniqueId()).getValue().longValue();
			}
			
			if(TimerUtils.elapsed(Time, 17000L)) {
				verbose = 0;
				//debug("Reset");
				Time = TimerUtils.nowlong();
				
			}
			//debug("yaw: " + player.getEyeLocation().getYaw() % 3 + " pitch: " + player.getEyeLocation().getPitch() % 2);
			if(Math.abs(e.getPitch() - user.getLastPitch()) < 0.1 && MathUtils.elapsed(user.getLastAimB()) > 500L && user.getLastYawDifference() / 2 > user.getLastPitchDifference() / 3 && Math.abs(user.getLastYawDifference() - Math.abs(e.getYaw() - user.getLastYaw())) > 1.0D && user.getLastPitchDifference() != Math.abs(user.getLastPitch() - e.getPitch()) && Math.abs(user.getLastPitch() - e.getPitch()) > 0.004D && Math.abs(user.getLastPitchDifference() - Math.abs(e.getPitch() - user.getLastPitch())) < 0.008) {
                verbose++;
                //debug("Player: " + player.getName() + " Verbose(+1): " + verbose + " LastPitchDif: " + user.getLastPitchDifference() + " PitchDif: " + Math.abs(e.getPitch() - user.getLastPitch()) + " YawDif: " + Math.abs(e.getYaw() - user.getLastYaw()) + " LastYawDif: " + user.getLastYawDifference());
                user.setLastAimB(System.currentTimeMillis());
            } else if((MathUtils.elapsed(user.getLastAimB()) <= 500L) && user.getLastYawDifference() / 2 > user.getLastPitchDifference() / 3 && Math.abs(user.getLastYawDifference() - Math.abs(e.getYaw() - user.getLastYaw())) > 1.0D && user.getLastPitchDifference() != Math.abs(user.getLastPitch() - e.getPitch()) && Math.abs(user.getLastPitch() - e.getPitch()) > 0.004D && Math.abs(user.getLastPitchDifference() - Math.abs(e.getPitch() - user.getLastPitch())) < 0.008) {
                user.setLastAimB(System.currentTimeMillis());
            }
	        
			
			if(verbose > 8) {
				
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
			
			if(e.getPitch() > 80 || e.getPitch() < -80) {
				return;
			}
			
			if(Math.abs(pitchDifference - user.getLastPitchDifferenceAimC()) < 0.000009 && user.getLastYawDifference() >= 1.0) {
				verbose++;
			} else {
				if(user.getLastYawDifference() > 1.0 && Math.abs(pitchDifference - user.getLastPitchDifferenceAimC()) > 0.000009) {
					verbose = 0;
				}
					
			}
			
			if(verbose > 15) {
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