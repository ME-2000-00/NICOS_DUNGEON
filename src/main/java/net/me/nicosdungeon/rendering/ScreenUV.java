package net.me.nicosdungeon.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.me.nicosdungeon.NicosDungoen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.Uniform;
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
    // for debugging
    private static Color test_color = Color.GREEN;

    public static void init() {
        WorldRenderEvents.START.register((context) -> {
                    // setup custom shader
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
        });

        WorldRenderEvents.END.register((context) -> {
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

        } else {
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            test_color = Color.RED;
        }


        // passing uniforms
        Uniform PMUniform = CUSTOM_SHADER.getUniform("ProjMat");
        if (PMUniform != null) {
            PMUniform.set(ctx.projectionMatrix()); // If `matrix` is Matrix4f
            NicosDungoen.LOGGER.info(ctx.projectionMatrix().toString());
        }

        Uniform MVMUniform = CUSTOM_SHADER.getUniform("ModelViewMat");
        if (MVMUniform != null) {
            MVMUniform.set(ctx.matrixStack().peek().getPositionMatrix()); // If `matrix` is Matrix4f
            NicosDungoen.LOGGER.info(ctx.matrixStack().peek().getPositionMatrix().toString());
        }


        // basic quad setup
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

        add_point_to_buffer(ctx, buffer, new Vector3f(NicosDungoen.Triangle_position).add(offset1), test_color);
        add_point_to_buffer(ctx, buffer, new Vector3f(NicosDungoen.Triangle_position).add(offset2), test_color);
        add_point_to_buffer(ctx, buffer, new Vector3f(NicosDungoen.Triangle_position).add(offset3), test_color);
        add_point_to_buffer(ctx, buffer, new Vector3f(NicosDungoen.Triangle_position).add(offset4), test_color);

        BufferRenderer.draw(buffer.end());
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
    }
}
