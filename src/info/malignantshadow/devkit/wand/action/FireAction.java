package info.malignantshadow.devkit.wand.action;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import info.malignantshadow.api.commands.CommandContext;
import info.malignantshadow.api.util.arguments.Argument;
import info.malignantshadow.api.util.arguments.ArgumentTypes;
import info.malignantshadow.devkit.wand.DevWand;
import info.malignantshadow.devkit.wand.DevWandAction;

public class FireAction extends DevWandAction {
	
	public static void ignite(Entity e, double seconds) {
		if (e == null || seconds == 0) //if seconds is negative, the ticks will be reduced
			return;
		
		e.setFireTicks(e.getFireTicks() + (int) (seconds * 20));
	}
	
	private static double getTime(CommandContext context) {
		Number time = (Integer) context.get("radiusOrTime");
		if (time == null)
			time = 3;
		
		return time.doubleValue();
	}
	
	public static void self(CommandContext context, DevWand wand) {
		ignite(wand.getOwner().getHandle(), getTime(context));
	}
	
	public static void selected(CommandContext context, DevWand wand) {
		double time = getTime(context);
		for (Entity e : wand.getContext().getSelectedEntities())
			ignite(e, time);
	}
	
	public static void target(CommandContext context, DevWand wand) {
		Number radius = (Number) context.get("radiusOrTime");
		if (radius == null)
			radius = 1;
		
		Location target = wand.getOwner().getTargetLocation().getBlock().getRelative(wand.getOwner().getTargetFace()).getLocation();
		double r = radius.doubleValue();
		int ri = radius.intValue();
		double d = (r * r);
		for (int x = target.getBlockX() - ri; x <= target.getBlockX() + ri; x++) {
			for (int y = target.getBlockY() - ri; y <= target.getBlockY() + ri; y++) {
				for (int z = target.getBlockZ() - ri; z <= target.getBlockZ() + ri; z++) {
					Location here = new Location(target.getWorld(), x, y, z);
					Block b = target.getWorld().getBlockAt(x, y, z);
					if (b != null && b.getType() == Material.AIR && here.distanceSquared(target) <= (d))
						b.setType(Material.FIRE);
				}
			}
		}
	}
	
	public static void random(CommandContext context, DevWand wand) {
		List<Entity> entities = wand.getContext().getSelectedEntities();
		if (entities.isEmpty())
			return;
		
		ignite(entities.get(new Random().nextInt(entities.size())), getTime(context));
	}
	
	public FireAction() {
		super("fire", "Set fire to selection or target");
		withModeArg();
		withArg(new Argument("radiusOrTime", "radius | time", "For 'target' mode: The radius of flames (Default: 1). Others: The time an entity is to remain on fire in seconds (Default: 3)", false)
			.withAcceptedTypes(ArgumentTypes.NUMBER)
			.thatMayBeNull());
		withModeHandlers(FireAction::self, FireAction::selected, FireAction::target, FireAction::random);
	}
	
}