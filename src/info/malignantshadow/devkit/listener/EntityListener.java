package info.malignantshadow.devkit.listener;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import info.malignantshadow.devkit.wand.DevWand;

public class EntityListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity e = event.getEntity();
		if (e.getType() == EntityType.PLAYER)
			return;
		
		List<DevWand> wands = DevWand.getWands();
		for (DevWand w : wands)
			w.getContext().removeFromSelection(e);
	}
	
}
