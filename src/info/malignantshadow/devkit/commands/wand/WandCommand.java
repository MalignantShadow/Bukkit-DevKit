package info.malignantshadow.devkit.commands.wand;

import info.malignantshadow.api.bukkit.commands.BukkitCommandSender;
import info.malignantshadow.api.bukkit.selectors.BukkitSelector;
import info.malignantshadow.api.commands.CommandContext;
import info.malignantshadow.api.util.selectors.Selector;
import info.malignantshadow.devkit.wand.DevWand;

public class WandCommand {
	
	private static String INCLUDE_SELF_FORMAT = "Your wand is%s %scluding yourself in selections.";
	private static String VERBOSE_FORMAT = "Your wand is%s %s verbose";
	
	private WandCommand() {
	}
	
	//wand on
	public static void on(CommandContext context) {
		setState(context, true);
	}
	
	//wand off
	public static void off(CommandContext context) {
		setState(context, false);
	}
	
	//wand ...
	public static void toggle(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		// only proceed in this case of /wand
		// otherwise send unknown sub-command message
		if (context.getExtra().length > 0) {
			context.printErr("Unknown sub-command: &e%s", context.getExtra()[0]);
			return;
		}
		
		doSetState(context, wand, !wand.getContext().getState());
	};
	
	//wand clear
	public static void clear(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		wand.getContext().clear();
		context.print("Wand selection and actions cleared.");
	};
	
	//wand include-self [boolean]
	public static void includeSelf(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		//view state if not given
		Boolean state = (Boolean) context.get("state");
		if (state == null) {
			context.print(INCLUDE_SELF_FORMAT, " currently", wand.getContext().shouldIncludeSelf() ? "in" : "ex");
			return;
		}
		
		wand.getContext().setIncludeSelf(state);
		context.print(INCLUDE_SELF_FORMAT, " now", state ? "in" : "ex");
	}
	
	//wand selector <selector>
	public static void selector(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		Selector selector = (Selector) context.get("selector");
		String input = context.getInputFor("selector");
		
		BukkitSelector bSelector = new BukkitSelector(selector);
		if (!bSelector.isValid()) {
			context.printErr("Invalid selector - &e%s", context.getInputFor("selector"));
			return;
		}
		
		wand.getContext().setSelector(bSelector);
		context.print("Selector set to &e%s", input);
	};
	
	//wand mode <mode>
	public static void mode(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		Integer mode = (Integer) context.get("mode");
		
		wand.getContext().setSelectionMode(mode);
		context.print("Selection mode set to &e%s", context.getInputFor("mode"));
	};
	
	//wand verbose [boolean]
	public static void verbose(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		//view state if not given
		Boolean verbose = (Boolean) context.get("state");
		if (verbose == null) {
			context.print(VERBOSE_FORMAT, " currently", wand.getContext().isVerbose() ? "" : "not");
			return;
		}
		
		wand.getContext().setVerbose(verbose);
		context.print(VERBOSE_FORMAT, " now", verbose ? "" : "not");
	};
	
	private static void doSetState(CommandContext context, DevWand wand, boolean state) {
		wand.getContext().setState(state);
		context.print("Your wand is now %s.", state ? "on" : "off");
	}
	
	private static void setState(CommandContext context, boolean state) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		if (wand.getContext().getState() == state) {
			context.printErr("Your wand is already %s.", state ? "on" : "off");
			return;
		}
		
		doSetState(context, wand, state);
	}
	
}
