package mokkagnom.dasgesetz.BlockLock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.block.Block;

public class BlockLockUser implements Serializable
{
    private UUID uuid;
    private List<BlockLock> blockLocks;
    private List<UUID> friends;
    private boolean useSneakMenu;

    public BlockLockUser(UUID uuid)
    {
        this.uuid = uuid;
        this.blockLocks = new ArrayList<BlockLock>();
        this.friends = new ArrayList<UUID>();
        useSneakMenu = true;
    }

    public BlockLock createBlockLock(Block b)
    {
        BlockLock bl = new BlockLock(b, this);
        blockLocks.add(bl);
        return bl;
    }

    public boolean removeBlockLock(BlockLock bl)
    {
        return blockLocks.remove(bl);
    }

    public boolean addFriend(UUID friend)
    {
        if (friends.contains(friend))
            return false;
        else
        {
            friends.add(friend);
            return true;
        }
    }

    public boolean addFriend(UUID friend, Block block)
    {
        for (BlockLock i : blockLocks)
        {
            if (i.getBlock().equals(block))
            {
                i.addFriend(friend);
                return true;
            }
        }
        return false;
    }

    public boolean removeFriend(UUID friend)
    {
        if (friends.contains(friend))
        {
            friends.remove(friend);
            return true;
        }
        else
            return false;
    }

    public boolean removeFriend(UUID friend, Block block)
    {
        for (BlockLock i : blockLocks)
        {
            if (i.getBlock().equals(block))
            {
                i.removeFriend(friend);
                return true;
            }
        }
        return false;
    }

    public void setUseSneakMenu(boolean usm)
    {
        useSneakMenu = usm;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public List<BlockLock> getBlockLocks()
    {
        return blockLocks;
    }

    public List<UUID> getFriends()
    {
        return friends;
    }

    public boolean getUseSneakMenu()
    {
        return useSneakMenu;
    }
}
