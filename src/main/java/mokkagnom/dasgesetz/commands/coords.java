package mokkagnom.dasgesetz.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class coords implements TabExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 1)
		{
			Player p = Bukkit.getServer().getPlayer(args[0]);
			if (p == null)
			{
				sender.sendMessage("Spieler \"" + args[0] + "\" nicht gefunden.");
			}
			else
			{
				sender.sendMessage(p.getLocation().toString());
			}
			return true;
		}
		else
		{
			sender.sendMessage("Ungültiger Syntax");
			return false;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
	{
		if(args.length==1)
		{
			return null;
		}
		return Arrays.asList("");
	}
}
