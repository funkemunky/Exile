package anticheat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import anticheat.Fiona;
import anticheat.packets.events.PacketBlockPlacementEvent;
import anticheat.packets.events.PacketHeldItemChangeEvent;
import anticheat.packets.events.PacketKeepAliveEvent;
import anticheat.packets.events.PacketPlayerEvent;
import anticheat.packets.events.PacketSwingArmEvent;
import anticheat.user.User;

public class EventPacket implements Listener {
	
	@EventHandler
	public void packet(PacketPlayerEvent e) {
		Fiona.getAC().getchecksmanager().event(e);
	}
	
	@EventHandler
	public void swingArm(PacketSwingArmEvent e) {
		Fiona.getAC().getchecksmanager().event(e);
	}
	
	@EventHandler
	public void itemChange(PacketHeldItemChangeEvent e) {
		Fiona.getAC().getchecksmanager().event(e);
	}
	
	@EventHandler
	public void onKeepAlive(PacketKeepAliveEvent e) {
		Fiona.getAC().getchecksmanager().event(e);
	}
	
	@EventHandler
	public void blockPlace(PacketBlockPlacementEvent e) {
		Fiona.getAC().getchecksmanager().event(e);
	}
	
	@EventHandler
	public void regainHealth(EntityRegainHealthEvent e) {
		Fiona.getAC().getchecksmanager().event(e);
		
		if(e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			
			User user = Fiona.getUserManager().getUser(player.getUniqueId());
			
			user.setLastHeal(System.currentTimeMillis());
		}
	}

}
