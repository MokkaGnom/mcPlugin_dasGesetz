package mokkagnom.dasgesetz.Farming;

//Bukkit-Event:
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Bukkit;
//Bukkit:
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
//Java:
import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;

public class Timber implements Listener
{
	public static final Material[] timberMaterial = { Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.MANGROVE_LOG, Material.OAK_LOG,
			Material.SPRUCE_LOG };
	public static final Material[] timberAxeMaterial = { Material.DIAMOND_AXE, Material.GOLDEN_AXE, Material.IRON_AXE, Material.NETHERITE_AXE, Material.STONE_AXE, Material.WOODEN_AXE };
	private final boolean breakLeaves;
	private final int breakLeavesRadius;

	public Timber(boolean breakLeaves, int breakLeavesRadius)
	{
		this.breakLeaves = breakLeaves;
		this.breakLeavesRadius = breakLeavesRadius;
	}

	public void breakLeaves(Block first, Block last)
	{
		if (last == null)
			last = first;

		final Location l1 = first.getLocation();
		final Location l2 = first.getLocation();
		l1.add(breakLeavesRadius, 0, breakLeavesRadius);
		l2.subtract(breakLeavesRadius, 0, breakLeavesRadius);
		Location tmp = l2.clone();

		for (; tmp.getY() < last.getY() + breakLeavesRadius; tmp.add(0, 1, 0))
		{
			for (tmp.setX(l2.getX()); tmp.getX() < l1.getX(); tmp.add(1, 0, 0))
			{
				for (tmp.setZ(l2.getZ()); tmp.getZ() < l1.getZ(); tmp.add(0, 0, 1))
				{
					if (tmp.getBlock().getBlockData() instanceof Leaves) // Breaking Leaf
					{
						if (!((Leaves) tmp.getBlock().getBlockData()).isPersistent())
						{
							tmp.getBlock().breakNaturally();
						}
					}
				}
			}
		}
	}

	public Block breakWood(Block b, Player p, int logBreakIndex, int axeUsedIndex)
	{
		Chunk chunk = b.getChunk();
		Block nextBlock = b;
		List<Block> logBlocks = new ArrayList<Block>();

		// Putting every log block from the tree in a list
		while (nextBlock.getType().equals(timberMaterial[logBreakIndex]))
		{
			logBlocks.add(nextBlock);
			nextBlock = chunk.getBlock(nextBlock.getX() & 15, nextBlock.getY() + 1, nextBlock.getZ() & 15);
		}

		// Add damage to the axe
		ItemStack item = p.getInventory().getItemInMainHand();
		Damageable im = (Damageable) item.getItemMeta();

		// Checking if axe has enought durability for tree
		while (im.getDamage() + logBlocks.size() > timberAxeMaterial[axeUsedIndex].getMaxDurability())
		{
			logBlocks.remove(logBlocks.size() - 1);
		}

		im.setDamage(im.getDamage() + logBlocks.size());
		item.setItemMeta(im);
		p.getInventory().setItemInMainHand(item);

		// Removing the tree logs and putting it in player inventory or dropping it, if full
		for (Block i : logBlocks)
		{
			HashMap<Integer, ItemStack> drop = p.getInventory().addItem(new ItemStack(i.getType(), 1));
			p.updateInventory();

			for (Entry<Integer, ItemStack> entry : drop.entrySet())
			{
				p.getWorld().dropItemNaturally(i.getLocation(), entry.getValue());
			}

			i.setType(Material.AIR);
		}

		if (logBlocks.size() > 0)
			return logBlocks.get(logBlocks.size() - 1);
		else
			return null;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		int logBreakIndex = -1, axeUsedIndex = -1;
		Player p = event.getPlayer();

		// Checking for axe
		for (int i = 0; i < timberAxeMaterial.length; i++)
		{
			if (p.getInventory().getItemInMainHand().getType().equals(timberAxeMaterial[i]))
			{
				axeUsedIndex = i;
				break;
			}
		}

		// Checking for log
		for (int i = 0; i < timberMaterial.length; i++)
		{
			if (event.getBlock().getType().equals(timberMaterial[i]))
			{
				logBreakIndex = i;
				break;
			}
		}

		// Breaking tree
		if (axeUsedIndex > -1 && logBreakIndex > -1 && p.hasPermission("dg.timberPermission"))
		{
			try
			{
				Block b = breakWood(event.getBlock(), event.getPlayer(), logBreakIndex, axeUsedIndex);
				if (breakLeaves)
				{
					breakLeaves(event.getBlock(), b);
				}
			}
			catch (Exception e)
			{
				event.getPlayer().sendMessage("Timber Error: " + e.getLocalizedMessage());
				Bukkit.getConsoleSender().sendMessage("Timber Error: " + e.getLocalizedMessage());
			}
		}
	}
}