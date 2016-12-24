package info.malignantshadow.devkit.wand.action;

import info.malignantshadow.api.bukkit.plugin.ShadowAPI;
import info.malignantshadow.api.util.Time;
import info.malignantshadow.api.util.arguments.Argument;
import info.malignantshadow.api.util.arguments.ArgumentTypes;
import info.malignantshadow.devkit.wand.DevWand;
import info.malignantshadow.devkit.wand.DevWandAction;

public class DelayAction extends DevWandAction {
	
	private static final Time DEFAULT = new Time();
	
	private static Handler HANDLER = (context) -> {
		Integer index = (Integer) context.getData(DevWand.K_ACTION_INDEX);
		if (index == null) {
			context.printErr("Could not activate action - unknown index");
			return;
		}
		
		ShadowAPI.getInstance().runTaskLater(((Time) context.get("time")).toSeconds() * 20L, () -> ((DevWand) context.getData(DevWand.K_WAND)).activate(index + 1));
	};
	
	static {
		DEFAULT.seconds = 1;
	}
	
	public DelayAction() {
		super(true, "delay", "Delay the next action");
		withArg(new Argument("time", "The amount of time to delay (Default: 1s)", false)
			.withAcceptedTypes(ArgumentTypes.TIME)
			.withDefault(DEFAULT));
		withHandler(HANDLER);
	}
	
}
