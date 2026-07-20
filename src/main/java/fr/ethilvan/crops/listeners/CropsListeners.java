package fr.ethilvan.crops.listeners;

import fr.ethilvan.crops.Crops;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;


public class CropsListeners implements Listener {

	public Crops plugin;

	private final Material[] tools = new Material[]{
			Material.WOODEN_HOE,
			Material.STONE_HOE,
			Material.COPPER_HOE,
			Material.IRON_HOE,
			Material.GOLDEN_HOE,
			Material.DIAMOND_HOE,
			Material.NETHERITE_HOE,
	};

	private final Material[] crops = new Material[]{
			Material.WHEAT,
			Material.CARROTS,
			Material.POTATOES,
			Material.BEETROOTS,
			Material.NETHER_WART,
	};

	private final Material[] dropsToReduce = new Material[]{
			Material.CARROT,
			Material.POTATO,
			Material.NETHER_WART,
	};

	private final Material[] dropsToCancel = new Material[]{
			Material.WHEAT_SEEDS,
			Material.BEETROOT_SEEDS,
	};


	public CropsListeners(Crops plugin) {
		this.plugin = plugin;
	}


	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		// Item in hand
		ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
		boolean isHoe = Arrays.asList(tools).contains(item.getType());

		// Block hit
		Block block = e.getBlock();

		if (isHoe && Arrays.asList(crops).contains(block.getType())) {
			Ageable ageable = (Ageable) e.getBlock().getBlockData();

			e.setCancelled(true);

			// Skip if not completely grown
			if (ageable.getAge() != ageable.getMaximumAge()) {
				return;
			}

			// Reduce drops if item used as seed
			@NotNull Collection<ItemStack> drops = block.getDrops();
			for (ItemStack drop : drops) {
				if (Arrays.asList(dropsToCancel).contains(drop.getType())) {
					continue;
				}
				if (Arrays.asList(dropsToReduce).contains(drop.getType())) {
					drop.setAmount(drop.getAmount() - 1);
				}
				block.getWorld().dropItem(block.getLocation(), drop);
			}

			// Reset age of crop (first stage)
			ageable.setAge(0);
			block.setBlockData(ageable);

			// Skip if not damageable
			if (!(item.getItemMeta() instanceof Damageable damageable)) {
				return;
			}

			// Skip if unbreakable
			if (damageable.isUnbreakable()) {
				return;
			}

			int unbreakingLevel = damageable.getEnchantLevel(Enchantment.UNBREAKING);
			int currentDamage = damageable.hasDamageValue() ? damageable.getDamage() : 0;

			ThreadLocalRandom random = ThreadLocalRandom.current();

			float breakRatio = switch (unbreakingLevel) {
				case 1 -> 0.5f;
				case 2 -> 0.33f;
				case 3 -> 0.25f;
				default -> 1f;
			};
			float randomFloat = random.nextFloat();

			// Skip randomly depending on unbreaking enchantment
			if (randomFloat < 1f - breakRatio) {
				return;
			}

			// Damage tool
			damageable.setDamage(currentDamage + 1);
			if (currentDamage + 1 >= item.getType().getMaxDurability()) {
				item.setAmount(0);
				e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
			}
			else {
				item.setItemMeta(damageable);
			}
		}
	}
}
