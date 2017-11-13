package anticheat.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketedMovementEvent extends Event implements Cancellable {
	private static HandlerList handlerList = new HandlerList();
    private boolean cancelled = false;
    private final Player player;
    private final Location from;
    private final Location to;

    public HandlerList getHandlers() {
        return handlerList;
    }

    public PacketedMovementEvent(Player player, Location from, Location to) {
        this.player = player;
        this.to = to;
        this.from = from;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Location getTo() {
        return this.to;
    }

    public Location getFrom() {
        return this.from;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
    
}