package mokkagnom.dasgesetz.BlockLock;

// Bukkit:
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
// Java:
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockLock implements Serializable
{
	private static final long serialVersionUID = -7616202315683796612L;
	private transient BlockLockManagerMenu blmm;
	private int blockPosition[];
	private String worldName;
	private BlockLockUser owner;
	private List<UUID> friends;
	private boolean hopperLock;
	private boolean redstoneLock;
	private boolean blockBelowLock;
	private BlockLock secondBlockLock;

	public BlockLock(Block block, BlockLockUser owner)
	{
		blockPosition = new int[3];
		blockPosition[0] = block.getX();
		blockPosition[1] = block.getY();
		blockPosition[2] = block.getZ();
		worldName = block.getWorld().getName();

		this.blmm = null;
		this.owner = owner;
		this.friends = new ArrayList<UUID>();
		this.hopperLock = true;
		this.redstoneLock = true;
		this.blockBelowLock = true;

		secondBlockLock = null;
	}

	protected void finalize()
	{
		if (this.blmm != null)
		{
			HandlerList.unregisterAll(this.blmm);
		}
	}

	public boolean unlock()
	{
		return owner.removeBlockLock(this);
	}

	public boolean createManagerMenu(BlockLockManager blManager)
	{
		if (blmm == null)
		{
			this.blmm = new BlockLockManagerMenu(blManager, this);
			blManager.getManager().getMain().getServer().getPluginManager().registerEvents(blmm, blManager.getManager().getMain());
			return true;
		}
		return false;
	}

	public boolean openManagerInventory(Player p)
	{
		if (checkIfPermissionToOpen(p.getUniqueId()))
		{
			try
			{
				blmm.open(p);
				return true;
			}
			catch(Exception e)
			{
				Bukkit.getLogger().severe("BlockLock: openManagerInventory(Player p): blmm.open(Player p) Exception: " + e.getLocalizedMessage());
				return false;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return 0: No Chest | 1: Single | 2: Right | 3: Left
	 */
	static public int checkIfDoubleChest(Block b)
	{
		if (b.getBlockData() instanceof Chest chest)
		{
			Chest.Type type = chest.getType();
			if (type.equals(Chest.Type.SINGLE))
				return 1;
			else if (type.equals(Chest.Type.LEFT))
				return 2;
			else if (type.equals(Chest.Type.RIGHT))
				return 3;
		}
		return 0;
	}

	/**
	 * 
	 * @return 0: No Chest | 1: Single | 2: Right | 3: Left
	 */
	public int checkIfDoubleChest()
	{
		return checkIfDoubleChest(getBlock());
	}

	public boolean checkIfDoor()
	{
		Block b = getBlock();
		return (b.getBlockData() instanceof Door);
	}

	public boolean checkIfPermissionToOpen(UUID uuid)
	{
		Player p = Bukkit.getPlayer(uuid);
		if ((p != null && p.hasPermission("dg.blockLockByPassPermission")) || owner.getUuid().equals(uuid)) // Owner / Admin
		{
			return true;
		}
		for (UUID i : friends) // Local friends
		{
			if (i.equals(uuid))
				return true;
		}
		for (UUID i : owner.getFriends()) // Global friends
		{
			if (i.equals(uuid))
				return true;
		}
		return false;
	}

	public boolean addFriend(UUID friend)
	{
		if (!friends.contains(friend) && friends.size() < 54)
		{
			friends.add(friend);
			return true;
		}
		return false;
	}

	public boolean removeFriend(UUID friend)
	{
		if (friends.contains(friend))
		{
			friends.remove(friend);
			return true;
		}
		return false;
	}

	public String getLocationAsString()
	{
		return "X" + blockPosition[0] + "Y" + blockPosition[1] + "Z" + blockPosition[2];
	}

	// Getter/Setter

	public boolean isHopperLock()
	{
		return hopperLock;
	}

	public void setHopperLock(boolean hopperLock)
	{
		this.hopperLock = hopperLock;
	}

	public boolean isRedstoneLock()
	{
		return redstoneLock;
	}

	public void setRedstoneLock(boolean redstoneLock)
	{
		this.redstoneLock = redstoneLock;
	}

	public boolean isBlockBelowLock()
	{
		return blockBelowLock;
	}

	public void setBlockBelowLock(boolean blockBelowLock)
	{
		this.blockBelowLock = blockBelowLock;
	}

	public Block getBlock()
	{
		return Bukkit.getServer().getWorld(worldName).getBlockAt(blockPosition[0], blockPosition[1], blockPosition[2]);
	}

	public BlockLockUser getOwner()
	{
		return owner;
	}

	public Inventory getInventory()
	{
		try
		{
			Block block = getBlock();
			if (block.getState() instanceof InventoryHolder)
			{
				return ((InventoryHolder) block.getState()).getInventory();
			}
			return null;
		}
		catch (Exception e)
		{
			Bukkit.getLogger().severe("BL.getInventory: InventoryHolder Exception: " + e.getLocalizedMessage());
			return null;
		}
	}

	public List<UUID> getLocaFriends()
	{
		return friends;
	}

	public List<UUID> getAllFriends()
	{
		List<UUID> list = new ArrayList<UUID>();

		for (UUID i : owner.getFriends())
		{
			list.add(i);
		}
		for (UUID i : friends)
		{
			if (!list.contains(i))
				list.add(i);
		}

		return list;
	}

	public BlockLockManagerMenu getBlockLockManagerMenu()
	{
		return blmm;
	}

	public void setBlockLockManagerMenu(BlockLockManagerMenu blmm)
	{
		this.blmm = blmm;
	}

	public BlockLock getSecondBlockLock()
	{
		return secondBlockLock;
	}

	public void setSecondBlockLock(BlockLock bl)
	{
		secondBlockLock = bl;
	}

}
