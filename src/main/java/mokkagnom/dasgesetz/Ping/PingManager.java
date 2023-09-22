package mokkagnom.dasgesetz.Ping;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import mokkagnom.dasgesetz.Main;

public class PingManager implements Listener
{
    private Main main;
    private long lastPingTime;
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
        if (checkCooldown() && event.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.STICK)
                && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
        {
            new Ping(this, event.getPlayer().getTargetBlock(null, 255), event.getPlayer().getName());
            lastPingTime = System.currentTimeMillis();
        }
    }

    public boolean checkCooldown()
    {
        return lastPingTime + cooldown <= System.currentTimeMillis();
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