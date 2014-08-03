package com.bluepowermod.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import com.bluepowermod.client.gui.widget.BaseWidget;
import com.bluepowermod.client.gui.widget.IGuiWidget;
import com.bluepowermod.client.gui.widget.WidgetMode;
import com.bluepowermod.containers.ContainerItemDetector;
import com.bluepowermod.network.NetworkHandler;
import com.bluepowermod.network.messages.MessageGuiUpdate;
import com.bluepowermod.references.Refs;
import com.bluepowermod.tileentities.tier1.TileItemDetector;

public class GuiItemDetector extends GuiBase {
    
    private static final ResourceLocation resLoc = new ResourceLocation(Refs.MODID, "textures/gui/item_detector.png");
    private final TileItemDetector        itemDetector;
    
    public GuiItemDetector(InventoryPlayer invPlayer, TileItemDetector itemDetector) {
    
        super(new ContainerItemDetector(invPlayer, itemDetector), resLoc);
        this.itemDetector = itemDetector;
    }
    
    @Override
    public void initGui() {
    
        super.initGui();
        WidgetMode modeWidget = new WidgetMode(0, guiLeft + 152, guiTop + 10, 176, 3, Refs.MODID + ":textures/gui/item_detector.png");
        modeWidget.value = itemDetector.mode;
        addWidget(modeWidget);
    }
    
    @Override
    public void actionPerformed(IGuiWidget widget) {
    
        BaseWidget baseWidget = (BaseWidget) widget;
        NetworkHandler.sendToServer(new MessageGuiUpdate(itemDetector, widget.getID(), baseWidget.value));
    }
}