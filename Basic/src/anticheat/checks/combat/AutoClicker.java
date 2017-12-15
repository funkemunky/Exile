package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.events.TickEvent;
import anticheat.events.TickType;
import anticheat.packets.events.PacketKillauraEvent;
import anticheat.packets.events.PacketPlayerType;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {TickEvent.class, PacketKillauraEvent.class, PlayerQuitEvent.class})
public class AutoClicker extends Checks {
	
    public Map<UUID, Long> LastMillis;
    public Map<UUID, List<Long>> ClickTimes;
    public Map<UUID, Map.Entry<Integer, Long>> ClickTimestuff;

	public AutoClicker() {
		super("AutoClicker", ChecksType.COMBAT, Exile.getAC(), 15, true, true);
		
        this.LastMillis = new WeakHashMap<UUID, Long>();
        this.ClickTimes = new WeakHashMap<UUID, List<Long>>();
        this.ClickTimestuff = new WeakHashMap<UUID, Map.Entry<Integer, Long>>();
	}
	
	@Override
	protected void onEvent(Event event) {
		if(!getState()) {
			return;
		}
		
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			
			UUID uuid = e.getPlayer().getUniqueId();
			
			if(LastMillis.containsKey(uuid)) {
				LastMillis.remove(uuid);
			}
			
			if(ClickTimes.containsKey(uuid)) {
				ClickTimes.remove(uuid);
			}
			
			if(ClickTimestuff.containsKey(uuid)) {
				ClickTimestuff.containsKey(uuid);
			}
		}
		
		if (event instanceof TickEvent) {
			TickEvent e = (TickEvent) event;
			if(e.getType() != TickType.SECOND) {
				return;
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
				if(Exile.getAC().getPing().getTPS() > 17 && Exile.getAC().getPing().getPing(player) < 500) {
					if (user.getLeftClicks() > 20) {
						if(user.getLeftClicks() >= 30) {
							user.setVL(this, user.getVL(this) + 2);
							this.advancedalert(player, 100D);
						}
						this.advancedalert(player, (user.getLeftClicks() - 19) * 10D);
						alert(player, Color.Gray + "Reason: " + Color.White + "FastClick " + Color.Gray + "CPS: " + Color.White +  user.getLeftClicks() + "");
					}
					
				}
				user.setLeftClicks(0);
				user.setRightClicks(0);
			}
		}
		if(event instanceof PacketKillauraEvent) {
			PacketKillauraEvent e = (PacketKillauraEvent) event;
			if(e.getType() != PacketPlayerType.ARM_SWING) {
				return;
			}
			
			Player player = e.getPlayer();
	        int verbose = 0;
	        long Time = System.currentTimeMillis();
	        if (this.ClickTimestuff.containsKey(player.getUniqueId())) {
	            verbose = this.ClickTimestuff.get(player.getUniqueId()).getKey();
	            Time = this.ClickTimestuff.get(player.getUniqueId()).getValue();
	        }
	        if (this.LastMillis.containsKey(player.getUniqueId())) {
	            final long MS = TimerUtils.nowlong() - this.LastMillis.get(player.getUniqueId());
	            if (MS > 500L || MS < 5L) {
	                this.LastMillis.put(player.getUniqueId(), TimerUtils.nowlong());
	                return;
	            }
	            if (this.ClickTimes.containsKey(player.getUniqueId())) {
	                final List<Long> ClickTimes = this.ClickTimes.get(player.getUniqueId());
	                if (ClickTimes.size() == 3) {
	                    this.ClickTimes.remove(player.getUniqueId());
	                    Collections.sort(ClickTimes);
	                    long Range = ClickTimes.get(ClickTimes.size() - 1) - ClickTimes.get(0);
	                    
	                    if (Range >= 0 && Range <= 2) {
	                        ++verbose;
	                        Time = System.currentTimeMillis();
	                    }
	                }
	                else {
	                    ClickTimes.add(MS);
	                    this.ClickTimes.put(player.getUniqueId(), ClickTimes);
	                }
	            }
	            else {
	                final List<Long> ClickTimes = new ArrayList<Long>();
	                ClickTimes.add(MS);
	                this.ClickTimes.put(player.getUniqueId(), ClickTimes);
	            }
	        }
	        if (this.ClickTimestuff.containsKey(player.getUniqueId()) && TimerUtils.elapsed(Time, 5000L)) {
	            verbose = 0;
	            Time = TimerUtils.nowlong();
	        }
	        if (verbose > 4) {
	            verbose = 0;
	            User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
	            user.setVL(this, user.getVL(this) + 1);
	            
	            alert(player, Color.Gray + "Reason: " + Color.White + "Continuous Patterns " + Color.Gray + "CPS: " + Color.White + user.getLeftClicks());
	            ClickTimestuff.remove(player.getUniqueId());
	        }
	        this.LastMillis.put(player.getUniqueId(), TimerUtils.nowlong());
	        this.ClickTimestuff.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(verbose, Time));
	    }
	}
	
}
