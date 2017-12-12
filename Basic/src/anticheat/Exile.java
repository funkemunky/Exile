package anticheat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.checks.other.PME;
import anticheat.commands.CommandManager;
import anticheat.detections.Checks;
import anticheat.detections.ChecksManager;
import anticheat.events.EventInventory;
import anticheat.events.EventJoinQuit;
import anticheat.events.EventPacket;
import anticheat.events.EventPacketMoveEvent;
import anticheat.events.EventPacketReadVelocity;
import anticheat.events.EventPacketUse;
import anticheat.events.EventPlayerAttack;
import anticheat.events.EventPlayerInteractEvent;
import anticheat.events.EventPlayerMove;
import anticheat.events.EventPlayerRespawn;
import anticheat.events.EventPlayerTeleport;
import anticheat.events.EventPlayerVelocity;
import anticheat.events.EventProjectileLaunch;
import anticheat.events.EventTick;
import anticheat.events.TickEvent;
import anticheat.events.TickType;
import anticheat.gui.GUI;
import anticheat.gui.GUIListener;
import anticheat.packets.PacketCore;
import anticheat.user.User;
import anticheat.user.UserManager;
import anticheat.utils.Color;
import anticheat.utils.Ping;
import anticheat.utils.TxtFile;

public class Exile extends JavaPlugin {

	private ChecksManager checksmanager;
	private static Exile Exile;
	public PacketCore packet;
	private UserManager userManager;
	private Ping ping;
	private GUI gui;
	private CommandManager commandManager;
	BufferedWriter bw = null;
	public static String hwid;
	public ArrayList<Player> playersBanned = new ArrayList<Player>();
	File file = new File(getDataFolder(), "JD.txt");

	public static Exile getAC() {
		return Exile;
	}
	
	public Ping getPing() {
		return ping;
	}

	public ChecksManager getChecks() {
		return checksmanager;
	}

	public UserManager getUserManager() {
		return userManager;
	}
	
	public GUI getGUIManager() {
		return gui;
	}

	public String getPrefix() {
		return Color.translate(getConfig().getString("Prefix") + " ");
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public void onEnable() {
		this.getServer().getConsoleSender()
				.sendMessage(Color.translate("&7------------------------------------------"));
		Exile = this;
		this.userManager = new UserManager();
		this.ping = new Ping(this);
		this.gui = new GUI();
		this.getServer().getConsoleSender().sendMessage(Color.translate("&6 Exile &f Loaded Main class!"));
		checksmanager = new ChecksManager(this);
		this.getServer().getConsoleSender().sendMessage(Color.translate("&6 Exile &f Loaded checks!"));
		commandManager = new CommandManager();
		this.getServer().getConsoleSender().sendMessage(Color.translate("&6 Exile &f Loaded commands!"));
		this.packet = new PacketCore(this);
		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveDefaultConfig();
		}
		this.getServer().getConsoleSender().sendMessage(Color.translate("&6 Exile &f Loaded Configuration!"));

		this.getServer().getConsoleSender().sendMessage(Color.translate("&6 Exile &f Loaded players data's!"));
		commandManager.init();
		checksmanager.init();
		for (Checks check : checksmanager.getDetections()) {
			if (getConfig().contains("checks." + check.getName())) {
				check.setState(getConfig().getBoolean("checks." + check.getName() + ".enabled"));
				check.setBannable(getConfig().getBoolean("checks." + check.getName() + ".bannable"));
			} else {
				getConfig().set("checks." + check.getName() + ".enabled", check.getState());
				getConfig().set("checks." + check.getName() + ".bannable", check.isBannable());
			}
		}
		registerEvents();
		this.getServer().getConsoleSender().sendMessage(Color.translate("&6 Exile &f Registered events!"));
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
			this.getServer().getConsoleSender().sendMessage(Color.translate("&6 Exile &f Made Exile file!"));

		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				this.getServer().getConsoleSender()
						.sendMessage(Color.translate("&6 Exile &f Made JudgementDay txt file!"));
				e.printStackTrace();
			}
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			getAC().getUserManager().add(new User(player));
		}
		this.getServer().getConsoleSender()
				.sendMessage(Color.translate("&6 Exile &f added all online players to User list!"));

		this.getServer().getConsoleSender().sendMessage(Color.translate("&6 Exile &f Loaded Exile!"));
		this.getServer().getConsoleSender()
				.sendMessage(Color.translate("&7------------------------------------------"));

	}

	public void onDisable() {
		for (Checks check : checksmanager.getDetections()) {
			getConfig().set("checks." + check.getName() + ".enabled", check.getState());
			getConfig().set("checks." + check.getName() + ".bannable", check.isBannable());
			saveConfig();
		}
	}

	public void clearVLS() {
		for (Player online : Bukkit.getOnlinePlayers()) {
			getAC().getUserManager().getUser(online.getUniqueId()).getVLs().clear();
		}
	}

	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EventPlayerMove(), this);
		pm.registerEvents(new EventPlayerAttack(), this);
		pm.registerEvents(new EventTick(), this);
		pm.registerEvents(new EventJoinQuit(), this);
		pm.registerEvents(new EventPlayerVelocity(), this);
		pm.registerEvents(new EventPlayerInteractEvent(), this);
		pm.registerEvents(new EventPacketUse(), this);
		pm.registerEvents(new EventPacket(), this);
		pm.registerEvents(new EventPacketReadVelocity(), this);
		pm.registerEvents(new EventPlayerTeleport(), this);
		pm.registerEvents(new EventPlayerRespawn(), this);
		pm.registerEvents(new EventProjectileLaunch(), this);
		pm.registerEvents(new EventInventory(), this);
		pm.registerEvents(new EventPacketMoveEvent(), this);
		pm.registerEvents(new GUIListener(), this);

		PME pme = (PME) getChecks().getCheckByName("PME");
		this.getServer().getMessenger().registerIncomingPluginChannel((Plugin) this, "LOLIMAHACKER",
				(PluginMessageListener) pme);

		new BukkitRunnable() {
			public void run() {
				clearVLS();
			}
		}.runTaskTimerAsynchronously(this, 0L, 12000L);

		new BukkitRunnable() {
			public void run() {
				getServer().getPluginManager().callEvent(new TickEvent(TickType.FASTEST));

			}
		}.runTaskTimer(this, 0L, 1L);
		new BukkitRunnable() {
			public void run() {
				getServer().getPluginManager().callEvent(new TickEvent(TickType.FAST));

				for (Player player : Bukkit.getOnlinePlayers()) {
					for (Checks check : getAC().getChecks().getDetections()) {
						check.kick(player);
					}
				}
			}
		}.runTaskTimer(this, 0L, 5L);
		new BukkitRunnable() {
			public void run() {
				getServer().getPluginManager().callEvent(new TickEvent(TickType.SECOND));
			}
		}.runTaskTimer(this, 0L, 20L);
	}

	public void createLog(Player player, Checks checkBanned) {
		TxtFile logFile = new TxtFile(this, File.separator + "logs", player.getName());
		User user = getAC().getUserManager().getUser(player.getUniqueId());
		Map<Checks, Integer> Checks = user.getVLs();
		logFile.addLine("-=-=-=-=-=-=-=-=-=- " + player.getName() + " was banned for: " + checkBanned.getName()
				+ " -=-=-=-=-=-=-=-=-=-");
		logFile.addLine("Checks set off:");
		for (Checks check : Checks.keySet()) {
			Integer Violations = Checks.get(check);
			logFile.addLine("- " + check.getName() + " (" + Violations + " VL)");
		}
		logFile.write();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		this.getCommandManager().CmdHandler(sender, label, args);
		return true;
	}

}