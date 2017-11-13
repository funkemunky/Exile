package anticheat.checks.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Fiona;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketReadVelocityEvent;
import anticheat.user.User;
import anticheat.utils.MathUtils;
import anticheat.utils.MiscUtils;

@ChecksListener(events = {EntityDamageByEntityEvent.class, PlayerQuitEvent.class})
public class Velocity extends Checks {
	
    private Map<UUID, ArrayList<Double>> velocity;
	public Velocity() {
		super("Velocity", ChecksType.MOVEMENT, Fiona.getAC(), 10, true, true);
		this.velocity = new HashMap<UUID, ArrayList<Double>>();
	}
	
	
	@Override
	protected void onEvent(Event event) {
		if(!getState()) {
			return;
		}
		
		if(event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			Player p = e.getPlayer();
			UUID uuid = p.getUniqueId();
			
			if(velocity.containsKey(uuid)) {
				this.velocity.remove(uuid);
			}
		}
		
		if(event instanceof PacketReadVelocityEvent) {
			PacketReadVelocityEvent e = (PacketReadVelocityEvent) event;
			Player player = e.getPlayer();
			ArrayList<Double> velocityValues = new ArrayList<Double>();
			double x = e.getX();
			double y1 = e.getY();
			double y2 = player.getVelocity().getY();
			double z = e.getZ();
			if(!MiscUtils.blocksNear(player) && (x + z) < 0.048) {
				return;
			}
			
			if(this.velocity.containsKey(player.getUniqueId())) {
				velocityValues = this.velocity.get(player.getUniqueId());
			}
			
			velocityValues.add(y2);
			System.out.print("Velocity Y1:" + y1);
			System.out.print("Velocity Y2:" + y2);
			
			
			if(velocityValues.size() >= 5) {
				double all = 0;
				for(double y3 : velocityValues) {
					all+= y3;
				}
				if((all / velocityValues.size()) < (y2 - 0.15)) {
				    User user = Fiona.getUserManager().getUser(player.getUniqueId());
				    user.setVL(this, user.getVL(this) + 1);
					this.Alert(player, MathUtils.trim(3, all / 3D) + " < " + MathUtils.trim(3, y2 - 0.15));
				}
				System.out.print("Velocity Avrage:" + all / 3D);
				velocityValues.clear();
			}
			
			this.velocity.put(player.getUniqueId(), velocityValues);
		}
	}

}
