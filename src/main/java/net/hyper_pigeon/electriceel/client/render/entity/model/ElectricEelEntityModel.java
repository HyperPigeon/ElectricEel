package net.hyper_pigeon.electriceel.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ElectricEelEntityModel<E extends ElectricEelEntity> extends AnimalModel<ElectricEelEntity> {

    @Nullable
    private final ModelPart root;
    private final ElectricEelModelPart body;
    private final ElectricEelModelPart segment1;
    private final ElectricEelModelPart pectoralFin1;
    private final ElectricEelModelPart pectoralFin2;
    private final ElectricEelModelPart segment2;
    private final ElectricEelModelPart segment3;
    private final ElectricEelModelPart segment4;
    private final ElectricEelModelPart segment5;
    private final ElectricEelModelPart segment6;
    private final ElectricEelModelPart segment7;
    private final ElectricEelModelPart segment8;
    private final ElectricEelModelPart segment9;
    private final ElectricEelModelPart segment10;
    private final ElectricEelModelPart head;
    private final ElectricEelModelPart jaw;

    private final List<ElectricEelModelPart> bodyParts;
    private float tickDelta;



    public ElectricEelEntityModel(ModelPart root) {
        this.root = root;

        this.head = new ElectricEelModelPart(root.getChild("head"));
        this.body = new ElectricEelModelPart(root.getChild("body"));
        this.jaw = new ElectricEelModelPart(head.getModelPart().getChild("jaw"));
        this.segment1 = new ElectricEelModelPart(body.getModelPart().getChild("segment1"));
        this.pectoralFin1 = new ElectricEelModelPart(segment1.getModelPart().getChild("pectoralFin1"));
        this.pectoralFin2 = new ElectricEelModelPart(segment1.getModelPart().getChild("pectoralFin2"));
        this.segment2 =  new ElectricEelModelPart(body.getModelPart().getChild("segment2"));
        this.segment3 =  new ElectricEelModelPart(body.getModelPart().getChild("segment3"));
        this.segment4 =  new ElectricEelModelPart(body.getModelPart().getChild("segment4"));
        this.segment5 =  new ElectricEelModelPart(body.getModelPart().getChild("segment5"));
        this.segment6 =  new ElectricEelModelPart(body.getModelPart().getChild("segment6"));
        this.segment7 =  new ElectricEelModelPart(body.getModelPart().getChild("segment7"));
        this.segment8 =  new ElectricEelModelPart(body.getModelPart().getChild("segment8"));
        this.segment9 =  new ElectricEelModelPart(body.getModelPart().getChild("segment9"));
        this.segment10 =  new ElectricEelModelPart(body.getModelPart().getChild("segment10"));
        this.bodyParts = ImmutableList.of(head, segment1,segment2,segment3,segment4,segment5,segment6,segment7,segment8,segment9,segment10);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(28, 0).cuboid(-2.5F, -1.5F, -3.0F, 5.0F, 3.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 16.0F, -11.0F));

        ModelPartData jaw = head.addChild("jaw", ModelPartBuilder.create().uv(32, 32).cuboid(-2.5F, -0.5F, -4.0F, 5.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 1.0F));

        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.5F, 16.0F, -11.0F));

        ModelPartData segment1 = body.addChild("segment1", ModelPartBuilder.create().uv(14, 5).cuboid(-2.0F, -2.0F, 2.0F, 4.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(14, 0).cuboid(-0.5F, 3.0F, 5.0F, 1.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fin2_r1 = segment1.addChild("pectoralFin1", ModelPartBuilder.create().uv(22, 0).cuboid(-1.9F, -8.0F, 1.7F, 1.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, 8.0F, 2.0F, 0.0F, -0.2618F, 0.0F));

        ModelPartData fin1_r1 = segment1.addChild("pectoralFin2", ModelPartBuilder.create().uv(46, 12).cuboid(1.9F, -8.0F, 1.7F, 1.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, 8.0F, 2.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData segment2 = body.addChild("segment2", ModelPartBuilder.create().uv(0, 11).cuboid(-2.0F, -2.0F, 7.0F, 4.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(46, 32).cuboid(-0.5F, 3.0F, 7.0F, 1.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData segment3 = body.addChild("segment3", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -2.0F, 12.0F, 4.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(32, 38).cuboid(-0.5F, 3.0F, 12.0F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData segment4 = body.addChild("segment4", ModelPartBuilder.create().uv(26, 21).cuboid(-1.5F, -2.0F, 17.0F, 3.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(42, 2).cuboid(-0.5F, 3.0F, 17.0F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData segment5 = body.addChild("segment5", ModelPartBuilder.create().uv(0, 22).cuboid(-1.5F, -2.0F, 22.0F, 3.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(40, 42).cuboid(-0.5F, 3.0F, 22.0F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData segment6 = body.addChild("segment6", ModelPartBuilder.create().uv(14, 16).cuboid(-1.5F, -2.0F, 27.0F, 3.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 43).cuboid(-0.5F, 3.0F, 27.0F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData segment7 = body.addChild("segment7", ModelPartBuilder.create().uv(22, 32).cuboid(-1.0F, -2.0F, 32.0F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(19, 43).cuboid(-0.5F, 3.0F, 32.0F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData segment8 = body.addChild("segment8", ModelPartBuilder.create().uv(12, 27).cuboid(-1.0F, -2.0F, 37.0F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(46, 20).cuboid(-0.5F, 3.0F, 37.0F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData segment9 = body.addChild("segment9", ModelPartBuilder.create().uv(28, 10).cuboid(-1.0F, -2.0F, 42.0F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 33).cuboid(-0.5F, 3.0F, 42.0F, 1.0F, 3.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData segment10 = body.addChild("segment10", ModelPartBuilder.create().uv(38, 15).cuboid(-0.5F, -2.0F, 47.0F, 1.0F, 5.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-0.5F, 0.0F, 53.0F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(9, 38).cuboid(-0.5F, 3.0F, 48.0F, 1.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));


        return TexturedModelData.of(modelData, 64, 64);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.head.getModelPart().render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.body.getModelPart().render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body.getModelPart());
    }

    private float lerpAngleDegrees(float start, float end) {
        return this.lerpAngleDegrees(0.05f, start, end);
    }

    private float lerpAngleDegrees(float delta, float start, float end) {
        return MathHelper.lerpAngleDegrees(delta, start, end);
    }

    @Override
    public void animateModel(ElectricEelEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        this.tickDelta = tickDelta;
    }

    public float getEelYaw(ElectricEelEntity entity) {
        Vec3d vec3d = new Vec3d(entity.getX()-entity.getMoveControl().getTargetX(), entity.getY()-entity.getMoveControl().getTargetY(),
                entity.getZ()-entity.getMoveControl().getTargetZ());
        vec3d = vec3d.normalize();
        float yaw = (float) Math.atan2(vec3d.x,vec3d.z);
        return -1*yaw;
    }




    @Override
    public void setAngles(ElectricEelEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

        this.head.previousPitch = this.head.currentPitch;
        this.head.previousYaw = this.head.currentYaw;
        this.head.currentPitch = headPitch * ((float)Math.PI / 180);
        this.head.currentYaw = headYaw * ((float)Math.PI / 180);
        this.head.getModelPart().pitch = this.head.currentPitch;
        this.head.getModelPart().yaw = this.head.currentYaw;

        ElectricEelModelPart previousPart = this.head;
        for(int i = 1; i < bodyParts.size(); i++){
            bodyParts.get(i).previousPitch = bodyParts.get(i).currentPitch;
            bodyParts.get(i).previousYaw = bodyParts.get(i).currentYaw;
            bodyParts.get(i).currentPitch = this.lerpAngleDegrees(0.10f,bodyParts.get(i).previousPitch,previousPart.previousPitch);
            bodyParts.get(i).currentYaw = this.lerpAngleDegrees(0.10f,bodyParts.get(i).previousYaw,previousPart.previousYaw);
            bodyParts.get(i).getModelPart().pitch = bodyParts.get(i).currentPitch;
            bodyParts.get(i).getModelPart().yaw = bodyParts.get(i).currentYaw;
            previousPart = bodyParts.get(i);
        }

    }

    private class ElectricEelModelPart {
        private ModelPart modelPart;
        public float previousPitch = 0;
        public float currentPitch = 0;
        public float previousYaw = 0;
        public float currentYaw = 0;

        public ElectricEelModelPart(ModelPart modelPart){
            this.modelPart = modelPart;
        }

        public ModelPart getModelPart(){
            return modelPart;
        }
    }

}