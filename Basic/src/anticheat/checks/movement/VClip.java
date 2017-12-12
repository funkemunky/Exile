package anticheat.checks.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.MiscUtils;

public class VClip extends Checks {

	public VClip() {
		super("VClip", ChecksType.MOVEMENT, Exile.getAC(), 20, true, false);
	}

	public static List<Material> allowed = new ArrayList<Material>();
	public ArrayList<Player> teleported = new ArrayList<Player>();
	public HashMap<Player, Location> lastLocation = new HashMap<Player, Location>();

	static {
		allowed.add(Material.PISTON_EXTENSION);
		allowed.add(Material.PISTON_STICKY_BASE);
		allowed.add(Material.PISTON_BASE);
		allowed.add(Material.SIGN_POST);
		allowed.add(Material.WALL_SIGN);
		allowed.add(Material.STRING);
		allowed.add(Material.AIR);
		allowed.add(Material.FENCE_GATE);
		allowed.add(Material.CHEST);
	}

	@EventHandler
	public void onEvent(Event event) {
		if (!getState()) {
			return;
		}
		
		if(event instanceof PlayerTeleportEvent) {
			PlayerTeleportEvent e = (PlayerTeleportEvent) event;
			
			if(e.getCause() != TeleportCause.UNKNOWN) {
				teleported.add(e.getPlayer());
			}
		}

		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;

			Player p = e.getPlayer();

			Location to = e.getTo().clone();
			Location from = e.getFrom().clone();

			if (from.getY() == to.getY()) {
				return;
			}

			if (teleported.remove(e.getPlayer())) {
				return;
			}

			if (p.getAllowFlight()) {
				return;
			}
			if (p.getVehicle() != null) {
				return;
			}

			if (e.getTo().getY() <= 0 || e.getTo().getY() >= p.getWorld().getMaxHeight()) {
				return;
			}

			if (!MiscUtils.blocksNear(p)) {
				return;
			}

			if ((p.getLocation().getY() < 0.0D) || (p.getLocation().getY() > p.getWorld().getMaxHeight())) {
				return;
			}

			double yDist = from.getY() - to.getY();
			for (double y = 0; y < Math.abs(yDist); y++) {
				Location l = yDist < -0.2 ? from.clone().add(0.0D, y, 0.0D) : to.clone().add(0.0D, y, 0.0D);
				if ((yDist > 20 || yDist < -20) && l.getBlock().getType() != Material.AIR
						&& l.getBlock().getType().isSolid() && !allowed.contains(l.getBlock().getType())) {
					p.kickPlayer("No");
					User user = Exile.getAC().getUserManager().getUser(p.getUniqueId());
					user.setVL(this, user.getVL(this) + 1);

					alert(p, l.getBlock().getType().name());
					p.teleport(from);
					return;
				}
				if (l.getBlock().getType() != Material.AIR && Math.abs(yDist) > 1.0 && l.getBlock().getType().isSolid()
						&& !allowed.contains(l.getBlock().getType())) {
					User user = Exile.getAC().getUserManager().getUser(p.getUniqueId());
					user.setVL(this, user.getVL(this) + 1);

					alert(p, l.getBlock().getType().name());
					p.teleport(lastLocation.get(p));
				} else {
					lastLocation.put(p, p.getLocation());
				}
			}
		}
	}
}
