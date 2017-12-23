package anticheat.checks.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.MathUtils;
import anticheat.utils.MiscUtils;
import anticheat.utils.PlayerUtils;

@ChecksListener(events = { PlayerMoveEvent.class, PlayerQuitEvent.class })
public class Speed extends Checks {
	
	private Map<UUID, Integer> verbose;

	public Speed() {
		super("Speed", ChecksType.MOVEMENT, Exile.getAC(), 3, true, true);
		
		this.verbose = new HashMap<UUID, Integer>();
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}

		if (event instanceof PlayerMoveEvent) {
			
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player player = e.getPlayer();

			User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
			
			double vector = MathUtils.offset(MathUtils.getHorizontalVector(e.getFrom().toVector()),
					MathUtils.getHorizontalVector(e.getTo().toVector()));
			double maxSpeed = 0;
			Location from = ((PlayerMoveEvent) event).getFrom().clone();
			Location to = ((PlayerMoveEvent) event).getTo().clone();
			
			int speed = PlayerUtils.getPotionEffectLevel(player, PotionEffectType.SPEED);
			
			Location l = player.getLocation();
			int x = l.getBlockX();
			int y = l.getBlockY();
			int z = l.getBlockZ();
			
			if(player.isFlying()) {
				return;
			}
			
			if(Math.abs(System.currentTimeMillis() - user.getLoginMIllis()) < 1250L) {
				return;
			}
			
			if (player.getVehicle() != null) {
				return;
			}
			
			if((System.currentTimeMillis() - user.getTookVelocity()) < 1800L) {
				return;
			}
			
			Location below = player.getLocation().clone().subtract(0.0D, 1.0D, 0.0D);
			Location loc2 = new Location(player.getWorld(), x, y + 1, z);
			Location above = new Location(player.getWorld(), x, y + 2, z);
			
			int verbose = this.verbose.getOrDefault(player.getUniqueId(), 0);
			
			double onGroundDiff = (to.getY() - from.getY());

			if(Exile.getAC().getPing().getTPS() > 17.4) {
				if (PlayerUtils.isReallyOnground(player) && !e.isCancelled() && !PlayerUtils.isOnClimbable(player, 0) && !PlayerUtils.isOnClimbable(player, -1) && (System.currentTimeMillis() - user.getTeleported()) > 1200L && !player.hasPotionEffect(PotionEffectType.JUMP)
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
						&& onGroundDiff != 0.3841593618424213 && onGroundDiff != 0.2000000476837016
						&& onGroundDiff != 0.011929668006757765
						&& onGroundDiff != 0.4053654548823289 && onGroundDiff != 0.07840000152587834
						&& onGroundDiff != 0.40444491418477213 && onGroundDiff != 0.0209196219069554) {
					user.setVL(Speed.this, user.getVL(this) + 1);
					alert(player, Color.Gray + "Reason: " + Color.White + "NormalMovements " + Color.Gray + "Illegal Value: " + Color.White + onGroundDiff);
				}
			}
			
			if(user.getGroundTicks() > 0) {
				if(user.getGroundTicks() == 1) {
					if(speed == 0) {
						maxSpeed = 0.52;
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.586;
						}
					} else if(speed == 1) {
						maxSpeed = 0.57;
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.63;
						}
					} else if(speed == 2) {
						maxSpeed = 0.612;
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.687;
						}
					} else if(speed == 3) {
						maxSpeed = 0.7;
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.75;
						}
					} else if(speed == 4) {
						maxSpeed = 0.78;
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.83;
						}
					} else if(speed >= 5) {
						maxSpeed = 0.878 + ((speed - 5) * 0.9);
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.93 + ((speed - 5) * 0.9);
						}
					}
				} else if(user.getGroundTicks() == 2) {
					if(speed == 0) {
						maxSpeed = 0.54;
					} else if(speed == 1) {
						maxSpeed = 0.572;
					} else if(speed == 2) {
						maxSpeed = 0.62;
					} else if(speed == 3) {
						maxSpeed = 0.715;
					} else if(speed == 4) {
						maxSpeed = 0.765;
					} else if(speed >= 5) {
						maxSpeed = 0.812 + ((speed - 5) * 0.584);
					}
				} else if(user.getGroundTicks() == 3) {
					if(speed == 0) {
						maxSpeed = 0.498;
					} else if(speed == 1) {
						maxSpeed = 0.56;
					} else if(speed == 2) {
						maxSpeed = 0.61;
					} else if(speed == 3) {
						maxSpeed = 0.7118;
					} else if(speed == 4) {
						maxSpeed = 0.761;
					} else if(speed >= 5) {
						maxSpeed = 0.816 + ((speed - 5) * 0.59);
					}
				} else if(user.getGroundTicks() == 4) {
					if(speed == 0) {
						maxSpeed = 0.491;
					} else if(speed == 1) {
						maxSpeed = 0.5484;
					} else if(speed == 2) {
						maxSpeed = 0.601;
					} else if(speed == 3) {
						maxSpeed = 0.71001;
					} else if(speed == 4) {
						maxSpeed = 0.7621;
					} else if(speed >= 5) {
						maxSpeed = 0.818 + ((speed - 5) * 0.59);
					}
				} else if(user.getGroundTicks() >= 5) {
					if(speed == 0) {
						maxSpeed = 0.4901;
						
						if(MiscUtils.isStair(below.getBlock())) {
							maxSpeed = 0.562;
						}
					} else if(speed == 1) {
						maxSpeed = 0.5401;
						
						if(MiscUtils.isStair(below.getBlock())) {
							maxSpeed = 0.562;
						}
					} else if(speed == 2) {
						maxSpeed = 0.6;
					} else if(speed == 3) {
						maxSpeed = 0.71;
					} else if(speed == 4) {
						maxSpeed = 0.762;
					} else if(speed >= 5) {
						maxSpeed = 0.82 + ((speed - 5) * 0.6);
					}
				}
			} else {
				if(user.getAirTicks() == 1) {
					if(speed == 0) {
						maxSpeed = 0.352845;
						
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.381;
						}
					} else if(speed == 1) {
						maxSpeed = 0.37;
						
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.397;
						}
					} else if(speed == 2) {
						maxSpeed = 0.385;
						
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.413;
						}
					} else if(speed == 3) {
						maxSpeed = 0.416;
						
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.441;
						}
					} else if(speed == 4) {
						maxSpeed = 0.432;
						
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.462;
						}
					} else if(speed >= 5) {
						maxSpeed = 0.454 + ((speed - 5) * 0.5);
						
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.491 + ((speed - 5) * 0.5);
						}
					}

				} else if(user.getAirTicks() == 2) {
					if(speed == 0) {
						maxSpeed = 0.347;
						
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.68;
						}
					} else if(speed == 1) {
						maxSpeed = 0.362;
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.72;
						}
					} else if(speed == 2) {
						maxSpeed = 0.375;
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.8;
						}
					} else if(speed == 3) {
						maxSpeed = 0.404;
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.86;
						}
					} else if(speed == 4) {
						maxSpeed = 0.419;
						if(user.getBlockTicks() > 0) {
							maxSpeed = 0.92;
						}
					} else if(speed >= 5) {
						maxSpeed = 0.44 + ((speed - 5) * 0.5);
						if(user.getBlockTicks() > 0) {
							maxSpeed = 1.0 + ((speed - 5) * 0.5);
						}
					}

				} else if(user.getAirTicks() == 3) {
					if(speed == 0) {
						maxSpeed = 0.341;
					} else if(speed == 1) {
						maxSpeed = 0.356;
					} else if(speed == 2) {
						maxSpeed = 0.37;
					} else if(speed == 3) {
						maxSpeed = 0.395;
					} else if(speed == 4) {
						maxSpeed = 0.408;
					} else if(speed >= 5) {
						maxSpeed = 0.429 + ((speed - 5) * 0.38);
					}

				} else if(user.getAirTicks() == 4) {
					if(speed == 0) {
						maxSpeed = 0.34;
					} else if(speed == 1) {
						maxSpeed = 0.349;
					} else if(speed == 2) {
						maxSpeed = 0.359;
					} else if(speed == 3) {
						maxSpeed = 0.385;
					} else if(speed == 4) {
						maxSpeed = 0.396;
					} else if(speed >= 5) {
						maxSpeed = 0.414 + ((speed - 5) * 0.425);
					}
					
				} else if(user.getAirTicks() >= 5) {
					if(speed == 0) {
						maxSpeed = 0.613;
					} else if(speed == 1) {
						maxSpeed = 0.645;
					} else if(speed == 2) {
						maxSpeed = 0.679;
					} else if(speed == 3) {
						maxSpeed = 0.74;
					} else if(speed == 4) {
						maxSpeed = 0.78;
					} else if(speed >= 5) {
						maxSpeed = 0.805 + ((speed - 5) * 0.4);
					}
				}
				if(user.getIceTicks() > 0) {
					maxSpeed+= 0.2;
				}
				if(PlayerUtils.isInWater(player) ) {
					maxSpeed -= 0.1;
				}
			}
			
			
			if(vector > maxSpeed) {
				verbose+= 2;
			} else {
				verbose = verbose > 0 ? verbose - 1 : verbose;
			}
			
			if(verbose > 7) {
				user.setVL(this, user.getVL(this) + 1);
				verbose = 0;
				alert(player, Color.Gray + "Reason: " + Color.White + "Kanker");
			}
			
			this.verbose.put(player.getUniqueId(), verbose);
		}
		if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			
			if(verbose.containsKey(e.getPlayer().getUniqueId())) {
				verbose.remove(e.getPlayer().getUniqueId());
			}
		}
	}
}