package net.linkedsquash.nocturne.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.block.ModBlocks;
import net.linkedsquash.nocturne.client.simulations.ClientWhipPhysicsManager;
import net.linkedsquash.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class WhipEntityRenderer extends EntityRenderer<WhipEntity> {
    private static final int SEGMENTS = 16;
    private static final float HANDLE_WIDTH = 0.055F;
    private static final float TIP_WIDTH = 0.014F;
    private static final float TIP_MARKER_SIZE = 0.14F;

    private final BlockRenderDispatcher blockRenderer;

    public WhipEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(
            WhipEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        Player owner = entity.getPlayerOwner();
        if (owner != null) {
            renderWhip(entity, owner, partialTick, poseStack, buffer, packedLight);
        }

        // Keep the bloodstone tip visible without letting it dwarf the whip.
        poseStack.pushPose();
        poseStack.translate(-TIP_MARKER_SIZE * 0.5, -TIP_MARKER_SIZE * 0.5, -TIP_MARKER_SIZE * 0.5);
        poseStack.scale(TIP_MARKER_SIZE, TIP_MARKER_SIZE, TIP_MARKER_SIZE);
        blockRenderer.renderSingleBlock(
                ModBlocks.BLOODSTONE_BLOCK.get().defaultBlockState(),
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderWhip(
            WhipEntity entity,
            Player owner,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        // Convert the hand position into coordinates relative to the entity.
        Vec3 entityPosition = entity.getPosition(partialTick);
        Vec3 handPosition = owner.getRopeHoldPosition(partialTick);

        List<Vec3> points = ClientWhipPhysicsManager.get(entity)
                .getPoints()
                .stream()
                .map(point -> point.subtract(entityPosition))
                .toList();        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vector3f lookVector = camera.getLookVector();
        Vec3 cameraLook = new Vec3(lookVector.x(), lookVector.y(), lookVector.z());

        VertexConsumer vertices =
                buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        PoseStack.Pose pose = poseStack.last();

        for (int i = 0; i < points.size() - 1; i++) {
            Vec3 a = points.get(i);
            Vec3 b = points.get(i + 1);
            Vec3 direction = b.subtract(a).normalize();
            float v0 = i / (float) (points.size() - 1);
            float v1 = (i + 1) / (float) (points.size() - 1);

            // Make the ribbon face the camera.
            Vec3 side = direction.cross(cameraLook);
            if (side.lengthSqr() < 1.0E-6) {
                side = direction.cross(new Vec3(0.0, 1.0, 0.0));
            }
            if (side.lengthSqr() < 1.0E-6) {
                side = new Vec3(1.0, 0.0, 0.0);
            }
            side = side.normalize();
            Vec3 aSide = side.scale(widthAt(v0) * 0.5);
            Vec3 bSide = side.scale(widthAt(v1) * 0.5);

            Vec3 aLeft = a.subtract(aSide);
            Vec3 aRight = a.add(aSide);
            Vec3 bLeft = b.subtract(bSide);
            Vec3 bRight = b.add(bSide);

            addVertex(vertices, pose, aLeft, 0.0F, v0, packedLight);
            addVertex(vertices, pose, aRight, 1.0F, v0, packedLight);
            addVertex(vertices, pose, bRight, 1.0F, v1, packedLight);
            addVertex(vertices, pose, bLeft, 0.0F, v1, packedLight);
        }
    }

    private static float widthAt(float t) {
        return HANDLE_WIDTH + (TIP_WIDTH - HANDLE_WIDTH) * t;
    }

    private static void addVertex(
            VertexConsumer vertices,
            PoseStack.Pose pose,
            Vec3 position,
            float u,
            float v,
            int packedLight
    ) {
        vertices.addVertex(pose, (float) position.x, (float) position.y, (float) position.z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(WhipEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                Nocturne.MOD_ID,
                "textures/entity/whipentity.png"
        );
    }
}
