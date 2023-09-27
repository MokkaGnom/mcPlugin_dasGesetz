package mokkagnom.dasgesetz.Other;

// Bukkit:
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
// Java:
import java.util.Calendar;

public class Messages implements Listener
{
	private String message;

	public Messages(String message)
	{
		this.message = message;
	}

	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent event)
	{
		// greetPlayer(event.getPlayer());
		event.getPlayer().sendMessage(message);
	}

	public void greetPlayer(Player p)
	{
		String message = "";
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

		if (hour < 12) // Morgen
		{
			message = "Guten Morgen";
		}
		else if (hour >= 12 && hour < 15) // Mittag
		{
			message = "Guten Mittag";
		}
		else if (hour >= 15 && hour < 18) // Nachmittag
		{
			message = "Guten Nachmittag";
		}
		else if (hour >= 18 && hour < 23) // Abend
		{
			message = "Guten Abend";
		}
		else
		{
			message = "Hallo";
		}

		p.sendMessage(message + " " + p.getName() + "!");
	}

}
