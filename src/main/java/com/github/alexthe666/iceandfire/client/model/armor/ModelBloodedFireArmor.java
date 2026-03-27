package com.github.alexthe666.iceandfire.client.model.armor;


import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ModelBloodedFireArmor extends ArmorModelBase {
        private static final ModelPart INNER_MODEL = createModelData(
                        CubeDeformation.NONE.extend(INNER_MODEL_OFFSET), 0.0F)
                        .getRoot().bake(64, 64);
        private static final ModelPart OUTER_MODEL = createModelData(
                        CubeDeformation.NONE.extend(OUTER_MODEL_OFFSET), 0.0F)
                        .getRoot().bake(64, 64);

        public ModelBloodedFireArmor(boolean inner) {
                super(getBakedModel(inner));
        }

        public static MeshDefinition createModelData(CubeDeformation deformation, float offset) {
                MeshDefinition meshDef = HumanoidModel.createMesh(deformation, offset);
                PartDefinition root = meshDef.getRoot();
                PartDefinition head = root.getChild("head");
                PartDefinition body = root.getChild("body");
                PartDefinition rightArm = root.getChild("right_arm");
                PartDefinition leftArm = root.getChild("left_arm");


                head.addOrReplaceChild("head_jaw",
                                CubeListBuilder.create().texOffs(6, 51)
                                                .addBox(-3.5F, 4.0F, -7.4F, 7, 2, 5, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.0F, -5.4F, 0.0F,
                                                -0.091106F, 0.0F, 0.0F));

                PartDefinition snout = head.addOrReplaceChild("head_snout",
                                CubeListBuilder.create().texOffs(6, 44)
                                                .addBox(-3.5F, -2.8F, -8.8F, 7, 2, 5, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.0F, -5.6F, 0.0F,
                                                0.045553F, 0.0F, 0.0F));
                snout.addOrReplaceChild("head_rightTeeth",
                                CubeListBuilder.create().texOffs(6, 34)
                                                .addBox(-3.6F, 0.1F, -8.9F, 4, 1, 5, new CubeDeformation(0.025F)),
                                PartPose.offset(0.0F, -1.0F, 0.0F));
                snout.addOrReplaceChild("head_leftTeeth",
                                CubeListBuilder.create().texOffs(6, 34).mirror()
                                                .addBox(-0.4F, 0.1F, -8.9F, 4, 1, 5, new CubeDeformation(0.025F)),
                                PartPose.offset(0.0F, -1.0F, 0.0F));

                head.addOrReplaceChild("head_fire1",
                                CubeListBuilder.create().texOffs(41, 50)
                                                .addBox(-2.0F, -5.0F, -1.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(-0.525F, 1.65F, 4.15F,
                                                0.0F, 1.4704F, 0.0F));
                head.addOrReplaceChild("head_fire2",
                                CubeListBuilder.create().texOffs(36, 48)
                                                .addBox(-1.0F, -5.0F, -1.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(7.1F, -0.1F, 2.75F,
                                                0.0F, -0.7854F, 0.0F));
                head.addOrReplaceChild("head_fire3",
                                CubeListBuilder.create().texOffs(41, 50)
                                                .addBox(-2.0F, -3.0F, -1.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(-4.775F, -3.35F, -2.85F,
                                                0.0F, 1.0777F, 0.0F));
                head.addOrReplaceChild("head_fire4",
                                CubeListBuilder.create().texOffs(35, 57)
                                                .addBox(-1.0F, -7.0F, -1.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(4.1F, -3.1F, -5.25F,
                                                0.0F, -0.7854F, 0.0F));

                PartDefinition hornRU1 = head.addOrReplaceChild("rightHorn_upper1",
                                CubeListBuilder.create().texOffs(48, 44)
                                                .addBox(-1.0F, -0.5F, 0.0F, 2, 3, 5, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(-3.6F, -8.0F, 1.0F,
                                                0.31416F, -0.33161F, -0.19199F));
                hornRU1.addOrReplaceChild("rightHorn_upper2",
                                CubeListBuilder.create().texOffs(46, 36)
                                                .addBox(-0.5F, -0.8F, 0.0F, 1, 2, 5, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.0F, 0.3F, 4.5F,
                                                -0.07505F, 0.0F, 0.0F));

                head.addOrReplaceChild("rightHorn_lower",
                                CubeListBuilder.create().texOffs(46, 36)
                                                .addBox(-0.5F, -0.8F, 0.0F, 1, 2, 5, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(-4.0F, -4.0F, 0.7F,
                                                -0.06981F, -0.48869F, -0.08727F));

                PartDefinition hornLU1 = head.addOrReplaceChild("leftHorn_upper1",
                                CubeListBuilder.create().texOffs(48, 44).mirror()
                                                .addBox(-1.0F, -0.5F, 0.0F, 2, 3, 5, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(3.6F, -8.0F, 1.0F,
                                                0.31416F, 0.33161F, 0.19199F));
                hornLU1.addOrReplaceChild("leftHorn_upper2",
                                CubeListBuilder.create().texOffs(46, 36).mirror()
                                                .addBox(-0.5F, -0.8F, 0.0F, 1, 2, 5, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.0F, 0.3F, 4.5F,
                                                -0.07505F, 0.0F, 0.0F));

                head.addOrReplaceChild("leftHorn_lower",
                                CubeListBuilder.create().texOffs(46, 36).mirror()
                                                .addBox(-0.5F, -0.8F, 0.0F, 1, 2, 5, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(4.0F, -4.0F, 0.7F,
                                                -0.06981F, 0.48869F, 0.08727F));


                PartDefinition bodyFire1 = body.addOrReplaceChild("body_fire1",
                                CubeListBuilder.create().texOffs(36, 48)
                                                .addBox(-1.0F, -5.0F, -1.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(2.35F, 7.5F, -2.025F,
                                                0.0F, -0.2487F, 0.0F));
                bodyFire1.addOrReplaceChild("body_fire1_r1",
                                CubeListBuilder.create().texOffs(36, 48)
                                                .addBox(-1.5F, -2.5F, 0.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(-5.0F, 2.5F, 0.25F,
                                                0.0F, 0.4363F, 0.0F));
                bodyFire1.addOrReplaceChild("body_fire1_r2",
                                CubeListBuilder.create().texOffs(35, 57).mirror()
                                                .addBox(-1.5F, -2.5F, 0.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(0.0F, -0.25F, -1.0F,
                                                0.0F, 3.0543F, 0.0F));
                body.addOrReplaceChild("body_fire2",
                                CubeListBuilder.create().texOffs(41, 50).mirror()
                                                .addBox(-2.0F, -5.0F, -1.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(-2.775F, 4.5F, -1.625F,
                                                0.0F, 0.2487F, 0.0F));

                body.addOrReplaceChild("body_bone1",
                                CubeListBuilder.create().texOffs(36, 38)
                                                .addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.0F, 0.9F, 0.2F,
                                                1.18386F, 0.0F, 0.0F));
                body.addOrReplaceChild("body_bone2",
                                CubeListBuilder.create().texOffs(36, 38)
                                                .addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.0F, 3.5F, 0.6F,
                                                1.18386F, 0.0F, 0.0F));
                body.addOrReplaceChild("body_bone3",
                                CubeListBuilder.create().texOffs(36, 38)
                                                .addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.0F, 6.4F, 0.0F,
                                                1.18386F, 0.0F, 0.0F));

                rightArm.addOrReplaceChild("rightArm_fire1",
                                CubeListBuilder.create().texOffs(35, 57)
                                                .addBox(-2.0F, -5.0F, -1.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(-0.75F, 3.75F, -1.9F,
                                                0.0F, 0.2531F, 0.0F));
                rightArm.addOrReplaceChild("rightArm_fire2",
                                CubeListBuilder.create().texOffs(35, 57)
                                                .addBox(-2.0F, -5.0F, -1.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(-0.25F, 3.25F, 3.35F,
                                                0.0F, -0.1396F, 0.0F));
                rightArm.addOrReplaceChild("rightArm_fire3",
                                CubeListBuilder.create().texOffs(41, 51)
                                                .addBox(-2.75F, -5.0F, -1.25F, 3, 5, 0),
                                PartPose.offsetAndRotation(-2.75F, 1.75F, -0.4F,
                                                0.0F, 1.3003F, 0.0F));
                rightArm.addOrReplaceChild("rightArm_bone1",
                                CubeListBuilder.create().texOffs(36, 38)
                                                .addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(-0.5F, -1.2F, 0.0F,
                                                -3.14159F, 0.0F, -0.17453F));
                rightArm.addOrReplaceChild("rightArm_bone2",
                                CubeListBuilder.create().texOffs(36, 38)
                                                .addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(-1.8F, -0.1F, 0.0F,
                                                -3.14159F, 0.0F, -0.26180F));

                leftArm.addOrReplaceChild("leftArm_fire1",
                                CubeListBuilder.create().texOffs(35, 57).mirror()
                                                .addBox(-2.0F, -5.0F, -1.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(1.5F, 1.5F, -1.9F,
                                                0.0F, -0.1396F, 0.0F));
                leftArm.addOrReplaceChild("leftArm_fire2",
                                CubeListBuilder.create().texOffs(35, 57).mirror()
                                                .addBox(-0.25F, -5.0F, -1.25F, 3, 5, 0),
                                PartPose.offsetAndRotation(2.75F, 1.75F, -0.4F,
                                                0.0F, -1.3003F, 0.0F));
                leftArm.addOrReplaceChild("leftArm_bone1",
                                CubeListBuilder.create().texOffs(36, 38).mirror()
                                                .addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.5F, -1.2F, 0.0F,
                                                -3.14159F, 0.0F, 0.17453F));
                leftArm.addOrReplaceChild("leftArm_bone2",
                                CubeListBuilder.create().texOffs(36, 38).mirror()
                                                .addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(1.8F, -0.1F, 0.0F,
                                                -3.14159F, 0.0F, 0.26180F));

                PartDefinition rightLeg = root.addOrReplaceChild("right_leg",
                                CubeListBuilder.create().texOffs(0, 16)
                                                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation),
                                PartPose.offset(-1.9F, 12.0F + offset, 0.0F));
                PartDefinition leftLeg = root.addOrReplaceChild("left_leg",
                                CubeListBuilder.create().texOffs(0, 16).mirror()
                                                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation),
                                PartPose.offset(1.9F, 12.0F + offset, 0.0F));

                rightLeg.addOrReplaceChild("rightLeg_fire1",
                                CubeListBuilder.create().texOffs(36, 48)
                                                .addBox(-1.0F, -5.0F, -1.0F, 2, 5, 0),
                                PartPose.offsetAndRotation(-1.625F, 8.75F, -1.625F,
                                                0.0F, 0.2487F, 0.0F));
                PartDefinition rFire2 = rightLeg.addOrReplaceChild("rightLeg_fire2",
                                CubeListBuilder.create().texOffs(42, 49)
                                                .addBox(-1.0F, -5.0F, 1.0F, 2, 5, 0),
                                PartPose.offsetAndRotation(-1.625F, 12.75F, 1.625F,
                                                0.0F, -0.2487F, 0.0F));
                rFire2.addOrReplaceChild("rightLeg_fire2_r1",
                                CubeListBuilder.create().texOffs(35, 57)
                                                .addBox(-1.0F, -2.5F, 0.0F, 2, 5, 0),
                                PartPose.offsetAndRotation(-1.847F, -5.5F, -1.571F,
                                                0.0F, -1.6144F, 0.0F));
                rightLeg.addOrReplaceChild("rightLeg_fire3",
                                CubeListBuilder.create().texOffs(35, 57)
                                                .addBox(-1.0F, -5.0F, -1.0F, 2, 5, 0),
                                PartPose.offsetAndRotation(-2.625F, 2.75F, -1.625F,
                                                0.0F, 0.2487F, 0.0F));

                rightLeg.addOrReplaceChild("rightLeg_bone1",
                                CubeListBuilder.create().texOffs(0, 34)
                                                .addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.0F, 5.0F, 0.4F,
                                                -1.41145F, 0.0F, 0.0F));
                rightLeg.addOrReplaceChild("rightLeg_bone2",
                                CubeListBuilder.create().texOffs(0, 34)
                                                .addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(-0.7F, 3.6F, -0.4F,
                                                -1.41145F, 0.0F, 0.0F));
                rightLeg.addOrReplaceChild("rightLeg_bone3",
                                CubeListBuilder.create().texOffs(0, 34)
                                                .addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(-0.8F, 0.0F, -0.8F,
                                                -1.22173F, 1.22173F, -0.17453F));

                leftLeg.addOrReplaceChild("leftLeg_fire1",
                                CubeListBuilder.create().texOffs(34, 57).mirror()
                                                .addBox(-1.0F, -5.0F, 1.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(0.875F, 9.5F, 1.625F,
                                                0.0F, 0.2487F, 0.0F));
                leftLeg.addOrReplaceChild("leftLeg_fire2",
                                CubeListBuilder.create().texOffs(41, 50).mirror()
                                                .addBox(-1.0F, -5.0F, -1.0F, 3, 5, 0),
                                PartPose.offsetAndRotation(0.875F, 11.5F, -1.625F,
                                                0.0F, -0.2487F, 0.0F));
                leftLeg.addOrReplaceChild("leftLeg_fire3",
                                CubeListBuilder.create().texOffs(42, 50)
                                                .addBox(-1.0F, -5.0F, -1.0F, 2, 5, 0),
                                PartPose.offsetAndRotation(0.575F, 2.75F, -1.625F,
                                                0.0F, -0.144F, 0.0F));
                PartDefinition lFire4 = leftLeg.addOrReplaceChild("leftLeg_fire4",
                                CubeListBuilder.create(),
                                PartPose.offset(3.472F, 7.25F, 0.054F));
                lFire4.addOrReplaceChild("leftLeg_fire4_r1",
                                CubeListBuilder.create().texOffs(42, 50).mirror()
                                                .addBox(-1.0F, -2.5F, 0.0F, 2, 5, 0),
                                PartPose.offsetAndRotation(-0.6F, 2.0F, -0.5F,
                                                0.0F, 2.0071F, 0.0F));

                leftLeg.addOrReplaceChild("leftLeg_bone1",
                                CubeListBuilder.create().texOffs(0, 34).mirror()
                                                .addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.0F, 5.0F, 0.4F,
                                                -1.41145F, 0.0F, 0.0F));
                leftLeg.addOrReplaceChild("leftLeg_bone2",
                                CubeListBuilder.create().texOffs(0, 34).mirror()
                                                .addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.7F, 3.6F, -0.4F,
                                                -1.41145F, 0.0F, 0.0F));
                leftLeg.addOrReplaceChild("leftLeg_bone3",
                                CubeListBuilder.create().texOffs(0, 34).mirror()
                                                .addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, new CubeDeformation(0.025F)),
                                PartPose.offsetAndRotation(0.8F, 0.0F, -0.8F,
                                                -1.22173F, -1.22173F, 0.17453F));

                return meshDef;
        }

        public static ModelPart getBakedModel(boolean inner) {
                return inner ? INNER_MODEL : OUTER_MODEL;
        }
}

