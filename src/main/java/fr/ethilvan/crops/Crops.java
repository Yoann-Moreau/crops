package fr.ethilvan.crops;

import fr.ethilvan.crops.listeners.CropsListeners;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Crops extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().info("Enabled " + this.getName());

		Bukkit.getServer().getPluginManager().registerEvents(new CropsListeners(this), this);
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabled " + this.getName());
	}
}
