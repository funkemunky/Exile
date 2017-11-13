package anticheat.checks.other;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Fiona;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketKeepAliveEvent;
import anticheat.packets.events.PacketKeepAliveEvent.PacketKeepAliveType;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { PacketKeepAliveEvent.class, PlayerQuitEvent.class })
public class PingSpoof extends Checks {

	public Map<UUID, Map.Entry<Long, Long>> SpoofTicks;
	public Map<UUID, Map.Entry<Long, Long>> lastSpoof;
	public Map<UUID, Map.Entry<Integer, Long>> lastlastSpoof;
	public Map<UUID, Double> violations;

	public PingSpoof() {
		super("PingSpoof", ChecksType.OTHER, Fiona.getAC(), 3, true, false);

		this.SpoofTicks = new WeakHashMap<UUID, Map.Entry<Long, Long>>();
		this.lastSpoof = new HashMap<UUID, Map.Entry<Long, Long>>();
		this.lastlastSpoof = new WeakHashMap<UUID, Map.Entry<Integer, Long>>();
		this.violations = new WeakHashMap<UUID, Double>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}

		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			Player player = e.getPlayer();
			UUID uuid = player.getUniqueId();

			if (this.SpoofTicks.containsKey(uuid)) {
				this.SpoofTicks.remove(uuid);
			}
			if (this.lastlastSpoof.containsKey(uuid)) {
				this.lastlastSpoof.remove(uuid);
			}
			if (this.lastSpoof.containsKey(uuid)) {
				this.lastSpoof.remove(uuid);
			}
		}
		if (event instanceof PacketKeepAliveEvent) {
			PacketKeepAliveEvent e = (PacketKeepAliveEvent) event;
			Player player = e.getPlayer();

			long Time = System.currentTimeMillis();
			long Time1 = System.currentTimeMillis();
			long lastSpoof = 0;
			long lastSpoof1 = 0;
			long lastTime = System.currentTimeMillis();
			double Violations = this.violations.getOrDefault(player.getUniqueId(), 0D);
			int lastPing = Fiona.getAC().getPing().getPing(player);

			if (this.SpoofTicks.containsKey(player.getUniqueId())) {
				Time = this.SpoofTicks.get(player.getUniqueId()).getKey();
				Time1 = this.SpoofTicks.get(player.getUniqueId()).getValue();
			}
			if (this.lastSpoof.containsKey(player.getUniqueId())) {
				lastSpoof = this.lastSpoof.get(player.getUniqueId()).getKey();
				lastSpoof1 = this.lastSpoof.get(player.getUniqueId()).getValue();
			}
			if (this.lastlastSpoof.containsKey(player.getUniqueId())) {
				lastTime = this.lastlastSpoof.get(player.getUniqueId()).getValue();
				lastPing = this.lastlastSpoof.get(player.getUniqueId()).getKey();
			}
			long elapsed = 0;
			long elapsed1 = 0;
			if (e.getType() == PacketKeepAliveType.CLIENT) {
				elapsed = MathUtils.elapsed(Time);
			}
			if (e.getType() == PacketKeepAliveType.SERVER) {
				elapsed1 = MathUtils.elapsed(Time1);
			}

			if (Math.abs(elapsed - Fiona.getAC().getPing().getPing(player)) < 30 && lastSpoof1 < 1780L
					&& Math.abs(lastSpoof1 + lastSpoof) > 2000L && elapsed - lastSpoof < 5
					&& Math.abs(lastTime - elapsed) < 30
					&& Math.abs(lastPing - Fiona.getAC().getPing().getPing(player)) < 16) {
				Violations++;
			} else {
				Violations = Violations >= 0.2 ? Violations -= 0.2 : Violations;
				if (Violations > 4) {
					Violations = 0D;
				}
			}

			if (Violations > 2) {
				User user = Fiona.getUserManager().getUser(player.getUniqueId());
				user.setVL(this, user.getVL(this) + 1);
				this.Alert(player, Color.Gray + "Ping Spoofed: " + Color.Green + Fiona.getAC().getPing().getPing(player)
						+ Color.Gray + " Delay: " + Color.Green + elapsed + Color.Aqua);
			}

			Time = TimerUtils.nowlong();
			Time1 = TimerUtils.nowlong();

			this.SpoofTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Long, Long>(Time, Time1));
			this.lastlastSpoof.put(player.getUniqueId(),
					new AbstractMap.SimpleEntry<Integer, Long>(Fiona.getAC().getPing().getPing(player), lastSpoof));
			this.lastSpoof.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Long, Long>(
					elapsed == 0 ? lastSpoof : elapsed, elapsed1 == 0 ? lastSpoof : elapsed1));
			this.violations.put(player.getUniqueId(), Violations);
		}
	}

}
