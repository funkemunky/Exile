package anticheat.detections;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import anticheat.Exile;
import anticheat.utils.Color;
import anticheat.utils.JsonMessage;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class Checks {

	public static Exile ac;
	public ChecksType type;
	private String name;
	private boolean state;
	private boolean bannable;
	public static ArrayList<String> playersToBan = new ArrayList<>();
	private long delay = -1;
	private long interval = 1000;

	private int weight;

	public Checks(String name, ChecksType type, Exile ac, Integer weight, boolean state, boolean bannable) {
		this.name = name;
		Checks.ac = ac;
		this.type = type;
		this.weight = weight;
		this.bannable = bannable;
		this.state = state;
		ac.getChecks();
		ChecksManager.getDetections().add(this);
	}

	public int getWeight() {
		return weight;
	}

	public boolean isBannable() {
		return this.bannable;
	}

	public void setBannable(boolean bannable) {
		this.bannable = bannable;
	}

	public void debug(String string) {
		Bukkit.broadcastMessage(Color.Aqua + "DEBUG: " + string);

	}

	public boolean getState() {
		return this.state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public void toggle() {
		this.setState(!this.state);
	}

	public void toggleBans() {
		this.setBannable(!this.bannable);
	}

	public String getName() {
		return name;
	}

	protected void onEvent(Event event) {
	}

	public void Alert(Player p, String value) {
		long l = System.currentTimeMillis() - this.delay;
		if (l > this.interval) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (Exile.getAC().getUserManager().getUser(player.getUniqueId()).isHasAlerts() && (player.isOp() || player.hasPermission("Exile.staff"))) {
					JsonMessage msg = new JsonMessage();
					Exile.getAC();
					msg.addText(Color.translate(Exile.getAC().getConfig().getString("Alert_Message")
							.replaceAll("%prefix%", Exile.getAC().getPrefix()).replaceAll("%player%", p.getName())
							.replaceAll("%check%", getName().toUpperCase()).replaceAll("%info%", value)
							.replaceAll("%violations%",
									String.valueOf(
											Exile.getAC().getUserManager().getUser(p.getUniqueId()).getVL(this)))))
							.addHoverText(Color.Gray + "Teleport to " + p.getName() + "?")
							.setClickEvent(JsonMessage.ClickableType.RunCommand, "/tp " + p.getName());
					msg.sendToPlayer(player);
				}
			}
			this.delay = System.currentTimeMillis();

		}
	}
	
	public void advancedAlert(Player player, double chance) {
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (Exile.getAC().getUserManager().getUser(player.getUniqueId()).hasAdvancedAlerts() && (player.isOp() || player.hasPermission("Exile.staff"))) {
				JsonMessage msg = new JsonMessage();
				Exile.getAC();
				msg.addText(Color.translate(Exile.getAC().getConfig().getString("Advanced_Alert_Message")
						.replaceAll("%prefix%", Exile.getAC().getPrefix()).replaceAll("%player%", player.getName())
						.replaceAll("%check%", getName().toUpperCase())
						.replaceAll("%info%", Color.Gray + "Ping: " + Color.White + Exile.getAC().getPing().getPing(player) + Color.Gray + "TPS: " + Color.White + Exile.getAC().getPing().getTPS())
						.replaceAll("%chance%", String.valueOf(chance))
						.replaceAll("%violations%",
								String.valueOf(
										Exile.getAC().getUserManager().getUser(player.getUniqueId()).getVL(this)))))
						.addHoverText(Color.Gray + "Teleport to " + player.getName() + "?")
						.setClickEvent(JsonMessage.ClickableType.RunCommand, "/tp " + player.getName());
				msg.sendToPlayer(online);
			}
		}
	}

	public void kick(Player p) {
		if (Exile.getAC().getUserManager().getUser(p.getUniqueId()).needBan(this) && this.isBannable() && !p.isOp() && !Exile.getAC().playersBanned.contains(p)) {
			Exile.getAC().createLog(p, this);
			Exile.getAC().getServer().dispatchCommand(Exile.getAC().getServer().getConsoleSender(),
					Color.translate(Exile.getAC().getConfig().getString("Punish_Cmd")
							.replaceAll("%player%", p.getName()).replaceAll("%check%", this.getName().toUpperCase())));
			Bukkit.broadcastMessage(Color.translate(
					Exile.getAC().getConfig().getString("Punish_Broadcast").replaceAll("%player%", p.getName()).replaceAll("%check%", this.getName().toUpperCase())));
			Exile.getAC().playersBanned.add(p);
		}
	}
}