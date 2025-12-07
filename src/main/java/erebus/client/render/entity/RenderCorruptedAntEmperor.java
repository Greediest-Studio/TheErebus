package erebus.client.render.entity;

import erebus.client.model.entity.ModelFireAntSoldier;
import erebus.entity.EntityCorruptedAntEmperor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCorruptedAntEmperor extends RenderLiving<EntityCorruptedAntEmperor> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("erebus:textures/entity/corrupted_ant_emperor.png");

    public RenderCorruptedAntEmperor(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelFireAntSoldier(), 1.2F);
    }

    @Override
    protected void preRenderCallback(EntityCorruptedAntEmperor entity, float partialTickTime) {
        GlStateManager.scale(4.0F, 4.0F, 4.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCorruptedAntEmperor entity) {
        return TEXTURE;
    }
}
