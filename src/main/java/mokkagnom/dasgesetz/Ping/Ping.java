package mokkagnom.dasgesetz.Ping;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;

public class Ping
{
    Ping(PingManager pm, Block block, String playername)
    {
        Block b = getAir(block);
        AreaEffectCloud aec = (AreaEffectCloud) b.getWorld().spawnEntity(b.getLocation(), EntityType.AREA_EFFECT_CLOUD);
        aec.setColor(Color.fromRGB(255, 183, 197));
        aec.setDuration(pm.getTime() / 50);
        aec.setCustomName(playername + "'s Ping");
        aec.setCustomNameVisible(true);
        aec.setGlowing(true);
        aec.setGravity(false);
        aec.setRadius(1);
        aec.setSilent(true);
        // aec.setRadiusPerTick(0);
    }

    public Block getAir(Block b)
    {
        if (b.getRelative(1, 0, 0).getType().equals(Material.AIR))
            return b.getRelative(1, 0, 0);
        else if (b.getRelative(-1, 0, 0).getType().equals(Material.AIR))
            return b.getRelative(-1, 0, 0);
        else if (b.getRelative(0, 1, 0).getType().equals(Material.AIR))
            return b.getRelative(0, 1, 0);
        else if (b.getRelative(0, -1, 0).getType().equals(Material.AIR))
            return b.getRelative(0, -1, 0);
        else if (b.getRelative(0, 0, 1).getType().equals(Material.AIR))
            return b.getRelative(0, 0, 1);
        else if (b.getRelative(0, 0, -1).getType().equals(Material.AIR))
            return b.getRelative(0, 0, -1);

        return b;
    }
}
