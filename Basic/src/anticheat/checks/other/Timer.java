package anticheat.checks.other;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketPlayerEvent;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {PacketPlayerEvent.class, PlayerQuitEvent.class})
public class Timer extends Checks {
	
	private Map<UUID, Map.Entry<Integer, Long>> packets;
	private Map<UUID, Integer> verbose;
	
	public Timer() {
		super("Timer", ChecksType.OTHER, Exile.getAC(), 10, true, false);
		
		this.packets = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.verbose = new HashMap<UUID, Integer>();
	}
	
	protected void onEvent(Event event) {
		if(!getState()) {
			return;
		}
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			
			if(packets.containsKey(e.getPlayer().getUniqueId())) {
				packets.remove(e.getPlayer().getUniqueId());
			}
			if(verbose.containsKey(e.getPlayer().getUniqueId())) {
				verbose.remove(e.getPlayer().getUniqueId());
			}
		}
		if(event instanceof PacketPlayerEvent) {
			PacketPlayerEvent e = (PacketPlayerEvent) event;
			
			Player player = e.getPlayer();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			int packets = 0;
			long Time = System.currentTimeMillis();
			int verbose = this.verbose.getOrDefault(player.getUniqueId(), 0);
			
			if (this.packets.containsKey(player.getUniqueId())) {
				packets = this.packets.get(player.getUniqueId()).getKey().intValue();
				Time = this.packets.get(player.getUniqueId()).getValue().longValue();
			}
			
			double threshold = 41;
			if(MathUtils.elapsed(user.getLastPacket()) > 100L) {
				verbose = 0;
			}
			if(TimerUtils.elapsed(Time, 2000L)) {
				if(packets > threshold + user.getPosPackets()) {
					verbose++;
				} else {
					verbose = 0;
				}
				
				if(verbose > 3) {
					user.setVL(this, user.getVL(this) + 1);
					
					verbose = 0;
					
					alert(player, Color.Gray + "Reason: " + Color.Red + "Experimental " + Color.Gray + "Timer Speed: " + Color.Green + MathUtils.round(packets / threshold, 2));
				}
				packets = 0;
				Time = TimerUtils.nowlong();
				user.setPosPackets(0);
			}
			packets++;
			user.setLastPacket(System.currentTimeMillis());
			this.packets.put(player.getUniqueId(), new SimpleEntry<Integer, Long>(packets, Time));
			this.verbose.put(player.getUniqueId(), verbose);
		}
	}

}