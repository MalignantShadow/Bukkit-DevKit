package info.malignantshadow.devkit.wand.action;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import info.malignantshadow.api.bukkit.players.BukkitWorld;
import info.malignantshadow.api.commands.CommandContext;
import info.malignantshadow.devkit.wand.DevWand;
import info.malignantshadow.devkit.wand.DevWandAction;

public class TeleportAction extends DevWandAction {
	
	/**
	 * Teleport the wand owner to a random entity in the wand's selection. If the selection is empty, nothing happens.
	 * 
	 * @param wand
	 *            The wand
	 */
	public static final void random(CommandContext context, DevWand wand) {
		Random r = new Random();
		List<Entity> entities = wand.getContext().getSelectedEntities();
		if (entities.isEmpty())
			return;
		
		wand.getOwner().teleport(entities.get(r.nextInt(entities.size())).getLocation());
	}
	
	/**
	 * Send all entities within the wand's selection to where the wand owner is looking.
	 * 
	 * @param wand
	 *            The wand
	 */
	public static final void selected(CommandContext context, DevWand wand) {
		List<Entity> entities = wand.getContext().getSelectedEntities();
		if (entities.isEmpty())
			return;
		
		Location safeTp = BukkitWorld.getSafePosition(wand.getOwner().getTargetLocation());
		entities.forEach(e -> e.teleport(safeTp));
	}
	
	/**
	 * Teleport all entities within the wand's selection to the wand owner's location.
	 * 
	 * @param wand
	 *            The wand
	 */
	public static final void self(CommandContext context, DevWand wand) {
		List<Entity> entities = wand.getContext().getSelectedEntities();
		if (entities.isEmpty())
			return;
		
		entities.forEach((e) -> e.teleport(wand.getOwner().getLocation()));
	}
	
	/**
	 * Teleport the wand owner to where they are looking.
	 * 
	 * @param wand
	 *            The wand
	 */
	public static final void target(CommandContext context, DevWand wand) {
		wand.getOwner().teleportToTarget();
	}
	
	public TeleportAction() {
		super("teleport", "Teleport yourself or your selection", "tp");
		withModeArg();
		withModeHandlers(TeleportAction::self, TeleportAction::selected, TeleportAction::target, TeleportAction::random);
	}
	
}
