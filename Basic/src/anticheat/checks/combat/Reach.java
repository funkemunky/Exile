package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.ArrayList;
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
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.PlayerUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { PlayerMoveEvent.class, EntityDamageByEntityEvent.class,
		PlayerQuitEvent.class })
public class Reach extends Checks {

	public Map<Player, Integer> count;
	public Map<Player, Map.Entry<Double, Double>> offsets;
	public Map<Player, Long> reachTicks;
	public Map<Player, ArrayList<Double>> reachs;
	private ArrayList<Player> projectileHit;

	public Reach() {
		super("Reach", ChecksType.COMBAT, Exile.getAC(), 11, true, true);

		this.count = new HashMap<Player, Integer>();
		this.offsets = new WeakHashMap<Player, Map.Entry<Double, Double>>();
		this.reachTicks = new HashMap<Player, Long>();
		this.projectileHit = new ArrayList<Player>();
		this.reachs = new HashMap<Player, ArrayList<Double>>();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}
		if (event instanceof EntityDamageByEntityEvent) {
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
					(PlayerUtils.getEyeLocation(damager).distance(player.getEyeLocation()) - 0.32) - yDif);

			double Difference;

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
			double MaxReach = 3.2;
			double YawDifference = Math.abs(damager.getEyeLocation().getYaw() - player.getEyeLocation().getYaw());
			double lastHorizontal = 0.0D;
			if (this.offsets.containsKey(damager)) {
				lastHorizontal = ((Double) (this.offsets.get(damager)).getValue()).doubleValue();
			}
			MaxReach += (YawDifference * 0.002);
			MaxReach += lastHorizontal * 1.3;
			if (damager.getLocation().getY() > player.getLocation().getY()) {
				Difference = damager.getLocation().getY() - player.getLocation().getY();
				MaxReach += Difference / 2.5;
			} else if (player.getLocation().getY() > damager.getLocation().getY()) {
				Difference = player.getLocation().getY() - damager.getLocation().getY();
				MaxReach += Difference / 2.5;
			}
			MaxReach += damager.getWalkSpeed() <= 0.2 ? 0 : damager.getWalkSpeed() - 0.2;
			int PingD = Exile.getAC().getPing().getPing(damager);
			int PingP = Exile.getAC().getPing().getPing(player);
			MaxReach += ((PingD + PingP) / 2) * 0.0026;
			if (TimerUtils.elapsed(Time, 30000)) {
				count.remove(damager);
				Time = System.currentTimeMillis();
			}
			if (Reach > MaxReach) {
				count.put(damager, Count + 1);
			} else {
				if (Count >= -2) {
					count.put(damager, Count - 1);
				}
			}
			User user = Exile.getUserManager().getUser(damager.getUniqueId());
			if (Count > 1 && Reach > MaxReach && Reach < 20.0) {
				count.remove(damager);
				this.Alert(damager, Color.Gray + "Ping: " + Color.White + Exile.getAC().getPing().getPing(damager) +  Color.Gray + " Reach: " + Color.Green
						+ Reach + Color.Gray + " > " + Color.Green + MaxReach);
				user.setVL(this, user.getVL(this) + 1);
				return;
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
			if (this.projectileHit.contains(e.getPlayer())) {
				this.projectileHit.remove(e.getPlayer());
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
					new AbstractMap.SimpleEntry(Double.valueOf(OffsetXZ), Double.valueOf(horizontal)));
		}
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (!(e.getDamager() instanceof Player)) {
				return;
			}
			if (e.getCause() != DamageCause.PROJECTILE) {
				return;
			}

			Player player = (Player) e.getDamager();

			this.projectileHit.add(player);
		}
	}
}
