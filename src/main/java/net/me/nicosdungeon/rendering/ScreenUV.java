package net.me.nicosdungeon.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.me.nicosdungeon.NicosDungoen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.FloatBuffer;

public class ScreenUV {
    private static ShaderProgram CUSTOM_SHADER;

    public static void init() {
        WorldRenderEvents.LAST.register((context) -> {

            if (CUSTOM_SHADER == null) {
                try {
                    CUSTOM_SHADER = new ShaderProgram(
                            MinecraftClient.getInstance().getResourceManager(),
                            "nduv",
                            VertexFormats.POSITION_COLOR
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if (NicosDungoen.Global_render) {
                render(context);
            }
        });
    }

    private static void add_point_to_buffer(WorldRenderContext ctx, BufferBuilder buffer, Vector3f point, Color color) {
        // NicosDungoen.LOGGER.info("[VERTEX BUFFER]:     Vertex: " + point);
        Vec3d cameraPos = ctx.camera().getPos();
        point = new Vector3f((float) (point.x - cameraPos.x), (float) (point.y - cameraPos.y), (float) (point.z - cameraPos.z));
        buffer.vertex(point.x, point.y, point.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    private static void render(WorldRenderContext ctx) {
        if (CUSTOM_SHADER != null) {

            // setting shader
            RenderSystem.setShader(() -> CUSTOM_SHADER);

        } else {
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // Quad corners (centered at px,py,pz, facing camera)
        Vector3f offset1 = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f offset2 = new Vector3f(1.0f, 0.0f, 0.0f);
        Vector3f offset3 = new Vector3f(1.0f, 1.0f, 0.0f);
        Vector3f offset4 = new Vector3f(0.0f, 1.0f, 0.0f);

        add_point_to_buffer(ctx, buffer, new Vector3f(NicosDungoen.Triangle_position).add(offset1), Color.RED);
        add_point_to_buffer(ctx, buffer, new Vector3f(NicosDungoen.Triangle_position).add(offset2), Color.BLUE);
        add_point_to_buffer(ctx, buffer, new Vector3f(NicosDungoen.Triangle_position).add(offset3), Color.GREEN);
        add_point_to_buffer(ctx, buffer, new Vector3f(NicosDungoen.Triangle_position).add(offset4), Color.YELLOW);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
    }
}
