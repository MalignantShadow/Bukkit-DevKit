package info.malignantshadow.devkit.commands.wand;

import java.util.List;

import info.malignantshadow.api.bukkit.commands.BukkitCommandSender;
import info.malignantshadow.api.commands.CommandContext;
import info.malignantshadow.devkit.wand.DevWand;
import info.malignantshadow.devkit.wand.DevWandAction;
import info.malignantshadow.devkit.wand.DevWandContext;

public class WandActionCommand {
	
	//wand actions 
	public static void view(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		List<CommandContext> actions = wand.getContext().getActions();
		if (actions.isEmpty()) {
			context.print("You do not have any actions added to your wand");
			return;
		}
		
		actions.forEach(a -> context.print(a));
	}
	
	//wand actions activate
	public static void activate(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		((DevWandAction) context.get("action")).createContext(context.getSender(), context.getInputFor("action"), context.getExtra()).dispatchSelf();
	}
	
	//wand actions add <name> [args]:extra
	public static void add(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		safeAdd(context, wand);
	}
	
	//wand actions clear [name]
	public static void clear(CommandContext context) {
		if (!context.hasInputFor("name")) {
			doClear(context);
			return;
		}
		
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		context.print("&e%d&r actions removed.", wand.getContext().removeAll((String) context.get("name")).size());
	}
	
	//wand actions list [page|action]
	public static void list(CommandContext context) {
		DevWandAction.getActionManager().dispatch(context.getSender(), "??", new String[] { context.getInputFor("arg") });
	}
	
	//wand actions remove [index]
	public static void remove(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		DevWandContext wandContext = wand.getContext();
		int actionSize = wandContext.getActions().size();
		int index = (Integer) context.get("index");
		if (index < 0)
			index = actionSize - index;
		
		if (index < 0 || index >= actionSize) {
			context.printErr("No action at index &e%d", index);
			return;
		}
		
		CommandContext removed = wandContext.removeAction(index);
		context.print("Action removed: &e%s", removed.toString());
	}
	
	//wand action set <name> [args]:extra
	public static void set(CommandContext context) {
		DevWand wand = doClear(context);
		if (wand != null)
			safeAdd(context, wand);
	}
	
	private static void safeAdd(CommandContext context, DevWand wand) {
		DevWandContext wandContext = wand.getContext();
		try {
			DevWandAction action = (DevWandAction) context.get("action");
			CommandContext actionContext = action.createContext(context.getSender(), context.getInputFor("action"), context.getExtra());
			if (context.getCommand().getName().startsWith("add") && context.hasInputFor("index"))
				wandContext.addAction((Integer) context.get("index"), actionContext);
			else if (context.getCommand().getName().startsWith("set") && context.hasInputFor("index"))
				wandContext.setAction((Integer) context.get("index"), actionContext);
			else
				wandContext.addAction(actionContext);
			context.print("Action added: &e%s", actionContext.toString());
		} catch (IllegalArgumentException e) {
			context.printErr(e.getMessage());
		}
	}
	
	private static DevWand doClear(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return null;
		
		int size = wand.getContext().clearActions();
		context.print("Activation sequence cleared. %d removed.", size);
		return wand;
	}
	
}
