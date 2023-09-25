package mokkagnom.dasgesetz.DeathChest;

import org.bukkit.Bukkit;
//Bukkit:
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
//Java:
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import mokkagnom.dasgesetz.Main;

public class DeathChest
{
	private Block chestBlock;
	private ArmorStand armorStand;
	private Inventory inventory;
	private UUID owner;
	private DeathChestRemoveTask removeTask;

	public DeathChest(Player p, List<ItemStack> items, Main main, long timer, boolean dropItems)
	{
		owner = p.getUniqueId();
		chestBlock = p.getLocation().getBlock();

		// Creating Chest:
		// Moving up till air or water is found
		while (!(chestBlock.getType().equals(Material.AIR) || chestBlock.getType().equals(Material.WATER)))
		{
			chestBlock = chestBlock.getChunk().getBlock(chestBlock.getX() & 15, chestBlock.getY() + 1, chestBlock.getZ() & 15);
		}
		chestBlock.setType(Material.CHEST);

		// Creating ArmorStand for visualisation:
		armorStand = (ArmorStand) chestBlock.getWorld().spawnEntity(chestBlock.getLocation().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
		armorStand.setVisible(false);
		armorStand.setVisualFire(false);
		armorStand.setCollidable(false);
		armorStand.setSilent(true);
		armorStand.setInvulnerable(true);
		armorStand.setGravity(false);
		armorStand.setSmall(true);
		armorStand.setCustomNameVisible(true);
		armorStand.setCustomName(p.getName() + "'s Deathchest");

		// Creating Inventory:
		int invSize = 9;
		while (items.size() - invSize > 0 && invSize < 54)
		{
			invSize += 9;
		}
		inventory = Bukkit.createInventory(null, invSize, p.getName() + "'s Deathchest");
		for (ItemStack i : items)
		{
			inventory.addItem(i);
		}

		// Creating automated removal
		removeTask = new DeathChestRemoveTask(chestBlock, armorStand, owner, dropItems);
		removeTask.runTaskLater(main, timer);

		// Message to player
		DeathChestManager.sendMessage(owner, "Created at (" + chestBlock.getX() + ", " + chestBlock.getY() + ", " + chestBlock.getZ() + ") T: " + timer / 20 + "s");
	}

	public boolean collect()
	{
		List<ItemStack> newItems = new ArrayList<ItemStack>();
		Player player = Bukkit.getServer().getPlayer(owner);

		if (player.isSneaking())
		{
			for (ItemStack i : inventory.getContents())
			{
				if (i == null)
					continue;

				HashMap<Integer, ItemStack> drop = player.getInventory().addItem(i);
				player.updateInventory();

				for (Entry<Integer, ItemStack> entry : drop.entrySet())
				{
					if (entry.getValue().getAmount() > 0)
					{
						newItems.add(entry.getValue());
					}
				}
			}

			if (newItems.size() == 0)
			{
				inventory.clear();
			}
			else
			{
				ItemStack[] array = new ItemStack[newItems.size()];
				newItems.toArray(array);
				inventory.setContents(array);
			}
		}
		else
		{
			player.openInventory(inventory);
		}
		return removeIfEmpty();
	}

	public boolean removeIfEmpty()
	{
		boolean empty = true;

		for (ItemStack item : inventory.getContents())
		{
			if (item != null)
			{
				empty = false;
				break;
			}
		}

		if (!chestBlock.getType().equals(Material.CHEST))
		{
			remove(true);
			return true;
		}
		else if (empty)
		{
			remove();
			return true;
		}
		else
			return false;

	}

	public void remove()
	{
		remove(false);
	}

	public void remove(boolean override)
	{
		if (chestBlock.getType().equals(Material.CHEST) || override)
		{
			try
			{
				if (armorStand.isValid() || override)
				{
					armorStand.remove();
				}
				inventory.clear();
				removeTask.cancel();
				if (chestBlock.getType().equals(Material.CHEST))
					chestBlock.setType(Material.AIR);
			}
			catch (Exception e)
			{
				Bukkit.getLogger().severe("DeathChest: remove exception (override: " + override + "): " + e.getLocalizedMessage());
				return;
			}
			DeathChestManager.sendMessage(owner, "Removed at X:" + chestBlock.getLocation().getX() + " Y:" + chestBlock.getLocation().getY() + " Z:" + chestBlock.getLocation().getZ());
			Bukkit.getConsoleSender().sendMessage("Removed Death Chest from " + owner.toString() + " at X:" + chestBlock.getLocation().getX() + " Y:" + chestBlock.getLocation().getY()
					+ " Z:" + chestBlock.getLocation().getZ());
		}
	}

	public boolean checkIfOwner(Player p)
	{
		return owner.equals(p.getUniqueId());
	}

	public Block getBlock()
	{
		return chestBlock;
	}

	public Player getOwner()
	{
		return Bukkit.getServer().getPlayer(owner);
	}

	public Inventory getChestInventory()
	{
		return inventory;
	}
}