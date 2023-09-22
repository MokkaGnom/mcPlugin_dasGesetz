package mokkagnom.dasgesetz.Ping;

import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;


public class Ping
{
    Ping(PingManager pm, Block block, String playername)
    {
        AreaEffectCloud aec = (AreaEffectCloud) block.getWorld().spawnEntity(block.getLocation(), EntityType.AREA_EFFECT_CLOUD);
        aec.setColor(Color.fromRGB(255, 183, 197));
        aec.setDuration(pm.getTime());
        aec.setCustomName(playername);
        aec.setCustomNameVisible(true);
        aec.setGlowing(true);
        aec.setGravity(false);
        aec.setRadius(1);
        aec.setSilent(true);
        //aec.setRadiusPerTick(0);
    }
}
