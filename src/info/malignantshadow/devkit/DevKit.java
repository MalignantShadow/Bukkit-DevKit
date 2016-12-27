package info.malignantshadow.devkit;

import org.bukkit.ChatColor;

import info.malignantshadow.api.bukkit.commands.BukkitArgumentTypes;
import info.malignantshadow.api.bukkit.commands.BukkitCommand;
import info.malignantshadow.api.bukkit.commands.BukkitCommandManager;
import info.malignantshadow.api.bukkit.commands.BukkitHelpColors;
import info.malignantshadow.api.bukkit.plugin.ShadowPlugin;
import info.malignantshadow.api.util.arguments.Argument;
import info.malignantshadow.api.util.arguments.ArgumentTypes;
import info.malignantshadow.devkit.commands.wand.WandActionCommand;
import info.malignantshadow.devkit.commands.wand.WandCommand;
import info.malignantshadow.devkit.commands.wand.WandSelectionCommand;
import info.malignantshadow.devkit.listener.EntityListener;
import info.malignantshadow.devkit.listener.PlayerListener;
import info.malignantshadow.devkit.wand.DevWand;
import info.malignantshadow.devkit.wand.DevWandAction;

public class DevKit extends ShadowPlugin {
	
	private static DevKit _instance;
	
	public static final BukkitHelpColors HELP_COLORS;
	
	static {
		HELP_COLORS = new BukkitHelpColors() {
			
			@Override
			public ChatColor getSeparatorColor() {
				return ChatColor.GRAY;
			}
			
			@Override
			public ChatColor getArgsColor() {
				return ChatColor.GOLD;
			}
			
			@Override
			public ChatColor getAliasColor() {
				return ChatColor.YELLOW;
			}
			
			@Override
			public ChatColor getDescriptionColor() {
				return ChatColor.YELLOW;
			}
			
		};
	}
	
	public static final DevKit getInstance() {
		return _instance;
	}
	
	@Override
	public void onStart() {
		_instance = this;
		registerCommands();
		registerEvents(new PlayerListener());
		registerEvents(new EntityListener());
	}
	
	@Override
	public void onEnd() {
		
	}
	
	private void registerCommands() {
		new BukkitCommandManager(this) //Turn the Spigot
			
			//wand
			.push(new BukkitCommand("wand", "Toggle your wand's on/off state", "w")
				.withRequiredPermissions(DevWand.PERMISSION)
				.withHandler(WandCommand::toggle)
				.withSubCommands(new BukkitCommandManager()
					.withDefaultColors(HELP_COLORS)
					
					//wand action
					.push(new BukkitCommand("actions", "View your wand's activation sequence", "a")
						.withRequiredPermissions(DevWand.PERMISSION)
						.withHandler(WandActionCommand::view)
						.withSubCommands(new BukkitCommandManager()
							.withDefaultColors(HELP_COLORS)
							
							//wand actions activate <action> [args]:extra
							.push(new BukkitCommand("activate", "Activate an action without adding it to the wand", "@")
								.withArg(new Argument("action", "The name of the action", true)
									.withAcceptedTypes(DevWandAction.TYPE))
								.withExtra("args", "The arguments to the action", false)
								.withHandler(WandActionCommand::activate))
							
							//wand actions add <action> [args]:extra
							.push(new BukkitCommand("add", "Add an action to your wand's activation sequence", "+")
								.withRequiredPermissions(DevWand.PERMISSION)
								.withArg(new Argument("action", "The name of the action", true)
									.withAcceptedTypes(DevWandAction.TYPE))
								.withExtra("args", "The arguments to the action", false)
								.withHandler(WandActionCommand::add))
							
							//wand actions add-index <index> <action> [args]:extra
							.push(new BukkitCommand("add-index", "Add an action at a certain (magical) index", "+index")
								.withRequiredPermissions(DevWand.PERMISSION)
								.withArg(new Argument("index", "The index of the action", true)
									.withAcceptedTypes(ArgumentTypes.INT))
								.withArg(new Argument("action", "The name of the action", true)
									.withAcceptedTypes(DevWandAction.TYPE))
								.withExtra("arg", "The arguments to the actoin", false)
								.withHandler(WandActionCommand::add))
							
							//wand actions clear
							.push(new BukkitCommand("clear", "Clear your wand's activation sequence", "!")
								.withRequiredPermissions(DevWand.PERMISSION)
								.withHandler(WandActionCommand::clear))
							
							//wand actions list
							.push(new BukkitCommand("list", "Show available actions", "*")
								.withRequiredPermissions(DevWand.PERMISSION)
								.withArg(new Argument("arg", "page|action", "The page or action to view", false)
									.withAcceptedTypes(ArgumentTypes.INT, DevWandAction.TYPE)
									.withDefault(1))
								.withHandler(WandActionCommand::list))
							
							//wand actions remove [index=-1]
							.push(new BukkitCommand("remove", "Remove an action from your wand's activation sequence", "-")
								.withRequiredPermissions(DevWand.PERMISSION)
								.withArg(new Argument("index", "The index to remove. (Default: last)", false)
									.withAcceptedTypes(ArgumentTypes.INT)
									.withDefault(-1))
								.withHandler(WandActionCommand::remove))
							
							//wand actions remove-all <action>
							.push(new BukkitCommand("remove-all", "Remove all actions with the given name", "-*")
								.withRequiredPermissions(DevWand.PERMISSION)
								.withArg(new Argument("action", "The name of the argument", true)
									.withAcceptedTypes(DevWandAction.TYPE))
								.withHandler(WandActionCommand::clear))
							
							//wand actions set <action> [args]:extra
							.push(new BukkitCommand("set", "Clear all actions from the activation sequence then add another", "~")
								.withRequiredPermissions(DevWand.PERMISSION)
								.withArg(new Argument("action", "The name of the action", true)
									.withAcceptedTypes(DevWandAction.TYPE))
								.withExtra("args", "The arguments to the action", false)
								.withHandler(WandActionCommand::set))
							
							//wand actions set-index <index> <action> [args]:extra
							.push(new BukkitCommand("set-index", "Set an action at a certain (magical) index", "~index")
								.withRequiredPermissions(DevWand.PERMISSION)
								.withArg(new Argument("index", "The index of the action", true)
									.withAcceptedTypes(ArgumentTypes.INT))
								.withArg(new Argument("action", "The name of the action", true)
									.withAcceptedTypes(DevWandAction.TYPE))
								.withExtra("args", "The arguments to the action", false)
								.withHandler(WandActionCommand::set))
							
							//wand actions ?
							.withHelpCommand()))
					
					//wand clear
					.push(new BukkitCommand("clear", "Clear the selection and all actions", "!")
						.withRequiredPermissions(DevWand.PERMISSION)
						.withHandler(WandCommand::clear))
					
					//wand include-self [boolean]
					.push(new BukkitCommand("include-self", "Include or exclude youself for selections. (Default: false)", "is")
						.withRequiredPermissions(DevWand.PERMISSION)
						.withHandler(WandCommand::includeSelf))
					
					//wand mode <mode>
					.push(new BukkitCommand("mode", "Set the selection mode", "m")
						.withRequiredPermissions(DevWand.PERMISSION)
						.withArg(new Argument("mode", "", "The selection mode", true)
							.withAcceptedTypes(DevWand.SEL_MODE_TYPE))
						.withHandler(WandCommand::mode))
					
					//wand off
					.push(new BukkitCommand("off", "Turn off the DevWand")
						.withRequiredPermissions(DevWand.PERMISSION)
						.withHandler(WandCommand::off))
					
					//wand on
					.push(new BukkitCommand("on", "Turn on the DevWand")
						.withRequiredPermissions(DevWand.PERMISSION)
						.withHandler(WandCommand::on))
					
					//wand restore <name>
					.push(new BukkitCommand("restore", "Restore a wand context")
						.withRequiredPermissions(true, DevWand.PERMISSION, DevWand.PERMISSION_SAVE)
						.withArg(new Argument("name", "The name of the context", true))
						.withHandler(null))
					
					//wand save <name> TODO: wand save argument type
					.push(new BukkitCommand("save", "Save your wand's context for later use. (Selection not included)")
						.withRequiredPermissions(true, DevWand.PERMISSION, DevWand.PERMISSION_SAVE)
						.withArg(new Argument("name", "The name of the context", true))
						.withHandler(null))
					
					//wand selector <selector>
					.push(new BukkitCommand("selector", "Set the wand's selector", "s")
						.withRequiredPermissions(DevWand.PERMISSION)
						.withArg(new Argument("selector", "The selector", true)
							.withAcceptedTypes(ArgumentTypes.SELECTOR))
						.withHandler(WandCommand::selector))
					
					//wand selection
					.push(new BukkitCommand("selection", "View your wand's selection", "sel")
						.withRequiredPermissions(DevWand.PERMISSION)
						.withHandler(WandSelectionCommand::view)
						.withSubCommands(new BukkitCommandManager()
							.withDefaultColors(HELP_COLORS)
							
							//wand selection add <entities>
							.push(new BukkitCommand("add", "Add entities to your wand's selection", "+")
								.withRequiredPermissions(DevWand.PERMISSION)
								.withArg(new Argument("entities", "player|selector", "The entities to add. Can be a player name/uuid or target selector", true)
									.withAcceptedTypes(BukkitArgumentTypes.ONLINE_PLAYER, ArgumentTypes.SELECTOR))
								.withHandler(WandSelectionCommand::add))
							
							//wand selection clear [type]
							.push(new BukkitCommand("clear", "Clean up your wand's selection", "!")
								.withArg(new Argument("type", "", false)
									.withAcceptedTypes(BukkitArgumentTypes.ENTITY_TYPE)
									.thatMayBeNull())
								.withHandler(WandSelectionCommand::clear))
							
							//wand selection remove <type> [index=-1]
							.push(new BukkitCommand("remove", "Remove an entity from your wand's selection", "-")
								.withArg(new Argument("type", "The type of entity to remove", true)
									.withAcceptedTypes(BukkitArgumentTypes.ENTITY_TYPE))
								.withArg(new Argument("index", "The index of the entity to remove. (Default: last)", false)
									.withAcceptedTypes(ArgumentTypes.INT)
									.withDefault(-1))
								.withHandler(WandSelectionCommand::remove))
							
							//wand selection set <entities>
							.push(new BukkitCommand("set", "Set the selection of your wand", "~")
								.withRequiredPermissions(DevWand.PERMISSION)
								.withArg(new Argument("entities", "player|selector", "The entities to add. Can be a player name/uuid or target selector", true)
									.withAcceptedTypes(BukkitArgumentTypes.ONLINE_PLAYER, ArgumentTypes.SELECTOR))
								.withHandler(WandSelectionCommand::set))
							
							//wand selection ?
							.withHelpCommand()))
					
					//wand verbose [state]
					.push(new BukkitCommand("verbose", "Set the verbose state of your wand (Default: false)", "v")
						.withArg(new Argument("state", "The new verbose state", false)
							.withAcceptedTypes(ArgumentTypes.BOOLEAN)
							.thatMayBeNull())
						.withHandler(WandCommand::verbose))
					
					//wand ?
					.withHelpCommand()))
			
			// Pour it all in the Bukkit (I like terrible puns shh)
			.registerSelf();
		
	}
	
}
