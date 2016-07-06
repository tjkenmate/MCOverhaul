/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class AnvilRepairEvent extends PlayerEvent
{

    private final ItemStack left; // The left side of the input
    private final ItemStack right; // The right side of the input
    private final ItemStack output; // Set this to set the output stack
    private float breakChance; // Anvil's chance to break (reduced by 1 durability) when this is complete. Default is 12% (0.12f)

    public AnvilRepairEvent(EntityPlayer player, ItemStack output, ItemStack left, ItemStack right)
    {
        super(player);
        this.output = output;
        this.left = left;
        this.right = right;
        this.setBreakChance(0.12f);
    }

    /**
     * Fired when the player removes a "repaired" item from the Anvil's Output slot.
     *
     * breakChance specifies as a percentage the chance that the anvil will be "damaged" when used.
     *
     * ItemStacks are the inputs/output from the anvil. They cannot be edited.
     */
    public ItemStack getLeft() { return left; }
    public ItemStack getRight() { return right; }
    public ItemStack getOutput() { return output; }
    public float getBreakChance() { return breakChance; }
    public void setBreakChance(float breakChance) { this.breakChance = breakChance; }
}