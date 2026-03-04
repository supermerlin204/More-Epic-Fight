package org.merlin204.mef.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class MEFRenderTypes extends RenderType {
    public MEFRenderTypes(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }


    public static RenderType alwaysOnTop(ResourceLocation texture) {
        return RenderType.create(
                "always_on_top",
                DefaultVertexFormat.POSITION_TEX_COLOR,
                VertexFormat.Mode.QUADS,
                256,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderStateShard.TransparencyStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(RenderStateShard.DepthTestStateShard.NO_DEPTH_TEST)
                        .setWriteMaskState(RenderStateShard.WriteMaskStateShard.COLOR_WRITE)
                        .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader))
                        .setLightmapState(RenderStateShard.LightmapStateShard.NO_LIGHTMAP)
                        .setOverlayState(RenderStateShard.OverlayStateShard.NO_OVERLAY)
                        .setCullState(RenderStateShard.CullStateShard.NO_CULL)
                        .createCompositeState(false)
        );
    }

}
