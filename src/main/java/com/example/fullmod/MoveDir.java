package com.example.fullmod;

public enum MoveDir {
    POS_X(1, 0),
    NEG_X(-1, 0),
    POS_Z(0, 1),
    NEG_Z(0, -1);

    public final int dx;
    public final int dz;

    MoveDir(int dx, int dz) {
        this.dx = dx;
        this.dz = dz;
    }

    /** 是否沿 X 轴移动 */
    public boolean isX() {
        return dx != 0;
    }

    /** 是否沿 Z 轴移动 */
    public boolean isZ() {
        return dz != 0;
    }

    /** 方向符号（+1 / -1） */
    public int sign() {
        return dx != 0 ? dx : dz;
    }
}

