package info.malignantshadow.devkit.wand.action;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import info.malignantshadow.api.bukkit.helpers.BukkitPlayer;
import info.malignantshadow.api.commands.CommandContext;
import info.malignantshadow.api.util.arguments.Argument;
import info.malignantshadow.api.util.arguments.ArgumentTypes;
import info.malignantshadow.devkit.wand.DevWand;
import info.malignantshadow.devkit.wand.DevWandAction;
import info.malignantshadow.devkit.wand.DevWandContext;

public class LaunchAction extends DevWandAction {
	
	public static void launch(List<Entity> entities, Location tpLocation, Vector direction, int power) {
		for (Entity e : entities) {
			if (e == null)
				continue;
			
			launch(e, tpLocation, direction, power);
		}
	}
	
	public static void launch(Entity e, Location tpLocation, Vector direction, int power) {
		if (tpLocation != null)
			e.teleport(tpLocation);
		
		Vector clone = direction.clone();
		e.setVelocity(clone.multiply(power));
	}
	
	public static void self(CommandContext context, DevWand wand) {
		Player handle = wand.getOwner().getHandle();
		launch(handle, null, handle.getLocation().getDirection(), (Integer) context.get("power"));
	}
	
	//launch selection at looking direction
	public static void selected(CommandContext context, DevWand wand) {
		DevWandContext wandContext = wand.getContext();
		if (wandContext.getSelection().isEmpty())
			return;
		
		BukkitPlayer owner = wand.getOwner();
		Vector direction = owner.getHandle().getLocation().getDirection();
		Location tpLocation = (Boolean) context.get("tp") ? owner.getLocation() : null;
		launch(wandContext.getSelectedEntities(), tpLocation, direction, (Integer) context.get("power"));
	}
	
	//launch selection towards target
	public static void target(CommandContext context, DevWand wand) {
		DevWandContext wandContext = wand.getContext();
		if (wand.getContext().getSelection().isEmpty())
			return;
		
		BukkitPlayer owner = wand.getOwner();
		Vector direction = owner.getHandle().getLocation().toVector().subtract(owner.getTargetLocation().toVector());
		Location tpLocation = (Boolean) context.get("tp") ? owner.getLocation() : null;
		launch(wandContext.getSelectedEntities(), tpLocation, direction, (Integer) context.get("power"));
	}
	
	//launch selection in random directions
	public static void random(CommandContext context, DevWand wand) {
		
	}
	
	public LaunchAction() {
		super("launch", "Launch entities or yourself");
		withModeArg();
		withArg(new Argument("power", "The amount of power to use (Default: 2)", false)
			.withAcceptedTypes(ArgumentTypes.INT)
			.withDefault(2));
		withArg(new Argument("tp", "Teleport the entity to you before launch? (Default: false)", false)
			.withAcceptedTypes(ArgumentTypes.BOOLEAN)
			.withDefault(false));
		withModeHandlers(LaunchAction::self, LaunchAction::selected, LaunchAction::target, LaunchAction::random);
	}
	
}
