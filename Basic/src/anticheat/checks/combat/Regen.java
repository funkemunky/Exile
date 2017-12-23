package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Difficulty;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { PlayerQuitEvent.class, EntityRegainHealthEvent.class })
public class Regen extends Checks {

	public Map<UUID, Map.Entry<Integer, Long>> verbose;

	public Regen() {
		super("Regen", ChecksType.COMBAT, Exile.getAC(), 5, true, true);

		this.verbose = new WeakHashMap<UUID, Map.Entry<Integer, Long>>();
	}

	public boolean checkFastHeal(Player player) {
		User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
		if (user.getLastHeal() != 0) {
			long l = user.getLastHeal();
			user.setLastHeal(0L);
			if (System.currentTimeMillis() - l < 2500L) {
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
			if (this.verbose.containsKey(player.getUniqueId())) {
				this.verbose.remove(player.getUniqueId());
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
			if (Exile.getAC().getPing().getTPS() < 17) {
				return;
			}
			Player player = (Player) e.getEntity();
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			if (player.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
				return;
			}
			if(player.getFoodLevel() < 20) {
				return;
			}
			if((System.currentTimeMillis() - user.getLastPotionSplash()) < 215L) {
				return;
			}
			int Count = 0;
			long Time = System.currentTimeMillis();
			if (this.verbose.containsKey(player.getUniqueId())) {
				Count = this.verbose.get(player.getUniqueId()).getKey().intValue();
				Time = this.verbose.get(player.getUniqueId()).getValue().longValue();
			}
			if (checkFastHeal(player)) {
				if (!player.getLocation().getBlock().getType().isSolid()) {
					Count++;
					e.setCancelled(true);
				}
			}
			if (Count > 4) {
				if (!player.getLocation().getBlock().getType().isSolid()) {
					user.setVL(this, user.getVL(this) == 0 ? 1 : user.getVL(this) + 1);
					alert(player, "*");
					this.advancedalert(player, 99.9);
				}
				Count = 0;
			}
			if ((this.verbose.containsKey(player.getUniqueId())) && (TimerUtils.elapsed(Time, 60000L))) {
				Count = 0;
				Time = TimerUtils.nowlong();
			}
			this.verbose.put(player.getUniqueId(),
					new AbstractMap.SimpleEntry<Integer, Long>(Integer.valueOf(Count), Long.valueOf(Time)));
		}
	}

}
