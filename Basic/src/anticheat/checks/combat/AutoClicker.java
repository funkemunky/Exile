package anticheat.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.events.TickEvent;
import anticheat.events.TickType;
import anticheat.user.User;

@ChecksListener(events = {TickEvent.class})
public class AutoClicker extends Checks {

	public AutoClicker() {
		super("AutoClicker", ChecksType.COMBAT, Exile.getAC(), 15, true, true);
	}
	
	@Override
	protected void onEvent(Event event) {
		if(!getState()) {
			return;
		}
		
		if (event instanceof TickEvent) {
			TickEvent e = (TickEvent) event;
			if(e.getType() != TickType.SECOND) {
				return;
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				User user = Exile.getUserManager().getUser(player.getUniqueId());
				if(Exile.getAC().getPing().getTPS() > 17 && Exile.getAC().getPing().getPing(player) < 500) {
					if (user.getLeftClicks() > 20) {
						if(user.getLeftClicks() >= 30) {
							user.setVL(this, user.getVL(this) + 1);
						}
						
						this.Alert(player, user.getLeftClicks() + " CPS");
					}
				}
				user.setLeftClicks(0);
				user.setRightClicks(0);
			}
		}
	}
	
}
