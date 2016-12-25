package info.malignantshadow.devkit.wand;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import info.malignantshadow.api.bukkit.selectors.BukkitSelector;
import info.malignantshadow.api.commands.CommandContext;
import info.malignantshadow.api.config.ConfigSection;
import info.malignantshadow.api.config.ConfigSequence;
import info.malignantshadow.api.config.ConfigSerializable;
import info.malignantshadow.api.util.ListUtil;
import info.malignantshadow.api.util.MultipleValueMap;

public class DevWandContext implements ConfigSerializable {
	
	private List<CommandContext> _actions;
	private BukkitSelector _selector;
	private MultipleValueMap<EntityType, Entity> _selection;
	private boolean _state, _verbose, _includeSelf;
	private int _selMode;
	
	public DevWandContext() {
		_state = false;
		_verbose = false;
		_includeSelf = false;
		_selMode = DevWand.SEL_ADD_SELECTOR;
		clear();
	}
	
	public DevWandContext(ConfigSection config) {
		/*
		 * name:
		 * mode:
		 * actions:
		 * - name:
		 * args:
		 * - str
		 * - str
		 * state:
		 * verbose:
		 * include_self:
		 * selector:
		 */
		
	}
	
	@Override
	public ConfigSection serializeAsConfig() {
		ConfigSection section = new ConfigSection();
		section.set(_state, "state");
		section.set(_verbose, "verbose");
		section.set(_includeSelf, "include_self");
		section.set(_selMode, "mode");
		section.set(_selector.toString(), "selector");
		
		ConfigSequence actions = new ConfigSequence();
		for (CommandContext a : _actions) {
			ConfigSection actionSection = new ConfigSection();
			actionSection.set(a.getCommand().getName(), "name");
			actionSection.set(a.getInputJoined(), "args");
		}
		section.set(actions, "actions");
		
		return section;
	}
	
	public void clear() {
		_actions = new ArrayList<CommandContext>();
		_selection = new MultipleValueMap<EntityType, Entity>();
	}
	
	public void setVerbose(boolean verbose) {
		_verbose = verbose;
	}
	
	public boolean isVerbose() {
		return _verbose;
	}
	
	public void setIncludeSelf(boolean include) {
		_includeSelf = include;
	}
	
	public boolean shouldIncludeSelf() {
		return _includeSelf;
	}
	
	public void setSelectionMode(int mode) {
		_selMode = Math.max(DevWand.SEL_ADD_TARGET, Math.min(mode, DevWand.SEL_SET_SELECTOR));
	}
	
	public int getSelectionMode() {
		return _selMode;
	}
	
	public void setSelector(BukkitSelector selector) {
		_selector = selector;
	}
	
	public BukkitSelector getSelector() {
		return _selector;
	}
	
	public void setState(boolean state) {
		_state = state;
	}
	
	public boolean getState() {
		return _state;
	}
	
	public MultipleValueMap<EntityType, Entity> getSelection() {
		return _selection;
	}
	
	public boolean addToSelection(Entity e) {
		return addToSelection(null, e);
	}
	
	public boolean addToSelection(Player owner, Entity e) {
		if (e == null || (!_includeSelf && e.equals(owner)) || _selection.contains(e.getType(), e))
			return false;
		
		_selection.add(e.getType(), e);
		return true;
	}
	
	public int addToSelection(List<Entity> entities) {
		int count = 0;
		for (Entity e : entities)
			if (addToSelection(e))
				count++;
			
		return count;
	}
	
	public boolean removeFromSelection(Entity e) {
		return _selection.remove(e.getType(), e);
	}
	
	public int removeFromSelection(List<Entity> entities) {
		int count = 0;
		for (Entity e : entities)
			if (removeFromSelection(e))
				count++;
			
		return count;
	}
	
	public int clearSelection() {
		int size = _selection.size();
		_selection.clear();
		return size;
	}
	
	public int clearSelection(EntityType type) {
		List<Entity> entities = _selection.getMap().get(type);
		if (entities == null || entities.isEmpty())
			return 0;
		
		int size = entities.size();
		entities.clear();
		return size;
	}
	
	public boolean selectionIsEmpty() {
		return _selection.isEmpty();
	}
	
	public List<Entity> getSelectedEntities() {
		if (selectionIsEmpty())
			return new ArrayList<Entity>();
		
		return _selection.values();
	}
	
	public boolean addAction(CommandContext action) {
		return addAction(_actions.size(), action);
	}
	
	public boolean addAction(int index, CommandContext action) {
		if (action == null)
			return false;
		
		if (index <= 0)
			_actions.add(0, action);
		else if (index >= _actions.size())
			_actions.add(action);
		else
			_actions.add(index, action);
		
		return true;
	}
	
	public CommandContext removeAction(int index) {
		if (index <= 0 || index >= _actions.size())
			return null;
		
		return _actions.remove(index);
	}
	
	public List<CommandContext> removeAll(String name) {
		List<CommandContext> removed = ListUtil.slice(_actions, c -> c.getCommand().hasAlias(name));
		removed.forEach(c -> _actions.remove(c));
		return removed;
	}
	
	public boolean setAction(int index, CommandContext action) {
		if (action == null || index < 0 || index >= _actions.size())
			return false;
		
		_actions.set(index, action);
		return true;
	}
	
	public List<CommandContext> getActions() {
		return _actions;
	}
	
	public int clearActions() {
		int size = _actions.size();
		_actions.clear();
		return size;
	}
	
}
