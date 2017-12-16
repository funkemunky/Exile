package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketUseEntityEvent;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.PlayerUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { EntityDamageByEntityEvent.class, PlayerQuitEvent.class })
public class Reach extends Checks {

	public Map<Player, Map.Entry<Integer, Long>> count;
	public Map<Player, Integer> bCount;
	public Map<Player, Integer> useCount;

	public Reach() {
		super("Reach", ChecksType.COMBAT, Exile.getAC(), 3, true, true);

		this.count = new WeakHashMap<Player, Map.Entry<Integer, Long>>();
		this.bCount = new WeakHashMap<Player, Integer>();
		this.useCount = new WeakHashMap<Player, Integer>();
	}

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
			if (!(e.getDamager() instanceof Player)) {
				return;
			}
			if (!(e.getEntity() instanceof Player)) {
				return;
			}

			Player damager = (Player) e.getDamager();
			Player player = (Player) e.getEntity();
			double yDif = Math
					.abs(PlayerUtils.getEyeLocation(damager).getY() - PlayerUtils.getEyeLocation(player).getY());
			double Reach = MathUtils.trim(2,
					PlayerUtils.getEyeLocation(damager).distance(PlayerUtils.getEyeLocation(player)) - 0.32);

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

			int verbose = 0;
			long Time = System.currentTimeMillis();
			if (count.containsKey(player)) {
				verbose = count.get(player).getKey().intValue();
				Time = count.get(player).getValue().longValue();
			}
			double MaxReach = 3.0D;
			double YawDifference = Math
					.abs(180 - Math.abs(damager.getLocation().getYaw() - player.getLocation().getYaw()));
			User user = Exile.getAC().getUserManager().getUser(damager.getUniqueId());
			double lastHorizontal = user.getDeltaXZ();

			int PingD = Exile.getAC().getPing().getPing(damager);
			int PingP = Exile.getAC().getPing().getPing(player);

			MaxReach += YawDifference > 100 && user.getDeltaY() < 0.2 ? YawDifference * 0.01 : YawDifference * 0.001;
			MaxReach += damager.getWalkSpeed() <= 0.2 ? 0 : damager.getWalkSpeed() - 0.2;
			Reach -= yDif > 2.0 ? yDif * 0.9 : yDif > 1.0 ? yDif * 0.5 : yDif > 0 ? yDif * 0.2 : 0;
			MaxReach += lastHorizontal * 0.5;
			MaxReach += ((PingD + PingP) / 2) * 0.0024;
			MaxReach += user.getHits() > 2 && player.getVelocity().length() > 0.21 && user.getDeltaXZ() > 0.21 ? 1.0D
					: 0D;

			if (Reach > MaxReach) {
				verbose = user.getHits() < 3 ? verbose + 2 : verbose + 1;
			} else {
				verbose = verbose > -5 ? verbose-- : verbose;
			}

			if (TimerUtils.elapsed(Time, 13000L)) {
				verbose = 0;
				Time = TimerUtils.nowlong();
			}

			if (verbose > 24 && Reach > MaxReach) {
				user.setVL(this, user.getVL(this) + 1);

				verbose = 0;

				alert(damager, Color.Gray + "Reason: " + Color.White + "Counted" + Color.Gray + " Surplus: "
						+ Color.White + MathUtils.trim(2, Reach - MaxReach));
			}

			count.put(player, new AbstractMap.SimpleEntry<Integer, Long>(verbose, Time));
		}
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

			if (e.getCause() != DamageCause.ENTITY_ATTACK) {
				return;
			}
			if (!(e.getDamager() instanceof Player)) {
				return;
			}
			if (!(e.getEntity() instanceof Player)) {
				return;
			}

			Player damager = (Player) e.getDamager();
			Player player = (Player) e.getEntity();
			double yDif = Math
					.abs(PlayerUtils.getEyeLocation(damager).getY() - PlayerUtils.getEyeLocation(player).getY());
			double Reach = MathUtils.trim(2,
					(PlayerUtils.getEyeLocation(damager).distance(player.getEyeLocation()) - 0.32) - yDif);

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

			if (!bCount.containsKey(damager)) {
				bCount.put(damager, 0);
			}

			int Count = bCount.get(damager);

			double MaxReach = 3.0D;
			double YawDifference = Math
					.abs(180 - Math.abs(damager.getLocation().getYaw() - player.getLocation().getYaw()));
			User user = Exile.getAC().getUserManager().getUser(damager.getUniqueId());
			double lastHorizontal = user.getDeltaXZ();

			MaxReach += YawDifference > 100 && player.getVelocity().getY() < 0.2 ? YawDifference * 0.01
					: YawDifference * 0.001;
			MaxReach += lastHorizontal * 0.7;

			MaxReach += damager.getWalkSpeed() <= 0.2 ? 0 : damager.getWalkSpeed() - 0.2;
			int PingD = Exile.getAC().getPing().getPing(damager);
			int PingP = Exile.getAC().getPing().getPing(player);
			MaxReach += ((PingD + PingP) / 2) * 0.0024;

			if (Reach > MaxReach) {
				bCount.put(damager, Count + 1);
			} else {
				if (Count > -2) {
					bCount.put(damager, Count - 1);
				}
			}
			if (Count > 6 && Reach > MaxReach) {
				bCount.remove(damager);
				alert(damager,
						Color.Gray + "Reason: " + Color.White + "Ogre " + Color.Gray + "Ping: " + Color.White
								+ Exile.getAC().getPing().getPing(damager) + Color.Gray + " Reach: " + Color.White
								+ Reach + Color.Gray + " > " + Color.White + MaxReach);
				user.setVL(this, user.getVL(this) + 1);
				return;
			}
		}
		if (event instanceof PacketUseEntityEvent) {
			PacketUseEntityEvent e = (PacketUseEntityEvent) event;

			if (e.getAction() != EntityUseAction.ATTACK) {
				return;
			}

			Player player = e.getAttacker();
			Entity damaged = e.getAttacked();
			double yDif = Math.abs(PlayerUtils.getEyeLocation(player).getY()
					- damaged.getLocation().clone().add(0.0D, 1.0D, 0.0D).getY());
			double Reach = MathUtils.trim(2,
					(PlayerUtils.getEyeLocation(player).distance(damaged.getLocation().clone().add(0.0D, 1.0D, 0.0D))
							- 0.32) - yDif);

			if (Exile.getAC().getPing().getPing(player) > 425) {
				return;
			}
			
			if(e.getAttacked() instanceof Player) {
				if(Exile.getAC().getPing().getPing((Player) e.getAttacked()) > 425) {
					return;
				}
			}
			
			if(Exile.getAC().getPing().getTPS() < 17) {
				return;
			}
			
			int verbose = this.useCount.getOrDefault(player, 0);
			
			if(Reach > 6.0) {
				verbose++;
			}
			
			if(verbose > 5) {
				User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
				
				user.setVL(this, user.getVL(this) + 1);
				
				alert(player,
						Color.Gray + "Reason: " + Color.White + "Blatant " + Color.Gray + "Ping: " + Color.White
								+ Exile.getAC().getPing().getPing(player) + Color.Gray + " Reach: " + Color.White
								+ Reach + Color.Gray + " > " + Color.White + "6.0");
			}

		}
		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			if (count.containsKey(e.getPlayer())) {
				count.remove(e.getPlayer());
			}
			if (bCount.containsKey(e.getPlayer())) {
				bCount.remove(e.getPlayer());
			}
			if(useCount.containsKey(e.getPlayer())) {
				useCount.remove(e.getPlayer());
			}
		}
	}
}