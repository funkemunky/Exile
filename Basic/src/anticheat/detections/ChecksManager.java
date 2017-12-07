package anticheat.detections;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;

import anticheat.Exile;
import anticheat.checks.combat.AimAssist;
import anticheat.checks.combat.AutoClicker;
import anticheat.checks.combat.DoubleClick;
import anticheat.checks.combat.Fastbow;
import anticheat.checks.combat.KillAura;
import anticheat.checks.combat.Reach;
import anticheat.checks.combat.Regen;
import anticheat.checks.movement.Fly;
import anticheat.checks.movement.Jesus;
import anticheat.checks.movement.NoFall;
import anticheat.checks.movement.Speed;
import anticheat.checks.other.NoSwing;
import anticheat.checks.other.PME;

public class ChecksManager {

	private static List<Checks> detections = new ArrayList<>();

	public ChecksManager(Exile ac) {
	}

	public static List<Checks> getDetections() {
		return detections;
	}

	public static Checks getCheckByName(String name) {
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
		new NoSwing();
		new Fly();
		new Jesus();
		new PME();
		new Fastbow();
		new Regen();
		new AutoClicker();
		new NoFall();
		new AimAssist();
		new DoubleClick();
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
