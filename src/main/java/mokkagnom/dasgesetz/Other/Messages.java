package mokkagnom.dasgesetz.Other;

// Bukkit:
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldSaveEvent;

import mokkagnom.dasgesetz.Manager;

public class Messages implements Listener
{
	private Manager manager;
	private String message;

	public Messages(Manager m)
	{
		this.manager = m;
		message = manager.getConfig().getString("Messages.Message");
	}

	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent event)
	{
		event.getPlayer().sendMessage(message);
	}

	@EventHandler
	public void onWorldSave(WorldSaveEvent event)
	{
		if (event.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName()))
			message = manager.getConfig().getString("Messages.Message");
	}
}
