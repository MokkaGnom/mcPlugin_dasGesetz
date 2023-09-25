package mokkagnom.dasgesetz.DeathChest;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathChestRemoveTask extends BukkitRunnable
{
	private DeathChest deathChest;
	private boolean dropItems;

	public DeathChestRemoveTask(DeathChest dc, boolean dropItems)
	{
		this.deathChest = dc;
		this.dropItems = dropItems;
	}

	@Override
	public void run()
	{
		if (dropItems)
		{
			Inventory inv = deathChest.getChestInventory();
			Block block = deathChest.getBlock();
			for (ItemStack i : inv.getContents())
			{
				if (i == null)
					continue;
				block.getWorld().dropItemNaturally(block.getLocation(), i);
			}
		}
		deathChest.remove();
	}
}
