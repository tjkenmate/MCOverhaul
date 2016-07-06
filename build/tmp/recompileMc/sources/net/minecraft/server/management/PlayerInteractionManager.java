package net.minecraft.server.management;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class PlayerInteractionManager
{
    /** Forge reach distance */
    private double blockReachDistance = 5.0d;
    /** The world object that this object is connected to. */
    public World theWorld;
    /** The EntityPlayerMP object that this object is connected to. */
    public EntityPlayerMP thisPlayerMP;
    private GameType gameType = GameType.NOT_SET;
    /** True if the player is destroying a block */
    private boolean isDestroyingBlock;
    private int initialDamage;
    private BlockPos destroyPos = BlockPos.ORIGIN;
    private int curblockDamage;
    /**
     * Set to true when the "finished destroying block" packet is received but the block wasn't fully damaged yet. The
     * block will not be destroyed while this is false.
     */
    private boolean receivedFinishDiggingPacket;
    private BlockPos delayedDestroyPos = BlockPos.ORIGIN;
    private int initialBlockDamage;
    private int durabilityRemainingOnBlock = -1;

    public PlayerInteractionManager(World worldIn)
    {
        this.theWorld = worldIn;
    }

    public void setGameType(GameType type)
    {
        this.gameType = type;
        type.configurePlayerCapabilities(this.thisPlayerMP.capabilities);
        this.thisPlayerMP.sendPlayerAbilities();
        this.thisPlayerMP.mcServer.getPlayerList().sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.UPDATE_GAME_MODE, new EntityPlayerMP[] {this.thisPlayerMP}));
        this.theWorld.updateAllPlayersSleepingFlag();
    }

    public GameType getGameType()
    {
        return this.gameType;
    }

    public boolean survivalOrAdventure()
    {
        return this.gameType.isSurvivalOrAdventure();
    }

    /**
     * Get if we are in creative game mode.
     */
    public boolean isCreative()
    {
        return this.gameType.isCreative();
    }

    /**
     * if the gameType is currently NOT_SET then change it to par1
     */
    public void initializeGameType(GameType type)
    {
        if (this.gameType == GameType.NOT_SET)
        {
            this.gameType = type;
        }

        this.setGameType(this.gameType);
    }

    public void updateBlockRemoving()
    {
        ++this.curblockDamage;

        if (this.receivedFinishDiggingPacket)
        {
            int i = this.curblockDamage - this.initialBlockDamage;
            IBlockState iblockstate = this.theWorld.getBlockState(this.delayedDestroyPos);
            Block block = iblockstate.getBlock();

            if (block.isAir(iblockstate, theWorld, delayedDestroyPos))
            {
                this.receivedFinishDiggingPacket = false;
            }
            else
            {
                float f = iblockstate.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.delayedDestroyPos) * (float)(i + 1);
                int j = (int)(f * 10.0F);

                if (j != this.durabilityRemainingOnBlock)
                {
                    this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.delayedDestroyPos, j);
                    this.durabilityRemainingOnBlock = j;
                }

                if (f >= 1.0F)
                {
                    this.receivedFinishDiggingPacket = false;
                    this.tryHarvestBlock(this.delayedDestroyPos);
                }
            }
        }
        else if (this.isDestroyingBlock)
        {
            IBlockState iblockstate1 = this.theWorld.getBlockState(this.destroyPos);
            Block block1 = iblockstate1.getBlock();

            if (block1.isAir(iblockstate1, theWorld, destroyPos))
            {
                this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.destroyPos, -1);
                this.durabilityRemainingOnBlock = -1;
                this.isDestroyingBlock = false;
            }
            else
            {
                int k = this.curblockDamage - this.initialDamage;
                float f1 = iblockstate1.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.destroyPos) * (float)(k + 1); // Forge: Fix network break progress using wrong position
                int l = (int)(f1 * 10.0F);

                if (l != this.durabilityRemainingOnBlock)
                {
                    this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.destroyPos, l);
                    this.durabilityRemainingOnBlock = l;
                }
            }
        }
    }

    /**
     * If not creative, it calls sendBlockBreakProgress until the block is broken first. tryHarvestBlock can also be the
     * result of this call.
     */
    public void onBlockClicked(BlockPos pos, EnumFacing side)
    {
        net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event = net.minecraftforge.common.ForgeHooks.onLeftClickBlock(thisPlayerMP, pos, side, net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(thisPlayerMP, getBlockReachDistance() + 1));
        if (event.isCanceled())
        {
            // Restore block and te data
            thisPlayerMP.connection.sendPacket(new SPacketBlockChange(theWorld, pos));
            theWorld.notifyBlockUpdate(pos, theWorld.getBlockState(pos), theWorld.getBlockState(pos), 3);
            return;
        }

        if (this.isCreative())
        {
            if (!this.theWorld.extinguishFire((EntityPlayer)null, pos, side))
            {
                this.tryHarvestBlock(pos);
            }
        }
        else
        {
            IBlockState iblockstate = this.theWorld.getBlockState(pos);
            Block block = iblockstate.getBlock();

            if (this.gameType.isAdventure())
            {
                if (this.gameType == GameType.SPECTATOR)
                {
                    return;
                }

                if (!this.thisPlayerMP.isAllowEdit())
                {
                    ItemStack itemstack = this.thisPlayerMP.getHeldItemMainhand();

                    if (itemstack == null)
                    {
                        return;
                    }

                    if (!itemstack.canDestroy(block))
                    {
                        return;
                    }
                }
            }

            this.initialDamage = this.curblockDamage;
            float f = 1.0F;

            if (!iblockstate.getBlock().isAir(iblockstate, theWorld, pos))
            {
                if (event.getUseBlock() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
                {
                    block.onBlockClicked(this.theWorld, pos, this.thisPlayerMP);
                    this.theWorld.extinguishFire((EntityPlayer)null, pos, side);
                }
                else
                {
                    // Restore block and te data
                    thisPlayerMP.connection.sendPacket(new SPacketBlockChange(theWorld, pos));
                    theWorld.notifyBlockUpdate(pos, theWorld.getBlockState(pos), theWorld.getBlockState(pos), 3);
                }
                f = iblockstate.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, pos);
            }
            if (event.getUseItem() == net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
            {
                if (f >= 1.0F)
                {
                    // Restore block and te data
                    thisPlayerMP.connection.sendPacket(new SPacketBlockChange(theWorld, pos));
                    theWorld.notifyBlockUpdate(pos, theWorld.getBlockState(pos), theWorld.getBlockState(pos), 3);
                }
                return;
            }

            if (!iblockstate.getBlock().isAir(iblockstate, theWorld, pos) && f >= 1.0F)
            {
                this.tryHarvestBlock(pos);
            }
            else
            {
                this.isDestroyingBlock = true;
                this.destroyPos = pos;
                int i = (int)(f * 10.0F);
                this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), pos, i);
                this.durabilityRemainingOnBlock = i;
            }
        }
    }

    public void blockRemoving(BlockPos pos)
    {
        if (pos.equals(this.destroyPos))
        {
            int i = this.curblockDamage - this.initialDamage;
            IBlockState iblockstate = this.theWorld.getBlockState(pos);

            if (!iblockstate.getBlock().isAir(iblockstate, theWorld, pos))
            {
                float f = iblockstate.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, pos) * (float)(i + 1);

                if (f >= 0.7F)
                {
                    this.isDestroyingBlock = false;
                    this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), pos, -1);
                    this.tryHarvestBlock(pos);
                }
                else if (!this.receivedFinishDiggingPacket)
                {
                    this.isDestroyingBlock = false;
                    this.receivedFinishDiggingPacket = true;
                    this.delayedDestroyPos = pos;
                    this.initialBlockDamage = this.initialDamage;
                }
            }
        }
    }

    /**
     * Stops the block breaking process
     */
    public void cancelDestroyingBlock()
    {
        this.isDestroyingBlock = false;
        this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.destroyPos, -1);
    }

    /**
     * Removes a block and triggers the appropriate events
     */
    private boolean removeBlock(BlockPos pos)
    {
        return removeBlock(pos, false);
    }

    private boolean removeBlock(BlockPos pos, boolean canHarvest)
    {
        IBlockState iblockstate = this.theWorld.getBlockState(pos);
        boolean flag = iblockstate.getBlock().removedByPlayer(iblockstate, theWorld, pos, thisPlayerMP, canHarvest);

        if (flag)
        {
            iblockstate.getBlock().onBlockDestroyedByPlayer(this.theWorld, pos, iblockstate);
        }

        return flag;
    }

    /**
     * Attempts to harvest a block
     */
    public boolean tryHarvestBlock(BlockPos pos)
    {
        int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(theWorld, gameType, thisPlayerMP, pos);
        if (exp == -1)
        {
            return false;
        }
        else
        {
            IBlockState iblockstate = this.theWorld.getBlockState(pos);
            TileEntity tileentity = this.theWorld.getTileEntity(pos);
            Block block = iblockstate.getBlock();

            if ((block instanceof BlockCommandBlock || block instanceof BlockStructure) && !this.thisPlayerMP.func_189808_dh())
            {
                this.theWorld.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
                return false;
            }
            else
            {
                ItemStack stack = thisPlayerMP.getHeldItemMainhand();
                if (stack != null && stack.getItem().onBlockStartBreak(stack, pos, thisPlayerMP)) return false;

                this.theWorld.playEvent(this.thisPlayerMP, 2001, pos, Block.getStateId(iblockstate));
                boolean flag1 = false;

                if (this.isCreative())
                {
                    flag1 = this.removeBlock(pos);
                    this.thisPlayerMP.connection.sendPacket(new SPacketBlockChange(this.theWorld, pos));
                }
                else
                {
                    ItemStack itemstack1 = this.thisPlayerMP.getHeldItemMainhand();
                    ItemStack itemstack2 = itemstack1 == null ? null : itemstack1.copy();
                    boolean flag = iblockstate.getBlock().canHarvestBlock(theWorld, pos, thisPlayerMP);

                    if (itemstack1 != null)
                    {
                        itemstack1.onBlockDestroyed(this.theWorld, iblockstate, pos, this.thisPlayerMP);

                        if (itemstack1.stackSize <= 0)
                        {
                            net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this.thisPlayerMP, itemstack1, EnumHand.MAIN_HAND);
                            this.thisPlayerMP.setHeldItem(EnumHand.MAIN_HAND, (ItemStack)null);
                        }
                    }

                    flag1 = this.removeBlock(pos, flag);
                    if (flag1 && flag)
                    {
                        iblockstate.getBlock().harvestBlock(this.theWorld, this.thisPlayerMP, pos, iblockstate, tileentity, itemstack2);
                    }
                }

                // Drop experience
                if (!this.isCreative() && flag1 && exp > 0)
                {
                    iblockstate.getBlock().dropXpOnBlockBreak(theWorld, pos, exp);
                }
                return flag1;
            }
        }
    }

    public EnumActionResult processRightClick(EntityPlayer player, World worldIn, ItemStack stack, EnumHand hand)
    {
        if (this.gameType == GameType.SPECTATOR)
        {
            return EnumActionResult.PASS;
        }
        else if (player.getCooldownTracker().hasCooldown(stack.getItem()))
        {
            return EnumActionResult.PASS;
        }
        else
        {
            if (net.minecraftforge.common.ForgeHooks.onItemRightClick(player, hand, stack)) return net.minecraft.util.EnumActionResult.PASS;
            int i = stack.stackSize;
            int j = stack.getMetadata();
            ActionResult<ItemStack> actionresult = stack.useItemRightClick(worldIn, player, hand);
            ItemStack itemstack = (ItemStack)actionresult.getResult();

            if (itemstack == stack && itemstack.stackSize == i && itemstack.getMaxItemUseDuration() <= 0 && itemstack.getMetadata() == j)
            {
                return actionresult.getType();
            }
            else
            {
                player.setHeldItem(hand, itemstack);

                if (this.isCreative())
                {
                    itemstack.stackSize = i;

                    if (itemstack.isItemStackDamageable())
                    {
                        itemstack.setItemDamage(j);
                    }
                }

                if (itemstack.stackSize == 0)
                {
                    player.setHeldItem(hand, (ItemStack)null);
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemstack, hand);
                }

                if (!player.isHandActive())
                {
                    ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                }

                return actionresult.getType();
            }
        }
    }

    public EnumActionResult processRightClickBlock(EntityPlayer player, World worldIn, @Nullable ItemStack stack, EnumHand hand, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (this.gameType == GameType.SPECTATOR)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof ILockableContainer)
            {
                Block block1 = worldIn.getBlockState(pos).getBlock();
                ILockableContainer ilockablecontainer = (ILockableContainer)tileentity;

                if (ilockablecontainer instanceof TileEntityChest && block1 instanceof BlockChest)
                {
                    ilockablecontainer = ((BlockChest)block1).getLockableContainer(worldIn, pos);
                }

                if (ilockablecontainer != null)
                {
                    player.displayGUIChest(ilockablecontainer);
                    return EnumActionResult.SUCCESS;
                }
            }
            else if (tileentity instanceof IInventory)
            {
                player.displayGUIChest((IInventory)tileentity);
                return EnumActionResult.SUCCESS;
            }

            return EnumActionResult.PASS;
        }
        else
        {
            net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks
                    .onRightClickBlock(player, hand, stack, pos, facing, net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(thisPlayerMP, getBlockReachDistance() + 1));
            if (event.isCanceled()) return EnumActionResult.PASS;

            net.minecraft.item.Item item = stack == null ? null : stack.getItem();
            EnumActionResult ret = item == null ? EnumActionResult.PASS : item.onItemUseFirst(stack, player, worldIn, pos, facing, hitX, hitY, hitZ, hand);
            if (ret != EnumActionResult.PASS) return ret;

            boolean bypass = true;
            for (ItemStack s : new ItemStack[]{player.getHeldItemMainhand(), player.getHeldItemOffhand()}) //TODO: Expand to more hands? player.inv.getHands()?
                bypass = bypass && (s == null || s.getItem().doesSneakBypassUse(s, worldIn, pos, player));
            EnumActionResult result = EnumActionResult.PASS;

            if (!player.isSneaking() || bypass || event.getUseBlock() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW)
            {
                IBlockState iblockstate = worldIn.getBlockState(pos);
                if(event.getUseBlock() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
                if (iblockstate.getBlock().onBlockActivated(worldIn, pos, iblockstate, player, hand, stack, facing, hitX, hitY, hitZ))
                {
                    result = EnumActionResult.SUCCESS;
                }
            }

            if (stack == null)
            {
                return EnumActionResult.PASS;
            }
            else if (player.getCooldownTracker().hasCooldown(stack.getItem()))
            {
                return EnumActionResult.PASS;
            }
            else
            {
                if (stack.getItem() instanceof ItemBlock && !player.func_189808_dh())
                {
                    Block block = ((ItemBlock)stack.getItem()).getBlock();

                    if (block instanceof BlockCommandBlock || block instanceof BlockStructure)
                    {
                        return EnumActionResult.FAIL;
                    }
                }

                if (this.isCreative())
                {
                    int j = stack.getMetadata();
                    int i = stack.stackSize;
                    if (result != EnumActionResult.SUCCESS && event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY
                            || result == EnumActionResult.SUCCESS && event.getUseItem() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW) {
                    EnumActionResult enumactionresult = stack.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                    stack.setItemDamage(j);
                    stack.stackSize = i;
                    return enumactionresult;
                    } else return result;
                }
                else
                {
                    if (result != EnumActionResult.SUCCESS && event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY
                            || result == EnumActionResult.SUCCESS && event.getUseItem() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW)
                    return stack.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
                    else return result;
                }
            }
        }
    }

    /**
     * Sets the world instance.
     */
    public void setWorld(WorldServer serverWorld)
    {
        this.theWorld = serverWorld;
    }

    public double getBlockReachDistance()
    {
        return blockReachDistance;
    }
    public void setBlockReachDistance(double distance)
    {
        blockReachDistance = distance;
    }
}