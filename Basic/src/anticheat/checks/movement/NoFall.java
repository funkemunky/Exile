package anticheat.checks.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.MathUtils;
import anticheat.utils.PlayerUtils;

@ChecksListener(events = { PlayerMoveEvent.class, PlayerQuitEvent.class })
public class NoFall extends Checks {
	
	private Map<UUID, Integer> verbose;
	
	public NoFall() {
		super("Nofall", ChecksType.MOVEMENT, Exile.getAC(), 9, true, true);
		
		verbose = new HashMap<UUID, Integer>();
	}

	@Override
	protected void onEvent(Event event) {

		if (!this.getState()) {
			return;
	    }
		
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			
			UUID uuid = e.getPlayer().getUniqueId();
			
			if(verbose.containsKey(uuid)) {
				verbose.remove(uuid);
			}
		}

		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player p = e.getPlayer();
			User user = Exile.getAC().getUserManager().getUser(e.getPlayer().getUniqueId());
			
			Location from = e.getFrom().clone();
			Location to = e.getTo().clone();
			
			double realDistance = user.getRealFallDistance();
			
			Location l = p.getLocation();
			
			int verbose = this.verbose.getOrDefault(p.getUniqueId(), 0);

			if (p.getRemainingAir() == 300 && MathUtils.elapsed(Exile.getAC().getUserManager().getUser(p.getUniqueId()).isHit()) < 1000L) {
				return;
			}

			if (p.getGameMode().equals(GameMode.CREATIVE) || p.getVehicle() != null || p.getAllowFlight()) {
				return;
			}
			

			if (p.isOnGround() && realDistance > 3.0 && !PlayerUtils.isOnGround(l)) {				
				user.setVL(this, user.getVL(this) + 1);
				
				alert(p, "Spoofed onGround");
			}
			if(p.getFallDistance() == 0.0 && realDistance > 3.0 && from.getY() > to.getY() && !PlayerUtils.isOnGround(l))  {	
				verbose++;
			} else {
				verbose = verbose > 0 ? verbose-- : 0;
			}
			
			if(verbose > 2) {
                user.setVL(this, user.getVL(this) + 1);
				
                verbose = 0;
				alert(p, "Spoofed FallDistance");
			}
			
			this.verbose.put(p.getUniqueId(), verbose);
		}
	}
}