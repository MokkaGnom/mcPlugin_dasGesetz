package mokkagnom.dasgesetz.Farming;

// Bukkit:
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
//Java:
import java.util.Arrays;
import java.util.List;

public class EasyFarming implements Listener
{
	private static final List<Material> allowedBlocks = Arrays.asList(Material.BEETROOTS, Material.WHEAT, Material.CARROTS, Material.POTATOES);

	public EasyFarming()
	{
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			Block b = event.getClickedBlock();
			int index = allowedBlocks.indexOf(b.getType());
			if (index > -1 && event.getPlayer().hasPermission("dg.easyFarmingPermission"))
			{
				Ageable ageable = (Ageable) b.getBlockData();
				if (ageable.getAge() == ageable.getMaximumAge())
				{
					if (b.breakNaturally())
					{
						b.setType(allowedBlocks.get(index), true);
					}
				}
			}
		}
	}
}
