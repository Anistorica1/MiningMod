package com.example.fullmod;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;


public class CommandMining extends CommandBase {

    @Override
    public List<String> addTabCompletionOptions(
            ICommandSender sender,
            String[] args,
            BlockPos pos
    ) {
        if (args.length == 1 || args.length == 2) {
            return getListOfStringsMatchingLastWord(
                    args,
                    "wool", "prismarine","stop"
            );
        }
        return null;
    }
    @Override
    public String getCommandName() {
        return "mining";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mining <block_name1> <block_name2> <block_name3>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length != 1 && args.length != 2 && args.length != 3) {
            sender.addChatMessage(
                    new ChatComponentText("§c用法: /mining <block_name1> <block_name2> <block_name3> or /minging stop")
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
        List<Block> blocks = new ArrayList<Block>();

        for (String name : args) {
            Block block = Block.getBlockFromName(name);

            if (block == null || block == Blocks.air) {
                sender.addChatMessage(
                        new ChatComponentText("§c未知方块: " + name)
                );
                return;
            }

            blocks.add(block);
        }
        FullTestMod.instance.startMining(blocks);
        for (Block block : blocks) {
            sender.addChatMessage(
                    new ChatComponentText("§a开始挖掘: " + block.getLocalizedName())
            );
        }

    }
    @Override
    public int getRequiredPermissionLevel() {
        return 0; // 所有人都能用客户端命令
    }
}
