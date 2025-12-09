package com.example.fullmod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class CommandGoto extends CommandBase {

    @Override
    public String getCommandName() {
        return "goto"; // 命令名：/goto
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/goto <x> <y> <z> or /goto stop" ;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length == 1 && args[0].equalsIgnoreCase("stop")) {
            FullTestMod.instance.stopPathWalk();
        }
        if (args.length != 3 && !args[0].equalsIgnoreCase("stop") ){
            sender.addChatMessage(new ChatComponentText("§c用法: /goto <x> <y> <z> or /goto stop"));
            return;
        }

        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);

            FullTestMod.instance.startWalking(new BlockPos(x, y, z));
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("§c坐标必须是整数"));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // 所有人都能用客户端命令
    }
}

