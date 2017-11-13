package anticheat.checks.movement;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.PlayerUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {PlayerMoveEvent.class, PlayerQuitEvent.class, PlayerTeleportEvent.class, PlayerDeathEvent.class})
public class NoFall extends Checks {

	public Map<UUID, Map.Entry<Long, Integer>> NoFallTicks;
	public Map<UUID, Double> FallDistance;
	public ArrayList<Player> cancel;

	public NoFall() {
		super("NoFall", ChecksType.MOVEMENT, Exile.getAC(), 6, true, true);

		this.NoFallTicks = new HashMap<UUID, Map.Entry<Long, Integer>>();
		this.FallDistance = new HashMap<UUID, Double>();
		this.cancel = new ArrayList<Player>();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}
		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			if (FallDistance.containsKey(e.getPlayer().getUniqueId())) {
				FallDistance.remove(e.getPlayer().getUniqueId());
			}
			if (FallDistance.containsKey(e.getPlayer().getUniqueId())) {
				FallDistance.containsKey(e.getPlayer().getUniqueId());
			}
		}
		if (event instanceof PlayerTeleportEvent) {
			PlayerTeleportEvent e = (PlayerTeleportEvent) event;
			if (e.getCause() == TeleportCause.ENDER_PEARL) {
				cancel.add(e.getPlayer());
			}
		}
		if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			cancel.add(e.getEntity());
		}
		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player player = e.getPlayer();
			Damageable dplayer = (Damageable) e.getPlayer();
			if (this.cancel.contains(player)) {
				cancel.remove(player);
				return;
			}
			if (player.getAllowFlight()) {
				return;
			}
			if (player.getGameMode().equals(GameMode.CREATIVE)) {
				return;
			}
			if (player.getVehicle() != null) {
				return;
			}
			if (dplayer.getHealth() <= 0.0D) {
				return;
			}
			if (PlayerUtils.isOnClimbable(player, 0)) {
				return;
			}
			if (PlayerUtils.isInWater(player)) {
				return;
			}
			double Falling = 0.0D;
			if ((!PlayerUtils.isOnGround(player)) && (e.getFrom().getY() > e.getTo().getY())) {
				if (this.FallDistance.containsKey(player.getUniqueId())) {
					Falling = ((Double) this.FallDistance.get(player.getUniqueId())).doubleValue();
				}
				Falling += e.getFrom().getY() - e.getTo().getY();
			}
			this.FallDistance.put(player.getUniqueId(), Double.valueOf(Falling));
			if (Falling < 3.0D) {
				return;
			}
			long Time = System.currentTimeMillis();
			int Count = 0;
			if (this.NoFallTicks.containsKey(player.getUniqueId())) {
				Time = ((Long) (this.NoFallTicks.get(player.getUniqueId())).getKey()).longValue();
				Count = Integer.valueOf(
						((Integer) (this.NoFallTicks.get(player.getUniqueId())).getValue()).intValue())
						.intValue();
			}
			if ((player.isOnGround()) || (player.getFallDistance() == 0.0F)) {
				player.damage(5);
				Count += 2;
			} else {
				Count--;
			}
			if ((this.NoFallTicks.containsKey(player.getUniqueId())) && (TimerUtils.elapsed(Time, 10000L))) {
				Count = 0;
				Time = System.currentTimeMillis();
			}
			if (Count > 4) {
				Count = 0;
				User user = Exile.getUserManager().getUser(player.getUniqueId());
				user.setVL(this, user.getVL(this) + 1);
				this.Alert(player, "*");
				this.FallDistance.put(player.getUniqueId(), Double.valueOf(0.0D));
				
			}
			this.NoFallTicks.put(player.getUniqueId(),
					new AbstractMap.SimpleEntry(Long.valueOf(Time), Integer.valueOf(Count)));
		}
	}

}