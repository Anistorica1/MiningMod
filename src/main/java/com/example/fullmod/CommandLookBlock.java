package com.example.fullmod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class CommandLookBlock extends CommandBase {

    @Override
    public String getCommandName() {
        return "lookblock"; // 命令名：/lookblock
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lookblock <x> <y> <z> <speed> or /lookblock stop" ;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 4 ) {
            sender.addChatMessage(new ChatComponentText("§c用法: /lookblock <x> <y> <z> <speed>"));
            return;
        }

        try {
            float x = Float.parseFloat(args[0]);
            float y = Float.parseFloat(args[1]);
            float z = Float.parseFloat(args[2]);
            float speed = Float.parseFloat(args[3]);
            FullTestMod.instance.smoothLookToBlockPos(new BlockPos(x, y, z),speed);
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("§c坐标必须是整数"));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // 所有人都能用客户端命令
    }
}

