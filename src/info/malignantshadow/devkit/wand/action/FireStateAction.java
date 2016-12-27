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

public class FireStateAction extends DevWandAction {
	
	public static final int DEF_RADIUS = 1;
	public static final int DEF_TIME = 3;
	
	private static double getTime(CommandContext context) {
		Number time = (Integer) context.get("radiusOrTime");
		if (time == null)
			time = DEF_TIME;
		
		return time.doubleValue();
	}
	
	private boolean _state;
	private Material _testType, _replaceType;
	
	public FireStateAction(boolean state, String name, String desc, String argDesc, String... aliases) {
		super(false, name, desc, aliases);
		_state = state;
		_testType = state ? Material.AIR : Material.FIRE;
		_replaceType = state ? Material.FIRE : Material.AIR;
		withModeArg();
		withArg(new Argument("radiusOrTime", "radius | time", String.format(argDesc, DEF_RADIUS, DEF_TIME), false)
			.withAcceptedTypes(ArgumentTypes.NUMBER)
			.thatMayBeNull());
		withModeHandlers(this::self, this::selected, this::target, this::random);
	}
	
	public void setState(Entity e, double seconds) {
		if (e == null || seconds == 0) //if seconds is negative, the ticks will be reduced
			return;
		
		e.setFireTicks(e.getFireTicks() + (int) (seconds * 20 * (_state ? 1 : -1)));
	}
	
	public void self(CommandContext context, DevWand wand) {
		setState(wand.getOwner().getHandle(), getTime(context));
	}
	
	public void selected(CommandContext context, DevWand wand) {
		double time = getTime(context);
		for (Entity e : wand.getContext().getSelectedEntities())
			setState(e, time);
	}
	
	public void target(CommandContext context, DevWand wand) {
		Number radius = (Number) context.get("radiusOrTime");
		if (radius == null)
			radius = DEF_RADIUS;
		
		Object[] lastTargets = wand.getOwner().getLastTwoTargets(true, true);
		Location target = null;
		if (lastTargets[0] instanceof Entity)
			target = ((Entity) lastTargets[0]).getLocation();
		else if (lastTargets[1] instanceof Entity)  //test second to last
			target = ((Entity) lastTargets[1]).getLocation();
		else  //second to last is block
			target = ((Block) lastTargets[0]).getLocation();
		
		double r = radius.doubleValue();
		int ri = radius.intValue();
		double d = (r * r);
		for (int x = target.getBlockX() - ri; x <= target.getX() + ri; x++) {
			for (int y = target.getBlockY() - ri; y <= target.getY() + ri; y++) {
				for (int z = target.getBlockZ() - ri; z <= target.getZ() + ri; z++) {
					Location here = new Location(target.getWorld(), x, y, z);
					Block b = target.getWorld().getBlockAt(x, y, z);
					if (b != null && b.getType() == _testType && here.distanceSquared(target) <= (d))
						b.setType(_replaceType);
				}
			}
		}
	}
	
	public void random(CommandContext context, DevWand wand) {
		List<Entity> entities = wand.getContext().getSelectedEntities();
		if (entities.isEmpty())
			return;
		
		setState(entities.get(new Random().nextInt(entities.size())), getTime(context));
	}
	
}
