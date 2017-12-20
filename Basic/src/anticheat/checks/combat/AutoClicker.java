package anticheat.checks.combat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Exile;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.events.TickEvent;
import anticheat.events.TickType;
import anticheat.packets.events.PacketKillauraEvent;
import anticheat.user.User;
import anticheat.utils.Color;
import anticheat.utils.Pattern;

@ChecksListener(events = {TickEvent.class, PacketKillauraEvent.class, PlayerQuitEvent.class, PlayerInteractEvent.class})
public class AutoClicker extends Checks {

	public AutoClicker() {
		super("AutoClicker", ChecksType.COMBAT, Exile.getAC(), 15, true, true);
	}
	
	@Override
	protected void onEvent(Event event) {
		if(!getState()) {
			return;
		}
		
		if (event instanceof TickEvent) {
			TickEvent e = (TickEvent) event;
			if(e.getType() != TickType.SECOND) {
				return;
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
				if(Exile.getAC().getPing().getTPS() > 17 && Exile.getAC().getPing().getPing(player) < 500) {
					if (user.getLeftClicks() > 20) {
						if(user.getLeftClicks() >= 30) {
							user.setVL(this, user.getVL(this) + 2);
							this.advancedalert(player, 100D);
						}
						this.advancedalert(player, (user.getLeftClicks() - 19) * 10D);
						alert(player, Color.Gray + "Reason: " + Color.White + "FastClick " + Color.Gray + "CPS: " + Color.White +  user.getLeftClicks() + "");
					}
					
				}
				user.setLeftClicks(0);
				user.setRightClicks(0);
			}
		}
		if(event instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent) event;
	        Player player = e.getPlayer();
	        User user = Exile.getAC().getUserManager().getUser(player.getUniqueId());
	        if (e.getAction() == Action.LEFT_CLICK_AIR) {
	            user.setRatioAttack(user.getRatioAttack() - 1);
	            float diff = (float)(System.currentTimeMillis() - user.getSwingTime());
	            Pattern pattern = user.getPattern();
	            if (pattern == null) {
	                pattern = new Pattern();
	                user.setPattern(pattern);
	            }
	            else if (diff <= 210.0f) {
	                int low = pattern.getLow();
	                int high = pattern.getHigh();
	                int amount = pattern.getAmount();
	                if (diff <= 75.0f) {
	                    if (high > 0) {
	                        ++amount;
	                        pattern.addHighs(high);
	                        high = 0;
	                    }
	                    ++low;
	                }
	                else if (diff > 75.0f) {
	                    if (low > 0) {
	                        ++amount;
	                        pattern.addLows(low);
	                        low = 0;
	                    }
	                    ++high;
	                }
	                if (amount >= 10) {
	                    int oscHigh = Pattern.getOscillation(pattern.getAllHighs());
	                    int oscLow = Pattern.getOscillation(pattern.getAllLows());
	                    pattern.addPatternHigh(oscHigh);
	                    pattern.addPatternLow(oscLow);
	                    int neededAmount = 10;
	                    if (pattern.getPatternHigh().size() >= neededAmount && pattern.getPatternLow().size() >= neededAmount) {
	                        List<Integer> patternsHigh = pattern.getPatternHigh();
	                        Map<Integer, Integer> patternsHighCount = new HashMap<>();
	                        for (Integer pHigh : patternsHigh) {
	                            if (!patternsHighCount.containsKey(pHigh)) {
	                                patternsHighCount.put(pHigh, 1);
	                            } else {
	                                patternsHighCount.put(pHigh, patternsHighCount.get(pHigh) + 1);
	                            }
	                        }
	                        for (Integer key : patternsHighCount.keySet()) {
	                            int value = patternsHighCount.get(key);
	                            if (value >= neededAmount - 1) {
	                                user.setVL(this, user.getVL(this) + 1);
	                                
	                                alert(player, Color.Gray + "Reason: " + Color.White + "Type F");
	                            } else {
	                                if (value < neededAmount * 0.8) {
	                                    continue;
	                                }
	                                user.setVL(this, user.getVL(this) + 1);
	                                
	                                alert(player, Color.Gray + "Reason: " + Color.White + "Type E");
	                            }
	                        }
	                        List<Integer> patternsLow = pattern.getPatternLow();
	                        Map<Integer, Integer> patternsLowCount = new HashMap<>();
	                        for (Integer pLow : patternsLow) {
	                            if (!patternsLowCount.containsKey(pLow)) {
	                                patternsLowCount.put(pLow, 1);
	                            } else {
	                                patternsLowCount.put(pLow, patternsLowCount.get(pLow) + 1);
	                            }
	                        }
	                        for (Integer key2 : patternsLowCount.keySet()) {
	                            int value2 = patternsLowCount.get(key2);
	                            if (value2 >= neededAmount * 2.0) {
	                                user.setVL(this, user.getVL(this) + 1);
	                                
	                                alert(player, Color.Gray + "Reason: " + Color.White + "Type D");
	                            }
	                        }
	                        pattern.getPatternHigh().clear();
	                        pattern.getPatternLow().clear();
	                    }
	                    amount = 0;
	                    pattern.getAllHighs().clear();
	                    pattern.getAllLows().clear();
	                    high = 0;
	                    low = 0;
	                }
	                if (high <= 0) {
	                    if (low > 0) {}
	                }
	                pattern.setLow(low);
	                pattern.setHigh(high);
	                pattern.setAmount(amount);
	            }
	            if (diff <= 255.0f) {
	                double temp = (double)diff;
	                if (temp <= 5.0) {
	                    temp = 0.0;
	                }
	                else if (temp <= 55.0) {
	                    temp = 50.0;
	                }
	                else if (temp <= 105.0) {
	                    temp = 100.0;
	                }
	                else if (temp <= 155.0) {
	                    temp = 150.0;
	                }
	                else if (temp <= 205.0) {
	                    temp = 200.0;
	                }
	                else if (temp <= 255.0) {
	                    temp = 250.0;
	                }
	                user.addInterval2(temp);
	            }
	            double diffDiff = Math.abs((double)diff - user.getLastDiff());
	            int freq2 = user.getClickFreq2();
	            if (user.getAddDiff()) {
	                user.addClickDiff(diffDiff);
	                user.setAddDiff(false);
	                if (user.getClicksDiffs().size() >= 12) {
	                    double total = 0.0;
	                    for (Double number : user.getClicksDiffs()) {
	                        total += number;
	                    }
	                    int diffLast = (int)Math.abs(total - (double)user.getLastSum());
	                    if (diffLast <= 60) {
	                        freq2 += 2;
	                        if (diffLast <= 6) {
	                            freq2 += 4;
	                            if (diffLast <= 1) {
	                                ++freq2;
	                            }
	                        }
	                        if (freq2 > 35) {
                                user.setVL(this, user.getVL(this) + 1);
                                
                                alert(player, Color.Gray + "Reason: " + Color.White + "Type A");
	                            freq2 = 5;
	                        }
	                    }
	                    else {
	                        freq2 -= 3;
	                        if (freq2 < 0) {
	                            freq2 = 0;
	                        }
	                    }
	                    user.setLastSum((int)total);
	                    user.clearClickDiffs();
	                }
	            }
	            else {
	                user.setAddDiff(true);
	            }
	            user.setClickFreq2(freq2);
	            int freq3 = user.getClickFreq();
	            if (diffDiff <= 51.0) {
	                ++freq3;
	                if (diffDiff <= 1.0) {
	                    freq3 += 3;
	                }
	                if (freq3 > 115) {
                        user.setVL(this, user.getVL(this) + 1);
                        
                        alert(player, Color.Gray + "Reason: " + Color.White + "Interval");
	                    freq3 = 95;
	                }
	            }
	            else {
	                freq3 -= 8;
	                if (diffDiff >= 99.0 && diffDiff <= 100.7) {
	                    freq3 -= 33;
	                }
	                if (freq3 < 0) {
	                    freq3 = 0;
	                }
	            }
	            user.setClickFreq(freq3);
	            if (diffDiff <= 255.0) {
	                user.setIntervalAmount(user.getIntervalAmount() + 1);
	                double temp2 = diffDiff;
	                if (temp2 <= 5.0) {
	                    temp2 = 0.0;
	                }
	                else if (temp2 <= 55.0) {
	                    temp2 = 50.0;
	                }
	                else if (temp2 <= 105.0) {
	                    temp2 = 100.0;
	                }
	                else if (temp2 <= 155.0) {
	                    temp2 = 150.0;
	                }
	                else if (temp2 <= 205.0) {
	                    temp2 = 200.0;
	                }
	                else if (temp2 <= 255.0) {
	                    temp2 = 250.0;
	                }
	                user.addInterval(temp2);
	            }
	            double total2 = user.getTotalClickTime() + diff;
	            int clicks = user.getClicks() + 1;
	            if (total2 >= 990.0) {
	                user.setGlobalClicks(user.getGlobalClicks() + clicks);
	                user.setGlobalClicksAmount(user.getGlobalClicksAmount() + 1);
	                if (clicks > user.getMaxCps()) {
	                    user.setMaxCps(clicks);
	                }
	                int max = 25;
	                int level = user.getClickLevel();
	                if (clicks >= max) {
	                    level += 5;
	                    if (clicks >= max * 3) {
	                        level += 15;
	                    }
	                    else if (clicks >= 23) {
	                        level += 5;
	                    }
	                    if (level > 5) {
	                        if (level > 20) {
	                            level = 10;
	                        }
	                    }
	                }
	                else {
	                    if (--level < 0) {
	                        level = 0;
	                    }
	                }
	                if (clicks >= 3 && diff <= 200.0f ) {
	                    int time = user.getCpsOscillationTime() + 1;
	                    int lowest = user.getLowestCps();
	                    int highest = user.getHighestCps();
	                    if (lowest == -1) {
	                        lowest = clicks;
	                    }
	                    else if (clicks < lowest) {
	                        lowest = clicks;
	                    }
	                    if (highest == -1) {
	                        highest = clicks;
	                    }
	                    else if (clicks > highest) {
	                        highest = clicks;
	                    }
	                    int oscillation = highest - lowest;
	                    int oscLevel = user.getCpsOscillationLevel();
	                    if (time >= 9) {
	                        if (highest >= 8) {
	                            if (highest >= 9 && oscillation <= 5) {
	                                oscLevel += 2;
	                            }
	                            if (oscillation <= 3) {
	                            }
	                            else {
	                                --oscLevel;
	                            }
	                        }
	                        else {
	                            --oscLevel;
	                        }
	                        time = 0;
	                        highest = -1;
	                        lowest = -1;
	                    }
	                    if (oscillation > 2) {
	                        time = 0;
	                        highest = -1;
	                        lowest = -1;
	                    }
	                    if (oscLevel < 0) {
	                        oscLevel = 0;
	                    }
	                    if (oscLevel >= 10) {
                            user.setVL(this, user.getVL(this) + 1);
                            
                            alert(player, Color.Gray + "Reason: " + Color.White + "Osc");
	                        oscLevel = 0;
	                    }
	                    user.setLowestCps(lowest);
	                    user.setHighestCps(highest);
	                    user.setCpsOscillationTime(time);
	                    user.setCpsOscillationLevel(oscLevel);
	                }
	                user.setClickLevel(level);
	                total2 = diff;
	                clicks = 1;
	            }
	            user.setLastDiff(diff);
	            user.setClicks(clicks);
	            user.setTotalClickTime(total2);
	            user.setSwingTime(System.currentTimeMillis());
	        }
		}
	}
	
}
