package anticheat.checks.movement;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.MathUtils;

@ChecksListener(events = { PlayerMoveEvent.class })
public class NoFall extends Checks {
	
	private Map<Player, Integer> fallVerbose;

	public NoFall() {
		super("NoFall", ChecksType.MOVEMENT, Exile.getAC(), 9, true, true);
		
		this.fallVerbose = new WeakHashMap<Player, Integer>();	
	}

	@Override
	protected void onEvent(Event event) {

		if (!this.getState()) {
			return;
	    }

		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player p = e.getPlayer();

			Location from = e.getFrom().clone();
			Location to = e.getTo().clone();
			Vector v = to.toVector();
			double diff = v.distance(from.toVector());
			Location l = p.getLocation();
			int x = l.getBlockX();
			int y = l.getBlockY();
			int z = l.getBlockZ();
			
			int fallVerbose = this.fallVerbose.getOrDefault(p, 0);
			
			Location blockLoc = new Location(p.getWorld(), x, y - 1, z);

			if (p.getRemainingAir() == 300 && MathUtils.elapsed(Exile.getAC().getUserManager().getUser(p.getUniqueId()).isHit()) < 1000L) {
				return;
			}

			if (p.getGameMode().equals(GameMode.CREATIVE) || p.getVehicle() != null || p.getAllowFlight()) {
				return;
			}
			
			User user = Exile.getAC().getUserManager().getUser(e.getPlayer().getUniqueId());

			if (p.isOnGround() && diff > 0.8 && blockLoc.getBlock().getType().equals(Material.AIR)) {				
				user.setVL(this, user.getVL(this) + 1);
				
				alert(p, "Spoofed onGround");
			}
			
			if(p.getFallDistance() < diff && diff > 0.8 && blockLoc.getBlock().getType().equals(Material.AIR))  {	
                user.setVL(this, user.getVL(this) + 1);
				
				alert(p, "Spoofed FallDistance");
			}
			
			this.fallVerbose.put(p, fallVerbose);
		}
	}
}
