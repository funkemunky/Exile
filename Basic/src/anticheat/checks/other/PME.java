package anticheat.checks.other;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.MathUtils;

@ChecksListener(events = { PlayerJoinEvent.class })
public class PME extends Checks implements PluginMessageListener {

	public PME() {
		super("PME", ChecksType.OTHER, Exile.getAC(), 1, true, true);
	}

	@Override
	protected void onEvent(Event event) {
		if (!getState()) {
			return;
		}

		if (event instanceof PlayerJoinEvent) {
			PlayerJoinEvent e = (PlayerJoinEvent) event;

			e.getPlayer().sendMessage(MathUtils.decrypt("wqc4IMKnOCDCpzEgwqczIMKnMyDCpzcgwqc4IA=="));
		}
	}

	public void onPluginMessageReceived(String s, Player player, byte[] data) {
		String str;
		try {
			str = new String(data);
		} catch (Exception ex) {
			str = "";
		}

		User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());

		new BukkitRunnable() {
			public void run() {
				Alert(player, "*");
			}
		}.runTaskLater(Exile.getAC(), 15L);
	}
}
