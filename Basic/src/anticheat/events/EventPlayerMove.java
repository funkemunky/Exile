package anticheat.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import anticheat.Exile;
import anticheat.user.User;
import anticheat.utils.MiscUtils;
import anticheat.utils.PlayerUtils;

public class EventPlayerMove implements Listener {
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Exile.getAC().getChecks().event(event);
		Player p = event.getPlayer();
		User user = Exile.getAC().getUserManager().getUser(p.getUniqueId());
		
		double horizontal = Math.sqrt(Math.pow(event.getTo().getX() - event.getFrom().getX(), 2.0)
				+ Math.pow(event.getTo().getZ() - event.getFrom().getZ(), 2.0));
		double vertical = Math.sqrt(Math.pow(event.getTo().getY() - event.getFrom().getY(), 2.0));
		if(!PlayerUtils.isOnGround(p.getLocation()) && event.getFrom().getY() > event.getTo().getY()) {
			user.setRealFallDistance(user.getRealFallDistance() + MiscUtils.getVerticalDistance(event.getFrom(), event.getTo()));;
		}
		
		if(PlayerUtils.isOnGround(p.getLocation())) {
			user.setRealFallDistance(0.0D);;
		}
		user.setDeltaXZ(horizontal);
		user.setDeltaY(vertical);

		user.setLastMove(System.currentTimeMillis());
		  double deltaX = Math.abs(event.getFrom().getX() - event.getTo().getX());
	        double deltaY = Math.abs(event.getFrom().getY() - event.getTo().getY());
	        double deltaZ = Math.abs(event.getFrom().getZ() - event.getTo().getZ());
	        user.setDeltaXZ2(deltaX + deltaZ);
	        user.setDeltaY2(deltaY);

		if (PlayerUtils.isOnGround(p.getLocation())) {
			user.setGroundTicks(user.getGroundTicks() + 1);
			user.setAirTicks(0);
		} else {
			user.setGroundTicks(0);
			user.setAirTicks(user.getAirTicks() + 1);
		}
		Location blockLoc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() - 1, p.getLocation().getZ());
		if(blockLoc.getBlock().getType() == Material.ICE || blockLoc.getBlock().getType() == Material.PACKED_ICE) {
			user.setIceTicks(user.getIceTicks() + 1);
		} else {
			user.setIceTicks(user.getIceTicks() > 0 ? user.getIceTicks() - 1 : 0);
		}
		
		Location above = p.getLocation().clone().add(0.0D, 2.0D, 0.0D);
		
		if(above.getBlock().getType().isSolid()) {
			user.setBlockTicks(user.getBlockTicks() + 1);
		} else {
			user.setBlockTicks(user.getBlockTicks() > 0 ? user.getBlockTicks() - 1 : 0);
		}
		
		if(event.isCancelled()) {
			user.setTeleported(System.currentTimeMillis());
		}
	}
}