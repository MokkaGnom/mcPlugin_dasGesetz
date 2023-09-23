package mokkagnom.dasgesetz;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import mokkagnom.dasgesetz.BlockLock.BlockLockCommands;
import mokkagnom.dasgesetz.BlockLock.BlockLockManager;
import mokkagnom.dasgesetz.DeathChest.*;
import mokkagnom.dasgesetz.Farming.EasyFarming;
import mokkagnom.dasgesetz.Farming.Timber;
import mokkagnom.dasgesetz.Ping.PingManager;
import mokkagnom.dasgesetz.Home.HomeCommands;
import mokkagnom.dasgesetz.Other.Messages;
import mokkagnom.dasgesetz.Other.blockLogger;
import mokkagnom.dasgesetz.commands.coords;
import mokkagnom.dasgesetz.commands.dasGesetz;
import mokkagnom.dasgesetz.commands.weatherClear;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;

public class Manager implements TabExecutor
{
	private Main main = null;
	private final String[] plugins = { "DasGesetz", "WeatherClear", "Coords", "BlockLogger", "Timber", "DeathChest", "BlockLock", "Messages", "Home", "EasyFarming", "Ping" };
	private final String[] commands = { "dasGesetz", "weatherClear", "coords", "blockLock", "home" };
	private final TabExecutor[] commandExe;
	private final Listener[] commandListener;

	public Manager(Main main)
	{
		this.main = main;
		commandListener = new Listener[] { new blockLogger(), new Timber(main.getConfig().getBoolean("Timber.BreakLeaves"), main.getConfig().getInt("Timber.BreakLeavesRadius")),
				new DeathChestManager(main, 12000, true), new BlockLockManager(this), new Messages(main.getConfig().getBoolean("Messages.ShowOPJoinMessage")), new EasyFarming(),
				new PingManager(main, main.getConfig().getInt("Ping.Duration"), main.getConfig().getInt("Ping.Cooldown")) };
		commandExe = new TabExecutor[] { new dasGesetz(), new weatherClear(), new coords(), new BlockLockCommands((BlockLockManager) commandListener[3]),
				new HomeCommands(this, main.getConfig().getInt("Homes.MaxHomes")) };
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 2)
		{
			int index = Arrays.asList(plugins).indexOf(args[0]);
			boolean deactivate = args[1] == "0";

			if (index == -1)
			{
				sender.sendMessage("Ungültiges Plugin");
				return false;
			}

			main.getConfig().set("Manager." + plugins[index], deactivate);

			if (deactivate)
				sender.sendMessage("Plugin: \"" + plugins[index] + "\" wurde aktiviert");
			else
				sender.sendMessage("Plugin: \"" + plugins[index] + "\" wurde deaktiviert");
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
		if (args.length == 1)
		{
			return Arrays.asList(plugins);
		}
		else if (args.length == 2)
		{
			return Arrays.asList("0", "1");
		}
		return Arrays.asList("");
	}

	public void createConfig()
	{
		FileConfiguration config = main.getConfig();

		config.addDefault("DeathChest.DespawnInTicks", 12000);
		config.addDefault("DeathChest.DespawnDropping", true);
		config.addDefault("Messages.ShowOPJoinMessage", true);
		config.addDefault("Homes.MaxHomes", 10);
		config.addDefault("Timber.BreakLeaves", true);
		config.addDefault("Timber.BreakLeavesRadius", 4);
		config.addDefault("Ping.Duration", 5000);
		config.addDefault("Ping.Cooldown", 5000);

		for (int i = 0; i < plugins.length; i++)
		{
			config.addDefault("Manager." + plugins[i], true);
		}

		config.options().copyDefaults(true);
		main.saveConfig();
	}

	public void enableAll()
	{
		try
		{
			FileConfiguration config = main.getConfig();

			for (int i = 0; i < plugins.length; i++)
			{
				if (config.getBoolean("Manager." + plugins[i]))
				{
					switch (i)
					{
					case 0: // dasGesetz:
						main.getCommand(commands[0]).setExecutor(commandExe[0]);
						main.getCommand(commands[0]).setTabCompleter(commandExe[0]);
						break;

					case 1: // weatherClear:
						main.getCommand(commands[1]).setExecutor(commandExe[1]);
						main.getCommand(commands[0]).setTabCompleter(commandExe[1]);
						break;

					case 2: // coords:
						main.getCommand(commands[2]).setExecutor(commandExe[2]);
						main.getCommand(commands[0]).setTabCompleter(commandExe[2]);
						break;

					case 3: // blockLogger:
						main.getCommand(commands[3]).setExecutor(commandExe[3]);
						main.getServer().getPluginManager().registerEvents(commandListener[0], main);
						break;

					case 4: // Timber:
						main.getServer().getPluginManager().registerEvents(commandListener[1], main);
						break;

					case 5: // DeathChest:
						main.getServer().getPluginManager().registerEvents(commandListener[2], main);
						break;

					case 6: // BlockLock:
						main.getCommand(commands[3]).setExecutor(commandExe[3]);
						main.getCommand(commands[3]).setTabCompleter(commandExe[3]);
						main.getServer().getPluginManager().registerEvents(commandListener[3], main);
						break;

					case 7: // Messages:
						main.getServer().getPluginManager().registerEvents(commandListener[4], main);
						break;

					case 8: // Home:
						main.getCommand(commands[4]).setExecutor(commandExe[4]);
						main.getCommand(commands[4]).setTabCompleter(commandExe[4]);
						break;

					case 9: // EasyFarming:
						main.getServer().getPluginManager().registerEvents(commandListener[5], main);
						break;

					case 10: // Ping:
						main.getServer().getPluginManager().registerEvents(commandListener[6], main);
						break;
					}
				}
			}

			((BlockLockManager) commandListener[3]).loadFromFile();
			((HomeCommands) commandExe[4]).loadFromFile();

			Bukkit.getConsoleSender().sendMessage("DGMANAGER: ENABLED PLUGINS");
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("DGMANAGER ERROR: " + e.getLocalizedMessage());
		}
	}

	public void disableAll()
	{
		try
		{
			for (int i = 0; i < commandExe.length; i++)
			{
				main.getCommand(commands[i]).setExecutor(null);
				main.getCommand(commands[i]).setTabCompleter(null);
			}
			for (int i = 0; i < commandListener.length; i++)
			{
				HandlerList.unregisterAll(commandListener[i]);
			}

			((blockLogger) commandListener[0]).deleteLogs();
			((DeathChestManager) commandListener[2]).removeAllDeathChests();
			((BlockLockManager) commandListener[3]).saveToFile();
			((HomeCommands) commandExe[4]).saveToFile();
			Bukkit.getConsoleSender().sendMessage("DGMANAGER: DISABLED PLUGINS");
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("DGMANAGER ERROR: " + e.getLocalizedMessage());
		}
	}

	public File getDataFolder()
	{
		return main.getDataFolder();
	}

	public Main getMain()
	{
		return main;
	}

}
