package com.bluepowermod.tileentities.tier1;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.bluepowermod.BluePower;
import com.bluepowermod.init.BPBlocks;
import com.bluepowermod.tileentities.TileBase;

import cpw.mods.fml.relauncher.ReflectionHelper;

/**
 * @author MineMaarten
 */
public class TileProjectTable extends TileBase implements IInventory {
    
    public final IInventory craftResult  = new InventoryCraftResult();
    
    private ItemStack[]     inventory    = new ItemStack[18];
    private ItemStack[]     craftingGrid = new ItemStack[9];
    private static Field    stackListFieldInventoryCrafting;
    
    public InventoryCrafting getCraftingGrid(Container listener) {
    
        InventoryCrafting inventoryCrafting = new InventoryCrafting(listener, 3, 3);
        if (stackListFieldInventoryCrafting == null) {
            stackListFieldInventoryCrafting = ReflectionHelper.findField(InventoryCrafting.class, "field_70466_a", "stackList");
        }
        try {
            stackListFieldInventoryCrafting.set(inventoryCrafting, craftingGrid);// Inject the array, so when stacks are being set to null by the
                                                                                 // container it'll make it's way over to the actual stacks.
            return inventoryCrafting;
        } catch (Exception e) {
            BluePower.log.error("This is about to go wrong, Project Table getCraftingGrid:");
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<ItemStack> getDrops() {
    
        List<ItemStack> drops = super.getDrops();
        for (ItemStack stack : inventory)
            if (stack != null) drops.add(stack);
        for (ItemStack stack : craftingGrid)
            if (stack != null) drops.add(stack);
        return drops;
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tag) {
    
        super.writeToNBT(tag);
        
        NBTTagList tagList = new NBTTagList();
        for (int currentIndex = 0; currentIndex < inventory.length; ++currentIndex) {
            if (inventory[currentIndex] != null) {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", (byte) currentIndex);
                inventory[currentIndex].writeToNBT(tagCompound);
                tagList.appendTag(tagCompound);
            }
        }
        tag.setTag("Items", tagList);
        
        tagList = new NBTTagList();
        for (int currentIndex = 0; currentIndex < craftingGrid.length; ++currentIndex) {
            if (craftingGrid[currentIndex] != null) {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", (byte) currentIndex);
                craftingGrid[currentIndex].writeToNBT(tagCompound);
                tagList.appendTag(tagCompound);
            }
        }
        tag.setTag("CraftingGrid", tagList);
        
    }
    
    @Override
    public void readFromNBT(NBTTagCompound tag) {
    
        super.readFromNBT(tag);
        
        NBTTagList tagList = tag.getTagList("Items", 10);
        inventory = new ItemStack[18];
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
            byte slot = tagCompound.getByte("Slot");
            if (slot >= 0 && slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(tagCompound);
            }
        }
        
        tagList = tag.getTagList("CraftingGrid", 10);
        craftingGrid = new ItemStack[9];
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
            byte slot = tagCompound.getByte("Slot");
            if (slot >= 0 && slot < craftingGrid.length) {
                craftingGrid[slot] = ItemStack.loadItemStackFromNBT(tagCompound);
            }
        }
    }
    
    @Override
    public int getSizeInventory() {
    
        return inventory.length;
    }
    
    @Override
    public ItemStack getStackInSlot(int i) {
    
        return inventory[i];
    }
    
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
    
        ItemStack itemStack = getStackInSlot(slot);
        if (itemStack != null) {
            if (itemStack.stackSize <= amount) {
                setInventorySlotContents(slot, null);
            } else {
                itemStack = itemStack.splitStack(amount);
                if (itemStack.stackSize == 0) {
                    setInventorySlotContents(slot, null);
                }
            }
        }
        
        return itemStack;
    }
    
    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
    
        ItemStack itemStack = getStackInSlot(i);
        if (itemStack != null) {
            setInventorySlotContents(i, null);
        }
        return itemStack;
    }
    
    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {
    
        inventory[i] = itemStack;
    }
    
    @Override
    public String getInventoryName() {
    
        return BPBlocks.project_table.getUnlocalizedName();
    }
    
    @Override
    public boolean hasCustomInventoryName() {
    
        return false;
    }
    
    @Override
    public int getInventoryStackLimit() {
    
        return 64;
    }
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
    
        return true;
    }
    
    @Override
    public void openInventory() {
    
    }
    
    @Override
    public void closeInventory() {
    
    }
    
    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
    
        return true;
    }
    
}
