package org.merlin204.mef.client.gui;

import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class ExecuteIconRenderCommand {
    public final Matrix4f matrix;          // 已经对齐到相机的变换矩阵
    public final ResourceLocation texture;
    public final float minX, minY, maxX, maxY;
    public final float minU, minV, maxU, maxV;

    public ExecuteIconRenderCommand(Matrix4f matrix, ResourceLocation texture,
                                    float minX, float minY, float maxX, float maxY,
                                    float minU, float minV, float maxU, float maxV) {
        this.matrix = new Matrix4f(matrix); // 复制一份，防止后续修改
        this.texture = texture;
        this.minX = minX; this.minY = minY; this.maxX = maxX; this.maxY = maxY;
        this.minU = minU; this.minV = minV; this.maxU = maxU; this.maxV = maxV;
    }
}