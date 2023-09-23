package mokkagnom.dasgesetz.BlockLock;

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
	private BlockLockManager clManager;

	public BlockLockCommands(BlockLockManager clManager)
	{
		this.clManager = clManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player p = (Player) sender;
		Block b = p.getTargetBlock(null, 255);

		if (args[0].equalsIgnoreCase("listFriends")) // listFriends
		{
			for (String s : clManager.listFriends(p, b))
			{
				sender.sendMessage(s);
			}
		}

		if (clManager.isBlockLockable(b))
		{
			if (args.length == 1) // lock/unlock
			{
				if (args[0].equalsIgnoreCase("unlock"))
				{
					clManager.unlock(p, b);
				}
				else if (args[0].equalsIgnoreCase("lock"))
				{
					clManager.lock(p, b);
				}
				else
				{
					BlockLockManager.sendMessage(p.getUniqueId(), "Unknown syntax (1)");
					return false;
				}
				return true;

			}
			else if (args.length == 2) // showmenu
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
		else
		{
			BlockLockManager.sendMessage(p.getUniqueId(), "Block not supported");
			return false;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 1)
		{
			return Arrays.asList("lock", "unlock", "addFriend", "removeFriend", "addGlobalFriend", "removeGlobalFriend", "listFriends", "showMenu");
		}
		else if (args.length == 2 && (args[0].equalsIgnoreCase("addFriend") || args[0].equalsIgnoreCase("removeFriend") || args[0].equalsIgnoreCase("addGlobalFriend")
				|| args[0].equalsIgnoreCase("removeGlobalFriend")))
		{
			return null;
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("showMenu"))
		{
			return Arrays.asList("0", "1");
		}
		return Arrays.asList("");
	}

}
