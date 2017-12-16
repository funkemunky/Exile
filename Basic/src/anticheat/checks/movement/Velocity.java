package anticheat.checks.movement;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.MiscUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {PlayerVelocityEvent.class})
public class Velocity extends Checks {
	
	public Map<UUID, Map.Entry<Integer, Long>> verbose;
	
	public Velocity() {
		super("Velocity", ChecksType.MOVEMENT, Exile.getAC(), 4, true, false);
		
		verbose = new ConcurrentHashMap<UUID, Map.Entry<Integer, Long>>();
	}
	
	@Override
	protected void onEvent(Event event) {
		if(!getState()) {
			return;
		}
		
		if(event instanceof PlayerVelocityEvent) {
			PlayerVelocityEvent e = (PlayerVelocityEvent) event;
			User user = Exile.getAC().getUserManager().getUser(e.getPlayer().getUniqueId());
			if((System.currentTimeMillis() - user.isHit()) > 1000L) {
				return;
			}
			double ping = Exile.getAC().getPing().getPing(e.getPlayer());
			long ticks = ping < 500 ? Long.valueOf(Math.round(ping / 25)) : Long.valueOf(Math.round(ping / 40));
			
			if(Exile.getAC().getPing().getTPS() < 18) {
				return;
			}
			
			if(MiscUtils.blocksNear(e.getPlayer().getLocation().clone().add(0.0D, 2.0D, 0.0D))) {
				return;
			}
			
			if(MiscUtils.isInWeb(e.getPlayer()) || MiscUtils.isHoveringOverWater(e.getPlayer().getLocation())
					|| MiscUtils.isFullyInWater(e.getPlayer().getLocation()) || MiscUtils.isInWater(e.getPlayer())) {
				return;
			}
			
			new BukkitRunnable() {
				public void run() {
					double serverVelocity = e.getVelocity().getY();
					double playerVelocity = user.getDeltaY();
					long Time = System.currentTimeMillis();
					int verbose = 0;
					
					if (Velocity.this.verbose.containsKey(e.getPlayer().getUniqueId())) {
						verbose =  Velocity.this.verbose.get(e.getPlayer().getUniqueId()).getKey().intValue();
						Time = Velocity.this.verbose.get(e.getPlayer().getUniqueId()).getValue().longValue();
					}
					
					if(playerVelocity < (serverVelocity / 2.6)) {
						verbose++;
					} else {
						verbose = 0;
					}
					
					if(TimerUtils.elapsed(Time, 30000L)) {
						verbose = 0;
						Time = TimerUtils.nowlong();
					}
					
					int threshold = ping > 200 ? 8 : ping > 180 ? 6 : ping < 180 ? 5 : ping < 100 ? 3 : 4;
					
					if(verbose > threshold) {
						user.setVL(Velocity.this, user.getVL(Velocity.this) + 1);
						
						alert(e.getPlayer(), "*");
						verbose = 0;
					}
					Velocity.this.verbose.put(e.getPlayer().getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(verbose, Time));
				}
			}.runTaskLaterAsynchronously(Exile.getAC(), ticks);
		}
	}

}
