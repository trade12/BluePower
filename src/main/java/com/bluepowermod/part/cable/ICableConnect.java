package com.bluepowermod.part.cable;

import net.minecraftforge.common.util.ForgeDirection;

public interface ICableConnect<C extends CableWall> {

    public void onConnect(C cable, ForgeDirection direction);

    public void onDisconnect(C cable, ForgeDirection direction);

}
