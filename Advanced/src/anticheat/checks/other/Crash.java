package anticheat.checks.other;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketBlockPlacementEvent;
import anticheat.packets.events.PacketHeldItemChangeEvent;
import anticheat.packets.events.PacketSwingArmEvent;
import anticheat.user.User;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {PacketSwingArmEvent.class, PacketHeldItemChangeEvent.class, PacketBlockPlacementEvent.class, PlayerQuitEvent.class})
public class Crash extends Checks {

	public Map<UUID, Map.Entry<Integer, Long>> faggotTicks;
	public Map<UUID, Map.Entry<Integer, Long>> faggot2Ticks;
	public Map<UUID, Map.Entry<Integer, Long>> faggot3Ticks;
	public List<UUID> faggots;

	public Crash() {
		super("Crash", ChecksType.OTHER, Exile.getAC(), 1, true, true);

		this.faggotTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.faggot2Ticks = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.faggot3Ticks = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.faggots = new ArrayList<UUID>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}

		if (event instanceof PacketSwingArmEvent) {
			PacketSwingArmEvent e = (PacketSwingArmEvent) event;
			Player faggot = e.getPlayer();
			int Count = 0;
			long Time = System.currentTimeMillis();
			if (this.faggots.contains(faggot.getUniqueId())) {
				e.getPacketEvent().setCancelled(true);
			} else {
				if (this.faggotTicks.containsKey(faggot.getUniqueId())) {
					Count = this.faggotTicks.get(faggot.getUniqueId()).getKey();
					Time = this.faggotTicks.get(faggot.getUniqueId()).getValue();
				}
				++Count;
				if (this.faggotTicks.containsKey(faggot.getUniqueId()) && TimerUtils.elapsed(Time, 100L)) {
					Count = 0;
					Time = TimerUtils.nowlong();
				}
				if (Count > 2000) {
					User user = Exile.getUserManager().getUser(faggot.getUniqueId());
					user.setVL(this, user.getVL(this) + 1);
					this.Alert(faggot, "*");
					this.faggots.add(faggot.getUniqueId());
				}
			}
			this.faggotTicks.put(faggot.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
		}
		if (event instanceof PacketHeldItemChangeEvent) {
			PacketHeldItemChangeEvent e = (PacketHeldItemChangeEvent) event;
			Player faggot = e.getPlayer();
			int Count = 0;
			long Time = System.currentTimeMillis();
			if (this.faggots.contains(faggot.getUniqueId())) {
				e.getPacketEvent().setCancelled(true);
			} else {
				if (this.faggot2Ticks.containsKey(faggot.getUniqueId())) {
					Count = this.faggot2Ticks.get(faggot.getUniqueId()).getKey();
					Time = this.faggot2Ticks.get(faggot.getUniqueId()).getValue();
				}
				++Count;
				if (this.faggot2Ticks.containsKey(faggot.getUniqueId()) && TimerUtils.elapsed(Time, 100L)) {
					Count = 0;
					Time = TimerUtils.nowlong();
				}
				if (Count > 2000) {
					User user = Exile.getUserManager().getUser(faggot.getUniqueId());
					user.setVL(this, user.getVL(this) + 1);
					this.Alert(faggot, "*");
					this.faggots.add(faggot.getUniqueId());
				}
			}
			this.faggot2Ticks.put(faggot.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
		}
		if (event instanceof PacketBlockPlacementEvent) {
			PacketBlockPlacementEvent e = (PacketBlockPlacementEvent) event;
			int Count = 0;
			long Time = System.currentTimeMillis();
			Player faggot = e.getPlayer();
			if (this.faggots.contains(faggot.getUniqueId())) {
				e.getPacketEvent().setCancelled(true);
			} else {
				if (this.faggot3Ticks.containsKey(faggot.getUniqueId())) {
					Count = this.faggot3Ticks.get(faggot.getUniqueId()).getKey();
					Time = this.faggot3Ticks.get(faggot.getUniqueId()).getValue();
				}
				++Count;
				if (this.faggot3Ticks.containsKey(faggot.getUniqueId()) && TimerUtils.elapsed(Time, 100L)) {
					Count = 0;
					Time = TimerUtils.nowlong();
				}
				if (Count > 2000) {
					User user = Exile.getUserManager().getUser(faggot.getUniqueId());
					user.setVL(this, user.getVL(this) + 1);
					this.Alert(faggot, "*");
					this.faggots.add(faggot.getUniqueId());
				}
			}
			this.faggot3Ticks.put(faggot.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
		}
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			Player player = e.getPlayer();
			UUID uuid = player.getUniqueId();
			if(this.faggotTicks.containsKey(uuid)) {
				this.faggotTicks.remove(uuid);
			}
			if(this.faggot2Ticks.containsKey(uuid)) {
				this.faggot2Ticks.remove(uuid);
			}
			if(this.faggot3Ticks.containsKey(uuid)) {
				this.faggot3Ticks.containsKey(uuid);
			}
		}
	}

}
