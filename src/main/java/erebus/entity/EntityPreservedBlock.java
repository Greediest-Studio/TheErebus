package erebus.entity;

import erebus.ModBlocks;
import erebus.api.ErebusAPI;
import erebus.blocks.BlockPreservedBlock;
import erebus.blocks.BlockPreservedBlock.EnumAmberType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityPreservedBlock extends EntityThrowable {

	public EntityPreservedBlock(World world) {
		super(world);
	}

	public EntityPreservedBlock(World world, EntityLivingBase entity) {
		super(world, entity);
	}

	public EntityPreservedBlock(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	public void onImpact(RayTraceResult mop) {
		if (getEntityWorld().isRemote)
			return;
		BlockPos pos = new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ));

		if (mop.entityHit != null && !(mop.entityHit instanceof EntityPlayer)) {
			if (canTrap(mop.entityHit)) {
				getEntityWorld().setBlockState(pos, ModBlocks.PRESERVED_BLOCK.getDefaultState().withProperty(BlockPreservedBlock.TYPE, EnumAmberType.AMBER_GLASS), 3);
				mop.entityHit.setDead();
			}
		} else if (mop.entityHit == null && ModBlocks.AMBER.canPlaceBlockAt(getEntityWorld(), pos))
			getEntityWorld().setBlockState(pos, ModBlocks.AMBER.getDefaultState(), 2);

		setDead();
	}

	private boolean canTrap(Entity entity) {
		return ErebusAPI.preservableEntityRegistry.canBePreserved(entity);
	}
}