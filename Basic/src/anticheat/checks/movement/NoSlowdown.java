package anticheat.checks.movement;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;

@ChecksListener(events = { PlayerInteractEvent.class, PlayerQuitEvent.class })
public class NoSlowdown extends Checks {
	
	public Map<UUID, Map.Entry<Integer, Long>> speedTicks;

	public NoSlowdown() {
		super("NoSlowDown", ChecksType.MOVEMENT, Exile.getAC(), 12, true, true);
		
		this.speedTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!getState()) {
			return;
		}
		
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			
			UUID uuid = e.getPlayer().getUniqueId();
			
			if(speedTicks.containsKey(uuid)) {
				speedTicks.remove(uuid);
			}
		}
		
		if(event instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent) event;
			 if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem() != null) {
					if(e.getItem().getType().equals(Material.EXP_BOTTLE) || e.getItem().getType().equals(Material.GLASS_BOTTLE) ||
							e.getItem().getType().equals(Material.POTION)) {
						return;
					}
		        	Player player = e.getPlayer();
		        	long Time = System.currentTimeMillis();
		        	int level = 0;
		            if (this.speedTicks.containsKey(player.getUniqueId()))
		            {
		                level = ((Integer)(this.speedTicks.get(player.getUniqueId())).getKey()).intValue();
		                Time = ((Long)(this.speedTicks.get(player.getUniqueId())).getValue()).longValue();
		            }
		            double diff = System.currentTimeMillis() - Time;
		            level = diff >= 2.0 ? (diff <= 51.0 ? (level += 2) : (diff <= 100.0 ? (level += 0) : (diff <= 500.0 ? (level -= 6) : (level -= 12)))) : ++level;
		            int max = 13;
		            if (level > max * 0.9D && diff <= 100.0D) {
						User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
						user.setVL(this, user.getVL(this) + 1);
						alert(player, "Autoblock");
		                if (level > max) {
		                    level = max / 4;
		                }
		            } else if (level < 0) {
		                level = 0;
		            }
		            this.speedTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(level, System.currentTimeMillis()));
		        }
		    }

	}

}
