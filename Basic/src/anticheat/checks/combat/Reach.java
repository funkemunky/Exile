package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketUseEntityEvent;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.MiscUtils;
import anticheat.utils.PlayerUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { PlayerMoveEvent.class, EntityDamageByEntityEvent.class, PacketUseEntityEvent.class,
		PlayerQuitEvent.class })
public class Reach extends Checks {

	public Map<Player, Integer> count;
	public Map<Player, Map.Entry<Double, Double>> offsets;
	public Map<Player, Long> reachTicks;
	public Map<Player, Map.Entry<Integer, Long>> reachbTicks;
	private Map<Player, Long> lastHit;

	public Reach() {
		super("Reach", ChecksType.COMBAT, Exile.getAC(), 5, true, true);

		this.count = new HashMap<Player, Integer>();
		this.offsets = new WeakHashMap<Player, Map.Entry<Double, Double>>();
		this.reachTicks = new HashMap<Player, Long>();
		this.lastHit = new WeakHashMap<Player, Long>();
		this.reachbTicks = new HashMap<Player, Map.Entry<Integer, Long>>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}
		
		if (event instanceof PacketUseEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			
			if (e.getCause() != DamageCause.ENTITY_ATTACK) {
				return;
			}
			if (!(e.getEntity() instanceof Player)) {
				return;
			}
			if (!(e.getDamager() instanceof Player)) {
				return;
			}
			Player damager = (Player) e.getDamager();
			Player player = (Player) e.getEntity();
			double yDif = Math.abs(PlayerUtils.getEyeLocation(damager).getY() - PlayerUtils.getEyeLocation(player).getY());
			double Reach = MathUtils.trim(2,
					damager.getEyeLocation().distance(player.getEyeLocation()));

			if (Exile.getAC().getPing().getTPS() < 17D) {
				return;
			}
			if (damager.getAllowFlight()) {
				return;
			}
			if (player.getAllowFlight()) {
				return;
			}

			if (Exile.getAC().getPing().getPing(player) > 550 || Exile.getAC().getPing().getPing(damager) > 550) {
				return;
			}

			if (!count.containsKey(damager)) {
				count.put(damager, 0);
			}

			int Count = count.get(damager);
			long Time = System.currentTimeMillis();
			double MaxReach = 2.9;
			double YawDifference = Math.abs(damager.getEyeLocation().getYaw() - player.getEyeLocation().getYaw());
			double lastHorizontal = 0.0D;
			if (this.offsets.containsKey(damager)) {
				lastHorizontal = ((Double) (this.offsets.get(damager)).getValue()).doubleValue();
			}
			MaxReach += YawDifference > 100  && player.getVelocity().getY() < 0.2 ? YawDifference * 0.01 : YawDifference * 0.001;
			MaxReach += lastHorizontal * 0.8;
			
			MaxReach += yDif > 1.0 ? 1.0 : yDif > 0.0 ? yDif * 0.28 : 0D;
			MaxReach += damager.getWalkSpeed() <= 0.2 ? 0 : damager.getWalkSpeed() - 0.2;
			int PingD = Exile.getAC().getPing().getPing(damager);
			int PingP = Exile.getAC().getPing().getPing(player);
			MaxReach += ((PingD + PingP) / 2) * 0.0024;
			if (TimerUtils.elapsed(Time, 30000)) {
				count.remove(damager);
				Time = System.currentTimeMillis();
			}
			if (Reach > MaxReach) {
				count.put(damager, Count + 2);
			} else {
				if (Count >= -2) {
					count.put(damager, Count - 1);
				}
			}
			User user = Exile.getAC().getUserManager().getUser(damager.getUniqueId());
			if (Count > 6 && Reach > MaxReach) {
				count.remove(damager);
				this.alert(damager, Color.Gray + "Reason: " + Color.White + "Counted " + Color.Gray + "Ping: " + Color.White + Exile.getAC().getPing().getPing(damager) +  Color.Gray + " Reach: " + Color.White
						+ Reach + Color.Gray + " > " + Color.White + MaxReach);
				user.setVL(this, user.getVL(this) + 1);
				return;
			}
		}
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			
			if (e.getCause() != DamageCause.ENTITY_ATTACK) {
				return;
			}
			if (!(e.getEntity() instanceof Player)) {
				return;
			}
			if (!(e.getDamager() instanceof Player)) {
				return;
			}
			
			Player damager = (Player) e.getDamager();
			Player player = (Player) e.getEntity();
			User userPlayer = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			User userDamager = Exile.getAC().getUserManager().getUser(damager.getUniqueId());
			
			double reach = MathUtils.trim(2,
					damager.getEyeLocation().distance(player.getEyeLocation()));
			
			if (Exile.getAC().getPing().getTPS() < 17D) {
				return;
			}
			if(userPlayer.isTeleported() || userDamager.isTeleported()) {
				return;
			}
			if (damager.getAllowFlight()) {
				return;
			}
			if (player.getAllowFlight()) {
				return;
			}

			if (Exile.getAC().getPing().getPing(player) > 550 || Exile.getAC().getPing().getPing(damager) > 550) {
				return;
			}

			double maxReach = 3.0D;
			double yDif = Math.abs(PlayerUtils.getEyeLocation(damager).getY() - PlayerUtils.getEyeLocation(player).getY());
			
			int PingD = Exile.getAC().getPing().getPing(damager);
			int PingP = Exile.getAC().getPing().getPing(player);
			
			if (Exile.getAC().getPing().getPing(player) > 550 || Exile.getAC().getPing().getPing(damager) > 550) {
				return;
			}
			
			double lastHorizontal = 0.0D;
			if (this.offsets.containsKey(damager)) {
				lastHorizontal = ((Double) (this.offsets.get(damager)).getValue()).doubleValue();
			}
			
			maxReach += yDif > 1.0 ? 1.0 : yDif > 0.0 ? yDif * 0.28 : 0D;
			maxReach += lastHorizontal * 0.442;
			maxReach += (PingD + PingP) * 0.00113;
			maxReach += MiscUtils.getOffsetOffCursor(damager, player) * 0.0008;
			
			if(reach > maxReach) {
				userDamager.setVL(this, userDamager.getVL(this) + 1);
				
				alert(damager, Color.Gray + "Reason: " + Color.White + "First Hit " + Color.Gray + "Ping: " + Color.White + Exile.getAC().getPing().getPing(damager) +  Color.Gray + " Reach: " + Color.White
						+ reach + Color.Gray + " > " + Color.White + maxReach);
			}
		}
		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			if (offsets.containsKey(e.getPlayer())) {
				offsets.remove(e.getPlayer());
			}
			if (count.containsKey(e.getPlayer())) {
				count.remove(e.getPlayer());
			}
			if (reachTicks.containsKey(e.getPlayer())) {
				reachTicks.remove(e.getPlayer());
			}
			if (this.lastHit.containsKey(e.getPlayer())) {
				this.lastHit.remove(e.getPlayer());
			}
		}
		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) {
				return;
			}
			double OffsetXZ = MathUtils.offset(MathUtils.getHorizontalVector(e.getFrom().toVector()),
					MathUtils.getHorizontalVector(e.getTo().toVector()));
			double horizontal = Math.sqrt(Math.pow(e.getTo().getX() - e.getFrom().getX(), 2.0)
					+ Math.pow(e.getTo().getZ() - e.getFrom().getZ(), 2.0));
			this.offsets.put(e.getPlayer(),
					new AbstractMap.SimpleEntry<Double, Double>(OffsetXZ, horizontal));
		}
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if(!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
				return;
			}
			
			if(e.getCause() != DamageCause.ENTITY_ATTACK) {
				return;
			}
			
			if(Exile.getAC().getPing().getTPS() < 17) {
				return;
			}
			
			Player player = (Player) e.getDamager();
			Player damaged = (Player) e.getEntity();
			
		    User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			if(Exile.getAC().getPing().getPing(player) > 500 || Exile.getAC().getPing().getPing(damaged) > 500) {
				return;
			}
			double yDif = Math.abs(PlayerUtils.getEyeLocation(player).getY() - PlayerUtils.getEyeLocation(damaged).getY());
			double Reach = MathUtils.trim(2, PlayerUtils.getEyeLocation(player).distance(damaged.getEyeLocation()) - yDif);
			
			int verbose = 0;
			long Time = System.currentTimeMillis();

			if (this.reachbTicks.containsKey(player)) {
				verbose = ((Integer) ( this.reachbTicks.get(player)).getKey()).intValue();
				Time = ((Long) ( this.reachbTicks.get(player)).getValue()).longValue();
			}
			
			if(Reach > 3.08 && TimerUtils.elapsed(this.lastHit.getOrDefault(player.getUniqueId(), System.currentTimeMillis()), 1080L)) {
				verbose++;
				Time = TimerUtils.nowlong();
			}
			
			if(TimerUtils.elapsed(Time, 45000L)) {
				verbose = 0;
			}
			
			if(verbose > 7) {
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player, Color.Gray + "Reason: " + Color.Green + "Average Hits " + Color.Gray + "Ping: " + Color.White + Exile.getAC().getPing().getPing(player) +  Color.Gray + " Reach: " + Color.Green
						+ Reach + Color.Gray + " > " + Color.Green + "3.08");
			}
			
			reachbTicks.put(player, new AbstractMap.SimpleEntry<Integer, Long>(verbose, Time));
			lastHit.put(player, System.currentTimeMillis());
		}
	}
}