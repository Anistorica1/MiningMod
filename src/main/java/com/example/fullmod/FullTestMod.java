package com.example.fullmod;

import net.minecraftforge.client.ClientCommandHandler;
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
    private boolean markFirst = true;
    private BlockPos[] poses = new BlockPos[100];
    private BlockPos targetPos = null;
    private boolean autoWalk = false;
    private float targetYaw = 0;
    private float targetPitch = 0;
    private float targetSpeed = 0;
    private boolean smoothLook = false;
    public static FullTestMod instance;
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new CommandGoto());
        ClientCommandHandler.instance.registerCommand(new CommandSmoothLook());
    }
    public FullTestMod() {
        instance = this;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        handleAutoWalk();
        handleAutoStep();
        handleSmoothLook();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // 检测 O 键是否从未按下 -> 按下一瞬间
        boolean currentRKey = Keyboard.isKeyDown(Keyboard.KEY_LBRACKET);
        if (currentRKey && !lastRKeyState) {
            running = !running;
            resetKeys();
            tickCounter = 0;
            phase = 0;
            markFirst = true;
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
        if(markFirst) {
            poses[0] = mc.objectMouseOver.getBlockPos();
            markFirst = false;
        }

//        switch (phase) {
//            case 0:
//                if (tickCounter ==1){
//                faceBlock(poses[0]);
//                press(mc.gameSettings.keyBindAttack);
//                press(mc.gameSettings.keyBindForward);}
//                if(!hasBlock(poses[0]))
//                {
//                    release(mc.gameSettings.keyBindAttack);
//                    phase ++;
//                    tickCounter = 0;
//                }
//                break;
//            case 1:
//                poses[1] = poses[0].add(0, -1, 0);
//                if (tickCounter == 1){
//                faceBlock(poses[1]);
//                press(mc.gameSettings.keyBindAttack);
//                press(mc.gameSettings.keyBindForward);}
//                if(!hasBlock(poses[1]))
//                {
//                    release(mc.gameSettings.keyBindAttack);
//                    phase ++;
//                    poses[0] = poses[1].add(1, 1, 0);
//                    tickCounter = 0;
//                }
//                break;
//                case 2:
//                    if (tickCounter == 1){
//                        faceBlock(poses[0]);
//                        press(mc.gameSettings.keyBindForward);}
//                    if(tickCounter == 2){
//                        phase = 0;
//                        tickCounter = 0;
//                        markFirst = true;
//                    }
//                    break;
//        }
        switch (phase) {
            case 0:
                phase++;
                break;
                case 1:
                    phase = 0;
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
    private void faceAngle(float yaw, float pitch) {
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
    private void handleAutoWalk() {
        if (!autoWalk || targetPos == null) return;

        double px = mc.thePlayer.posX;
        double pz = mc.thePlayer.posZ;

        double tx = targetPos.getX() + 0.5;
        double tz = targetPos.getZ() + 0.5;

        double dx = tx - px;
        double dz = tz - pz;

        double distance = Math.sqrt(dx * dx + dz * dz);

        // 1. 到达目的地
        if (distance < 0.5) {
            autoWalk = false;
            release(mc.gameSettings.keyBindForward);
            mc.thePlayer.addChatMessage(new ChatComponentText("§a[FullMod] 已到达目的地！"));
            return;
        }

        // 2. 计算 yaw
        float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90F);

        // 3. 设置玩家朝向
        mc.thePlayer.rotationYaw = yaw;
        mc.thePlayer.prevRotationYaw = yaw;

        // 4. 按住前进
        press(mc.gameSettings.keyBindForward);
    }
    public void walkTo(BlockPos pos) {
        this.targetPos = pos;
        this.autoWalk = true;
        mc.thePlayer.addChatMessage(new ChatComponentText("§e[FullMod] 正在前往：" + pos.toString()));
    }
    public void stopWalk(){
        this.autoWalk = false;
        mc.thePlayer.addChatMessage(new ChatComponentText("§e[FullMod] 已停止行动"));
    }
    public void handleSmoothLook() {
        if(!smoothLook) return;
        float currentYaw = mc.thePlayer.rotationYaw;
        float currentPitch = mc.thePlayer.rotationPitch;
        float targetYaw = this.targetYaw;
        float targetPitch = this.targetPitch;
        float speed = this.targetSpeed;
        // 计算差值
        float diffYaw = wrapAngleTo180_float(targetYaw - currentYaw);
        float diffPitch = targetPitch - currentPitch;

        // 限制每 tick 的旋转速度
        diffYaw = clamp(diffYaw, -speed, speed);
        diffPitch = clamp(diffPitch, -speed, speed);

        // 更新角度
        mc.thePlayer.rotationYaw = currentYaw + diffYaw;
        mc.thePlayer.rotationPitch = currentPitch + diffPitch;
        if(mc.thePlayer.rotationYaw ==targetYaw && mc.thePlayer.rotationPitch ==targetPitch) {
            smoothLook = false;
            mc.thePlayer.addChatMessage(new ChatComponentText("§a[FullMod] 已到达指定角度！"));
        }
    }
    public float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
    public float wrapAngleTo180_float(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }
    public void smoothLook(float targetYaw, float targetPitch, float speed) {
        this.targetYaw = targetYaw;
        this.targetPitch = targetPitch;
        this.targetSpeed = speed;
        this.smoothLook = true;
        mc.thePlayer.addChatMessage(new ChatComponentText("§e[FullMod] 面向：" + targetYaw+","+targetPitch));
    }
    private void handleAutoStep() {
        if (!autoWalk) return;

        // 当前朝向
        double yawRad = Math.toRadians(mc.thePlayer.rotationYaw);

        // 前方坐标（半格）
        double forwardX = mc.thePlayer.posX + (-Math.sin(yawRad)) * 0.5;
        double forwardZ = mc.thePlayer.posZ + ( Math.cos(yawRad)) * 0.5;

        BlockPos front = new BlockPos(forwardX, mc.thePlayer.posY - 0.1, forwardZ);
        BlockPos frontUp = front.up();

        boolean blockFront = !mc.theWorld.isAirBlock(front);
        boolean blockFrontUp = !mc.theWorld.isAirBlock(frontUp);

        // 前方一格高 → 自动跳
        if (blockFront && blockFrontUp) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
        } else {
            // 松开跳跃键
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
        }
    }

}
