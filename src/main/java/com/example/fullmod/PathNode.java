package com.example.fullmod;

import net.minecraft.util.BlockPos;

public class PathNode {
    public int x, y, z;       // 方块坐标
    public PathNode parent;   // 上一个节点（用于回溯路径）

    public PathNode(int x, int y, int z, PathNode parent) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.parent = parent;
    }

    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }
}
