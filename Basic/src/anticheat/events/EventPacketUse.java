package anticheat.events;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import anticheat.Exile;
import anticheat.packets.events.PacketUseEntityEvent;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.TxtFile;

public class EventPacketUse implements Listener {
	
	private ArrayList<UUID> isData = new ArrayList<UUID>();
	private HashMap<UUID, ArrayList<Double>> data = new HashMap<UUID, ArrayList<Double>>();
	
	@EventHandler
	public void onUse(PacketUseEntityEvent e) {
		Exile.getAC().getChecks().event(e);
		
		User user = Exile.getAC().getUserManager().getUser(e.getAttacker().getUniqueId());
		if(user != null) {
			if(user.isCollectingData()) {
				isData.add(e.getAttacker().getUniqueId());
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if(!isData.contains(player.getUniqueId())) {
			return;
		}
		
		ArrayList<Double> info = data.getOrDefault(player.getUniqueId(), new ArrayList<Double>());
		Location from = e.getFrom();
		Location to = e.getTo();
		double yawDifference = Math.abs(to.getYaw() - from.getYaw());
		info.add(yawDifference);
		
		if(info.size() % 10 == 0) {
			player.sendMessage(Exile.getAC().getPrefix() + " " + Color.Yellow + info.size() * 2 + "%");
			if(info.size() >= 50) {
				player.sendMessage(Exile.getAC().getPrefix() + Color.Green + " Done!");
				player.sendMessage(Exile.getAC().getPrefix() + Color.Red + " Writing check...");
				TxtFile file = new TxtFile(Exile.getAC(), File.separator + "checks", "Killaura");
				for(double dub : info) {
					file.addLine(String.valueOf(dub));
				}
				file.write();
				player.sendMessage(Exile.getAC().getPrefix() + Color.Red + " Completed Killaura check!");
				Exile.getAC().getUserManager().getUser(player.getUniqueId()).setCollectingData(false);
			}
		}
		data.put(player.getUniqueId(), info);
		isData.remove(player.getUniqueId());
	}

}
