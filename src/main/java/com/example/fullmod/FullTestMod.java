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

import java.util.ArrayList;
import java.util.List;

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
    private float startYaw, startPitch;
    private float targetYaw, targetPitch;
    private int smoothTicks = 0;
    private int maxSmoothTicks = 0;
    private boolean isSmoothLooking = false;
    private float targetSpeed = 0;
    private boolean smoothLook = false;
    public static FullTestMod instance;
    private List<PathNode> path = null;
    private int currentNodeIndex = 0;
    private boolean pathWalking = false;
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new CommandGoto());
        ClientCommandHandler.instance.registerCommand(new CommandSmoothLook());
        ClientCommandHandler.instance.registerCommand(new CommandLookBlock());
    }
    public FullTestMod() {
        instance = this;
    }
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        handleAutoStep();
        handleSmoothLook();
        handlePathWalk();
        handleSmoothLook2();
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
    public void walkTo(BlockPos pos) {
        this.targetPos = pos;
        this.autoWalk = true;
        mc.thePlayer.addChatMessage(new ChatComponentText("§e[FullMod] 正在前往：" + pos.toString()));
    }
    public void stopWalk(){
        this.autoWalk = false;
        resetKeys();
        mc.thePlayer.addChatMessage(new ChatComponentText("§e[FullMod] 已停止行动"));
    }
    public void handleSmoothLook() {
        if (!smoothLook) return;

        float currentYaw = mc.thePlayer.rotationYaw;
        float currentPitch = mc.thePlayer.rotationPitch;

        float diffYaw = wrapAngleTo180_float(targetYaw - currentYaw);
        float diffPitch = targetPitch - currentPitch;

        float speed = this.targetSpeed;

        // 如果已经非常接近目标角度 → 认为到达
        if (Math.abs(diffYaw) < 0.01f && Math.abs(diffPitch) < 0.01f) {
            mc.thePlayer.rotationYaw = targetYaw;
            mc.thePlayer.rotationPitch = targetPitch;
            smoothLook = false;
            mc.thePlayer.addChatMessage(new ChatComponentText("§a[FullMod] 已到达指定角度！"));
            return;
        }

        // 限制每 tick 旋转速度
        diffYaw = clamp(diffYaw, -speed, speed);
        diffPitch = clamp(diffPitch, -speed, speed);

        mc.thePlayer.rotationYaw = currentYaw + diffYaw;
        mc.thePlayer.rotationPitch = currentPitch + diffPitch;
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
    public void smoothLook(float yaw, float pitch, float durationSeconds) {
        this.startYaw = mc.thePlayer.rotationYaw;
        this.startPitch = mc.thePlayer.rotationPitch;

        this.targetYaw = yaw;
        this.targetPitch = pitch;

        this.maxSmoothTicks = (int)(durationSeconds * 20); // 秒 → tick
        this.smoothTicks = 0;

        this.isSmoothLooking = true;
    }

    private void handleAutoStep() {
        if (!pathWalking) return;

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
    public List<PathNode> generateSimplePath(BlockPos start, BlockPos end) {
        List<PathNode> path = new ArrayList<PathNode>();

        int dx = end.getX() - start.getX();
        int dz = end.getZ() - start.getZ();

        int steps = Math.max(Math.abs(dx), Math.abs(dz));

        for (int i = 0; i <= steps; i++) {
            int x = start.getX() + dx * i / steps;
            int z = start.getZ() + dz * i / steps;
            int y = start.getY(); // 简单版本不处理高度

            path.add(new PathNode(x, y, z, null));
        }

        return path;
    }
    public void startWalking(BlockPos target) {
        BlockPos start = new BlockPos(mc.thePlayer.posX, target.getY(), mc.thePlayer.posZ);
        this.path = generateSimplePath(start, target);
        this.currentNodeIndex = 0;
        this.pathWalking = true;
        mc.thePlayer.addChatMessage(new ChatComponentText("§a[FullMod] 开始路径寻路！"));
    }

    public void stopPathWalk() {
        this.pathWalking = false;
        resetKeys();
        mc.thePlayer.addChatMessage(new ChatComponentText("§a[FullMod] 已停止寻路"));
    }
    private void handlePathWalk() {
        if (!pathWalking || path == null) return;



        if (currentNodeIndex >= path.size()) {
            stopPathWalk();
            return;
        }
        PathNode node = path.get(currentNodeIndex);
        BlockPos nextPos = new BlockPos(node.x, node.y, node.z);

        if (isCliffAt(nextPos.down())) {
            resetKeys();
            mc.thePlayer.addChatMessage(new ChatComponentText("§c前方有坑，尝试绕开"));
            // TODO: 绕路逻辑或者停止
            stopPathWalk();
            return;
        }

        double px = mc.thePlayer.posX;
        double pz = mc.thePlayer.posZ;

        double dx = node.x + 0.5 - px;
        double dz = node.z + 0.5 - pz;

        double dist = Math.sqrt(dx*dx + dz*dz);

        // 到达该节点
        if (dist < 0.3) {
            currentNodeIndex++;
            return;
        }

        // 计算转向
        float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90F);

        mc.thePlayer.rotationYaw = yaw;
        mc.thePlayer.prevRotationYaw = yaw;

        // 前进
        press(mc.gameSettings.keyBindForward);
    }
    public float[] getRotationFromBlockPos(BlockPos pos) {
        double dx = pos.getX() + 0.5 - mc.thePlayer.posX;
        double dy = pos.getY() + 0.5 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dz = pos.getZ() + 0.5 - mc.thePlayer.posZ;

        double distXZ = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90F);
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, distXZ)));
        return new float[]{yaw, pitch};
    }
    public void smoothLookToBlockPos(BlockPos target, float speed) {
        float[] rotation = getRotationFromBlockPos(target);
        smoothLook(rotation[0], rotation[1], speed);
    }
    private float easeInOut(float t) {
        return (float)(t * t * (3 - 2 * t));
    }
    private void handleSmoothLook2(){
        if (this.isSmoothLooking) {
            if (smoothTicks >= maxSmoothTicks) {
                mc.thePlayer.rotationYaw = targetYaw;
                mc.thePlayer.rotationPitch = targetPitch;
                this.isSmoothLooking = false;
            } else {
                float t = (float)smoothTicks / maxSmoothTicks; // 0~1
                float k = easeInOut(t); // 使用缓动

                float newYaw = startYaw + (targetYaw - startYaw) * k;
                float newPitch = startPitch + (targetPitch - startPitch) * k;

                mc.thePlayer.rotationYaw = newYaw;
                mc.thePlayer.prevRotationYaw = newYaw;

                mc.thePlayer.rotationPitch = newPitch;
                mc.thePlayer.prevRotationPitch = newPitch;

                smoothTicks++;
            }
        }
    }
    private boolean isCliffAt(BlockPos pos) {
        // 检查 pos 下两格是否都是空气
        return mc.theWorld.isAirBlock(pos) && mc.theWorld.isAirBlock(pos.down());
    }


}