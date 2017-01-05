package info.malignantshadow.devkit.wand.action;

import java.util.List;
import java.util.Random;

import org.bukkit.entity.Entity;

import info.malignantshadow.api.bukkit.helpers.BukkitWorld;
import info.malignantshadow.api.commands.CommandContext;
import info.malignantshadow.api.util.arguments.Argument;
import info.malignantshadow.api.util.arguments.ArgumentTypes;
import info.malignantshadow.devkit.wand.DevWand;
import info.malignantshadow.devkit.wand.DevWandAction;

public class ExplodeAction extends DevWandAction {
	
	public static void self(CommandContext context, DevWand wand) {
		ExplodeInfo info = new ExplodeInfo(context);
		BukkitWorld.explosion(wand.getOwner().getLocation(), info.power, info.fire, info.destroy);
	}
	
	public static void selected(CommandContext context, DevWand wand) {
		if (wand.getContext().getSelection().isEmpty())
			return;
		
		ExplodeInfo info = new ExplodeInfo(context);
		for (Entity e : wand.getContext().getSelectedEntities())
			BukkitWorld.explosion(e.getLocation(), info.power, info.fire, info.destroy);
	}
	
	public static void target(CommandContext context, DevWand wand) {
		ExplodeInfo info = new ExplodeInfo(context);
		BukkitWorld.explosion(wand.getOwner().getTargetLocation(), info.power, info.fire, info.destroy);
	}
	
	public static void random(CommandContext context, DevWand wand) {
		if (wand.getContext().getSelection().isEmpty())
			return;
		
		ExplodeInfo info = new ExplodeInfo(context);
		List<Entity> entities = wand.getContext().getSelectedEntities();
		Entity e = entities.get(new Random().nextInt(entities.size()));
		BukkitWorld.explosion(e.getLocation(), info.power, info.fire, info.destroy);
	}
	
	public ExplodeAction() {
		super("explode", "Create explosions");
		withModeArg();
		withArg(new Argument("power", "The power of the explosion. (Default: 4 [TNT])", false)
			.withAcceptedTypes(ArgumentTypes.INT)
			.withDefault(4));
		withArg(new Argument("destroy", "Whether the explosion should destroy blocks. (Default: true)", false)
			.withAcceptedTypes(ArgumentTypes.BOOLEAN)
			.withDefault(true));
		withArg(new Argument("fire", "Whether the explosion should have fire. (Default: false)", false)
			.withAcceptedTypes(ArgumentTypes.BOOLEAN)
			.withDefault(false));
		withModeHandlers(ExplodeAction::self, ExplodeAction::selected, ExplodeAction::target, ExplodeAction::random);
	}
	
	private static class ExplodeInfo {
		
		public int power;
		public boolean destroy, fire;
		
		public ExplodeInfo(CommandContext context) {
			power = (Integer) context.get("power");
			destroy = (Boolean) context.get("destroy");
			fire = (Boolean) context.get("fire");
		}
		
	}
	
}
