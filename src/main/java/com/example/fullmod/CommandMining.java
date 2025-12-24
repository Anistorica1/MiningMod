package com.example.fullmod;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;


public class CommandMining extends CommandBase {
    @Override
    public String getCommandName() {
        return "mining";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mining <block_name>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length != 1) {
            sender.addChatMessage(
                    new ChatComponentText("§c用法: /mining <block_name> or /minging stop")
            );
            return;
        }
        if (args[0].equalsIgnoreCase("stop") ){
            sender.addChatMessage(
                    new ChatComponentText("§a停止挖掘")
            );
            FullTestMod.instance.stopMining();
            return;
        }
        String blockName = args[0];

        Block block = Block.getBlockFromName(blockName);

        if (block == null || block == Blocks.air) {
            sender.addChatMessage(
                    new ChatComponentText("§c未知方块: " + blockName)
            );
            return;
        }

        FullTestMod.instance.startMining(block);

        sender.addChatMessage(
                new ChatComponentText("§a开始挖掘: " + block.getLocalizedName())
        );
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 0; // 所有人都能用客户端命令
    }
}
