package anticheat.packets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import anticheat.Exile;
import anticheat.packets.events.PacketBlockPlacementEvent;
import anticheat.packets.events.PacketEntityActionEvent;
import anticheat.packets.events.PacketHeldItemChangeEvent;
import anticheat.packets.events.PacketKeepAliveEvent;
import anticheat.packets.events.PacketKillauraEvent;
import anticheat.packets.events.PacketPlayerEvent;
import anticheat.packets.events.PacketPlayerType;
import anticheat.packets.events.PacketReadVelocityEvent;
import anticheat.packets.events.PacketSwingArmEvent;
import anticheat.packets.events.PacketUseEntityEvent;
import anticheat.packets.events.PacketKeepAliveEvent.PacketKeepAliveType;

public class PacketCore {
	public Exile Exile;

	public PacketCore(Exile Exile) {
		super();
		this.Exile = Exile;

		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(this.Exile,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY }) {
			public void onPacketReceiving(final PacketEvent event) {
				final PacketContainer packet = event.getPacket();
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				try {
					Object playEntity = getNMSClass("PacketPlayInUseEntity");
					String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
					if (version.contains("1_7")) {
						if (packet.getHandle() == playEntity) {
							if (playEntity.getClass().getMethod("c") == null) {
								return;
							}
						}
					} else {
						if (packet.getHandle() == playEntity) {
							if (playEntity.getClass().getMethod("a") == null) {
								return;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				EnumWrappers.EntityUseAction type;
				try {
					type = packet.getEntityUseActions().read(0);
				} catch (Exception ex) {
					return;
				}

				final int entityId = (int) packet.getIntegers().read(0);
				Entity entity = null;
				for (final Entity entityentity : player.getWorld().getEntities()) {
					if (entityentity.getEntityId() == entityId) {
						entity = entityentity;
					}
				}
				Bukkit.getServer().getPluginManager().callEvent((Event) new PacketUseEntityEvent(type, player, entity));
				if (type == EntityUseAction.ATTACK) {
					Bukkit.getServer().getPluginManager()
							.callEvent(new PacketKillauraEvent(player, PacketPlayerType.USE));
				}
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(this.Exile,
				new PacketType[] { PacketType.Play.Client.POSITION_LOOK }) {
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent((Event) new PacketPlayerEvent(player,
						(double) event.getPacket().getDoubles().read(0),
						(double) event.getPacket().getDoubles().read(1),
						(double) event.getPacket().getDoubles().read(2), (float) event.getPacket().getFloat().read(0),
						(float) event.getPacket().getFloat().read(1), PacketPlayerType.POSLOOK));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(this.Exile,
				new PacketType[] { PacketType.Play.Server.ENTITY_VELOCITY }) {
			public void onPacketSending(PacketEvent event) {
				 Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				if (player == null) {
					return;
				}
				double x = packet.getIntegers().read(1).doubleValue() / 8000.0;
				double y = packet.getIntegers().read(2).doubleValue() / 8000.0;
				double z = packet.getIntegers().read(3).doubleValue() / 8000.0;
				Bukkit.getServer().getPluginManager().callEvent(new PacketReadVelocityEvent(player, x, y, z));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(
				(PacketListener) new PacketAdapter(this.Exile, new PacketType[] { PacketType.Play.Client.LOOK }) {
					public void onPacketReceiving(final PacketEvent event) {
						Player player = event.getPlayer();

						if (player == null) {
							return;
						}

						Bukkit.getServer().getPluginManager()
								.callEvent(new PacketPlayerEvent(player, event.getPacket().getDoubles().read(0),
										event.getPacket().getDoubles().read(1), event.getPacket().getDoubles().read(2),
										event.getPacket().getFloat().read(0), event.getPacket().getFloat().read(1),
										PacketPlayerType.POSLOOK));
					}
				});
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(this.Exile,
				new PacketType[] { PacketType.Play.Client.POSITION }) {
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent(
						(Event) new PacketPlayerEvent(player, (double) event.getPacket().getDoubles().read(0),
								(double) event.getPacket().getDoubles().read(1),
								(double) event.getPacket().getDoubles().read(2), player.getLocation().getYaw(),
								player.getLocation().getPitch(), PacketPlayerType.POSITION));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(this.Exile,
				new PacketType[] { PacketType.Play.Client.ENTITY_ACTION }) {
			public void onPacketReceiving(final PacketEvent event) {
				final PacketContainer packet = event.getPacket();
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager()
						.callEvent((Event) new PacketEntityActionEvent(player, (int) packet.getIntegers().read(1)));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(this.Exile,
				new PacketType[] { PacketType.Play.Client.KEEP_ALIVE }) {
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent((Event) new PacketKeepAliveEvent(player, PacketKeepAliveType.CLIENT));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(this.Exile,
				new PacketType[] { PacketType.Play.Server.KEEP_ALIVE }) {
			public void onPacketSending(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent((Event) new PacketKeepAliveEvent(player, PacketKeepAliveType.SERVER));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(this.Exile,
				new PacketType[] { PacketType.Play.Client.ARM_ANIMATION }) {
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager()
						.callEvent(new PacketKillauraEvent(player, PacketPlayerType.ARM_SWING));
				Bukkit.getServer().getPluginManager().callEvent((Event) new PacketSwingArmEvent(event, player));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(this.Exile,
				new PacketType[] { PacketType.Play.Client.HELD_ITEM_SLOT }) {
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent((Event) new PacketHeldItemChangeEvent(event, player));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(this.Exile,
				new PacketType[] { PacketType.Play.Client.BLOCK_PLACE }) {
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent((Event) new PacketBlockPlacementEvent(event, player));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(
				(PacketListener) new PacketAdapter(this.Exile, new PacketType[] { PacketType.Play.Client.FLYING }) {
					public void onPacketReceiving(final PacketEvent event) {
						final Player player = event.getPlayer();
						if (player == null) {
							return;
						}
						Bukkit.getServer().getPluginManager()
								.callEvent((Event) new PacketPlayerEvent(player, player.getLocation().getX(),
										player.getLocation().getY(), player.getLocation().getZ(),
										player.getLocation().getYaw(), player.getLocation().getPitch(),
										PacketPlayerType.FLYING));
					}
				});
	}

	public Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}