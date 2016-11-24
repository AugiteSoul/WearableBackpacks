package net.mcft.copy.backpacks.config;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.lang.reflect.Field;

import net.minecraftforge.common.config.Configuration;

import net.mcft.copy.backpacks.api.BackpackHelper;

// TODO: Implement setting syncronization.
// TODO: Implement a config screen thing.
public class BackpacksConfig {
	
	// == GENERAL ==
	
	public final Setting<Boolean> equipAsChestArmor = new SettingBoolean("general", "equipAsChestArmor", true)
		.setComment("If disabled, backpacks do not take up the player's chest armor equipment slot.");
	
	public final Setting<Boolean> enableEquippedInteraction = new SettingBoolean("general", "enableEquippedInteraction", true)
		.setComment("If enabled, allows equipped backpacks to be opened by players by right clicking the target's back.");
	
	//public final Setting<Boolean> enableSelfInteraction = new Setting<Boolean>("general", "enableSelfInteraction", false)
	//	.setComment("If enabled, allows players to open their own equipped backpack without requiring it to be placed first.");
	
	//public final Setting<Boolean> dropAsBlockOnDeath = new Setting<Boolean>("general", "dropAsBlockOnDeath", true)
	//	.setComment("If enabled, places equipped backpacks as a block on death, instead of scattering the items all around.");
	
	public final Setting<Boolean> enableHelpTooltips = new SettingBoolean("general", "enableHelpTooltips", true)
		.setComment("If enabled, adds helpful usage instructions to item tooltips.");
	
	// == BACKPACK ==
	
	public final Setting<Boolean> backpackEnabled = new SettingBoolean("backpack", "enabled", true);
	
	public final Setting<Integer> backpackRows = new SettingInteger("backpack", "rows", 4).setValidRange(1, 6)
		.setComment("Number of rows of storage in a normal backpack. Valid values are 1 to 6.\n" +
		            "Changing this doesn't affect placed or equipped backpacks until turned back into an item.");
	
	
	private final List<Setting<?>> _settings = new ArrayList<Setting<?>>();
	private final File _file;
	private Configuration _config;
	
	public BackpacksConfig(File file) {
		_file = file;
		try { for (Field field : getClass().getFields()) {
			if (!Setting.class.isAssignableFrom(field.getType())) continue;
			_settings.add((Setting<?>)field.get(this));
		} } catch (IllegalAccessException ex) { throw new RuntimeException(ex); }
	}
	
	public void load() {
		if (_config == null) _config = new Configuration(_file);
		else _config.load();
		for (Setting<?> setting : _settings)
			setting.load(_config);
		// Minor special-casing: We have to sync this up with our config value.
		BackpackHelper.equipAsChestArmor = equipAsChestArmor.getValue();
	}
	
	public void save() {
		if (_config == null) _config = new Configuration(_file);
		for (Setting<?> setting : _settings)
			setting.save(_config);
		_config.save();
	}
	
}
