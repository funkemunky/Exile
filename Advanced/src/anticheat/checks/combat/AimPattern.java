package anticheat.checks.combat;

import java.util.ArrayList;
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
import anticheat.packets.events.PacketPlayerEvent;
import anticheat.packets.events.PacketPlayerType;

@ChecksListener(events = { PacketPlayerEvent.class, PlayerQuitEvent.class })
public class AimPattern extends Checks {

	Map<UUID, Double> lastPitch;
	Map<UUID, Double> lastYaw;
	Map<UUID, Integer> count;
	Map<String, ArrayList<Double>> yawspitch;

	public AimPattern() {
		super("AimPattern", ChecksType.COMBAT, Fiona.getAC(), 10, true, true);

		this.lastPitch = new WeakHashMap<UUID, Double>();
		this.lastYaw = new WeakHashMap<UUID, Double>();
		this.count = new WeakHashMap<UUID, Integer>();
		this.yawspitch = new WeakHashMap<String, ArrayList<Double>>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}
		
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			Player player = e.getPlayer();
			UUID uuid = player.getUniqueId();
			
			if(this.lastPitch.containsKey(uuid)) {
				this.lastPitch.remove(uuid);
			}
			if(this.lastYaw.containsKey(uuid)) {
				this.lastYaw.remove(uuid);
			}
			if(this.count.containsKey(uuid)) {
				this.count.remove(uuid);
			}
			if(this.yawspitch.containsKey(uuid)) {
				this.yawspitch.remove(uuid);
			}
		}

		if (event instanceof PacketPlayerEvent) {
			PacketPlayerEvent e = (PacketPlayerEvent) event;
			if (e.getType() != PacketPlayerType.POSLOOK) {
				return;
			}
			if (e.getPlayer().getVehicle() != null) {
				return;
			}
			Player player = e.getPlayer();
			int Count = this.count.getOrDefault(player.getUniqueId(), 0);
			double pitch = player.getLocation().getPitch();
			double yaw = player.getLocation().getYaw();
			ArrayList<Double> yaws = new ArrayList<Double>();
			ArrayList<Double> pitches = new ArrayList<Double>();

			if (this.yawspitch.containsKey("Yaw")) {
				yaws = this.yawspitch.get("Yaw");
			}
			if (this.yawspitch.containsKey("Pitch")) {
				pitches = this.yawspitch.get("Pitch");
			}
			double lastPitch = this.lastPitch.getOrDefault(player.getUniqueId(), (double) -90);
			double lastYaw = this.lastYaw.getOrDefault(player.getUniqueId(), (double) -90);

			yaws.add(Math.abs(lastYaw - yaw));
			pitches.add(Math.abs(lastPitch - pitch));
			if (lastPitch != -90) {
				if (Math.abs(pitch - lastPitch) < 0.000001) {
					Count += 2;
					if (Math.abs(pitch - lastPitch) < 0.0009) {
						Count += 1;
					} else {
						Count = Count > 200 ? Count -= 2 : Count;
						Count = Count > 0 && Count <= 200 ? Count -= 1 : Count;
					}
				}
				if (Count > 420) {
					this.Alert(player, "Pitch Difference");
					Count = 0;
				}
				this.count.put(player.getUniqueId(), Count);
				this.lastPitch.put(player.getUniqueId(), pitch);
				this.lastYaw.put(player.getUniqueId(), yaw);
				this.yawspitch.put("Yaw", yaws);
				this.yawspitch.put("Pitch", pitches);
			}
		}
	}
}
