package mokkagnom.dasgesetz.BlockLock;

// Bukkit-Event:
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
// Bukkit:
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
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
	public static final List<Material> lockableBlocks = Arrays.asList(Material.CHERRY_DOOR, Material.ACACIA_DOOR, Material.ACACIA_TRAPDOOR, Material.BIRCH_DOOR, Material.BIRCH_TRAPDOOR,
			Material.CRIMSON_DOOR, Material.CRIMSON_TRAPDOOR, Material.DARK_OAK_DOOR, Material.DARK_OAK_TRAPDOOR, Material.IRON_DOOR, Material.IRON_TRAPDOOR, Material.JUNGLE_DOOR,
			Material.JUNGLE_TRAPDOOR, Material.MANGROVE_DOOR, Material.MANGROVE_TRAPDOOR, Material.OAK_DOOR, Material.OAK_TRAPDOOR, Material.SPRUCE_DOOR, Material.SPRUCE_TRAPDOOR,
			Material.WARPED_DOOR, Material.WARPED_TRAPDOOR, Material.CHEST, Material.TRAPPED_CHEST, Material.HOPPER, Material.FURNACE, Material.BLAST_FURNACE, Material.SMOKER,
			Material.BARREL, Material.BREWING_STAND, Material.OAK_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.BAMBOO_FENCE_GATE, Material.CHERRY_FENCE_GATE,
			Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.WARPED_FENCE_GATE, Material.CRIMSON_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.MANGROVE_FENCE_GATE);

	public static final String blockLockKey = "BlockLock";
	private Manager manager;
	private File saveFile;
	private List<BlockLockUser> players;
	private boolean globalHopperProtection;

	public BlockLockManager(Manager manager)
	{
		this.manager = manager;
		this.saveFile = new File(this.manager.getDataFolder(), "BlockLock.bin");
		players = new ArrayList<BlockLockUser>();
		globalHopperProtection = true;
	}

	public static boolean sendMessage(UUID receiver, String message, boolean error, boolean consoleLog)
	{
		if (receiver != null)
		{
			Player p = Bukkit.getServer().getPlayer(receiver);
			if (p != null && message != null)
			{
				if (consoleLog)
				{
					Bukkit.getConsoleSender().sendMessage("BlockLock Error: " + message);
				}
				if (error)
				{
					p.sendMessage("§7[§BBlockLock§7] §C" + message);
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

	public static boolean sendMessage(UUID receiver, String message, boolean error)
	{
		return sendMessage(receiver, message, error, false);
	}

	public static boolean sendMessage(UUID receiver, String message)
	{
		return sendMessage(receiver, message, false, false);
	}

	@EventHandler
	public void onWorldSave(WorldSaveEvent event)
	{
		if (event.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName()))
			saveToFile();
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Block b = event.getBlockPlaced();
		if (isBlockLockable(b))
		{
			if (!checkIfNextBlockIsLocked(b, event.getPlayer().getUniqueId()))
			{
				lock(event.getPlayer(), b);
			}
			else
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			Player p = event.getPlayer();
			Block block = event.getClickedBlock();

			if (block.hasMetadata(blockLockKey))
			{
				BlockLock bl = getBlockLock(block);
				if (bl.checkIfPermissionToOpen(p.getUniqueId()))
				{
					if (p.isSneaking() && getBlockLockUser(p.getUniqueId()).getUseSneakMenu())
					{
						bl.openManagerInventory(p);
						event.setCancelled(true);
					}
				}
				else
				{
					sendMessage(p.getUniqueId(), "No permission!", true);
					event.setCancelled(true);
				}
			}
		}
	}

	public void validateAll()
	{
		try
		{
			for (BlockLockUser blu : players)
			{
				List<BlockLock> blocklocks = new ArrayList<BlockLock>();
				blocklocks.addAll(blu.getBlockLocks());
				for (BlockLock bl : blocklocks)
				{
					if (!isBlockLockable(bl.getBlock()))
					{
						bl.unlock(this);
					}
				}
			}
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("BlockLock Error (Validate): " + e.getLocalizedMessage());
		}
	}

	public boolean saveToFile()
	{
		validateAll();
		try
		{
			saveFile.createNewFile(); // Creates saveFile, if not exists
			FileOutputStream f = new FileOutputStream(saveFile, false);
			ObjectOutputStream o = new ObjectOutputStream(f);

			// Write count of objects to file
			o.write(players.size());

			// Write objects to file
			for (BlockLockUser i : players)
			{
				o.writeObject(i);
			}

			o.close();
			f.close();
			Bukkit.getConsoleSender().sendMessage("BlockLockPlayers saved " + players.size() + " BlockLockPlayers");
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
				BlockLockUser blu = (BlockLockUser) o.readObject();
				blu.createAllBlockLockManagerMenus(this);
				players.add(blu);
			}

			o.close();
			f.close();
			Bukkit.getConsoleSender().sendMessage("BlockLock loaded " + players.size() + "/" + size + " BlockLockPlayers");
			Bukkit.getConsoleSender().sendMessage("BlockLock loaded " + getAllBlockLockBlocks().size() + " BlockLocks");
			return true;
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("BlockLock Error (Load): " + e.getLocalizedMessage());
		}
		return false;
	}

	public boolean lock(Player p, Block b)
	{
		if (!isBlockLockable(b))
		{
			BlockLockManager.sendMessage(p.getUniqueId(), "Block not supported");
			return false;
		}

		if (!b.hasMetadata(blockLockKey) && p.hasPermission("dg.blockLockPermission"))
		{
			getBlockLockUser(p.getUniqueId()).createBlockLock(b, this);
			sendMessage(p.getUniqueId(), b.getType().toString() + " locked!");
			return true;
		}
		sendMessage(p.getUniqueId(), b.getType().toString() + " is already locked!");
		return false;
	}

	public boolean unlock(Player p, Block b)
	{
		if (b.hasMetadata(blockLockKey))
		{
			BlockLock bl = getBlockLock(b);
			if (p.hasPermission("dg.blockLockPermission") && bl.checkIfPermissionToOpen(p.getUniqueId()))
			{
				bl.unlock(this);
				sendMessage(p.getUniqueId(), b.getType().toString() + " unlocked!");
				System.gc();
				return true;
			}
			else
			{
				sendMessage(p.getUniqueId(), "No permission!", true);
				return false;
			}
		}
		sendMessage(p.getUniqueId(), b.getType().toString() + " is not locked!");
		return false;
	}

	public void showMenu(Player p, boolean b)
	{
		getBlockLockUser(p.getUniqueId()).setUseSneakMenu(b);
	}

	public List<String> listFriends(Player owner, Block b)
	{
		List<String> list = new ArrayList<String>();
		BlockLock bl = getBlockLock(b);
		if (bl != null && bl.checkIfPermissionToOpen(owner.getUniqueId()))
		{
			for (UUID i : bl.getAllFriends())
			{
				list.add(Bukkit.getOfflinePlayer(i).getName() + "/" + i);
			}
		}
		else
		{
			for (UUID i : getBlockLockUser(owner.getUniqueId()).getFriends())
			{
				list.add(Bukkit.getOfflinePlayer(i).getName() + "/" + i);
			}
		}

		if (list.size() == 0)
			list.add("No friends");
		return list;
	}

	public boolean addFriend(UUID owner, Block b, UUID friend)
	{
		if (owner == null || friend == null)
			return false;

		BlockLock bl = getBlockLock(b);
		if (bl != null && bl.getOwner().getUuid().equals(owner))
		{
			return bl.addFriend(friend);
		}
		return false;
	}

	public boolean removeFriend(UUID owner, Block b, UUID friend)
	{
		if (owner == null || friend == null)
			return false;

		BlockLock bl = getBlockLock(b);
		if (bl != null && bl.getOwner().getUuid().equals(owner))
		{
			return bl.removeFriend(friend);
		}
		return false;
	}

	public boolean addGlobalFriend(UUID owner, UUID friend)
	{
		if (owner == null || friend == null)
			return false;
		return getBlockLockUser(owner).addFriend(friend);
	}

	public boolean removeGlobalFriend(UUID owner, UUID friend)
	{
		if (owner == null || friend == null)
			return false;
		return getBlockLockUser(owner).removeFriend(friend);
	}

	public BlockLockUser createBlockLockUser(UUID uuid)
	{
		BlockLockUser blu = new BlockLockUser(uuid);
		players.add(blu);
		return blu;
	}

	public boolean checkIfNextBlockIsLocked(Block b, UUID uuid)
	{
		BlockLock bl = null;
		if (getBlockLock(b.getRelative(1, 0, 0)) != null)
			bl = getBlockLock(b.getRelative(1, 0, 0));
		else if (getBlockLock(b.getRelative(-1, 0, 0)) != null)
			bl = getBlockLock(b.getRelative(-1, 0, 0));
		else if (getBlockLock(b.getRelative(0, 1, 0)) != null)
			bl = getBlockLock(b.getRelative(0, 1, 0));
		else if (getBlockLock(b.getRelative(0, -1, 0)) != null)
			bl = getBlockLock(b.getRelative(0, -1, 0));
		else if (getBlockLock(b.getRelative(0, 0, 1)) != null)
			bl = getBlockLock(b.getRelative(0, 0, 1));
		else if (getBlockLock(b.getRelative(0, 0, -1)) != null)
			bl = getBlockLock(b.getRelative(0, 0, -1));

		if (bl == null)
			return false;
		else if (bl.checkIfPermissionToOpen(uuid))
			return false;
		else
			return true;
	}

	public BlockLockUser getBlockLockUser(UUID uuid)
	{
		for (BlockLockUser i : players)
		{
			if (i.getUuid().equals(uuid))
				return i;
		}
		return createBlockLockUser(uuid);
	}

	public boolean isBlockLockable(Block block)
	{
		// return block instanceof Chest || block instanceof Door || block instanceof Gate || block instanceof InventoryHolder;
		return lockableBlocks.contains(block.getType());
	}

	public BlockLock getBlockLock(Block block)
	{
		if (block.hasMetadata(blockLockKey) && isBlockLockable(block))
		{
			for (BlockLockUser i : players)
			{
				for (BlockLock bl : i.getBlockLocks())
				{
					if (bl.getBlock().equals(block))
						return bl;
				}
			}
		}
		return null;
	}

	public BlockLock getBlockLockFromInventory(Inventory inv)
	{
		if (inv != null)
		{
			try
			{
				Location loc = inv.getLocation();
				if (loc != null)
				{
					Block b = loc.getBlock();
					if (b.hasMetadata(blockLockKey))
					{
						return getBlockLockUser(UUID.fromString(b.getMetadata(blockLockKey).get(0).asString())).getBlockLock(b);
					}
				}
			}
			catch (Exception e)
			{
				Bukkit.getLogger().warning("BlockLock: getBlockLockFromInventory: " + e.getLocalizedMessage());
			}
		}
		return null;
	}

	public List<BlockLockUser> getAllPlayer()
	{
		return players;
	}

	public List<BlockLock> getAllLockBlocksToPlayer(UUID owner)
	{
		for (BlockLockUser i : players)
		{
			if (i.getUuid().equals(owner))
				return i.getBlockLocks();
		}

		return new ArrayList<BlockLock>();
	}

	public List<Block> getAllBlockLockBlocks()
	{
		List<Block> blocks = new ArrayList<Block>();

		for (BlockLockUser i : players)
		{
			for (BlockLock bl : i.getBlockLocks())
			{
				blocks.add(bl.getBlock());
			}
		}

		return blocks;
	}

	public Manager getManager()
	{
		return manager;
	}

	public boolean getGlobalHopperProtection()
	{
		return globalHopperProtection;
	}

	public void setGlobalHopperProtection(boolean b)
	{
		globalHopperProtection = b;
	}

	/* ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- */
	// Protecting Block:

	/** Preventing BlockLocks (example: doors, hoppers) to be activated by Redstone */
	@EventHandler
	public void onRedstone(BlockRedstoneEvent event)
	{
		Block block = event.getBlock();
		if (block.hasMetadata(blockLockKey) && getBlockLock(block).isRedstoneLock())
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
			if (b.hasMetadata(blockLockKey))
			{
				BlockLock bl = getBlockLock(b);
				if (bl.getOwner().getUuid().equals(event.getPlayer().getUniqueId()))
					unlock(event.getPlayer(), b);
				else
					event.setCancelled(true);
			}
			/*
			 * else { int cibb = checkIfBlockBelow(b, event.getPlayer()); if (cibb == 1) event.setCancelled(true); else if (cibb == 2 && getBlockLock(b.getRelative(0, 1, 0)).checkIfDoor())
			 * unlock(event.getPlayer(), b.getRelative(0, 1, 0)); }
			 */

		}
		/*
		 * else { int cibb = checkIfBlockBelow(b, event.getPlayer()); if (cibb == 1) event.setCancelled(true); else if (cibb == 2 && getBlockLock(b.getRelative(0, 1, 0)).checkIfDoor())
		 * unlock(event.getPlayer(), b.getRelative(0, 1, 0)); }
		 */

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

	/** Preventing the Block from being blown up by Creeper, Wither or TNT */
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		try
		{
			if (event.getEntity() instanceof Creeper || event.getEntity() instanceof TNTPrimed || event.getEntity() instanceof Wither)
			{
				List<Block> removeBlocks = new ArrayList<Block>();
				for (Block i : event.blockList())
				{
					if (i.hasMetadata(blockLockKey))
						removeBlocks.add(i);
				}
				event.blockList().removeAll(removeBlocks);
			}
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("BL onEntityExplode: " + e.getLocalizedMessage());
		}
	}

	/** Preventing hoppers from grabbing/putting items from/to the container */
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event)
	{
		if (!globalHopperProtection)
			return;

		if (event.getSource().getType().equals(InventoryType.HOPPER) || event.getDestination().getType().equals(InventoryType.HOPPER))
		{
			BlockLock source = getBlockLockFromInventory(event.getSource());
			BlockLock dest = getBlockLockFromInventory(event.getDestination());

			if ((source != null && source.isHopperLock()) // Prevents Hopper, etc. from PUTTING items IN the chest
					|| (dest != null && dest.isHopperLock())) // Prevents Hopper, etc. from REMOVING items FROM the chest
			{
				event.setCancelled(true);
			}
		}
	}

}
