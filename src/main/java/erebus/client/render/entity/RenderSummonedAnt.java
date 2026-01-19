package erebus.client.render.entity;

import erebus.client.model.entity.ModelBlackAnt;
import erebus.entity.EntitySummonedAnt;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderSummonedAnt extends RenderLiving<EntitySummonedAnt> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("erebus:textures/entity/black_ant_kit.png");

    public RenderSummonedAnt(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelBlackAnt(), 0.5F);
    }

    @Override
    protected void preRenderCallback(EntitySummonedAnt ant, float partialTickTime) {
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySummonedAnt ant) {
        return TEXTURE;
    }
}