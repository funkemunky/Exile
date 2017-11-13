package anticheat.checks.other;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Fiona;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketSwingArmEvent;
import anticheat.user.User;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { EntityDamageByEntityEvent.class, PacketSwingArmEvent.class })
public class NoSwing extends Checks {

	public NoSwing() {
		super("NoSwing", ChecksType.OTHER, Fiona.getAC(), 20, true, true);
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}
		if (event instanceof EntityDamageByEntityEvent) {

			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			
			if(!Fiona.getAC().isEnabled()) {
				return;
			}

			Player p = (Player) e.getDamager();

			User user = Fiona.getUserManager().getUser(p.getUniqueId());

			if (e.getEntity() != null && p != null) {
				new BukkitRunnable() {
					public void run() {
						if (user.isHasSwung()) {
							user.setHasSwung(false);

						} else {
							if(Fiona.getAC().isEnabled()) {
								if(!(TimerUtils.nowlong() < (user.getLastSwing() + 1500L))) {
									user.setVL(NoSwing.this, user.getVL(NoSwing.this) + 1);
									Alert(p, "*");
								}
							}
						}
					}
				}.runTaskLater(Fiona.getAC(), 10L);
			}

		}
		if (event instanceof PacketSwingArmEvent) {

			PacketSwingArmEvent psav = (PacketSwingArmEvent) event;
			Player p = psav.getPlayer();

			User user = Fiona.getUserManager().getUser(p.getUniqueId());

			user.setHasSwung(true);

		}
	}
}
