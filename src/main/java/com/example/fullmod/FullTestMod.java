package com.example.fullmod;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
@Mod(modid = "farmingmod_v1", name = "Move Mod", version = "1.0")
public class FullTestMod {

    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean running = false;
    private int tickCounter = 0;
    private int phase = 0;
    private boolean lastRKeyState = false;
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // 检测 O 键是否从未按下 -> 按下一瞬间
        boolean currentRKey = Keyboard.isKeyDown(Keyboard.KEY_LBRACKET);
        if (currentRKey && !lastRKeyState) {
            running = !running;
            resetKeys();
            tickCounter = 0;
            phase = 0;
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    "§e[FullMod] 自动动作已 " + (running ? "§a开启" : "§c关闭")
            ));
        }
        lastRKeyState = currentRKey;

        if (!running) return;

        tickCounter++;

        // 获取方块坐标，防止空指针

        if (mc.objectMouseOver == null || mc.objectMouseOver.getBlockPos() == null) return;
//        BlockPos pos = mc.objectMouseOver.getBlockPos();
        BlockPos pos1 = new BlockPos(134,73,67);
        BlockPos pos2 = new BlockPos(134,73,65);
        switch (phase) {
            case 0:
                if (tickCounter == 1) {


                    faceBlock(pos1);
                    press(mc.gameSettings.keyBindAttack);
                }
                if (!hasBlock(pos1)){
                    release(mc.gameSettings.keyBindAttack);
                    phase++;
                    tickCounter = 0;
                }
                break;
            case 1:
                if (tickCounter == 1 && isBedrock(pos2)) {mc.thePlayer.sendChatMessage("case 1");
                faceBlock(pos2);
                press(mc.gameSettings.keyBindAttack);}
                if (!hasBlock(pos2)){
                    release(mc.gameSettings.keyBindAttack);
                    phase++;
                    tickCounter = 0;
                }

                break;
                case 2:
                    if (tickCounter == 1) {mc.thePlayer.sendChatMessage("case 2");}
                    break;
        }
    }
    private void faceBlock(BlockPos pos) {
        double dx = pos.getX() + 0.5 - mc.thePlayer.posX;
        double dy = pos.getY() + 0.5 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dz = pos.getZ() + 0.5 - mc.thePlayer.posZ;

        double distXZ = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90F);
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, distXZ)));

        // 强制覆盖
        mc.thePlayer.rotationYaw = yaw;
        mc.thePlayer.prevRotationYaw = yaw;

        mc.thePlayer.rotationPitch = pitch;
        mc.thePlayer.prevRotationPitch = pitch;
    }



    private void press(KeyBinding key) {
        KeyBinding.setKeyBindState(key.getKeyCode(), true);
    }

    private void release(KeyBinding key) {
        KeyBinding.setKeyBindState(key.getKeyCode(), false);
    }

    private void resetKeys() {
        release(mc.gameSettings.keyBindLeft);
        release(mc.gameSettings.keyBindRight);
        release(mc.gameSettings.keyBindForward);
        release(mc.gameSettings.keyBindSneak);
        release(mc.gameSettings.keyBindAttack);
    }
    private boolean hasBlock(BlockPos pos) {
        return !mc.theWorld.isAirBlock(pos);
    }
    private boolean isBedrock(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return block == Blocks.bedrock;
    }

}
