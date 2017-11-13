package anticheat.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import anticheat.Fiona;
import anticheat.checks.movement.NoSlowDown;
import anticheat.user.User;

public class EventPlayerInteractEvent implements Listener {

	@EventHandler
	public void onMove(PlayerInteractEvent event) {
		Fiona.getAC().getchecksmanager().event(event);
		Player p = (Player) event.getPlayer();
		User user = Fiona.getUserManager().getUser(p.getUniqueId());
		if (Fiona.getAC().getPing().getTPS() < 16) {
			return;
		}
		if (p.getItemInHand().getType().equals(Material.DIAMOND_SWORD)
				|| p.getItemInHand().getType().equals(Material.GOLD_SWORD)
				|| p.getItemInHand().getType().equals(Material.IRON_SWORD)
				|| p.getItemInHand().getType().equals(Material.STONE_SWORD)
				|| p.getItemInHand().getType().equals(Material.WOOD_SWORD)) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				NoSlowDown.hasword.add(p);

			}
		}
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			user.setLeftClicks(user.getLeftClicks() + 1);

		} else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			user.setRightClicks(user.getRightClicks() + 1);
		}
	}
}
