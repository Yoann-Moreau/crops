package fr.ethilvan.crops.listeners;

import fr.ethilvan.crops.Crops;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class CropsListeners implements Listener {

	public Crops crops;

	public CropsListeners(Crops crops) {
		this.crops = crops;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Material item = e.getPlayer().getInventory().getItemInMainHand().getType();
		boolean hoe = item.equals(Material.WOODEN_HOE) ||
				item.equals(Material.STONE_HOE) ||
				item.equals(Material.IRON_HOE) ||
				item.equals(Material.GOLDEN_HOE) ||
				item.equals(Material.DIAMOND_HOE) ||
				item.equals(Material.NETHERITE_HOE);

		if (e.getBlock().getType().equals(Material.WHEAT) && hoe) {
			e.setCancelled(true);
		}
	}
}
