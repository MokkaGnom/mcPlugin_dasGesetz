package mokkagnom.dasgesetz.BlockLock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.block.Chest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.type.Door;

public class BlockLockUser implements Serializable
{
    private static final long serialVersionUID = 8996225305309407338L;
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

    public BlockLock createBlockLock(Block b, BlockLockManager blm)
    {
        BlockLock bl = new BlockLock(b, this);
        blockLocks.add(bl);
        bl.createManagerMenu(blm);

        int doubleChest = bl.checkIfDoubleChest();
        if (doubleChest > 0)
        {
            try
            {
                /* if (doubleChest == 2)
                {
                    BlockLock bl2 = new BlockLock(b.getRelative(BlockFace.EAST), this);
                    blockLocks.add(bl2);
                    bl2.setBlockLockManagerMenu(bl.getBlockLockManagerMenu());
                    bl.setSecondBlockLock(bl2);
                    bl2.setSecondBlockLock(bl);
                }
                else if (doubleChest == 3)
                {
                    BlockLock bl2 = new BlockLock(b.getRelative(BlockFace.WEST), this);
                    blockLocks.add(bl2);
                    bl2.setBlockLockManagerMenu(bl.getBlockLockManagerMenu());
                    bl.setSecondBlockLock(bl2);
                    bl2.setSecondBlockLock(bl);
                } */
            }
            catch (Exception e)
            {
                Bukkit.getLogger().severe("createBlockLock: Double Chest Exception: " + e.getLocalizedMessage());
                BlockLockManager.sendMessage(uuid, "createBlockLock: Double Chest Exception: " + e.getLocalizedMessage(), true);
            }
        }
        else if (bl.checkIfDoor())
        {
            try
            {
                if (bl.getBlock().getRelative(0, 1, 0).getBlockData() instanceof Door)
                {
                    BlockLockManager.sendMessage(uuid, "Door");
                    BlockLock bl2 = new BlockLock(b.getRelative(0, 1, 0), this);
                    blockLocks.add(bl2);
                    bl2.setBlockLockManagerMenu(bl.getBlockLockManagerMenu());
                    bl.setSecondBlockLock(bl2);
                    bl2.setSecondBlockLock(bl);
                }
                else if (bl.getBlock().getRelative(0, -1, 0).getBlockData() instanceof Door)
                {
                    BlockLockManager.sendMessage(uuid, "Door");
                    BlockLock bl2 = new BlockLock(b.getRelative(0, -1, 0), this);
                    blockLocks.add(bl2);
                    bl2.setBlockLockManagerMenu(bl.getBlockLockManagerMenu());
                    bl.setSecondBlockLock(bl2);
                    bl2.setSecondBlockLock(bl);
                }
            }
            catch (Exception e)
            {
                Bukkit.getLogger().severe("createBlockLock: Door Exception: " + e.getLocalizedMessage());
                BlockLockManager.sendMessage(uuid, "createBlockLock: Door Exception: " + e.getLocalizedMessage(), true);
            }
        }

        return bl;
    }

    public boolean removeBlockLock(BlockLock bl)
    {
        return blockLocks.remove(bl) && blockLocks.remove(bl.getSecondBlockLock());
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
