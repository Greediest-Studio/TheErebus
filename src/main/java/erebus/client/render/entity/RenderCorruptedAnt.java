package erebus.client.render.entity;

import erebus.client.model.entity.ModelFireAntSoldier;
import erebus.entity.EntityCorruptedAnt;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCorruptedAnt extends RenderLiving<EntityCorruptedAnt> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("erebus:textures/entity/corrupted_ant.png");

    public RenderCorruptedAnt(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelFireAntSoldier(), 0.7F);
    }

    @Override
    protected void preRenderCallback(EntityCorruptedAnt entity, float partialTickTime) {
        GlStateManager.scale(0.9F, 0.9F, 0.9F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCorruptedAnt entity) {
        return TEXTURE;
    }
}
