package mokkagnom.dasgesetz.BlockLock;

import java.util.Arrays;
import java.util.List;

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
				else if (args[0].equalsIgnoreCase("listFriends"))
				{
					// TODO
				}
				else
				{
					BlockLockManager.sendMessage(p.getUniqueId(), "Ungültiger Syntax (1)");
					return false;
				}
				return true;

			}
			else if (args.length == 2) // Friends
			{
				Player friend = Bukkit.getPlayer(args[1]);
				if (friend == null)
				{
					for (OfflinePlayer i : Bukkit.getOfflinePlayers())
					{
						if (i.getName().equalsIgnoreCase(args[1]))
						{
							friend = i.getPlayer();
							break;
						}
					}
				}

				if (args[0].equalsIgnoreCase("addfriend"))
				{
					if (clManager.addFriend(p, b, friend))
						BlockLockManager.sendMessage(p.getUniqueId(), friend.getName() + " added (local)");
					else
						BlockLockManager.sendMessage(p.getUniqueId(), "Couldn't add (local) " + friend.getName());
				}
				else if (args[0].equalsIgnoreCase("removefriend"))
				{
					if (clManager.removeFriend(p, b, friend))
						BlockLockManager.sendMessage(p.getUniqueId(), friend.getName() + " removed (local)");
					else
						BlockLockManager.sendMessage(p.getUniqueId(), "Couldn't remove (local) " + friend.getName());
				}
				else
				{
					BlockLockManager.sendMessage(p.getUniqueId(), "Ungültiger Syntax (2)");
					return false;
				}
				return true;
			}
			else
			{
				BlockLockManager.sendMessage(p.getUniqueId(), "Ungültiger Syntax (0)");
				return false;
			}
		}
		else
		{
			BlockLockManager.sendMessage(p.getUniqueId(), "Kein unterstützter Block");
			return false;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 1)
		{
			return Arrays.asList("lock", "unlock", "addFriend", "removeFriend");
		}
		else if (args.length == 2 && (args[0].equalsIgnoreCase("addFriend") || args[0].equalsIgnoreCase("removeFriend")))
		{
			return null;
		}
		return Arrays.asList("");
	}

}
