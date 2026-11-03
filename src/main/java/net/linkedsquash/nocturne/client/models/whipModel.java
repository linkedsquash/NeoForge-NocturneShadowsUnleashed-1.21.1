package net.linkedsquash.nocturne.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class WhipModel extends EntityModel<WhipEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Nocturne.MOD_ID, "whip_entity"), "main");

    private final ModelPart bone;
//    private final ModelPart bone2;
//    private final ModelPart bone3;
//    private final ModelPart bone4;
//    private final ModelPart bone5;
//    private final ModelPart bone6;
//    private final ModelPart bone7;

    public WhipModel(ModelPart root) {
        this.bone = root.getChild("bone");
//        this.bone2 = root.getChild("bone2");
//        this.bone3 = root.getChild("bone3");
//        this.bone4 = root.getChild("bone4");
//        this.bone5 = root.getChild("bone5");
//        this.bone6 = root.getChild("bone6");
//        this.bone7 = root.getChild("bone7");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition bone = addSegment(root, "bone", 0.0F);
        PartDefinition bone2 = addSegment(bone, "bone2", -8.0F);
        PartDefinition bone3 = addSegment(bone2, "bone3", -8.0F);
        PartDefinition bone4 = addSegment(bone3, "bone4", -8.0F);
        PartDefinition bone5 = addSegment(bone4, "bone5", -8.0F);
        PartDefinition bone6 = addSegment(bone5, "bone6", -8.0F);
        addSegment(bone6, "bone7", -8.0F);

        return LayerDefinition.create(mesh, 16, 16);
    }

    private static PartDefinition addSegment(PartDefinition parent, String name, float pivotY) {
        return parent.addOrReplaceChild(name,
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, pivotY, 0.0F));
    }

    @Override
    public void setupAnim(
            WhipEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        Player owner = entity.getPlayerOwner();
        if (owner == null) {
            bone.xRot = 0.0F;
            bone.yRot = 0.0F;
            bone.yScale = 1.0F;
            return;
        }

        Vec3 toOwner = owner.getRopeHoldPosition(ageInTicks - entity.tickCount).subtract(entity.position());
        double horizontalLength = Math.sqrt(toOwner.x * toOwner.x + toOwner.z * toOwner.z);
        bone.xRot = (float) Math.atan2(horizontalLength, -toOwner.y);
        bone.yRot = (float) Math.atan2(-toOwner.x, -toOwner.z);
        // Seven 8-pixel segments make a 3.5-block model; stretch it to the owner's hand.
        bone.yScale = (float) Math.max(0.01, toOwner.length() / 3.5);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer,
                               int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

}
