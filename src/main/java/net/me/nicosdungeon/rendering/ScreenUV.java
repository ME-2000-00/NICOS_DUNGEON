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
import org.lwjgl.opengl.GL20;
import org.joml.Matrix4f;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryStack;


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
        Vec3d cameraPos = ctx.camera().getPos();

        Vector3f point_mcam = new Vector3f((float) (point.x - cameraPos.x), (float) (point.y - cameraPos.y), (float) (point.z - cameraPos.z));

        buffer.vertex(point_mcam.x, point_mcam.y, point_mcam.z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    private static void render(WorldRenderContext ctx) {
        if (CUSTOM_SHADER != null) {

            // setting shader
            RenderSystem.setShader(() -> CUSTOM_SHADER);

            // === Upload matrices with raw OpenGL ===
            uploadMatrixUniform(CUSTOM_SHADER.getUniform("ProjMat"), ctx.projectionMatrix());
            uploadMatrixUniform(CUSTOM_SHADER.getUniform("ModelViewMat"), ctx.matrixStack().peek().getPositionMatrix());


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

    private static void uploadMatrixUniform(GlUniform uniform, Matrix4f matrix) {
        if (uniform == null) return;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            matrix.get(fb); // JOML fills the buffer in column-major order (correct for OpenGL)
            GL20.glUniformMatrix4fv(uniform.getLocation(), false, fb);
        }
    }
}
