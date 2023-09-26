package mokkagnom.dasgesetz.DeathChest;

//Bukkit-Event:
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
//Bukkit:
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.block.Block;
//Java:
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import mokkagnom.dasgesetz.Main;

public class DeathChestManager implements Listener
{
	private Main main;
	private long timer;
	private boolean dropItems;
	private List<DeathChest> deathChests;

	public DeathChestManager(Main main, long timerInTicks, boolean dropItems)
	{
		this.main = main;
		this.timer = timerInTicks;
		this.dropItems = dropItems;
		deathChests = new ArrayList<DeathChest>();
	}

	/** Sends a message to a player */
	public static boolean sendMessage(UUID receiver, String message, boolean error)
	{
		if (receiver != null)
		{
			Player p = Bukkit.getServer().getPlayer(receiver);
			if (p != null && message != null)
			{
				if (error)
				{
					p.sendMessage("§7[§BDeathChest§7] §C" + message);
					Bukkit.getConsoleSender().sendMessage("DeathChest Error: " + message);
				}
				else
				{
					p.sendMessage("§7[§BDeathChest§7] §D" + message);
				}
				return true;
			}
		}
		return false;
	}

	/** Sends a message to a player */
	public static boolean sendMessage(UUID receiver, String message)
	{
		return sendMessage(receiver, message, false);
	}

	@EventHandler
	public void onWorldSave(WorldSaveEvent event)
	{
		if (event.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName()))
		{
			List<DeathChest> oldList = new ArrayList<DeathChest>();
			oldList.addAll(deathChests);

			for (DeathChest i : oldList) i.removeIfEmpty(true);
		}
	}

	/** Creates a DeathChest */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Player p = event.getEntity();
		if (p.hasPermission("dg.deathChestPermission"))
		{
			DeathChest dc = new DeathChest(this, p, event.getDrops(), main, timer, dropItems);
			deathChests.add(dc);
			if (dc.equals(deathChests.get(deathChests.size() - 1)))
				event.getDrops().clear();
		}
		else
		{
			sendMessage(p.getUniqueId(), "No permission to create a DeathChest!");
		}
	}

	/** Let a player with according permissions, collect the chest, either by sneak+click or normally opening the chest */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().equals(Material.CHEST))
		{
			Player p = event.getPlayer();
			DeathChest dc = getDeathChest(event.getClickedBlock());
			if (dc != null)
			{
				event.setCancelled(true); // To stop the "normal" chest inventory from opening
				if ((dc.checkIfOwner(p) && p.hasPermission("dg.deathChestPermission")) || p.hasPermission("dg.deathChestByPassPermission"))
				{
					try
					{
						if (dc.collect())
						{
							deathChests.remove(dc);
							sendMessage(p.getUniqueId(), "Collected!");
						}
					}
					catch (Exception e)
					{
						sendMessage(event.getPlayer().getUniqueId(), "Collect Error: " + e.getLocalizedMessage(), true);
					}
				}
				else
				{
					sendMessage(p.getUniqueId(), "No permission!");
					// sendMessage(dc.getOwner().getUniqueId(), p.getName() + " tried to open your Death Chest at X:" + dc.getBlock().getLocation().getX() + " Y:"
					// + dc.getBlock().getLocation().getY() + " Z:" + dc.getBlock().getLocation().getZ());
				}
			}
		}
	}

	/** Checks if DeathChest is empty and removes it if so */
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		List<DeathChest> list = new ArrayList<DeathChest>();
		list.addAll(deathChests);
		for (DeathChest i : list)
		{
			if (event.getInventory().equals(i.getChestInventory()))
			{
				i.removeIfEmpty();
			}
		}
	}

	public boolean removeDeathChest(DeathChest dc)
	{
		if (dc != null)
			return deathChests.remove(dc);
		return false;
	}

	/** Removing all DeathChests (this will drop their contents, just like timeout) */
	public void removeAllDeathChests()
	{
		List<DeathChest> list = new ArrayList<DeathChest>();
		list.addAll(deathChests);
		for (DeathChest i : list)
		{
			i.remove();
		}
		deathChests.clear();
	}

	public List<Block> getDeathChestBlocks()
	{
		List<Block> blocks = new ArrayList<Block>();
		for (DeathChest i : deathChests)
		{
			blocks.add(i.getBlock());
		}
		return blocks;
	}

	/** Checks if block is a DeathChest and returns it, otherwise null */
	public DeathChest getDeathChest(Block block)
	{
		if (block.getType().equals(Material.CHEST))
		{
			for (DeathChest i : deathChests)
			{
				if (i.getBlock().equals(block))
					return i;
			}
		}
		return null;
	}

	/** Checks if inventory belongs to a DeathChest */
	public boolean isInventoryDeathChest(Inventory inv)
	{
		for (DeathChest i : deathChests)
		{
			if (i.getChestInventory().equals(inv))
				return true;
		}
		return false;
	}

	/** Gets all DeathChest belonging to Player p */
	public List<DeathChest> getDeathChestFromPlayer(Player player)
	{
		List<DeathChest> dc = new ArrayList<DeathChest>();
		for (DeathChest i : deathChests)
		{
			if (i.checkIfOwner(player))
				dc.add(i);

		}
		return dc;
	}

	// Protecting DeathChest:

	/** Preventing player from putting items in the DeathChest */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		// Note:
		// event.getInventory() = DeathChest / Chest
		// event.getClickedInventory() = DeathChest / Chest or Player (depending on which inv was clicked)

		if (isInventoryDeathChest(event.getInventory()))
		{
			if ((event.getClickedInventory().getType().equals(InventoryType.PLAYER) && (event.isShiftClick() && event.isLeftClick()))
					|| (isInventoryDeathChest(event.getClickedInventory()) && !event.getCursor().getType().equals(Material.AIR)))
			{
				event.setCancelled(true);
			}
		}
	}

	/** Preventing anyone from breaking the DeathChest */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.getBlock().getType().equals(Material.CHEST))
		{
			if (getDeathChest(event.getBlock()) != null)
				event.setCancelled(true);
		}
	}

	/** Preventing the DeathChest from being damaged */
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event)
	{
		if (event.getBlock().getType().equals(Material.CHEST))
		{
			if (getDeathChest(event.getBlock()) != null)
				event.setCancelled(true);
		}
	}

	/** Preventing the DeathChest from being blown up by a Creeper, Wither or TNT */
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		try
		{
			if (event.getEntity() instanceof Creeper || event.getEntity() instanceof TNTPrimed || event.getEntity() instanceof Wither)
			{
				event.blockList().removeAll(getDeathChestBlocks());
			}
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("DC onEntityExplode: " + e.getLocalizedMessage());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say DC onEntityExplode: " + e.getLocalizedMessage());
		}
	}

	/** Preventing anyone(hoppers, etc) than the player from grabbing items from the DeathChest */
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event)
	{
		if ((isInventoryDeathChest(event.getSource()) && !event.getDestination().getType().equals(InventoryType.PLAYER)) || isInventoryDeathChest(event.getDestination()))
			event.setCancelled(true);
	}
}
