package info.malignantshadow.devkit.wand.action;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import info.malignantshadow.api.bukkit.helpers.BukkitWorld;
import info.malignantshadow.api.commands.CommandContext;
import info.malignantshadow.api.util.arguments.Argument;
import info.malignantshadow.api.util.arguments.ArgumentTypes;
import info.malignantshadow.devkit.wand.DevWand;
import info.malignantshadow.devkit.wand.DevWandAction;
import info.malignantshadow.devkit.wand.DevWandContext;

public class LightningAction extends DevWandAction {
	
	public static void strike(Location location, boolean effect) {
		strike(location, effect, true);
	}
	
	public static void strike(Location location, boolean effect, boolean highestY) {
		if (location == null || location.getWorld() == null)
			return;
		
		if (highestY) {
			location = BukkitWorld.getHighestNonAirLocation(location);
			location.setY(Math.min(location.getWorld().getMaxHeight(), location.getY()));
		}
		
		if (effect)
			location.getWorld().strikeLightningEffect(location);
		else
			location.getWorld().strikeLightning(location);
	}
	
	public static void self(CommandContext context, DevWand wand) {
		strike(wand.getOwner().getLocation(), (Boolean) context.get("effect"));
	}
	
	public static void selected(CommandContext context, DevWand wand) {
		DevWandContext wandContext = wand.getContext();
		if (wandContext.getSelection().isEmpty())
			return;
		
		wandContext.getSelectedEntities().forEach((e) -> strike(e.getLocation(), (Boolean) context.get("effect")));
	}
	
	public static void target(CommandContext context, DevWand wand) {
		strike(wand.getOwner().getTargetLocation(), (Boolean) context.get("effect"));
	}
	
	public static void random(CommandContext context, DevWand wand) {
		DevWandContext wandContext = wand.getContext();
		if (wandContext.getSelection().isEmpty())
			return;
		
		List<Entity> selected = wandContext.getSelectedEntities();
		strike(selected.get(new Random().nextInt(selected.size())).getLocation(), (Boolean) context.get("effect"));
	}
	
	public LightningAction() {
		super("lightning", "Strike lightning at a location");
		withModeArg();
		withArg(new Argument("effect", "Set to true if the lightning should not damage entities (Default: false)", false)
			.withAcceptedTypes(ArgumentTypes.BOOLEAN)
			.withDefault(false));
		withModeHandlers(LightningAction::self, LightningAction::selected, LightningAction::target, LightningAction::random);
	}
	
}
