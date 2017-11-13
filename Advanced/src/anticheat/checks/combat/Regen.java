package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Difficulty;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Fiona;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { PlayerQuitEvent.class, EntityRegainHealthEvent.class })
public class Regen extends Checks {

	public Map<UUID, Map.Entry<Integer, Long>> FastHealTicks;

	public Regen() {
		super("Regen", ChecksType.COMBAT, Fiona.getAC(), 5, true, true);

		this.FastHealTicks = new WeakHashMap<UUID, Map.Entry<Integer, Long>>();
	}

	public boolean checkFastHeal(Player player) {
		User user = Fiona.getUserManager().getUser(player.getUniqueId());
		if (user.getLastHeal() != 0) {
			long l = user.getLastHeal();
			user.setLastHeal(0L);
			if (System.currentTimeMillis() - l < 2800L) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}

		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;

			Player player = e.getPlayer();
			if (this.FastHealTicks.containsKey(player.getUniqueId())) {
				this.FastHealTicks.remove(player.getUniqueId());
			}
		}

		if (event instanceof EntityRegainHealthEvent) {
			EntityRegainHealthEvent e = (EntityRegainHealthEvent) event;
			if (!e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
				return;
			}
			if (!(e.getEntity() instanceof Player)) {
				return;
			}
			if (Fiona.getAC().getPing().getTPS() < 17) {
				return;
			}
			Player player = (Player) e.getEntity();

			if (player.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
				return;
			}
			int Count = 0;
			long Time = System.currentTimeMillis();
			if (this.FastHealTicks.containsKey(player.getUniqueId())) {
				Count = ((Integer) this.FastHealTicks.get(player.getUniqueId()).getKey()).intValue();
				Time = ((Long) this.FastHealTicks.get(player.getUniqueId()).getValue()).longValue();
			}
			if (checkFastHeal(player)) {
				if (!player.getLocation().getBlock().getType().isSolid()) {
					Count++;
				}
			}
			if (Count > 2) {
				if (!player.getLocation().getBlock().getType().isSolid()) {
					User user = Fiona.getUserManager().getUser(player.getUniqueId());
					user.setVL(this, user.getVL(this) == 0 ? 1 : user.getVL(this) + 1);
					this.Alert(player, "*");
				}
				Count = 0;
			}
			if ((this.FastHealTicks.containsKey(player.getUniqueId())) && (TimerUtils.elapsed(Time, 60000L))) {
				Count = 0;
				Time = TimerUtils.nowlong();
			}
			this.FastHealTicks.put(player.getUniqueId(),
					new AbstractMap.SimpleEntry<Integer, Long>(Integer.valueOf(Count), Long.valueOf(Time)));
		}
	}

}
