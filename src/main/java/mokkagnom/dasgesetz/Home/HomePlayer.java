package mokkagnom.dasgesetz.Home;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

public class HomePlayer implements Serializable
{
	private static final long serialVersionUID = 4568652202019256640L;
	private List<Home> homes;
	private UUID owner;
	public static int maxHomes = 10;

	public HomePlayer(UUID owner)
	{
		this.owner = owner;
		this.homes = new ArrayList<Home>();
	}

	public boolean teleport(String homeName)
	{
		for (Home i : homes)
		{
			if (i.getName().equalsIgnoreCase(homeName))
			{
				return i.teleport(Bukkit.getPlayer(owner));
			}
		}
		return false;
	}

	public boolean addHome(String name)
	{
		if (homes.size() < maxHomes)
		{
			for (Home i : homes)
			{
				if (i.getName().equalsIgnoreCase(name))
					return false;
			}
			homes.add(new Home(Bukkit.getPlayer(owner), name));
			return true;
		}
		return false;
	}

	public boolean removeHome(String name)
	{
		for (Home i : homes)
		{
			if (i.getName().equalsIgnoreCase(name))
			{
				homes.remove(i);
				return true;
			}
		}
		return false;
	}

	public String[] getAllHomes()
	{
		String[] homesStr = new String[homes.size()];

		for (int i = 0; i < homes.size(); i++)
		{
			homesStr[i] = homes.get(i).toString();
		}

		return homesStr;
	}
	
	public List<String> getAllHomeNames()
	{
		List<String> names = new ArrayList<String>();
		
		for(Home i : homes)
		{
			names.add(i.getName());
		}
		
		return names;
	}
	
	public UUID getOwner()
	{
		return owner;
	}
	

}
