package anticheat.checks.movement;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.AdvancedLicense;
import anticheat.utils.AdvancedLicense.ValidationType;
import anticheat.utils.PlayerUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { PlayerMoveEvent.class, PlayerQuitEvent.class })
public class Vclip extends Checks {
	public Map<Player, Location> flag = new HashMap<Player, Location>();
	public Location location;
	public TimerUtils t = new TimerUtils();

	public Vclip() {
		super("Vclip", ChecksType.MOVEMENT, Exile.getAC(), 3, true, true);
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}

		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			Player p = e.getPlayer();

			if (flag.containsKey(p)) {
				flag.remove(p);
			}
		}

		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player p = e.getPlayer();
			if (p.getGameMode().equals(GameMode.CREATIVE) || p.getVehicle() != null) {
				return;
			}
			if (PlayerUtils.isReallyOnground(p) && t.hasReached(location == null ? 500L : 2500L)) {
				flag.put(p, p.getLocation());
				location = p.getLocation();
				t.reset();
			}
			User user = Exile.getUserManager().getUser(p.getUniqueId());
			int vl = user.getVL(this);

			double diff = Math.abs(e.getTo().getY() - e.getFrom().getY());
			if (diff >= 2.0 && !PlayerUtils.isAir(p)) {
				Alert(p, "Type A");
				flag(p, flag.get(p));
				user.setVL(this, vl + 1);

			}
		}
	}

	public static void stuff() {
		ValidationType vt = new AdvancedLicense(Exile.hwid, "http://158.69.198.172/verify.php", Exile.getAC())
				.isValid();

		if (vt != ValidationType.VALID) {
			System.out.print("Disabled due to this plugin being invalid.");
			Bukkit.getPluginManager().disablePlugin(Exile.getAC());
		}
	}

	public void flag(Player p, Location l) {
		if (l != null) {
			p.teleport(l);
		}
	}
}
