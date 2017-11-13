package anticheat.detections;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;

import anticheat.Fiona;
import anticheat.checks.combat.AimPattern;
import anticheat.checks.combat.Fastbow;
import anticheat.checks.combat.KillAuraA;
import anticheat.checks.combat.Reach;
import anticheat.checks.combat.Regen;
import anticheat.checks.combat.ZeroPatterns;
import anticheat.checks.movement.Fly;
import anticheat.checks.movement.FlyB;
import anticheat.checks.movement.GroundSpoof;
import anticheat.checks.movement.Jesus;
import anticheat.checks.movement.NoSlowDown;
import anticheat.checks.movement.Speed;
import anticheat.checks.movement.Vclip;
import anticheat.checks.other.Crash;
import anticheat.checks.other.Exploit;
import anticheat.checks.other.NoSwing;
import anticheat.checks.other.PingSpoof;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class ChecksManager {

	private static List<Checks> detections = new ArrayList<>();

	public ChecksManager(Fiona ac) {
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
		new KillAuraA();
		new Vclip();
		new Jesus();
		new NoSwing();
		new Fly();
		new ZeroPatterns();
		new FlyB();
		new Crash();
		new NoSlowDown();
		new Exploit();
		new GroundSpoof();
		new Fastbow();
		new PingSpoof();
		new Regen();
		new AimPattern();
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
