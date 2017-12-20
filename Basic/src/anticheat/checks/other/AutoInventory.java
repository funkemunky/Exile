package anticheat.checks.other;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.Color;

@ChecksListener(events = {PlayerQuitEvent.class, PlayerMoveEvent.class})
public class AutoInventory extends Checks {
	
	private Map<UUID, Integer> verboseB;
	
	public AutoInventory() {
		super("AutoInventory", ChecksType.OTHER, Exile.getAC(), 2, true, false);
		
		verboseB = new HashMap<UUID, Integer>();
	}
	
	@Override
	protected void onEvent(Event event) {
		if(!getState()) {
			return;
		}
		
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			
			Player player = e.getPlayer();
			
			if(verboseB.containsKey(player.getUniqueId())) {
				verboseB.remove(player.getUniqueId());
			}
		}
		
		if(event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			
			Player player = e.getPlayer();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			if(!user.isInventoryOpen()) {
				if(verboseB.containsKey(player.getUniqueId())) {
					verboseB.remove(player.getUniqueId());
				}
				return;
			}
			
			int verbose = verboseB.getOrDefault(player.getUniqueId(), 0);
			
			verbose++;
			
			if(verbose > 20) {
				user.setVL(this, user.getVL(this) + 1);
				alert(player, Color.Gray + "Reason: " + Color.White + "Inventory Move");
			}
			
			verboseB.put(player.getUniqueId(), verbose);
		}
	}
}
