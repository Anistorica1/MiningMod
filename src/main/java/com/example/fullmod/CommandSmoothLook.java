package com.example.fullmod;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class CommandSmoothLook extends CommandBase {

    @Override
    public String getCommandName() {
        return "smoothlook";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/smoothlook <yaw> <pitch> <time>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.addChatMessage(new ChatComponentText("§c用法: /smoothlook <yaw> <pitch> <time>"));
            return;
        }

        try {
            float yaw = Float.parseFloat(args[0]);
            float pitch = Float.parseFloat(args[1]);
            float time = Float.parseFloat(args[2]);

            FullTestMod.instance.smoothLook(yaw,pitch,time);
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("§c坐标必须是整数"));
        }
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 0; // 允许所有人使用
    }
}

