package info.malignantshadow.devkit.listener;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import info.malignantshadow.api.bukkit.BukkitMessages;
import info.malignantshadow.api.bukkit.players.BukkitPlayer;
import info.malignantshadow.api.bukkit.selectors.BukkitSelector;
import info.malignantshadow.api.util.StringUtil;
import info.malignantshadow.devkit.wand.DevWand;
import info.malignantshadow.devkit.wand.DevWandContext;

public class PlayerListener implements Listener {
	
	private void clearIfNeeded(int selMode, Player player, DevWand wand) {
		if (selMode == DevWand.SEL_SET_SELECTOR || selMode == DevWand.SEL_SET_TARGET) {
			wand.getContext().clearSelection();
			BukkitMessages.sendMessage(player, "&aWand cleared.");
		}
	}
	
	private boolean checkItem(ItemStack item) {
		return item != null && item.getType() == Material.STICK;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBreakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player == null)
			return;
		
		DevWand wand = DevWand.getWand(player);
		if (wand == null || !wand.getContext().getState())
			return;
		
		ItemStack item = player.getEquipment().getItemInMainHand();
		if (item == null || item.getType() != Material.STICK)
			return;
		
		//if we are here, then the player broke a block while holding the wand.
		//since the wand is on, we want to cancel the event
		//this is the same behavior as WorldEdit
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClick(PlayerInteractEvent event) { //add or remove entities or activate wand
		Player player = event.getPlayer();
		DevWand wand = DevWand.getWand(player);
		
		if (wand == null || !wand.getContext().getState())
			return;
		
		DevWandContext wandContext = wand.getContext();
		ItemStack mainHand = player.getEquipment().getItemInMainHand();
		ItemStack offHand = player.getEquipment().getItemInOffHand();
		
		Action action = event.getAction();
		
		// left click - select
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK || action == Action.PHYSICAL) {
			if (!checkItem(mainHand))
				return;
			
			int selMode = wandContext.getSelectionMode();
			if (selMode == DevWand.SEL_ADD_SELECTOR || selMode == DevWand.SEL_REMOVE_SELECTOR || selMode == DevWand.SEL_SET_SELECTOR) {
				Location target = new BukkitPlayer(player).getTargetLocation();
				BukkitSelector selector = wandContext.getSelector();
				if (selector == null) {
				}
				
				Location prevRef = selector.getReferencePoint();
				selector.setReferencePoint(target);
				List<Entity> selected = selector.select(player);
				selector.setReferencePoint(prevRef); //reset reference point
				
				clearIfNeeded(selMode, player, wand);
				
				boolean add = selMode == DevWand.SEL_ADD_SELECTOR || selMode == DevWand.SEL_SET_SELECTOR;
				int count = (add ? wandContext.addToSelection(selected) : wandContext.removeFromSelection(selected));
				if (count > 0)
					BukkitMessages.sendMessage(player, String.format("&e%d &aEntit%s %s.", count, count > 1 ? "ies" : "y", add ? "added" : "removed"));
				else if (wandContext.isVerbose()) //if nothing happened, say so; but only if the wand is verbose
					BukkitMessages.sendMessage(player, "&eNothing happened.");
			} else { //set or add
				Entity target = new BukkitPlayer(player).getTargetEntity();
				if (target == null)
					return;
				
				clearIfNeeded(selMode, player, wand);
				
				boolean add = selMode == DevWand.SEL_ADD_TARGET || selMode == DevWand.SEL_SET_TARGET;
				boolean done = (add ? wandContext.addToSelection(player, target) : wandContext.removeFromSelection(target));
				if (done)
					BukkitMessages.sendMessage(player, String.format("&a%s %s.", StringUtil.toProperCase(target.getType().name()), add ? "added" : "removed"));
				else if (wandContext.isVerbose()) //if nothing happened, say so; but only if the wand is verbose
					BukkitMessages.sendMessage(player, "&eNothing happened.");
			}
		} else { //right click - activate
			if (!(checkItem(offHand) || checkItem(mainHand)))
				return;
			
			if (wandContext.isVerbose())
				BukkitMessages.sendMessage(player, "&aStarting activation sequence...");
			wand.activate();
		}
	}
	
}
