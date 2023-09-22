package mokkagnom.dasgesetz.Home;

// Bukkit:
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
// Java:
import java.io.Serializable;

public class Home implements Serializable
{
	private static final long serialVersionUID = -6297703952228213492L;
	private String name;
	private double blockPosition[];
	private String worldName;

	public Home(Player p, String name)
	{
		this.name = name;
		this.worldName = p.getWorld().getName();
		this.blockPosition = new double[3];
		this.blockPosition[0] = ((int) p.getLocation().getX()) + 0.5;
		this.blockPosition[1] = (int) p.getLocation().getY();
		this.blockPosition[2] = ((int) p.getLocation().getZ()) + 0.5;
	}

	public boolean teleport(Player p)
	{
		if ((Bukkit.getWorld(worldName).getBlockAt((int) blockPosition[0], (int) blockPosition[1], (int) blockPosition[2]).getType().equals(Material.AIR))
				&& (Bukkit.getWorld(worldName).getBlockAt((int) blockPosition[0], (int) blockPosition[1] + 1, (int) blockPosition[2]).getType().equals(Material.AIR)))
		{
			return p.teleport(new Location(Bukkit.getWorld(worldName), blockPosition[0], blockPosition[1], blockPosition[2]));
		}
		return false;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return name + " : " + worldName + "(" + blockPosition[0] + ", " + blockPosition[1] + ", " + blockPosition[2] + ")";
	}
}
