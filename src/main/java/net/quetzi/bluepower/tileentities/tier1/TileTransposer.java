/*
 * This file is part of Blue Power.
 *
 *     Blue Power is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Blue Power is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Blue Power.  If not, see <http://www.gnu.org/licenses/>
 *     
 *     @author Quetzi
 */

package net.quetzi.bluepower.tileentities.tier1;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.quetzi.bluepower.helper.IOHelper;
import net.quetzi.bluepower.tileentities.TileMachineBase;

public class TileTransposer extends TileMachineBase {
    
    private boolean isPowered;
    
    @Override
    public void updateEntity() {
    
        super.updateEntity();
        
    }
    
    @Override
    protected void redstoneChanged(boolean newValue) {
    
        if (newValue) {
            suckItems();
            pullItem();
        }
        
    }
    
    private void pullItem() {
    
        if (isBufferEmpty()) {
            ForgeDirection dir = getOutputDirection().getOpposite();
            TileEntity inputTE = getTileCache()[dir.ordinal()].getTileEntity();
            ItemStack extractedStack = IOHelper.extractOneItem(inputTE, dir);
            if (extractedStack != null) addItemToOutputBuffer(extractedStack);
        }
    }
    
    private void suckItems() {
    
    }
}
