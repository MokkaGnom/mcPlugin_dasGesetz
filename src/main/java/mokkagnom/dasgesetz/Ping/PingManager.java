package mokkagnom.dasgesetz.Ping;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import mokkagnom.dasgesetz.Main;

public class PingManager implements Listener
{
    private final String cooldownMetaKey = "pingTime";
    private final String colorMetaKey = "pingColor";
    private Main main;
    private int time;
    private int cooldown;

    public PingManager(Main main, int time, int cooldown)
    {
        this.main = main;
        this.time = time;
        this.cooldown = cooldown;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        if (p.hasPermission("dg.pingPermission") && checkCooldown(p) && p.getInventory().getItemInOffHand().getType().equals(Material.STICK)
                && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
        {
            Block b = p.getTargetBlock(null, 255);
            String color = null;
            if (p.hasMetadata(colorMetaKey))
                color = p.getMetadata(colorMetaKey).get(0).asString();
            new Ping(this, b, p.getName(), color);
            p.removeMetadata(cooldownMetaKey, main);
            p.setMetadata(cooldownMetaKey, new FixedMetadataValue(main, System.currentTimeMillis()));
            p.sendMessage("You pinged at " + b.getX() + ", " + b.getY() + ", " + b.getZ());
        }
    }

    public void setPlayerColor(Player p, String color)
    {
        if (p.hasMetadata(colorMetaKey))
            p.removeMetadata(colorMetaKey, main);

        p.setMetadata(colorMetaKey, new FixedMetadataValue(main, color));
    }

    public boolean checkCooldown(Player p)
    {
        if (p.hasMetadata(cooldownMetaKey))
        {
            return p.getMetadata(cooldownMetaKey).get(0).asLong() + cooldown <= System.currentTimeMillis();
        }
        else
        {
            p.setMetadata(cooldownMetaKey, new FixedMetadataValue(main, System.currentTimeMillis()));
            return true;
        }
    }

    public int getTime()
    {
        return time;
    }

    public int getCooldown()
    {
        return cooldown;
    }

    public Main getMain()
    {
        return main;
    }
}
