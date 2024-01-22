package fr.ethilvan.crops.listeners;

import fr.ethilvan.crops.Crops;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CropsListeners implements Listener {

	public Crops crops;

	public CropsListeners(Crops crops) {
		this.crops = crops;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
		boolean hoe = item.getType().equals(Material.WOODEN_HOE) ||
				item.getType().equals(Material.STONE_HOE) ||
				item.getType().equals(Material.IRON_HOE) ||
				item.getType().equals(Material.GOLDEN_HOE) ||
				item.getType().equals(Material.DIAMOND_HOE) ||
				item.getType().equals(Material.NETHERITE_HOE);

		Block block = e.getBlock();

		if (hoe &&
				(block.getType().equals(Material.WHEAT) ||
						block.getType().equals(Material.CARROTS) ||
						block.getType().equals(Material.POTATOES) ||
						block.getType().equals(Material.BEETROOTS)
				)
		) {
			Ageable ageable = (Ageable) e.getBlock().getBlockData();

			e.setCancelled(true);

			if (ageable.getAge() == ageable.getMaximumAge()) {

				@NotNull Collection<ItemStack> drops = block.getDrops();
				for (ItemStack drop : drops) {
					if (drop.getType().equals(Material.WHEAT_SEEDS) || drop.getType().equals(Material.BEETROOT_SEEDS)) {
						continue;
					}
					if (drop.getType().equals(Material.CARROT) || drop.getType().equals(Material.POTATO)) {
						drop.setAmount(drop.getAmount() - 1);
					}
					block.getWorld().dropItem(block.getLocation(), drop);
				}

				ageable.setAge(0);
				block.setBlockData(ageable);

				Damageable damageable = (Damageable) item.getItemMeta();
				damageable.setDamage(damageable.getDamage() + 1);
				if (damageable.getDamage() >= item.getType().getMaxDurability()) {
					item.setAmount(0);
					e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
				}
				else {
					item.setItemMeta(damageable);
				}
			}
		}
	}
}
