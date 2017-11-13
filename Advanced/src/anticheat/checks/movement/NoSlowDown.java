package anticheat.checks.movement;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import anticheat.Fiona;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.PlayerUtils;

/**
 * Created by XtasyCode on 11/08/2017.
 */

@ChecksListener(events = { PlayerMoveEvent.class, PlayerInteractEvent.class })
public class NoSlowDown extends Checks {

	public static ArrayList<Player> hasword = new ArrayList<Player>();
	public static ArrayList<Player> isininv = new ArrayList<Player>();
	public static Map<UUID, Map.Entry<Integer, Long>> speedTicks;

	public NoSlowDown() {
		super("NoSlowDown", ChecksType.MOVEMENT, Fiona.getAC(), 100, true, true);
		
		this.speedTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
	}

	@Override
	protected void onEvent(Event event) {
		if (this.getState() == false)
			return;
		
		if(event instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent) event;
			 if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem() != null) {
					if(e.getItem().equals(Material.EXP_BOTTLE) || e.getItem().getType().equals(Material.GLASS_BOTTLE) ||
							e.getItem().getType().equals(Material.POTION)) {
						return;
					}
		        	Player player = e.getPlayer();
		        	long Time = System.currentTimeMillis();
		        	int level = 0;
		            if (this.speedTicks.containsKey(player.getUniqueId()))
		            {
		                level = ((Integer)((Map.Entry)this.speedTicks.get(player.getUniqueId())).getKey()).intValue();
		                Time = ((Long)((Map.Entry)this.speedTicks.get(player.getUniqueId())).getValue()).longValue();
		            }
		            double diff = System.currentTimeMillis() - Time;
		            level = diff >= 2.0 ? (diff <= 51.0 ? (level += 2) : (diff <= 100.0 ? (level += 0) : (diff <= 500.0 ? (level -= 6) : (level -= 12)))) : ++level;
		            int max = 13;
		            if (level > max * 0.9D && diff <= 100.0D) {
						User user = Fiona.getUserManager().getUser(player.getUniqueId());
						user.setVL(this, user.getVL(this) + 1);
						this.Alert(player, "Autoblock");
		                if (level > max) {
		                    level = max / 4;
		                }
		            } else if (level < 0) {
		                level = 0;
		            }
		            this.speedTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(level), Long.valueOf(System.currentTimeMillis())));
		        }
		    }

	}

}
