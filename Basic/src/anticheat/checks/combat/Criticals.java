package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.MiscUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { EntityDamageByEntityEvent.class, PlayerMoveEvent.class, PlayerQuitEvent.class })
public class Criticals extends Checks {

	public Map<UUID, Map.Entry<Integer, Long>> criticals;
	private Map<UUID, Double> falldistances;

	public Criticals() {
		super("Criticals", ChecksType.COMBAT, Exile.getAC(), 4, true, true);

		this.criticals = new WeakHashMap<UUID, Map.Entry<Integer, Long>>();
		this.falldistances = new HashMap<UUID, Double>();
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
			
			if(this.criticals.containsKey(uuid)) {
				this.criticals.remove(uuid);
			}
			
			if(this.falldistances.containsKey(uuid)) {
				this.falldistances.remove(uuid);
			}
			
		}
		
		if(event instanceof PlayerMoveEvent) {
			
			PlayerMoveEvent e = (PlayerMoveEvent) event;
	        double Falling = 0.0;
	        Player player = e.getPlayer();
	        
	        if (!player.isOnGround() && e.getFrom().getY() > e.getTo().getY()) {
	        	
	            if (this.falldistances.containsKey(player.getUniqueId())) {
	                Falling = this.falldistances.get(player.getUniqueId());
	            }
	            
	            Falling += e.getFrom().getY() - e.getTo().getY();
	        }
	        
	        this.falldistances.put(player.getUniqueId(), Falling);
	        
		}

		if (event instanceof EntityDamageByEntityEvent) {
			
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			
			if (e.isCancelled()) {
				return;
			}

			if (!(e.getDamager() instanceof Player)) {
				return;
			}
			
			if (!e.getCause().equals((Object) EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
				return;
			}
			
			Player player = (Player) e.getDamager();
			
			if (player.getAllowFlight()) {
				return;
			}
			
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());

			if ((System.currentTimeMillis() - user.getTookVelocity()) < 2000L) {
				return;
			}
			
			if (MiscUtils.slabsNear(player.getLocation())) {
				return;
			}
			
			Location pL = player.getLocation().clone();
			pL.add(0.0, player.getEyeHeight() + 1.0, 0.0);
			
			if (MiscUtils.blocksNear(pL)) {
				return;
			}
			
			int verbose = 0;
			long Time = System.currentTimeMillis();
			
			if (this.criticals.containsKey(player.getUniqueId())) {
				verbose = this.criticals.get(player.getUniqueId()).getKey();
				Time = this.criticals.get(player.getUniqueId()).getValue();
			}
			
			if (!this.falldistances.containsKey(player.getUniqueId())) {
				return;
			}
			
			double realfalldistances = this.falldistances.get(player.getUniqueId());
			
			verbose = (double) player.getFallDistance() > 0.0 && !player.isOnGround() && realfalldistances == 0.0
					? ++verbose : 0;
			
			if (this.criticals.containsKey(player.getUniqueId()) && TimerUtils.elapsed(Time, 10000)) {
				verbose = 0;
				Time = TimerUtils.nowlong();
			}
			if (verbose >= 2) {
				verbose = 0;
				user.setVL(this, user.getVL(this) + 1);
				alert(player, "*");
			}
			this.criticals.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(verbose, Time));
			
		}
		
	}

}
