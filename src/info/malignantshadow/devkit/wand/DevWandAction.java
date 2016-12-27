package info.malignantshadow.devkit.wand;

import java.util.function.BiConsumer;

import info.malignantshadow.api.bukkit.commands.BukkitCommandManager;
import info.malignantshadow.api.bukkit.commands.BukkitCommandSender;
import info.malignantshadow.api.bukkit.commands.BukkitHelpListing;
import info.malignantshadow.api.commands.Command;
import info.malignantshadow.api.commands.CommandContext;
import info.malignantshadow.api.util.StringUtil;
import info.malignantshadow.api.util.arguments.Argument;
import info.malignantshadow.devkit.DevKit;
import info.malignantshadow.devkit.wand.action.DelayAction;
import info.malignantshadow.devkit.wand.action.ExplodeAction;
import info.malignantshadow.devkit.wand.action.FireStateAction;
import info.malignantshadow.devkit.wand.action.LaunchAction;
import info.malignantshadow.devkit.wand.action.LightningAction;
import info.malignantshadow.devkit.wand.action.TeleportAction;

public class DevWandAction extends Command {
	
	public static final int SELF = 1;
	public static final int SELECTED = 1 << 1;
	public static final int TARGET = 1 << 2;
	public static final int RANDOM = 1 << 3;
	
	private static BukkitCommandManager ACTIONS;
	
	public static final Argument.Type<Integer> MODE_TYPE = (input) -> {
		if (input.equalsIgnoreCase("self"))
			return SELF;
		if (StringUtil.eic(input, "sel", "selected"))
			return SELECTED;
		if (input.equalsIgnoreCase("target"))
			return TARGET;
		if (input.equalsIgnoreCase("random"))
			return RANDOM;
		
		return null;
	};
	
	public static final Argument.Type<DevWandAction> TYPE = DevWandAction::getAction;
	
	public static final Argument.Type<Integer> types(int modes) {
		return (input) -> {
			Integer type = MODE_TYPE.getValue(input);
			if (type == null || (modes & type) == 0)
				return null;
			
			return type;
		};
	}
	
	static {
		ACTIONS = new BukkitCommandManager()
			.withDefaultColors(DevKit.HELP_COLORS)
			.push(new DelayAction())
			.push(new ExplodeAction())
			.push(new FireStateAction(false, "extinguish", "Extinguish blocks or entities",
				"For 'target' mode - The radius of the blocks to extinguish (Default: %d). Others: The amount of time an entity should stop being on fire", "ext"))
			.push(new FireStateAction(true, "fire", "Set fire to blocks or entities",
				"For 'target' mode - The radius of the blocks to set aflame (Default: %d). Others: The amount of time an entity should be on fire"))
			.push(new LaunchAction())
			.push(new LightningAction())
			.push(new TeleportAction())
			.withHelpCommand("??");
	}
	
	public static void register(DevWandAction action) {
		ACTIONS.push(action);
	}
	
	public static DevWandAction getAction(String name) {
		return (DevWandAction) ACTIONS.getCommand(name);
	}
	
	public static boolean hasAction(String name) {
		return getAction(name) != null;
	}
	
	public static CommandContext createContext(BukkitCommandSender sender, String name, String[] args) {
		if (sender == null || !sender.isPlayer())
			return null;
		
		return ACTIONS.createContext(sender, name, args);
	}
	
	public static BukkitHelpListing getHelpListing(BukkitCommandSender who) {
		return ACTIONS.getHelpListing("", who);
	}
	
	public static BukkitCommandManager getActionManager() {
		return ACTIONS;
	}
	
	private boolean _stopsLoop;
	
	public DevWandAction(String name, String desc, String... aliases) {
		this(false, name, desc, aliases);
	}
	
	public DevWandAction(boolean stopsLoop, String name, String desc, String... aliases) {
		super(name, desc, aliases);
		_stopsLoop = stopsLoop;
	}
	
	public DevWandAction withModeArg() {
		return withModeArg(true);
	}
	
	public DevWandAction withModeArg(boolean required) {
		withArg(new Argument("mode", "The action's mode", required)
			.withAcceptedTypes(MODE_TYPE));
		return this;
	}
	
	public DevWandAction withModeHandlers(BiConsumer<CommandContext, DevWand> self, BiConsumer<CommandContext, DevWand> selected,
		BiConsumer<CommandContext, DevWand> target, BiConsumer<CommandContext, DevWand> random) {
		String name = getName();
		return (DevWandAction) withHandler((context) -> {
			DevWand wand = (DevWand) context.getData(DevWand.K_WAND);
			Integer mode = (Integer) context.get("mode");
			if (mode == null) {
				context.printErr("Action '%s': Invalid mode: %s", name, context.getInputFor("mode"));
				return;
			}
			
			if (mode == SELF && self != null)
				self.accept(context, wand);
			else if (mode == SELECTED && selected != null)
				selected.accept(context, wand);
			else if (mode == TARGET && target != null)
				target.accept(context, wand);
			else if (mode == RANDOM && random != null)
				random.accept(context, wand);
		});
	}
	
	public boolean stopsLoop() {
		return _stopsLoop;
	}
	
}
