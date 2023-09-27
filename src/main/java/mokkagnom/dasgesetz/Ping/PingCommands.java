package mokkagnom.dasgesetz.Ping;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class PingCommands implements TabExecutor
{
    private PingManager pingManager;

    public PingCommands(PingManager pm)
    {
        pingManager = pm;
    }

    static int getValueOfHex(char high, char low)
    {
        int value = 0;
        char[] hex = new char[2];
        hex[0] = high;
        hex[1] = low;

        for (int i = 0; i < 2; i++, value *= 10)
        {
            if (hex[i] >= 48 && hex[i] <= 57)
            {
                value += hex[i] - 48;
            }
            else
            {
                if (hex[i] == 'A' || hex[i] == 'a')
                    value += 10;
                else if (hex[i] == 'B' || hex[i] == 'b')
                    value += 11;
                else if (hex[i] == 'C' || hex[i] == 'c')
                    value += 12;
                else if (hex[i] == 'D' || hex[i] == 'd')
                    value += 13;
                else if (hex[i] == 'E' || hex[i] == 'e')
                    value += 14;
                else if (hex[i] == 'F' || hex[i] == 'f')
                    value += 15;
                else
                    return -1;
            }

        }
        return value / 10;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length == 2 && args[0].equalsIgnoreCase("setColor") && args[1].length() == 6)
        {
            if (getValueOfHex(args[1].charAt(0), args[1].charAt(1)) != -1 && getValueOfHex(args[1].charAt(2), args[1].charAt(3)) != -1
                    && getValueOfHex(args[1].charAt(4), args[1].charAt(5)) != -1)
            {

                pingManager.setPlayerColor((Player) sender, args[1]);
                sender.sendMessage(ChatColor.of("#" + args[1]) + "Successfully changed color");
                return true;
            }
            return false;
        }
        else
        {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length == 1)
            return Arrays.asList("setColor");
        else if (args.length == 2)
            return Arrays.asList("000000", "FFB7C5", "FFFFFF");
        else
            return Arrays.asList("");
    }

}
