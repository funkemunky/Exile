package anticheat.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import anticheat.detections.Checks;
import anticheat.utils.Pattern;
import lombok.Getter;
import lombok.Setter;

public class User {

	private Player player;
	private UUID uuid;
	private Map<Checks, Integer> vl;
	@Getter @Setter private int AirTicks = 0;
	@Getter @Setter private int GroundTicks = 0;
	@Getter @Setter private int IceTicks = 0;
	@Getter @Setter private int inva = 0;
	@Getter @Setter private int invb = 0;
	@Getter @Setter private double deltaXZ = 0D;
	@Getter @Setter private double deltaY = 0D;
	@Getter @Setter private int clickLevel = 0;
	@Getter @Setter private double lastLastYawDif;
	@Getter @Setter private boolean hasSwung = false;
	@Getter @Setter private boolean hasAlerts = false;
	@Getter @Setter private boolean hasAdvancedAlerts = false;
	@Getter @Setter private long lastSwing = 0L;
	@Getter @Setter private long teleported = 0L;
	@Getter @Setter private double lastYaw = 1.0D;
	@Getter @Setter private int clickFreg = 0;
	@Getter @Setter private int clickFreg2 = 0;
	@Getter @Setter private long lastMove = 0L;
	@Getter @Setter private double lastPitchDifferenceAimC;
	@Getter @Setter private double lastPitchAimC;
	@Getter @Setter private Map<Double, Integer> clickIntervals = new HashMap<Double, Integer>();
	@Getter @Setter private Map<Double, Integer> clickIntervals2 = new HashMap<Double, Integer>();
	@Getter @Setter private int usePackets = 0;
	@Getter @Setter private int swingPackets = 0;
    @Getter @Setter private int ratioAttackAmount = 0;
    @Getter @Setter private int ratioAttackLevel = 0;
	@Getter @Setter private long attackTime = 0;
	@Getter @Setter private double lastYawDifference = 0.0D;
	@Getter @Setter private double lastPitch = 1.0D;
	@Getter @Setter private int maxCps = 0;
	@Getter @Setter private int globalClicks = 0;
	@Getter @Setter private boolean bigPitch = false;
	@Getter @Setter private int bodyPositive2 = 0;
	@Getter @Setter private int intervalAmount = 0;
	@Getter @Setter private long swingTime = 0;
	@Getter @Setter private int globalClicksAmount = 0;
	@Getter @Setter private int ratioAttack = 0;
	@Getter @Setter private int lastSum = 0;
	@Getter @Setter private long lastInvClick = 0L;
	@Getter @Setter private long lastInvClickDifference = 0L;
	@Getter @Setter private Pattern pattern;
	@Getter @Setter private double reachVL = 0;
	@Getter @Setter boolean addDiff = true;
	@Getter @Setter private int highestCps = -1;
	@Getter @Setter private boolean collectingData = false;
	@Getter @Setter private int lowestCps = -1;
	@Getter @Setter private double lastDiff = 0.0;
	@Getter @Setter private int cpsOscillationTime = 0;
	@Getter @Setter private int cpsOscillationLevel = 0;
	@Getter @Setter private long lastPotionSplash = 0L;
	@Getter @Setter private Location getLastLocation;
	@Getter @Setter private long lastAimB;
	@Getter @Setter private boolean inventoryOpen = false;
	@Getter @Setter private int hits;
	@Getter @Setter private double deltaXZ2;
	@Getter @Setter private double lastDifference;
	@Getter @Setter private int swings;
	@Getter @Setter private double deltaY2;
	@Getter @Setter private double lastPitchDifference = 0.0D;
	@Getter @Setter private long tookVelocity = 0L;
	@Getter @Setter private List<Double> clickDifferences = new ArrayList<Double>();
	@Getter @Setter private long loginMillis = 0L;
	@Getter @Setter private long lastHeal = 0L;
	@Getter @Setter private long isHit = 0L;
	@Getter @Setter private int clicks = 0;
	@Getter @Setter private Player lastPlayer = null;

	@Getter @Setter private double totalClickTime = 0.0;

	@Getter @Setter private double yawOffset = 0.0D;
	@Getter @Setter private double lastYawOffset = 0.0D;

	private int leftClicks;
	private int rightClicks;

	public User(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId();
		this.vl = new HashMap<Checks, Integer>();
	}
	
	public double getYawOffset() {
		return yawOffset;
	}

	public void setYawOffset(double yawOffset) {
		this.yawOffset = yawOffset;
	}

	public double getLastYawOffset() {
		return lastYawOffset;
	}

	public void setLastYawOffset(double lastYawOffset) {
		this.lastYawOffset = lastYawOffset;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isStaff() {
		if (this.player.hasPermission("exile.staff") || this.player.isOp()) {
			return true;
		}
		return false;

	}

	public UUID getUUID() {
		return uuid;
	}

	public int getVL(Checks check) {
		return vl.getOrDefault(check, 0);
	}

	public void setVL(Checks check, int vl) {
		this.vl.put(check, vl);
	}

	public Map<Checks, Integer> getVLs() {
		return this.vl;
	}
	
	public void setDefenderLocation(Location loc) {
		this.getLastLocation = loc;
	}
	
	public Location getDefenderLocation() {
		return getLastLocation;
	}
	
	public Player getLastPlayer() {
		return lastPlayer;
	}

	public void setLastPlayer(Player lastPlayer) {
		this.lastPlayer = lastPlayer;
	}
	
	public int getSwings() {
		return swings;
	}
	
	public void setSwings(int swings) {
		this.swings = swings;
	}

	public boolean needBan(Checks check) {
		return getVL(check) > check.getWeight();
	}

	public int clearVL(Checks check) {
		return getVLs().put(check, 0);
	}

	public int getHits() {
		return hits;
	}
	
	public boolean isInventoryOpen() {
		return inventoryOpen;
	}

	public void setInventoryOpen(boolean inventoryOpen) {
		this.inventoryOpen = inventoryOpen;
	}
	
	public long getLastInvClick() {
		return lastInvClick;
	}

	public void setLastInvClick(long lastInvClick) {
		this.lastInvClick = lastInvClick;
	}

	public long getLastInvClickDifference() {
		return lastInvClickDifference;
	}

	public void setLastInvClickDifference(long lastInvClickDifference) {
		this.lastInvClickDifference = lastInvClickDifference;
	}

	public void addHit() {
		hits++;
	}

	public void resetHits() {
		hits = 0;
	}

	/**
	 * 
	 * @return lastPotionSplash
	 */

	public long getLastPotionSplash() {
		return this.lastPotionSplash;
	}

	/**
	 * 
	 * @param millis
	 */

	public void setLastPotionSplash(long millis) {
		this.lastPotionSplash = millis;
	}

	public void clearData() {
		this.player = null;
		this.uuid = null;
		this.vl.clear();
		;
		setAirTicks(0);
		setGroundTicks(0);
		setIceTicks(0);
		setRightClicks(0);
		setLeftClicks(0);
	}

	public Pattern getPattern() {
		return this.pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public void setClickLevel(int number) {
		this.clickLevel = number;
	}

	public int getClickLevel() {
		return this.clickLevel;
	}
	
	public void setDeltaXZ2(double xz) {
		this.deltaXZ2 = xz;
	}
	
	public double getDeltaXZ2() {
		return this.deltaXZ2;
	}
	
	public void setDeltaY2(double y) {
		this.deltaY2 = y;
	}
	
	public double getDeltaY2() {
		return this.deltaY2;
	}
	
	public void setLastLastYawDifference(double yawdif) {
		this.lastLastYawDif = yawdif;
	}
	
	public double getLastLastYawDifference() {
		return this.lastLastYawDif;
	}

	public long getTookVelocity() {
		return tookVelocity;
	}

	public void setTookVelocity(long tookVelocity) {
		this.tookVelocity = tookVelocity;
	}
	
	public long getLastMove() {
		return lastMove;
	}

	public void setLastMove(long lastMove) {
		this.lastMove = lastMove;
	}

	/**
	 * @return the airTicks
	 */
	public int getAirTicks() {
		return AirTicks;
	}

	/**
	 * @param airTicks
	 *            the airTicks to set
	 */
	public void setAirTicks(int airTicks) {
		AirTicks = airTicks;
	}

	/**
	 * 
	 * @param millis
	 */

	public void setLoginMillis(long millis) {
		loginMillis = millis;
	}

	/**
	 * 
	 * @return loginMillis
	 */

	public long getLoginMIllis() {
		return loginMillis;
	}
	
	public double getLastDifference() {
		return lastDifference;
	}

	public void setLastDifference(double lastDifference) {
		this.lastDifference = lastDifference;
	}
	
	public boolean isCollectingData() {
		return collectingData;
	}

	public void setCollectingData(boolean collectingData) {
		this.collectingData = collectingData;
	}

	public void resetUsePackets() {
		usePackets = 0;
	}

	public void addUsePackets() {
		usePackets++;
	}

	/**
	 * 
	 * @return usePackets
	 */

	public int getUsePackets() {
		return usePackets;
	}

	public void setGlobalClicks(int number) {
		this.globalClicks = number;
	}

	public int getGlobalClicks() {
		return this.globalClicks;
	}

	public void setGlobalClicksAmount(int number) {
		this.globalClicksAmount = number;
	}

	public int getGlobalClicksAmount() {
		return this.globalClicksAmount;
	}

	public void resetSwingPackets() {
		swingPackets = 0;
	}

	public long getTeleported() {
		return teleported;
	}

	public void setTeleported(long teleported) {
		this.teleported = teleported;
	}
	
	public void addSwingPackets() {
		swingPackets++;
	}
	
	public void setClickFreq(int freg) {
		this.clickFreg = freg;
	}
	
	public void setClickFreq2(int freg) {
		this.clickFreg2 = freg;
	}
	
	public int getClickFreq() {
		return this.clickFreg;
	}
	
	public int getClickFreq2() {
		return this.clickFreg2;
	}

	/**
	 * 
	 * @return swingPackets
	 */

	public int getSwingPackets() {
		return swingPackets;
	}

	/**
	 * 
	 * @param millis
	 */

	public void setLastHeal(long millis) {
		this.lastHeal = millis;
	}

	/**
	 * 
	 * @return lastHeal
	 */
	public long getLastHeal() {
		return this.lastHeal;
	}
	
	public void setBodyPositive(int num) {
		this.bodyPositive2 = num;
	}
	
	public int getBodyPositive() {
		return this.bodyPositive2;
	}

	public void setClicks(int number) {
		this.clicks = number;
	}

	public int getClicks() {
		return this.clicks;
	}

	public void setTotalClickTime(double number) {
		this.totalClickTime = number;
	}

	public double getTotalClickTime() {
		return this.totalClickTime;
	}
	
	public void setReachVL(double vl) {
		reachVL = vl;
	}
	
	public double getReachVL() {
		return reachVL;
	}

	public int getHighestCps() {
		return this.highestCps;
	}

	public void setHighestCps(int highestCps) {
		this.highestCps = highestCps;
	}

	public int getLowestCps() {
		return this.lowestCps;
	}

	public void setLowestCps(int lowestCps) {
		this.lowestCps = lowestCps;
	}

	public void setMaxCps(int number) {
		this.maxCps = number;
	}

	public int getMaxCps() {
		return this.maxCps;
	}

	public int getCpsOscillationTime() {
		return this.cpsOscillationTime;
	}

	public void setCpsOscillationTime(int cpsOscillationTime) {
		this.cpsOscillationTime = cpsOscillationTime;
	}
	
    public void setIntervalAmount(int number) {
        this.intervalAmount = number;
    }

    public int getIntervalAmount() {
        return this.intervalAmount;
    }
    
    public void setAddDiff(boolean bool) {
        this.addDiff = bool;
    }

    public boolean getAddDiff() {
        return this.addDiff;
    }

	public int getCpsOscillationLevel() {
		return this.cpsOscillationLevel;
	}
	
    public void setLastSum(int number) {
        this.lastSum = number;
    }

    public int getLastSum() {
        return this.lastSum;
    }

	public void setCpsOscillationLevel(int cpsOscillationLevel) {
		this.cpsOscillationLevel = cpsOscillationLevel;
	}
	
    public void setSwingTime(long number) {
        this.swingTime = number;
    }

    public long getSwingTime() {
        return this.swingTime;
    }
	
    public void setAttackTime(long number) {
        this.attackTime = number;
    }

    public long getAttackTime() {
        return this.attackTime;
    }

	/**
	 * 
	 * @param yaw
	 */

	public void setLastYaw(double yaw) {
		this.lastYaw = yaw;
	}

	/**
	 * 
	 * @return yaw
	 */

	public double getLastYaw() {
		return this.lastYaw;
	}

	/**
	 * 
	 * @param yawDifference
	 */

	public void setLastYawDifference(double yawDifference) {
		this.lastYawDifference = yawDifference;
	}

	/**
	 * 
	 * @return lastYawDifference
	 */

	public double getLastYawDifference() {
		return this.lastYawDifference;
	}
	
    public void setBigPitch(boolean bool) {
        this.bigPitch = bool;
    }

    public boolean getBigPitch() {
        return this.bigPitch;
    }

	/**
	 * 
	 * @param pitch
	 */

	public void setLastPitch(double pitch) {
		this.lastPitch = pitch;
	}

	/**
	 * 
	 * @return lastPitch
	 */

	public double getLastPitch() {
		return this.lastPitch;
	}

	/**
	 * 
	 * @param pitch
	 */

	public void setLastPitchAimC(double pitch) {
		this.lastPitchAimC = pitch;
	}

	/**
	 * 
	 * @return lastPitch
	 */

	public double getLastPitchAimC() {
		return this.lastPitchAimC;
	}

	public void setDeltaXZ(double offset) {
		deltaXZ = offset;
	}

	public double getDeltaXZ() {
		return deltaXZ;
	}

	public void setDeltaY(double offset) {
		deltaY = offset;
	}
	
    public void setLastDiff(double number) {
        this.lastDiff = number;
    }
    
    public int getRatioAttack() {
        return this.ratioAttack;
    }

    public void setRatioAttack(int ratioAttack) {
        this.ratioAttack = ratioAttack;
    }

    public int getRatioAttackAmount() {
        return this.ratioAttackAmount;
    }

    public void setRatioAttackAmount(int ratioAttackAmount) {
        this.ratioAttackAmount = ratioAttackAmount;
    }

    public int getRatioAttackLevel() {
        return this.ratioAttackLevel;
    }

    public void setRatioAttackLevel(int ratioAttackLevel) {
        this.ratioAttackLevel = ratioAttackLevel;
    }

    public double getLastDiff() {
        return this.lastDiff;
    }
    
    public void addClickDiff(double number) {
        this.clickDifferences.add(number);
    }

    public List<Double> getClicksDiffs() {
        return this.clickDifferences;
    }

    public void clearClickDiffs() {
        this.clickDifferences.clear();
    }
    
    public void addInterval(double key) {
        if (this.clickIntervals.containsKey(key)) {
            this.clickIntervals.put(key, this.clickIntervals.get(key) + 1);
        } else {
            this.clickIntervals.put(key, 1);
        }
    }
    
    public Map<Double, Integer> getIntervals() {
        return this.clickIntervals;
    }

    public void addInterval2(double key) {
        if (this.clickIntervals2.containsKey(key)) {
            this.clickIntervals2.put(key, this.clickIntervals2.get(key) + 1);
        } else {
            this.clickIntervals2.put(key, 1);
        }
    }

    public Map<Double, Integer> getIntervals2() {
        return this.clickIntervals2;
    }

	public double getDeltaY() {
		return deltaY;
	}

	/**
	 * 
	 * @param pitchDifference
	 */

	public void setLastPitchDifference(double pitchDifference) {
		this.lastPitchDifference = pitchDifference;
	}

	/**
	 * 
	 * @return lastPitchDifference
	 */

	public double getLastPitchDifference() {
		return this.lastPitchDifference;
	}

	/**
	 * 
	 * @param pitchDifference
	 */

	public void setLastPitchDifferenceAimC(double pitchDifference) {
		this.lastPitchDifferenceAimC = pitchDifference;
	}

	/**
	 * 
	 * @return lastPitchDifference
	 */

	public double getLastPitchDifferenceAimC() {
		return this.lastPitchDifferenceAimC;
	}

	/**
	 * 
	 * @param millis
	 */

	public void setLastAimB(long millis) {
		this.lastAimB = millis;
	}

	/**
	 * 
	 * @return lastAimB
	 */

	public long getLastAimB() {
		return this.lastAimB;
	}

	/**
	 * @return the groundTicks
	 */
	public int getGroundTicks() {
		return GroundTicks;
	}

	/**
	 * 
	 * @return isHit
	 */

	public long isHit() {
		return this.isHit;
	}

	/**
	 * 
	 * @param isHit
	 */

	public void setIsHit(long isHit) {
		this.isHit = isHit;
	}

	/**
	 * @param groundTicks
	 *            the groundTicks to set
	 */
	public void setGroundTicks(int groundTicks) {
		GroundTicks = groundTicks;
	}

	/**
	 * @return the iceTicks
	 */
	public int getIceTicks() {
		return IceTicks;
	}

	/**
	 * @param iceTicks
	 *            the iceTicks to set
	 */
	public void setIceTicks(int iceTicks) {
		IceTicks = iceTicks;
	}

	/**
	 * @return the hasAlerts
	 */
	public boolean isHasAlerts() {
		return hasAlerts;
	}

	/**
	 * @param hasAlerts
	 *            the hasAlerts to set
	 */
	public void setHasAlerts(boolean hasAlerts) {
		this.hasAlerts = hasAlerts;
	}

	/**
	 * 
	 * @return hasAdvancedAlerts
	 */

	public boolean hasAdvancedAlerts() {
		return this.hasAdvancedAlerts;
	}

	/**
	 * 
	 * @param hasAlerts
	 */

	public void setHasAdvancedAlerts(boolean hasAlerts) {
		this.hasAdvancedAlerts = hasAlerts;
	}

	/**
	 * @return the leftClicks
	 */
	public int getLeftClicks() {
		return leftClicks;
	}

	/**
	 * @param leftClicks
	 *            the leftClicks to set
	 */
	public void setLeftClicks(int leftClicks) {
		this.leftClicks = leftClicks;
	}

	/**
	 * @return the rightClicks
	 */
	public int getRightClicks() {
		return rightClicks;
	}

	/**
	 * @param rightClicks
	 *            the rightClicks to set
	 */
	public void setRightClicks(int rightClicks) {
		this.rightClicks = rightClicks;
	}

	/**
	 * @return the inva
	 */
	public int getInva() {
		return inva;
	}

	/**
	 * @param inva
	 *            the inva to set
	 */
	public void setInva(int inva) {
		this.inva = inva;
	}

	/**
	 * @return the invb
	 */
	public int getInvb() {
		return invb;
	}

	/**
	 * @param invb
	 *            the invb to set
	 */
	public void setInvb(int invb) {
		this.invb = invb;
	}

	/**
	 * @return the hasSwung
	 */
	public boolean isHasSwung() {
		return hasSwung;
	}

	/**
	 * @param hasSwung
	 *            the hasSwung to set
	 */
	public void setHasSwung(boolean hasSwung) {
		this.hasSwung = hasSwung;

		if (hasSwung) {
			this.lastSwing = System.currentTimeMillis();
		}
	}

	public long getLastSwing() {
		return this.lastSwing;
	}

}