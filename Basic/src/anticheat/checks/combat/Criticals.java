package anticheat.checks.combat;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.MiscUtils;

@ChecksListener(events = { EntityDamageByEntityEvent.class, PlayerMoveEvent.class, PlayerQuitEvent.class })
public class Criticals extends Checks {

	public Criticals() {
		super("Criticals", ChecksType.COMBAT, Exile.getAC(), 4, true, true);
	}

	@Override
	protected void onEvent(Event event) {
		
		if (!this.getState()) {
			return;
		}
		
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			
			if (!(e.getDamager() instanceof Player)) {
				return;
			}
			
			Player player = (Player) e.getDamager();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			if ((System.currentTimeMillis() - user.getTookVelocity()) < 1200L) {
				return;
			}
			
			if (MiscUtils.slabsNear(player.getLocation())) {
				return;
			}
			
			Location l = player.getLocation().clone();
			l.add(0.0, player.getEyeHeight() + 1.0, 0.0);
			
			if (MiscUtils.blocksNear(l)) {
				return;
			}
			
			if(player.getFallDistance() > 0.0D && user.getRealFallDistance() == 0.0D) {
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player, "*");
			}
		}
	}

}
