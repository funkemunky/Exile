package anticheat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import anticheat.Exile;
import anticheat.user.User;

public class EventPlayerInteractEvent implements Listener {

	@EventHandler
	public void onMove(PlayerInteractEvent event) {
		Exile.getAC().getChecks().event(event);
		Player p = (Player) event.getPlayer();
		User user = Exile.getAC().getUserManager().getUser(p.getUniqueId());
		if (Exile.getAC().getPing().getTPS() < 16) {
			return;
		}
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			user.setLeftClicks(user.getLeftClicks() + 1);

		} else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			user.setRightClicks(user.getRightClicks() + 1);
		}
	}
	
}
