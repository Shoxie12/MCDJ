package com.shoxie.mcdj.proxy;

import com.shoxie.mcdj.container.ContainerMusicGenerator;
import com.shoxie.mcdj.gui.GuiMusicGenerator;
import com.shoxie.mcdj.tile.TileMusicGenerator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMusicGenerator) {
            return new ContainerMusicGenerator(player.inventory, (TileMusicGenerator) tile);
        }  
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMusicGenerator) {
            return new GuiMusicGenerator((TileMusicGenerator) tile, new ContainerMusicGenerator(player.inventory, (TileMusicGenerator) tile));
        }
        return null;
    }
}