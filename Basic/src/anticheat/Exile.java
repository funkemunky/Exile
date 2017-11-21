package anticheat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
import anticheat.packets.PacketCore;
import anticheat.user.User;
import anticheat.user.UserManager;
import anticheat.utils.Color;
import anticheat.utils.Ping;

public class Exile extends JavaPlugin {

	private static ChecksManager checksmanager;
	private static Exile Exile;
	public PacketCore packet;
	private static UserManager userManager;
	private Ping ping;
	private static CommandManager commandManager;
	BufferedWriter bw = null;
	public static String hwid;
	public ArrayList<Player> playersBanned = new ArrayList<Player>();
	File file = new File(getDataFolder(), "JD.txt");

	public Ping getPing() {
		return this.ping;
	}

	public static Exile getAC() {
		return Exile;
	}

	public ChecksManager getChecks() {
		return checksmanager;
	}

	public static UserManager getUserManager() {
		return userManager;
	}

	public ChecksManager getchecksmanager() {
		return checksmanager;
	}

	public String getPrefix() {
		return Color.translate(getConfig().getString("Prefix") + " ");
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	@SuppressWarnings({ "static-access" })
	public void onEnable() {
		this.getServer().getConsoleSender()
				.sendMessage(Color.translate("&7------------------------------------------"));
		Exile = this;
		this.userManager = new UserManager();
		this.ping = new Ping(this);
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
			getUserManager().add(new User(player));
		}
		this.getServer().getConsoleSender().sendMessage(Color.translate("&6 Exile &f added all online players to User list!"));
		
		this.getServer().getConsoleSender().sendMessage(Color.translate("&6 Exile &f Loaded Exile!"));
		this.getServer().getConsoleSender()
				.sendMessage(Color.translate("&7------------------------------------------"));

	}

	@SuppressWarnings("static-access")
	public void onDisable() {
		for (Checks check : checksmanager.getDetections()) {
			getConfig().set("checks." + check.getName() + ".enabled", check.getState());
			getConfig().set("checks." + check.getName() + ".bannable", check.isBannable());
			saveConfig();
		}
	}

	public void clearVLS() {
		for (Player online : Bukkit.getOnlinePlayers()) {
			getUserManager().getUser(online.getUniqueId()).getVLs().clear();
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
			@SuppressWarnings("static-access")
			public void run() {
				getServer().getPluginManager().callEvent(new TickEvent(TickType.FAST));

				for (Player player : Bukkit.getOnlinePlayers()) {
					for (Checks check : getchecksmanager().getDetections()) {
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

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		this.getCommandManager().CmdHandler(sender, label, args);
		return true;
	}

}