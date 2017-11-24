package anticheat.checks.combat;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.comphenix.protocol.wrappers.EnumWrappers;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketUseEntityEvent;
import anticheat.user.User;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { PacketUseEntityEvent.class })
public class DoubleClick extends Checks {

	private Map<UUID, Long[]> clicks;

	public DoubleClick() {
		super("DoubleClick", ChecksType.COMBAT, Exile.getAC(), 100, true, false);

		this.clicks = new WeakHashMap<UUID, Long[]>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!getState()) {
			return;
		}

		if (event instanceof PacketUseEntityEvent) {
			PacketUseEntityEvent e = (PacketUseEntityEvent) event;

			if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
				return;
			}

			if (!(e.getAttacked() instanceof Player)) {
				return;
			}
			
			if(Exile.getAC().getPing().getTPS() < 18) {
				return;
			}
			
			Player player = e.getAttacker();
			
			if(Exile.getAC().getPing().getPing(player) > 267) {
				return;
			}
			
			long firstClick = 0;
			long secondClick = 0;
			
			if(clicks.containsKey(player.getUniqueId())) {
				firstClick = clicks.get(player.getUniqueId())[0];
				secondClick = clicks.get(player.getUniqueId())[1];
			}
			
			if(firstClick == 0) {
				firstClick = TimerUtils.nowlong();
			} else if(secondClick == 0) {
				secondClick = TimerUtils.nowlong();
				firstClick = TimerUtils.nowlong() - firstClick;
			} else {
				secondClick = TimerUtils.nowlong() - secondClick;
				
				if(secondClick == 0 && firstClick > 50) {
					User user = Exile.getUserManager().getUser(player.getUniqueId());
					user.setVL(this, user.getVL(this) + 1);
					
					if(user.getVL(this) > 50) {
						Alert(player, "Macros/Killaura/Autoclicker");
					}
				}
				secondClick = 0;
				firstClick = 0;
			}
		}
	}

}
