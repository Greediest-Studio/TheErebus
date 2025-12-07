package erebus.client.render.entity;

import erebus.client.model.entity.ModelFireAntSoldier;
import erebus.entity.EntityAntShell;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderAntShell extends RenderLiving<EntityAntShell> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("erebus:textures/entity/ant_shell.png");

    public RenderAntShell(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelFireAntSoldier(), 0.6F);
    }

    @Override
    protected void preRenderCallback(EntityAntShell entity, float partialTickTime) {
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAntShell entity) {
        return TEXTURE;
    }
}
