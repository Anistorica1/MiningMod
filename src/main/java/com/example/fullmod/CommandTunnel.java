package com.example.fullmod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandTunnel extends CommandBase {

    @Override
    public String getCommandName() {
        return "tunnel"; // 命令名：/tunnel
    }
    @Override
    public List<String> addTabCompletionOptions(
            ICommandSender sender,
            String[] args,
            BlockPos pos
    ) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(
                    args,
                    "start","stop"
            );
        }
        return null;
    }
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/tunnel start or /tunnel stop" ;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length == 1 && args[0].equalsIgnoreCase("stop")) {
            FullTestMod.instance.tunnelDisable();
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("start")) {
            FullTestMod.instance.tunnelEnable();
        }
        if (args.length != 1){
            sender.addChatMessage(new ChatComponentText("§c用法: /tunnel start or /tunnel stop"));
            return;
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // 所有人都能用客户端命令
    }
}

