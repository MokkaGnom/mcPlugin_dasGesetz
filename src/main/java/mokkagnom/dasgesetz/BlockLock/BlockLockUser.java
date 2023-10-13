package mokkagnom.dasgesetz.BlockLock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;

public class BlockLockUser implements Serializable
{
    private static final long serialVersionUID = 1335167459352378977L;
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

    public void createAllBlockLockManagerMenus(BlockLockManager blm)
    {
        for (BlockLock i : blockLocks)
        {
            i.createManagerMenu(blm);
        }
    }

    public BlockLock createBlockLock(Block b, BlockLockManager blm)
    {
        BlockLock bl = new BlockLock(blm, b, this);
        blockLocks.add(bl);
        bl.createManagerMenu(blm);

        /*
         * int doubleChestIndex = bl.checkIfDoubleChest(); BlockLockManager.sendMessage(uuid, "doubleChestIndex: " + doubleChestIndex); if (doubleChestIndex > 1) { try { Block block = null;
         * if (doubleChestIndex == 2) { if (BlockLock.checkIfDoubleChest(b.getRelative(1, 0, 0)) == 3) block = b.getRelative(1, 0, 0); else if (BlockLock.checkIfDoubleChest(b.getRelative(-1,
         * 0, 0)) == 3) block = b.getRelative(-1, 0, 0); else if (BlockLock.checkIfDoubleChest(b.getRelative(0, 0, 1)) == 3) block = b.getRelative(0, 0, 1); else if
         * (BlockLock.checkIfDoubleChest(b.getRelative(0, 0, -1)) == 3) block = b.getRelative(0, 0, -1); } else if (doubleChestIndex == 3) { if (BlockLock.checkIfDoubleChest(b.getRelative(1,
         * 0, 0)) == 2) block = b.getRelative(1, 0, 0); else if (BlockLock.checkIfDoubleChest(b.getRelative(-1, 0, 0)) == 2) block = b.getRelative(-1, 0, 0); else if
         * (BlockLock.checkIfDoubleChest(b.getRelative(0, 0, 1)) == 2) block = b.getRelative(0, 0, 1); else if (BlockLock.checkIfDoubleChest(b.getRelative(0, 0, -1)) == 2) block =
         * b.getRelative(0, 0, -1); }
         * 
         * if (block != null) { BlockLockManager.sendMessage(uuid, "Double Chest: " + block.getLocation().toString()); BlockLock bl2 = new BlockLock(block, this); blockLocks.add(bl2);
         * bl2.setBlockLockManagerMenu(bl.getBlockLockManagerMenu()); bl.setSecondBlockLock(bl2); bl2.setSecondBlockLock(bl); } } catch (Exception e) {
         * Bukkit.getLogger().severe("createBlockLock: Double Chest Exception: " + e.getLocalizedMessage()); BlockLockManager.sendMessage(uuid, "createBlockLock: Double Chest Exception: " +
         * e.getLocalizedMessage(), true); } }
         */

        if (bl.checkIfDoor())
        {
            try
            {
                if (bl.getBlock().getRelative(0, 1, 0).getBlockData() instanceof Door)
                {
                    BlockLock bl2 = new BlockLock(blm, b.getRelative(0, 1, 0), this);
                    blockLocks.add(bl2);
                    bl2.setBlockLockManagerMenu(bl.getBlockLockManagerMenu());
                    bl.setSecondBlockLock(bl2);
                    bl2.setSecondBlockLock(bl);
                }
                else if (bl.getBlock().getRelative(0, -1, 0).getBlockData() instanceof Door)
                {
                    BlockLock bl2 = new BlockLock(blm, b.getRelative(0, -1, 0), this);
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

    public boolean removeBlockLock(BlockLock bl, BlockLockManager blm)
    {
        bl.getBlock().removeMetadata(BlockLockManager.blockLockKey, blm.getManager().getMain());
        BlockLock bl2 = bl.getSecondBlockLock();
        if (bl2 != null)
            bl2.getBlock().removeMetadata(BlockLockManager.blockLockKey, blm.getManager().getMain());

        return blockLocks.remove(bl) && blockLocks.remove(bl2);
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

    public BlockLock getBlockLock(Block b)
    {
        for (BlockLock i : blockLocks)
        {
            if (i.getBlock().equals(b))
                return i;
        }
        return null;
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
