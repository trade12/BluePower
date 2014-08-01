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
 */

package com.bluepowermod.blocks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.bluepowermod.BluePower;
import com.bluepowermod.client.renderers.RendererBlockBase;
import com.bluepowermod.client.renderers.RendererBlockBase.EnumFaceType;
import com.bluepowermod.helper.IOHelper;
import com.bluepowermod.references.GuiIDs;
import com.bluepowermod.references.Refs;
import com.bluepowermod.tileentities.IBluePowered;
import com.bluepowermod.tileentities.IEjectAnimator;
import com.bluepowermod.tileentities.IRotatable;
import com.bluepowermod.tileentities.TileBase;
import com.bluepowermod.util.ForgeDirectionUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockContainerBase extends BlockBase implements ITileEntityProvider {
    
    @SideOnly(Side.CLIENT)
    private Map<String, IIcon> textures;
    
    public BlockContainerBase(Material material) {
    
        super(material);
        isBlockContainer = true;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
    
        try {
            return getTileEntity().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Method to be overwritten to fetch the TileEntity Class that goes with the block
     *
     * @return a .class
     */
    protected abstract Class<? extends TileEntity> getTileEntity();
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
    
        super.onNeighborBlockChange(world, x, y, z, block);
        // Only do this on the server side.
        if (!world.isRemote) {
            TileBase tileEntity = (TileBase) world.getTileEntity(x, y, z);
            if (tileEntity != null) {
                tileEntity.onBlockNeighbourChanged();
            }
        }
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
    
        if (player.isSneaking()) { return false; }
        
        TileEntity entity = world.getTileEntity(x, y, z);
        if (entity == null || !(entity instanceof TileBase)) { return false; }
        
        if (getGuiID() != GuiIDs.INVALID) {
            player.openGui(BluePower.instance, getGuiID().ordinal(), world, x, y, z);
            return true;
        }
        return false;
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
    
        TileBase tile = (TileBase) world.getTileEntity(x, y, z);
        for (ItemStack stack : tile.getDrops()) {
            IOHelper.spawnItemInWorld(world, stack, x + 0.5F, y + 0.5F, z + 0.5F);
        }
        super.breakBlock(world, x, y, z, block, meta);
        world.removeTileEntity(x, y, z);
    }
    
    /*  @Override
      public boolean onBlockEventReceived(World world, int x, int y, int z, int id, int data)
      {
          super.onBlockEventReceived(world, x, y, z, id, data);
          TileEntity tileentity = world.getTileEntity(x, y, z);
          return tileentity != null && tileentity.receiveClientEvent(id, data);
      }*/
    /**
     * Method to detect how the block was placed, and what way it's facing.
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack iStack) {
    
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IRotatable) {
            ((IRotatable) te).setFacingDirection(ForgeDirectionUtils.getDirectionFacing(player, canRotateVertical()).getOpposite());
        }
    }
    
    protected boolean canRotateVertical() {
    
        return true;
    }
    
    @Override
    public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
    
        TileEntity te = worldObj.getTileEntity(x, y, z);
        if (te instanceof IRotatable) {
            IRotatable rotatable = (IRotatable) te;
            ForgeDirection dir = rotatable.getFacingDirection();
            
            do {
                dir = ForgeDirection.getOrientation(dir.ordinal() + 1);
                if (dir == ForgeDirection.UNKNOWN) dir = ForgeDirection.DOWN;
            } while (!canRotateVertical() && (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN));
            rotatable.setFacingDirection(dir);
            return true;
        }
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
    
        textures = new HashMap<String, IIcon>();
        for (EnumFaceType faceType : EnumFaceType.values()) {
            boolean ejecting = false;
            do {
                boolean powered = false;
                do {
                    
                    String iconName = getIconName(faceType, ejecting, powered);
                    if (!textures.containsKey(iconName)) {
                        textures.put(iconName, iconRegister.registerIcon(iconName));
                    }
                    
                    powered = !powered;
                } while (powered == true);
                ejecting = !ejecting;
            } while (ejecting == true);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(EnumFaceType faceType, boolean ejecting, boolean powered) {
    
        return textures.get(getIconName(faceType, ejecting, powered));
    }
    
    protected IIcon getIcon(EnumFaceType faceType, boolean ejecting, boolean powered, int side, TileEntity te) {
    
        return getIcon(faceType, ejecting, powered);
    }
    
    @Override
    public Block setBlockName(String name) {
    
        super.setBlockName(name);
        textureName = Refs.MODID + ":" + Refs.MACHINE_TEXTURE_LOCATION + name;
        return this;
    }
    
    protected String getIconName(EnumFaceType faceType, boolean ejecting, boolean powered) {
    
        String iconName = textureName + "_" + faceType.toString().toLowerCase();
        if (faceType == EnumFaceType.SIDE) {
            if (ejecting) iconName += "_active";
            if (powered) iconName += "_powered";
        }
        return iconName;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
    
        if (textures == null) return super.getIcon(world, x, y, z, side);
        TileEntity te = world.getTileEntity(x, y, z);
        RendererBlockBase.EnumFaceType faceType = EnumFaceType.SIDE;
        boolean powered = false;
        boolean ejecting = false;
        if (te instanceof IRotatable) {
            ForgeDirection rotation = ((IRotatable) te).getFacingDirection();
            if (rotation.ordinal() == side) faceType = EnumFaceType.FRONT;
            if (rotation.getOpposite().ordinal() == side) faceType = EnumFaceType.BACK;
        }
        if (te instanceof IBluePowered) {
            powered = ((IBluePowered) te).isPowered();
        }
        if (te instanceof IEjectAnimator) {
            ejecting = ((IEjectAnimator) te).isEjecting();
        }
        return getIcon(faceType, ejecting, powered, side, te);
    }
    
    /**
     * This method will only be called from the item render method. the meta variable doesn't have any meaning.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
    
        if (textures == null) return super.getIcon(side, meta);
        return getIcon(EnumFaceType.values()[side == 0 ? 2 : side == 1 ? 1 : 0], false, false);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
    
        return RendererBlockBase.RENDER_ID;
    }
    
    /**
     * Method to be overwritten that returns a GUI ID
     *
     * @return
     */
    public abstract GuiIDs getGuiID();
}
