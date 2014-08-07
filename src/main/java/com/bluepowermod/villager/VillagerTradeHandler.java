package com.bluepowermod.villager;

import com.bluepowermod.init.BPItems;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

/**
 * @author kieran  on 07/08/2014.
 */
public class VillagerTradeHandler implements VillagerRegistry.IVillageTradeHandler {
    public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random)
    {
        recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 2), null, new ItemStack(BPItems.screwdriver, 1)));
        recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 4), null, new ItemStack(BPItems.red_doped_wafer, 16)));
        recipeList.add(new MerchantRecipe(new ItemStack(BPItems.ruby, 12), new ItemStack(Items.iron_ingot, 2), new ItemStack(Items.emerald, 8)));

    }
}
