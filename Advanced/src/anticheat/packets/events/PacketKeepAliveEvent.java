package anticheat.packets.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketKeepAliveEvent extends Event {
	public Player Player;
	private static final HandlerList handlers;
	private PacketKeepAliveType type;

	static {
		handlers = new HandlerList();
	}

	public PacketKeepAliveEvent(final Player Player, PacketKeepAliveType type) {
		super();
		this.Player = Player;
		this.type = type;
	}

	public Player getPlayer() {
		return this.Player;
	}
	
	public PacketKeepAliveType getType() {
		return this.type;
	}

	public HandlerList getHandlers() {
		return PacketKeepAliveEvent.handlers;
	}

	public static HandlerList getHandlerList() {
		return PacketKeepAliveEvent.handlers;
	}
	
	public enum PacketKeepAliveType {
		SERVER, CLIENT;
	}
}