package anticheat.checks.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Fiona;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.MiscUtils;
import anticheat.utils.PlayerUtils;

@ChecksListener(events = {PlayerMoveEvent.class, PlayerQuitEvent.class})
public class FlyB extends Checks {
	
	public Map<UUID, Long> flyTicksA;
	
	public FlyB() {
		super("FlyB", ChecksType.MOVEMENT, Fiona.getAC(), 4, true, true);
		this.flyTicksA = new HashMap<UUID, Long>();
	}
	
	@Override
	protected void onEvent(Event event) {
		if(!this.getState()) {
			return;
		}
		
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			Player p = e.getPlayer();
			UUID uuid = p.getUniqueId();
			
			if(this.flyTicksA.containsKey(uuid)) {
				this.flyTicksA.remove(uuid);
			}
		}
		
		if (event instanceof PlayerMoveEvent) {

			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player player = e.getPlayer();
			if (e.isCancelled()) {
				return;
			}
			if (player.getAllowFlight()) {
				return;
			}
			if (player.getVehicle() != null) {
				return;
			}
			if (Fiona.getAC().getPing().getPing(player) > 450) {
				return;
			}
			if (Fiona.getAC().getPing().getTPS() < 17) {
				return;
			}
			if (PlayerUtils.isInWater(player)) {
				return;
			}
			if (MiscUtils.isInWeb(player)) {
				return;
			}
			if (MiscUtils.blocksNear(player.getLocation())) {
				if (this.flyTicksA.containsKey(player.getUniqueId())) {
					this.flyTicksA.remove(player.getUniqueId());
				}
				return;
			}
			if ((e.getTo().getX() == e.getFrom().getX()) && (e.getTo().getZ() == e.getFrom().getZ())) {
				return;
			}
			if (Math.abs(e.getTo().getY() - e.getFrom().getY()) > 0.05) {
				if (this.flyTicksA.containsKey(player.getUniqueId())) {
					this.flyTicksA.remove(player.getUniqueId());
				}
				return;
			}
			long Time = System.currentTimeMillis();
			if (this.flyTicksA.containsKey(player.getUniqueId())) {
				Time = ((Long) this.flyTicksA.get(player.getUniqueId())).longValue();
			}
			long MS = System.currentTimeMillis() - Time;
			if (MS > 500L) {
				User user = Fiona.getAC().getUserManager().getUser(player.getUniqueId());
				user.setVL(this, user.getVL(this) + 1);
				this.Alert(player, ChatColor.GREEN + "Hovered 0.5s, Ping: " + Fiona.getAC().getPing().getPing(player));
				this.flyTicksA.remove(player.getUniqueId());
				return;
			}
			this.flyTicksA.put(player.getUniqueId(), Time);
		}
	}

}
