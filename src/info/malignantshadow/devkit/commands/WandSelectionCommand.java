package info.malignantshadow.devkit.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import info.malignantshadow.api.bukkit.commands.BukkitCommandSender;
import info.malignantshadow.api.bukkit.selectors.BukkitSelector;
import info.malignantshadow.api.commands.CommandContext;
import info.malignantshadow.api.commands.CommandSender;
import info.malignantshadow.api.util.ListUtil;
import info.malignantshadow.api.util.MultipleValueMap;
import info.malignantshadow.api.util.StringUtil;
import info.malignantshadow.api.util.selectors.Selector;
import info.malignantshadow.devkit.wand.DevWand;

public class WandSelectionCommand {
	
	private static void showSelection(CommandSender sender, EntityType type, List<Entity> entities) {
		if (entities == null || entities.isEmpty())
			return;
		
		List<String> messages = new ArrayList<String>();
		messages.add(String.format("&e%s &6(&e%d&6)", StringUtil.toProperCase(type.name().replace("_", " ")), entities.size()));
		if (type == EntityType.PLAYER)
			messages.add(ListUtil.join(entities, (e) -> "&6" + ((Player) e).getName(), "&7,&6 "));
		
		messages.forEach(msg -> sender.print(msg));
	}
	
	//wand selection
	public static void view(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		MultipleValueMap<EntityType, Entity> selection = wand.getContext().getSelection();
		if (selection.isEmpty()) {
			context.print("You have no entities selected");
			return;
		}
		
		List<EntityType> typesSorted = new ArrayList<EntityType>(selection.getMap().keySet());
		Collections.sort(typesSorted, (a, b) -> a.name().compareTo(b.name()));
		typesSorted.forEach((type) -> showSelection(context.getSender(), type, selection.getMap().get(type)));
	};
	
	//wand selection add <player|selector>
	public static void add(CommandContext context) {
		BukkitCommandSender sender = (BukkitCommandSender) context.getSender();
		DevWand wand = DevWand.getWand(sender);
		if (wand == null)
			return;
		
		safeAdd(context, wand);
	};
	
	//wand selection remove <type> [index=-1]
	public static void remove(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return;
		
		EntityType type = (EntityType) context.get("type");
		MultipleValueMap<EntityType, Entity> selection = wand.getContext().getSelection();
		
		int size = selection.size(type);
		int index = (Integer) context.get("index");
		if (index < 0)
			index = size - index;
		
		if (index < 0 || index >= size) {
			context.printErr("No entity at index &e%d", index);
			return;
		}
		
		if (selection.remove(type, index))
			context.print("Entity removed.");
	};
	
	//wand selection set <player|selector>
	public static void set(CommandContext context) {
		DevWand wand = doClear(context);
		if (wand != null)
			safeAdd(context, wand);
	};
	
	//wand clear [type]
	public static void clear(CommandContext context) {
		doClear(context);
	};
	
	private static DevWand doClear(CommandContext context) {
		DevWand wand = DevWand.getWand((BukkitCommandSender) context.getSender());
		if (wand == null)
			return null;
		
		EntityType type = (EntityType) context.get("type");
		int count = (type == null ? wand.getContext().clearSelection() : wand.getContext().clearSelection(type));
		context.print("Selection cleared. %d removed.", count);
		return wand;
	}
	
	private static void safeAdd(CommandContext context, DevWand wand) {
		Object argValue = context.get("entities");
		List<Entity> entities;
		if (argValue instanceof Player) {
			entities = Arrays.asList((Player) argValue);
		} else { //selector
			BukkitSelector bSelector = new BukkitSelector((Selector) argValue);
			if (!bSelector.isValid()) {
				context.print("Invalid selector: \"%s\"", context.getInputFor("entities"));
				return;
			}
			entities = bSelector.select(((BukkitCommandSender) context.getSender()).getHandle());
		}
		
		int count = wand.getContext().addToSelection(entities);
		context.print("%s Entit%s added. Use &e/wand selection&r to view selection", count, count > 1 ? "ies" : "y");
	}
	
}
