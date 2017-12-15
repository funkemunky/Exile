package anticheat.checks.movement;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.events.PacketedMovementEvent;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.MiscUtils;
import anticheat.utils.PlayerUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { PlayerMoveEvent.class, PacketedMovementEvent.class, EntityDamageByEntityEvent.class, PlayerQuitEvent.class })
public class Speed extends Checks {

	public TimerUtils t = new TimerUtils();
	public Location location;
	public Map<UUID, Map.Entry<Integer, Long>> speedTicks;
	public Map<UUID, Map.Entry<Integer, Long>> tooFastTicks;
	public Map<UUID, Long> lastHit;

	public Speed() {
		super("Speed", ChecksType.MOVEMENT, Exile.getAC(), 15, true, true);
		
		this.lastHit = new ConcurrentHashMap<UUID, Long>();
		this.tooFastTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.speedTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}

		if (event instanceof PlayerMoveEvent) {

			Location from = ((PlayerMoveEvent) event).getFrom().clone();
			Location to = ((PlayerMoveEvent) event).getTo().clone();
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player p = e.getPlayer();

			User user = Exile.getAC().getUserManager().getUser(p.getUniqueId());

			Location l = p.getLocation();
			int x = l.getBlockX();
			int y = l.getBlockY();
			int z = l.getBlockZ();
			Location blockLoc = new Location(p.getWorld(), x, y - 1, z);
			Location loc = new Location(p.getWorld(), x, y, z);
			Location loc2 = new Location(p.getWorld(), x, y + 1, z);
			Location above = new Location(p.getWorld(), x, y + 2, z);
			Location above3 = new Location(p.getWorld(), x - 1, y + 2, z - 1);
			long lastHitDiff = Math.abs(System.currentTimeMillis() - user.isHit());
			long loginDiff = Math.abs(System.currentTimeMillis() - user.getLoginMIllis());
			if (lastHitDiff < 1500L || p.getNoDamageTicks() != 0) {
				return;
			}
			
			if(loginDiff < 1250L) {
				return;
			}
			
			if (p.getVehicle() != null) {
				return;
			}

			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				return;
			}

			if (p.getAllowFlight()) {
				return;
			}

			if ((e.getTo().getX() == e.getFrom().getX()) && (e.getTo().getZ() == e.getFrom().getZ())
					&& (e.getTo().getY() == e.getFrom().getY())) {
				return;
			}

			if (user.getIceTicks() < 0) {
				user.setIceTicks(0);
			}
			if (blockLoc.getBlock().getType() == Material.ICE || blockLoc.getBlock().getType() == Material.PACKED_ICE) {
				user.setIceTicks(user.getIceTicks() + 1);
			} else {
				user.setIceTicks(user.getIceTicks() - 1);
			}

			double Airmaxspeed = 0.4;
			double maxSpeed = 0.42;
			double newmaxspeed = 0.75;
			if (user.getIceTicks() >= 100) {
				newmaxspeed = 1.0;
			}

			double ig = 0.28;
			double speed = PlayerUtils.offset(getHV(to.toVector()), getHV(from.toVector()));
			double onGroundDiff = (to.getY() - from.getY());


			if (p.hasPotionEffect(PotionEffectType.SPEED)) {
				int level = getPotionEffectLevel(p, PotionEffectType.SPEED);
				if (level > 0) {
					newmaxspeed = (newmaxspeed * (((level * 20) * 0.011) + 1));
					Airmaxspeed = (Airmaxspeed * (((level * 20) * 0.011) + 1));
					maxSpeed = (maxSpeed * (((level * 20) * 0.011) + 1));
					ig = (ig * (((level * 20) * 0.011) + 1));
				}
			}
			Airmaxspeed += p.getWalkSpeed() > 0.2 ? p.getWalkSpeed() * 0.8 : 0;
			maxSpeed += p.getWalkSpeed() > 0.2 ? p.getWalkSpeed() * 0.8 : 0;
			int vl = user.getVL(Speed.this);
			

			/** MOTION Y RELEATED HACKS **/
			if(Exile.getAC().getPing().getTPS() > 18.2) {
				if (PlayerUtils.isReallyOnground(p) && !e.isCancelled() && !PlayerUtils.isOnClimbable(p, 0) && !PlayerUtils.isOnClimbable(p, -1) && !user.isTeleported() && !p.hasPotionEffect(PotionEffectType.JUMP)
						&& above.getBlock().getType() == Material.AIR && loc2.getBlock().getType() == Material.AIR
						&& onGroundDiff > 0 && onGroundDiff != 0 && onGroundDiff != 0.41999998688697815
						&& onGroundDiff != 0.33319999363422426 && onGroundDiff != 0.1568672884460831
						&& onGroundDiff != 0.4044491418477924 && onGroundDiff != 0.4044449141847757
						&& onGroundDiff != 0.40444491418477746 && onGroundDiff != 0.24813599859094637
						&& onGroundDiff != 0.1647732812606676 && onGroundDiff != 0.24006865856430082
						&& onGroundDiff != 0.20000004768370516 && onGroundDiff != 0.19123230896968835
						&& onGroundDiff != 0.10900766491188207 && onGroundDiff != 0.20000004768371227
						&& onGroundDiff != 0.40444491418477924 && onGroundDiff != 0.0030162615090425504
						&& onGroundDiff != 0.05999999821186108 && onGroundDiff != 0.05199999886751172
						&& onGroundDiff != 0.06159999881982792 && onGroundDiff != 0.06927999889612124
						&& onGroundDiff != 0.07542399904870933 && onGroundDiff != 0.07532994414328797
						&& onGroundDiff != 0.08033919924402255 && onGroundDiff != 0.5 && onGroundDiff != 0.08427135945886555
						&& onGroundDiff != 0.340000110268593 && onGroundDiff != 0.30000001192092896
						&& onGroundDiff != 0.3955758986732967 && onGroundDiff != 0.019999999105930755
						&& onGroundDiff != 0.21560001587867816 && onGroundDiff != 0.13283301814746876
						&& onGroundDiff != 0.05193025879327907 && onGroundDiff != 0.1875 && onGroundDiff != 0.375
						&& onGroundDiff != 0.08307781780646728 && onGroundDiff != 0.125 && onGroundDiff != 0.25
						&& onGroundDiff != 0.01250004768371582 && onGroundDiff != 0.1176000022888175
						&& onGroundDiff != 0.0625 && onGroundDiff != 0.20000004768371582
						&& onGroundDiff != 0.4044448882341385 && onGroundDiff != 0.40444491418477835 
						&& onGroundDiff != 0.019999999105934307 && onGroundDiff != 0.4375
						&& onGroundDiff != 0.36510663985490055 && onGroundDiff != 0.4641593749554431
						&& onGroundDiff != 0.3841593618424213) {
					user.setVL(Speed.this, vl + 1);
					alert(p, Color.Gray + "Reason: " + Color.White + "NormalMovements " + Color.Gray + "Illegal Value: " + Color.White + onGroundDiff);
				}
			}

			/** ONGROUND SPEEDS **/
			if (PlayerUtils.isReallyOnground(p) && to.getY() == from.getY()) {
				if (speed >= maxSpeed && user.getGroundTicks() > 20 && p.getFallDistance() < 0.15
						&& blockLoc.getBlock().getType() != Material.ICE
						&& blockLoc.getBlock().getType() != Material.PACKED_ICE
						&& loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
						&& above3.getBlock().getType() == Material.AIR) {
					user.setVL(this, vl + 1);
					user.setGroundTicks(0);
					alert(p, Color.Gray + "Reason: " + Color.White + "onGround " + Color.Gray + "Speed: " + Color.White + speed + Color.Gray + " > " + Color.White + maxSpeed);

				}
			}

			/** MIDAIR MODIFIED SPEEDS **/
			if(Exile.getAC().getPing().getTPS() > 17.5) {
				if (!PlayerUtils.isReallyOnground(p) && speed >= Airmaxspeed && user.getIceTicks() < 10
						&& blockLoc.getBlock().getType() != Material.ICE && !blockLoc.getBlock().isLiquid()
						&& !loc.getBlock().isLiquid() && blockLoc.getBlock().getType() != Material.PACKED_ICE
						&& above.getBlock().getType() == Material.AIR && above3.getBlock().getType() == Material.AIR
						&& blockLoc.getBlock().getType() != Material.AIR) {
					user.setVL(this, vl + 1);
					user.setIceTicks(0);
					alert(p, Color.Gray + "Reason: " + Color.White + "midAir " + Color.Gray + "Speed: " + Color.White + speed + Color.Gray + " > " + Color.White + maxSpeed);

				}
			}
			/** GOING ABOVE THE SPEED LIMIT **/
			if (speed >= newmaxspeed && user.getIceTicks() < 10 && p.getFallDistance() < 0.6
					&& loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
					&& loc2.getBlock().getType() == Material.AIR) {
				user.setVL(this, vl + 1);
				user.setIceTicks(0);
				alert(p, Color.Gray + "Reason: " + Color.White + "Limit " + Color.Gray + "Speed: " + Color.White + speed + Color.Gray + " > " + Color.White + maxSpeed);

			}

			/** Vanilla speeds check **/
			if (speed > ig && !PlayerUtils.isAir(p) && onGroundDiff <= -0.4 && p.getFallDistance() <= 0.4
					&& !PlayerUtils.flaggyStuffNear(p.getLocation()) && blockLoc.getBlock().getType() != Material.ICE
					&& e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
					&& loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
					&& above3.getBlock().getType() == Material.AIR) {
				user.setVL(this, vl + 1);
				alert(p, Color.Gray + "Reason: " + Color.White + "Vanilla " + Color.Gray + "Speed: " + Color.White + speed + Color.Gray + " > " + Color.White + maxSpeed);

			}
		}
		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			if (speedTicks.containsKey(e.getPlayer().getUniqueId())) {
				speedTicks.remove(e.getPlayer().getUniqueId());
			}
			if (tooFastTicks.containsKey(e.getPlayer().getUniqueId())) {
				tooFastTicks.remove(e.getPlayer().getUniqueId());
			}
			if (lastHit.containsKey(e.getPlayer().getUniqueId())) {
				lastHit.remove(e.getPlayer().getUniqueId());
			}
		}
		if (event instanceof PacketedMovementEvent) {
			PacketedMovementEvent e = (PacketedMovementEvent) event;

			Player player = e.getPlayer();
			if ((e.getFrom().getX() == e.getTo().getX()) && (e.getFrom().getY() == e.getTo().getY())
					&& (e.getFrom().getZ() == e.getFrom().getZ())) {
				return;
			}
			if (player.getAllowFlight()) {
				return;
			}
			if (player.getVehicle() != null) {
				return;
			}
			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());

			long lastHitDiff = Math.abs(System.currentTimeMillis() - user.isHit());

			int Count = 0;
			long Time = TimerUtils.nowlong();
			if (this.speedTicks.containsKey(player.getUniqueId())) {
				Count = ((Integer) ( this.speedTicks.get(player.getUniqueId())).getKey()).intValue();
				Time = ((Long) ( this.speedTicks.get(player.getUniqueId())).getValue()).longValue();
			}
			int TooFastCount = 0;
			if (this.tooFastTicks.containsKey(player.getUniqueId())) {
				double OffsetXZ = MathUtils.offset(MathUtils.getHorizontalVector(e.getFrom().toVector()),
						MathUtils.getHorizontalVector(e.getTo().toVector())) / 1.9;
				double LimitXZ = 0.0D;
				if ((PlayerUtils.isOnGround(player)) && (player.getVehicle() == null)) {
					LimitXZ = 0.34D;
				} else {
					LimitXZ = 0.39D;
				}
				if (lastHitDiff < 800L) {
					LimitXZ += 2;
				} else if (lastHitDiff < 1600L) {
					LimitXZ += 0.8;
				} else if (lastHitDiff < 2000L) {
					LimitXZ += 0.3;
				}
				if (MiscUtils.slabsNear(player.getLocation())) {
					LimitXZ += 0.05D;
				}
				Location b = PlayerUtils.getEyeLocation(player);
				b.add(0.0D, 1.0D, 0.0D);
				if ((b.getBlock().getType() != Material.AIR) && (!MiscUtils.canStandWithin(b.getBlock()))) {
					LimitXZ = 0.69D;
				}
				Location below = e.getPlayer().getLocation().clone().add(0.0D, -1.0D, 0.0D);

				if (MiscUtils.isStair(below.getBlock())) {
					LimitXZ += 0.6;
				}

				if (isOnIce(player)) {
					if ((b.getBlock().getType() != Material.AIR) && (!MiscUtils.canStandWithin(b.getBlock()))) {
						LimitXZ = 1.0D;
					} else {
						LimitXZ = 0.75D;
					}
				}
				float speed = player.getWalkSpeed();
				LimitXZ += (speed > 0.2F ? speed * 10.0F * 0.33F : 0.0F);
				for (PotionEffect effect : player.getActivePotionEffects()) {
					if (effect.getType().equals(PotionEffectType.SPEED)) {
						if (player.isOnGround()) {
							LimitXZ += 0.061D * (effect.getAmplifier() + 1);
						} else {
							LimitXZ += 0.031D * (effect.getAmplifier() + 1);
						}
					}
				}
				if ((OffsetXZ > LimitXZ) && (!TimerUtils.elapsed(
						((Long) ( this.tooFastTicks.get(player.getUniqueId())).getValue()).longValue(),
						150L))) {
					TooFastCount = ((Integer) ( this.tooFastTicks.get(player.getUniqueId())).getKey())
							.intValue() + 3;
				} else {
					TooFastCount = TooFastCount > -150 ? TooFastCount-- : -150;
				}
			}
			if (TooFastCount > 7) {
				TooFastCount = 0;
				Count++;
			}
			if ((this.speedTicks.containsKey(player.getUniqueId())) && (TimerUtils.elapsed(Time, 30000L))) {
				Count = 0;
				Time = TimerUtils.nowlong();
			}
			if (Count > 2) {
				Count = 0;
				user.setVL(Speed.this, user.getVL(this) + 1);
				alert(player, Color.Gray + "Reason: " + Color.White + "Overall " + Color.White + Color.Gray +  "Speed: " + "N/A");
			}
			this.tooFastTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(TooFastCount,
					System.currentTimeMillis()));
			this.speedTicks.put(player.getUniqueId(),
					new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
		}
	}

	public boolean isOnIce(final Player player) {
		final Location a = player.getLocation();
		a.setY(a.getY() - 1.0);
		if (a.getBlock().getType().equals((Object) Material.ICE)) {
			return true;
		}
		a.setY(a.getY() - 1.0);
		return a.getBlock().getType().equals((Object) Material.ICE);
	}

	private int getPotionEffectLevel(Player p, PotionEffectType pet) {
		for (PotionEffect pe : p.getActivePotionEffects()) {
			if (pe.getType().getName().equals(pet.getName())) {
				return pe.getAmplifier() + 1;
			}
		}
		return 0;
	}

	private Vector getHV(Vector V) {
		V.setY(0);
		return V;
	}

}