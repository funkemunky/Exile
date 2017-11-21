package anticheat.checks.combat;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketKillauraEvent;
import anticheat.packets.events.PacketPlayerType;
import anticheat.packets.events.PacketUseEntityEvent;

@ChecksListener()
public class KillAura extends Checks {
	
	public KillAura() {
		super("KillAura", ChecksType.COMBAT, Exile.getAC(), 10, true, true);
	}
	
	@Override
	protected void onEvent(Event event) {
		if(!this.getState()) {
			return;
		}
		
		if(event instanceof PacketUseEntityEvent) {
			PacketUseEntityEvent e = (PacketUseEntityEvent) event;
			
			if(!(e.getAttacked() instanceof Player)) {
				return;
			}
			
			Player player = e.getAttacker();
			Player attacked = (Player) e.getAttacked();
			
			Vector playerLocation = player.getLocation().toVector();
	        Vector entityLocation = attacked.getLocation().toVector();

	        double difference = entityLocation.subtract(playerLocation).angle(player.getLocation().getDirection());
	        double distance = player.getLocation().distance(attacked.getLocation());
	        
	        this.debug("Difference: " + difference + " distance: " + distance);
		}
		if(event instanceof PacketKillauraEvent) {
			PacketKillauraEvent e = (PacketKillauraEvent) event;
			if(e.getType() == PacketPlayerType.ARM_SWING) {
				
			}
		}
	}

}
