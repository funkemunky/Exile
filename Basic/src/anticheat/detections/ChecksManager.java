package anticheat.detections;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;

import anticheat.Exile;
import anticheat.checks.combat.AimAssist;
import anticheat.checks.combat.AutoClicker;
import anticheat.checks.combat.Criticals;
import anticheat.checks.combat.DoubleClick;
import anticheat.checks.combat.Fastbow;
import anticheat.checks.combat.KillAura;
import anticheat.checks.combat.Reach;
import anticheat.checks.combat.Regen;
import anticheat.checks.movement.Fly;
import anticheat.checks.movement.Jesus;
import anticheat.checks.movement.NoFall;
import anticheat.checks.movement.NoSlowdown;
import anticheat.checks.movement.Phase;
import anticheat.checks.movement.Speed;
import anticheat.checks.movement.VClip;
import anticheat.checks.movement.Velocity;
import anticheat.checks.other.AutoInventory;
import anticheat.checks.other.InvalidPackets;
import anticheat.checks.other.PME;
import anticheat.checks.other.Timer;

public class ChecksManager {

	private List<Checks> detections = new ArrayList<>();

	public ChecksManager(Exile ac) {
	}

	public List<Checks> getDetections() {
		return detections;
	}

	public Checks getCheckByName(String name) {
		for (Checks check : getDetections()) {
			if (check.getName().equalsIgnoreCase(name)) {
				return check;
			}
		}
		return null;
	}

	// TODO: Init all your checks here.
	public void init() {
		new Reach();
		new Speed();
		new KillAura();
		new Criticals();
		new Fly();
		new Jesus();
		new PME();
		new Fastbow();
		new Regen();
		new AutoClicker();
		new NoSlowdown();
		new NoFall();
		new InvalidPackets();
		new AimAssist();
		new VClip();
		new DoubleClick();
		new Velocity();
		new AutoInventory();
		new Phase();
		new Timer();
	}

	public void event(Event event) {
		for (int i = 0; i < detections.size(); i++) {
			Checks detection = detections.get(i);
			Class<? extends Checks> clazz = detection.getClass();
			if (clazz.isAnnotationPresent(ChecksListener.class)) {
				Annotation annotation = clazz.getAnnotation(ChecksListener.class);
				ChecksListener handler = (ChecksListener) annotation;
				for (Class<?> type : handler.events()) {
					if (type == event.getClass()) {
						detection.onEvent(event);
					}
				}
			}
		}
	}
}
