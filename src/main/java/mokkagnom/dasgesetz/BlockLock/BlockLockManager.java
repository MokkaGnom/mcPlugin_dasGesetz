package mokkagnom.dasgesetz.BlockLock;

// Bukkit-Event:
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
// Bukkit:
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import mokkagnom.dasgesetz.Manager;

// Java:
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BlockLockManager implements Listener
{
	public static final List<Material> lockableBlocks = Arrays.asList(Material.ACACIA_DOOR, Material.ACACIA_TRAPDOOR, Material.BIRCH_DOOR, Material.BIRCH_TRAPDOOR, Material.CRIMSON_DOOR,
			Material.CRIMSON_TRAPDOOR, Material.DARK_OAK_DOOR, Material.DARK_OAK_TRAPDOOR, Material.IRON_DOOR, Material.IRON_TRAPDOOR, Material.JUNGLE_DOOR, Material.JUNGLE_TRAPDOOR,
			Material.MANGROVE_DOOR, Material.MANGROVE_TRAPDOOR, Material.OAK_DOOR, Material.OAK_TRAPDOOR, Material.SPRUCE_DOOR, Material.SPRUCE_TRAPDOOR, Material.WARPED_DOOR,
			Material.WARPED_TRAPDOOR, Material.CHEST, Material.TRAPPED_CHEST, Material.HOPPER, Material.FURNACE, Material.BLAST_FURNACE, Material.SMOKER, Material.BARREL);

	private Manager manager;
	private File saveFile;
	private List<BlockLock> blockLocks;

	public BlockLockManager(Manager manager)
	{
		this.manager = manager;
		this.saveFile = new File(this.manager.getDataFolder(), "BlockLock.bin");
		blockLocks = new ArrayList<BlockLock>();
	}

	public static boolean sendMessage(UUID receiver, String message, boolean error)
	{
		if (receiver != null)
		{
			Player p = Bukkit.getServer().getPlayer(receiver);
			if (p != null && message != null)
			{
				if (error)
				{
					p.sendMessage("§7[§BBlockLock§7] §C" + message);
					Bukkit.getConsoleSender().sendMessage("BlockLock Error: " + message);
				}
				else
				{
					p.sendMessage("§7[§BBlockLock§7] §D" + message);
				}
				return true;
			}
		}
		return false;
	}

	public static boolean sendMessage(UUID receiver, String message)
	{
		return sendMessage(receiver, message, false);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (isBlockLockable(event.getBlockPlaced()))
		{
			lock(event.getPlayer(), event.getBlockPlaced());
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			BlockLock bl = getBlockLock(event.getClickedBlock());
			Player p = event.getPlayer();

			if (bl != null)
			{
				if (open(p, event.getClickedBlock()))
				{
					if (p.isSneaking())
					{
						if (!bl.openManagerInventory(p))
						{
							sendMessage(p.getUniqueId(), "No permission!", true);
						}
						event.setCancelled(true);
					}
					else
						return;
				}
				else
				{
					sendMessage(p.getUniqueId(), "No permission!", true);
					event.setCancelled(true);
				}
			}

		}
	}

	public boolean saveToFile()
	{
		try
		{
			saveFile.createNewFile(); // Creates saveFile, if not exists
			FileOutputStream f = new FileOutputStream(saveFile, false);
			ObjectOutputStream o = new ObjectOutputStream(f);

			// Write count of objects to file
			o.write(blockLocks.size());

			// Write objects to file
			for (BlockLock i : blockLocks)
			{
				o.writeObject(i);
			}

			o.close();
			f.close();
			Bukkit.getConsoleSender().sendMessage("BlockLock saved " + blockLocks.size() + " BlockLocks");
			return true;
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("BlockLock Error (Save): " + e.getLocalizedMessage());
		}
		return false;
	}

	public boolean loadFromFile()
	{
		try
		{
			if (saveFile.createNewFile()) // Creates saveFile, if not exists
			{
				Bukkit.getConsoleSender().sendMessage("BlockLock Warning (Load): Skipped (Empty file)");
				return false;
			}
			FileInputStream f = new FileInputStream(saveFile);
			ObjectInputStream o = new ObjectInputStream(f);

			int size = o.read();

			for (int i = 0; i < size; i++)
			{
				blockLocks.add((BlockLock) o.readObject());
			}

			o.close();
			f.close();
			Bukkit.getConsoleSender().sendMessage("BlockLock loaded " + blockLocks.size() + "/" + size + " BlockLocks");
			return true;
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("BlockLock Error (Load): " + e.getLocalizedMessage());
		}
		return false;
	}

	public boolean open(Player p, Block b)
	{
		BlockLock bl = getBlockLock(b);
		return (bl != null && p.hasPermission("dg.blockLockPermission") && bl.checkIfPermissionToOpen(p.getUniqueId()));
	}

	public boolean lock(Player p, Block b)
	{
		BlockLock bl = getBlockLock(b);
		if (bl == null && p.hasPermission("dg.blockLockPermission"))
		{
			bl = new BlockLock(b, p.getUniqueId());
			bl.createManagerMenu(this);
			blockLocks.add(bl);
			sendMessage(p.getUniqueId(), b.getType().toString() + " locked!");
			return true;
		}
		sendMessage(p.getUniqueId(), b.getType().toString() + " is already locked!");
		return false;
	}

	public boolean unlock(Player p, Block b)
	{
		BlockLock bl = getBlockLock(b);
		if (bl != null && p.hasPermission("dg.blockLockPermission") && bl.getOwner().equals(p.getUniqueId()))
		{
			blockLocks.remove(bl);
			sendMessage(p.getUniqueId(), b.getType().toString() + " unlocked!");
			System.gc();
			return true;
		}
		sendMessage(p.getUniqueId(), b.getType().toString() + " is already unlocked!");
		return false;
	}

	public boolean addFriend(Player owner, Block b, Player friend)
	{
		if (friend == null)
			return false;

		BlockLock bl = getBlockLock(b);
		if (bl != null && bl.getOwner().equals(owner.getUniqueId()))
		{
			return bl.addFriend(friend.getUniqueId());
		}
		sendMessage(owner.getUniqueId(), "No permission!");
		return false;
	}

	public boolean removeFriend(Player owner, Block b, Player friend)
	{
		if (friend == null)
			return false;

		BlockLock bl = getBlockLock(b);
		if (bl != null && bl.getOwner().equals(owner.getUniqueId()))
		{
			return bl.removeFriend(friend.getUniqueId());
		}
		sendMessage(owner.getUniqueId(), "No permission!");
		return false;
	}

	public boolean addGlobalFriend(Player owner, Block b, Player friend)
	{
		sendMessage(owner.getUniqueId(), "Not yet available. Coming soon.");
		return false; // TODO
//		if (friend == null)
//			return false;
//
//		List<BlockLock> blocks = getAllLockBlocksToPlayer(owner.getUniqueId());
//		for (BlockLock i : blocks)
//		{
//			i.addFriend(friend.getUniqueId());
//		}
//		return true;
	}

	public boolean removeGlobalFriend(Player owner, Block b, Player friend)
	{
		sendMessage(owner.getUniqueId(), "Not yet available. Coming soon.");
		return false; // TODO
//		if (friend == null)
//			return false;
//
//		List<BlockLock> blocks = getAllLockBlocksToPlayer(owner.getUniqueId());
//		for (BlockLock i : blocks)
//		{
//			i.addFriend(friend.getUniqueId());
//		}
//		return true;
	}

	public boolean isBlockLockable(Block block)
	{
		return lockableBlocks.contains(block.getType());
	}

	public BlockLock getBlockLock(Block block)
	{
		if (isBlockLockable(block))
		{
			for (BlockLock i : blockLocks)
			{
				if (i.getBlock().equals(block))
					return i;
			}
		}
		return null;
	}

	public BlockLock getBlockLockFromInventory(Inventory inv)
	{
		for (BlockLock i : blockLocks)
		{
			if (i.getInventory().equals(inv))
				return i;
		}
		return null;
	}

	public List<UUID> getAllPlayer()
	{
		List<UUID> list = new ArrayList<UUID>();

		for (BlockLock i : blockLocks)
		{
			if (!list.contains(i.getOwner()))
			{
				list.add(i.getOwner());
			}
		}

		return list;
	}

	public List<BlockLock> getAllLockBlocksToPlayer(UUID owner)
	{
		List<BlockLock> blocks = new ArrayList<BlockLock>();

		for (BlockLock i : blockLocks)
		{
			if (i.getOwner().equals(owner))
			{
				blocks.add(i);
			}
		}

		return blocks;
	}

	public List<Block> getAllBlockLockBlocks()
	{
		List<Block> blocks = new ArrayList<Block>();

		for (BlockLock i : blockLocks)
		{
			blocks.add(i.getBlock());
		}

		return blocks;
	}

	public Manager getManager()
	{
		return manager;
	}

	/* ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- */
	// Protecting Block:

	/** Preventing BlockLocks (example: doors, hoppers) to be activated by Redstone */
	@EventHandler
	public void onRedstone(BlockRedstoneEvent event)
	{
		BlockLock bl = getBlockLock(event.getBlock());
		if (bl != null && bl.isRedstoneLock())
		{
			event.setNewCurrent(event.getOldCurrent());
		}
	}

	/** Preventing anyone from breaking the Block, or the block below */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		Block b = event.getBlock();
		if (isBlockLockable(b)) // Block
		{
			BlockLock bl = getBlockLock(b);
			if (bl != null)
			{
				if (bl.getOwner().equals(event.getPlayer().getUniqueId()))
					unlock(event.getPlayer(), b);
				else
					event.setCancelled(true);
			}
			else
			{
				int cibb = checkIfBlockBelow(b, event.getPlayer());
				if (cibb == 1)
					event.setCancelled(true);
				else if (cibb == 2 && getBlockLock(b.getRelative(0, 1, 0)).checkIfDoor())
					unlock(event.getPlayer(), b.getRelative(0, 1, 0));
			}

		}
		else
		{

			int cibb = checkIfBlockBelow(b, event.getPlayer());
			if (cibb == 1)
				event.setCancelled(true);
			else if (cibb == 2 && getBlockLock(b.getRelative(0, 1, 0)).checkIfDoor())
				unlock(event.getPlayer(), b.getRelative(0, 1, 0));
		}

	}

	/** Checks if Block b is below a BlockLock (0/1) and if Player p has permission to open it (1/2) */
	public int checkIfBlockBelow(Block b, Player p)
	{
		Block b2 = b.getRelative(0, 1, 0);
		BlockLock bl2 = getBlockLock(b2);
		if (bl2 != null && bl2.isBlockBelowLock())
		{
			if (bl2.checkIfPermissionToOpen(p.getUniqueId()))
				return 2;
			else
				return 1;
		}
		return 0;
	}

	/** Preventing the Block from being damaged */
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event)
	{
		if (event.getBlock().getType().equals(Material.CHEST))
		{
			if (getBlockLock(event.getBlock()) != null)
				event.setCancelled(true);
		}
	}

	/** Preventing the Block from being blown up by a Creeper or TNT */
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		try
		{
			if (event.getEntity() instanceof Creeper || event.getEntity() instanceof TNTPrimed)
			{
				event.blockList().removeAll(getAllBlockLockBlocks());
			}
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("DC onEntityExplode: " + e.getLocalizedMessage());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say DC onEntityExplode: " + e.getLocalizedMessage());
		}
	}

	/** Preventing anyone(hoppers, etc) than the player from grabbing items from the Container */
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event)
	{
		BlockLock source = getBlockLockFromInventory(event.getSource());
		BlockLock dest = getBlockLockFromInventory(event.getDestination());
		if ((source != null && !event.getDestination().getType().equals(InventoryType.PLAYER) && source.isHopperLock()) // Prevents Hopper, etc. from PUTTING items IN the chest
				|| (dest != null && dest.isHopperLock())) // Prevents Hopper, etc. from REMOVING items FROM the chest
		{
			event.setCancelled(true);
		}
	}

}
