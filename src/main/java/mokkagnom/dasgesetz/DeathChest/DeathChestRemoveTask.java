package mokkagnom.dasgesetz.DeathChest;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathChestRemoveTask extends BukkitRunnable
{
	private Block block;
	private ArmorStand armorstand;
	private UUID owner;
	private boolean dropItems;

	public DeathChestRemoveTask(Block block, ArmorStand armorstand, UUID owner, boolean dropItems)
	{
		this.block = block;
		this.armorstand = armorstand;
		this.owner = owner;
		this.dropItems = dropItems;
	}

	@Override
	public void run()
	{
		if (block.getType().equals(Material.CHEST))
		{
			if (armorstand.isValid())
			{
				armorstand.remove();
			}
			if (dropItems)
			{
				Chest chest = (Chest) block.getState();
				for (ItemStack i : chest.getBlockInventory().getContents())
				{
					if (i == null)
						continue;
					block.getWorld().dropItemNaturally(block.getLocation(), i);
				}
			}
			block.setType(Material.AIR);
			DeathChestManager.sendMessage(owner, "Removed at X:" + block.getLocation().getX() + " Y:" + block.getLocation().getY() + " Z:" + block.getLocation().getZ());
			Bukkit.getConsoleSender().sendMessage(
					"Removed Death Chest from " + owner.toString() + " at X:" + block.getLocation().getX() + " Y:" + block.getLocation().getY() + " Z:" + block.getLocation().getZ());
		}
	}
}
