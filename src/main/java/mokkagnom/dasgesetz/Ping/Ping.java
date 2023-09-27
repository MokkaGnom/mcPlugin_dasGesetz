package mokkagnom.dasgesetz.Ping;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;

public class Ping
{
    Ping(PingManager pm, Block block, String playername, String colorHex)
    {
        int[] colors = new int[3];
        if (colorHex != null && colorHex.length() == 6)
        {
            colors[0] = PingCommands.getValueOfHex(colorHex.charAt(0), colorHex.charAt(1));
            colors[1] = PingCommands.getValueOfHex(colorHex.charAt(2), colorHex.charAt(3));
            colors[2] = PingCommands.getValueOfHex(colorHex.charAt(4), colorHex.charAt(5));
        }
        else
        {
            colors[0] = 255;
            colors[1] = 183;
            colors[2] = 197;
        }

        Block b = getAir(block);
        AreaEffectCloud aec = (AreaEffectCloud) b.getWorld().spawnEntity(b.getLocation(), EntityType.AREA_EFFECT_CLOUD);
        aec.setColor(Color.fromRGB(colors[0], colors[1], colors[2]));
        aec.setDuration(pm.getTime() / 50);
        aec.setCustomName(playername + "'s Ping");
        aec.setCustomNameVisible(true);
        aec.setGlowing(true);
        aec.setGravity(false);
        aec.setRadius(1);
        aec.setSilent(true);
    }

    public Block getAir(Block b)
    {
        if (b.getRelative(0, 1, 0).getType().equals(Material.AIR))
            return b.getRelative(0, 1, 0);
        else if (b.getRelative(0, -1, 0).getType().equals(Material.AIR))
            return b.getRelative(0, -1, 0);
        else if (b.getRelative(1, 0, 0).getType().equals(Material.AIR))
            return b.getRelative(1, 0, 0);
        else if (b.getRelative(-1, 0, 0).getType().equals(Material.AIR))
            return b.getRelative(-1, 0, 0);
        else if (b.getRelative(0, 0, 1).getType().equals(Material.AIR))
            return b.getRelative(0, 0, 1);
        else if (b.getRelative(0, 0, -1).getType().equals(Material.AIR))
            return b.getRelative(0, 0, -1);

        return b;
    }
}
