package info.malignantshadow.devkit.wand;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import info.malignantshadow.api.bukkit.commands.BukkitCommandSender;
import info.malignantshadow.api.bukkit.players.BukkitPlayer;
import info.malignantshadow.api.bukkit.plugin.ShadowAPI;
import info.malignantshadow.api.commands.Command;
import info.malignantshadow.api.commands.CommandContext;
import info.malignantshadow.api.config.ConfigPairing;
import info.malignantshadow.api.config.ConfigSection;
import info.malignantshadow.api.config.processor.extension.YamlConfigProcessor;
import info.malignantshadow.api.util.ListUtil;
import info.malignantshadow.api.util.StringUtil;
import info.malignantshadow.api.util.arguments.Argument;

public class DevWand {
	
	private static List<DevWand> WANDS;
	
	public static final String K_WAND = "DevKit.wand.use";
	public static final String K_ACTION_INDEX = "DevKit.wand.action_index";
	
	//selection modes
	public static final int SEL_ADD_TARGET = 1;
	public static final int SEL_ADD_SELECTOR = 2;
	public static final int SEL_REMOVE_TARGET = 3;
	public static final int SEL_REMOVE_SELECTOR = 4;
	public static final int SEL_SET_TARGET = 5;
	public static final int SEL_SET_SELECTOR = 6;
	
	public static final Argument.Type<Integer> SEL_MODE_TYPE = (input) -> {
		if (StringUtil.eic(input, "add-target", "+target"))
			return SEL_ADD_TARGET;
		if (StringUtil.eic(input, "add-sel", "add-selector", "+sel", "+selector"))
			return SEL_ADD_SELECTOR;
		if (StringUtil.eic(input, "remove-target", "-target"))
			return SEL_REMOVE_TARGET;
		if (StringUtil.eic(input, "remove-sel", "remove-selector", "-sel", "-selector"))
			return SEL_REMOVE_SELECTOR;
		
		return null;
	};
	
	public static final String PERMISSION = "ShadowDev.wand";
	public static final String PERMISSION_SAVE = "ShadowDev.wand.save";
	
	public static final String DIR = "wand/";
	
	static {
		WANDS = new ArrayList<DevWand>();
	}
	
	public static List<DevWand> getWands() {
		return WANDS;
	}
	
	public static DevWand getWand(BukkitCommandSender sender) {
		return getWand(sender.getHandle());
	}
	
	public static DevWand getWand(CommandSender sender) {
		if (!(sender instanceof Player))
			return null;
		
		return getWand((Player) sender);
	}
	
	public static DevWand getWand(Player player) {
		return getWand(player, true);
	}
	
	public static DevWand getWand(Player player, boolean checkPerm) {
		if (player == null || (checkPerm && !player.hasPermission(PERMISSION)))
			return null;
		
		DevWand wand = getWandFromCache(player);
		if (wand != null)
			return wand;
		
		wand = new DevWand(new BukkitPlayer(player));
		WANDS.add(wand);
		return wand;
	}
	
	public static DevWand getWandFromCache(Player player) {
		if (player == null)
			return null;
		
		return ListUtil.find(WANDS, (w) -> player.getUniqueId().equals(w.getOwner().getUUID()));
	}
	
	private static File getFile(UUID uuid) {
		return new File(ShadowAPI.getInstance().getDataFolder(), DIR + "/" + uuid.toString() + ".yml");
	}
	
	private static ConfigSection saveContext(UUID uuid, DevWandContext context, String name) {
		if (uuid == null || context == null || name == null)
			return null;
		
		File file = getFile(uuid);
		YamlConfigProcessor processor = new YamlConfigProcessor();
		ConfigSection config = processor.getDocument(file);
		if (config == null)
			config = new ConfigSection();
		
		ConfigSection section = context.serializeAsConfig();
		config.set(section, "contexts", name);
		processor.putDocument(section, file);
		return section;
	}
	
	public static DevWandContext getContext(UUID uuid, String name) {
		if (uuid == null || name == null)
			return null;
		
		File file = getFile(uuid);
		ConfigSection config = new YamlConfigProcessor().getDocument(file);
		if (config == null)
			return null;
		
		ConfigPairing pair = config.get("contexts", name);
		if (!pair.isSection())
			return null;
		
		return new DevWandContext(pair.asSection());
		
	}
	
	private BukkitPlayer _owner;
	private DevWandContext _context;
	
	public DevWand(BukkitPlayer owner) {
		_owner = owner;
		_context = new DevWandContext();
	}
	
	public ConfigSection saveContext(String name) {
		return saveContext(_owner.getUUID(), _context, name);
	}
	
	public BukkitPlayer getOwner() {
		return _owner;
	}
	
	public DevWandContext getContext() {
		return _context;
	}
	
	public boolean setContext(DevWandContext context) {
		if (context == null)
			return false;
		
		_context = context;
		return true;
	}
	
	public void activate() {
		activate(0);
	}
	
	public void activate(int index) {
		List<CommandContext> actions = _context.getActions();
		if (index < 0 || index >= actions.size())
			return;
		
		boolean finished = false;
		for (int i = index; i < actions.size(); i++) {
			CommandContext context = actions.get(i);
			Command cmd = context.getCommand();
			if (context == null || cmd == null)
				return;
			
			context.setData(K_ACTION_INDEX, i);
			context.dispatchSelf();
			context.setData(K_ACTION_INDEX, null);
			
			finished = (i == actions.size() - 1);
			
			if (cmd instanceof DevWandAction && ((DevWandAction) cmd).stopsLoop())
				return;
		}
		
		if (finished && _context.isVerbose())
			_owner.sendMessage("&Done.");
	}
	
}
