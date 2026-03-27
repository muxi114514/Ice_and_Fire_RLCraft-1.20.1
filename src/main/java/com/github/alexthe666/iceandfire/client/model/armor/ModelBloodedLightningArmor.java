package com.github.alexthe666.iceandfire.client.model.armor;


import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;

public class ModelBloodedLightningArmor extends ArmorModelBase {
    private static final ModelPart INNER_MODEL = createModelData(
            CubeDeformation.NONE.extend(INNER_MODEL_OFFSET), 0.0F)
            .getRoot().bake(64, 64);
    private static final ModelPart OUTER_MODEL = createModelData(
            CubeDeformation.NONE.extend(OUTER_MODEL_OFFSET), 0.0F)
            .getRoot().bake(64, 64);

    public ModelBloodedLightningArmor(boolean inner) {
        super(getBakedModel(inner));
    }

    public static MeshDefinition createModelData(CubeDeformation deformation, float offset) {
        return ModelBloodedFireArmor.createModelData(deformation, offset);
    }

    public static ModelPart getBakedModel(boolean inner) {
        return inner ? INNER_MODEL : OUTER_MODEL;
    }
}

