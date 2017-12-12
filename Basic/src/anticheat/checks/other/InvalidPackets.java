package anticheat.checks.other;

import java.util.AbstractMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketEntityActionEvent;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { PacketEntityActionEvent.class, PlayerQuitEvent.class })
public class InvalidPackets extends Checks {

	private Map<Player, Map.Entry<Integer, Long>> verbose;

	public InvalidPackets() {
		super("InvalidPackets", ChecksType.OTHER, Exile.getAC(), 7, true, true);

		this.verbose = new WeakHashMap<Player, Map.Entry<Integer, Long>>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!getState()) {
			return;
		}

		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;

			if (verbose.containsKey(e.getPlayer())) {
				verbose.remove(e.getPlayer());
			}
		}

		if (event instanceof PacketEntityActionEvent) {
			PacketEntityActionEvent e = (PacketEntityActionEvent) event;
			if (e.getAction() != 1) {
				return;
			}
			Player player = e.getPlayer();

			int verbose = 0;
			long Time = -1L;
			if (this.verbose.containsKey(player)) {
				verbose = this.verbose.get(player).getKey().intValue();
				Time = this.verbose.get(player).getValue().longValue();
			}
			verbose++;
			if (this.verbose.containsKey(player)) {
				if (TimerUtils.elapsed(Time, 100L)) {
					verbose = 0;
					Time = System.currentTimeMillis();
				} else {
					Time = System.currentTimeMillis();
				}
			}
			if (verbose > 50) {
				User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player, Color.Gray + "Reason: " + Color.White + "Sneak");
				
				verbose = 0;
			}
			this.verbose.put(player,
					new AbstractMap.SimpleEntry<Integer, Long>(verbose, Time));
		}
	}

}
