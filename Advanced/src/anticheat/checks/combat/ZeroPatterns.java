package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Fiona;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketPlayerEvent;
import anticheat.packets.events.PacketPlayerType;
import anticheat.user.User;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {PacketPlayerEvent.class, PlayerQuitEvent.class})
public class ZeroPatterns extends Checks {
	
	Map<Player, Map.Entry<Double, Double>> difference;
	
	public ZeroPatterns() {
		super("ZeroPatterns", ChecksType.COMBAT, Fiona.getAC(), 12, false, false);
		
		this.difference = new WeakHashMap<Player, Map.Entry<Double, Double>>();
	}
	
	@Override
	protected void onEvent(Event event) {
		if(!this.getState()) {
			return;
		}
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			Player player = e.getPlayer();
			if(this.difference.containsKey(player)) {
				this.difference.remove(player);
			}
		}
		if(event instanceof PacketPlayerEvent) {
			PacketPlayerEvent e = (PacketPlayerEvent) event;
			if(e.getType() == PacketPlayerType.POSLOOK) {
				User user = Fiona.getUserManager().getUser(e.getPlayer().getUniqueId());
				long Time = user.getLoginMIllis();
				if(!TimerUtils.elapsed(Time, 1500)) {
					return;
				}
				if(e.getPlayer().getVehicle() != null) {
					return;
				}
				if(user.isTeleported()) {
					return;
				}
				double yaw = e.getYaw();
				double pitch = e.getPitch();
				double yawdif = this.difference.containsKey(e.getPlayer()) ? yaw - this.difference.get(e.getPlayer()).getKey() : 0;
				double pitchdif = this.difference.containsKey(e.getPlayer()) ? pitch - this.difference.get(e.getPlayer()).getValue() : 0;
				if(user.getLastYaw() == -0D && yawdif == -0D && pitchdif == 0D) {
					user.setVL(this, user.getVL(this) + 1);
					this.Alert(e.getPlayer(), "Experimental");
				}
				this.difference.put(e.getPlayer(), new AbstractMap.SimpleEntry<Double, Double>(yaw, pitch));
				user.setLastYaw(yawdif);
			}
		}
	}

}