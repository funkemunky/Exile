package anticheat.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import anticheat.detections.Checks;

public class User {

	private Player player;
	private UUID uuid;
	private Map<Checks, Integer> vl;
	private int AirTicks = 0;
	private int GroundTicks = 0;
	private int IceTicks = 0;
	private int inva = 0;
	private int invb = 0;
	private boolean hasSwung = false;
	private boolean hasAlerts = false;
	private long lastSwing = 0;
	private boolean teleported = false;
	private double lastYawDifference = 1.0D;
	private long loginMillis = 0L;
	private long lastHeal = 0L;
	private long isHit = 0L;

	private int leftClicks;
	private int rightClicks;

	public User(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId();
		this.vl = new HashMap<Checks, Integer>();
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isStaff() {
		if (this.player.hasPermission("fiona.staff") || this.player.isOp()) {
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

	public boolean needBan(Checks check) {
		return getVL(check) > check.getWeight();
	}

	public int clearVL(Checks check) {
		return getVLs().put(check, 0);
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
		this.loginMillis = millis;
	}
	
	/**
	 * 
	 * @return loginMillis
	 */
	
	public long getLoginMIllis() {
		return this.loginMillis;
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
	
	/**
	 * 
	 * @param teleported
	 */
	public void setTeleported(boolean teleported) {
		this.teleported = teleported;
	}
	
	/**
	 * 
	 * @return teleported
	 */
	
	public boolean isTeleported()  {
		return this.teleported;
	}
	
	/**
	 * 
	 * @param yaw
	 */
	
	public void setLastYaw(double yaw) {
		this.lastYawDifference = yaw;
	}
	
	/**
	 * 
	 * @return yaw
	 */
	
	public double getLastYaw() {
		return this.lastYawDifference;
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
	 * @param inva the inva to set
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
	 * @param invb the invb to set
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
	 * @param hasSwung the hasSwung to set
	 */
	public void setHasSwung(boolean hasSwung) {
		this.hasSwung = hasSwung;
		
		if(hasSwung) {
			this.lastSwing = System.currentTimeMillis();
		}
	}
	
	public long getLastSwing() {
		return this.lastSwing;
	}


}
