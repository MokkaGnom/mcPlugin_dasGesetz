package mokkagnom.dasgesetz.BlockLock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

// Bukkit:
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
// Java:

public class BlockLockCommands implements TabExecutor
{
	private static List<String> commands = Arrays.asList("lock", "unlock", "addFriend", "removeFriend", "addGlobalFriend", "removeGlobalFriend", "listFriends", "showMenu",
			"globalHopperProtection");
	private BlockLockManager clManager;

	public BlockLockCommands(BlockLockManager clManager)
	{
		this.clManager = clManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("You must be a player to use this command");
			return false;
		}

		Player p = (Player) sender;
		Block b = p.getTargetBlockExact(255);

		if (args.length == 1) // lock/unlock/listFriends
		{
			if (args[0].equalsIgnoreCase("unlock"))
			{
				clManager.unlock(p, b);
			}
			else if (args[0].equalsIgnoreCase("lock"))
			{
				clManager.lock(p, b);
			}
			else if (args[0].equalsIgnoreCase("listFriends"))
			{
				for (String s : clManager.listFriends(p, b))
				{
					sender.sendMessage(s);
				}
			}
			else
			{
				BlockLockManager.sendMessage(p.getUniqueId(), "Unknown syntax (1)");
				return false;
			}
			return true;

		}
		else if (args.length == 2) // showmenu & globalHopperProtection
		{
			if (args[0].equalsIgnoreCase("showMenu"))
			{
				boolean bool = args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("1");
				clManager.showMenu(p, bool);
				if (bool)
					BlockLockManager.sendMessage(p.getUniqueId(), "Menu active");
				else
					BlockLockManager.sendMessage(p.getUniqueId(), "Menu inactive");
				return true;
			}
			else if (args[0].equalsIgnoreCase("globalHopperProtection"))
			{
				if (p.isOp())
				{
					boolean bool = args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("1");
					clManager.setGlobalHopperProtection(bool);
					if (bool)
						BlockLockManager.sendMessage(p.getUniqueId(), "GlobalHopperProtection active");
					else
						BlockLockManager.sendMessage(p.getUniqueId(), "GlobalHopperProtection inactive");
					return true;
				}
				else
				{
					BlockLockManager.sendMessage(p.getUniqueId(), "No Permission");
					return true;
				}
			}

			// Friends:
			Player friendPlayer = Bukkit.getPlayer(args[1]);
			UUID owner = p.getUniqueId();
			UUID friend = null;
			String friendName = null;
			if (friendPlayer == null)
			{
				for (OfflinePlayer i : Bukkit.getOfflinePlayers())
				{
					if (i.getName().equalsIgnoreCase(args[1]))
					{
						friendPlayer = i.getPlayer();
						break;
					}
				}
			}

			if (friendPlayer != null)
			{
				try
				{
					friend = friendPlayer.getUniqueId();
					friendName = friendPlayer.getName();
				}
				catch (Exception e)
				{
					Bukkit.getLogger().severe("BLcmd: Friend: FriendPlayer Exception: " + e.getLocalizedMessage());
					BlockLockManager.sendMessage(owner, "BLcmd: Friend: FriendPlayer Exception: " + e.getLocalizedMessage());
				}

				if (args[0].equalsIgnoreCase("addfriend"))
				{
					if (clManager.addFriend(owner, b, friend))
						BlockLockManager.sendMessage(owner, friendName + " added (local)");
					else
						BlockLockManager.sendMessage(owner, "Couldn't add (local) " + friendName);
				}
				else if (args[0].equalsIgnoreCase("removefriend"))
				{
					if (clManager.removeFriend(owner, b, friend))
						BlockLockManager.sendMessage(owner, friendName + " removed (local)");
					else
						BlockLockManager.sendMessage(owner, "Couldn't remove (local) " + friendName);
				}
				else if (args[0].equalsIgnoreCase("addGlobalFriend"))
				{
					if (clManager.addGlobalFriend(owner, friend))
						BlockLockManager.sendMessage(owner, friendName + " added (global)");
					else
						BlockLockManager.sendMessage(owner, "Couldn't add (global) " + friendName);
				}
				else if (args[0].equalsIgnoreCase("removeGlobalFriend"))
				{
					if (clManager.removeGlobalFriend(owner, friend))
						BlockLockManager.sendMessage(owner, friendName + " removed (global)");
					else
						BlockLockManager.sendMessage(owner, "Couldn't remove (global) " + friendName);
				}
				else
				{
					BlockLockManager.sendMessage(p.getUniqueId(), "Unknown syntax (2)");
					return false;
				}
				return true;
			}
			else
			{
				BlockLockManager.sendMessage(p.getUniqueId(), "Couldn't find player \"" + args[1] + "\"");
				return true;
			}
		}
		else
		{
			BlockLockManager.sendMessage(p.getUniqueId(), "Unknown syntax (0)");
			return false;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("You must be a player to use this command");
			return Arrays.asList("");
		}

		Player p = (Player) sender;

		if (args.length == 1)
		{
			if (sender instanceof Player && ((Player) sender).isOp())
				return commands;
			else
			{
				List<String> list = new ArrayList<String>();
				list.addAll(commands);
				list.remove("globalHopperProtection");
				return list;
			}
		}
		else if (args.length == 2)
		{
			if ((args[0].equalsIgnoreCase("addFriend") || args[0].equalsIgnoreCase("addGlobalFriend")) || args[0].equalsIgnoreCase("removeFriend"))
			{
				return null;
			}
			else if (args[0].equalsIgnoreCase("removeGlobalFriend"))
			{
				return clManager.getBlockLockUser(p.getUniqueId()).getFriendsAsString();
			}
			else if ((args[0].equalsIgnoreCase("showMenu") || args[0].equalsIgnoreCase("globalHopperProtection")))
			{
				return Arrays.asList("0", "1");
			}
		}
		return Arrays.asList("");
	}

}
