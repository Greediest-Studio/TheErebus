package erebus.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import erebus.ModBlocks;
import erebus.ModBlocks.IHasCustomItem;
import erebus.ModBlocks.ISubBlocksBlock;
import erebus.api.IErebusEnum;
import erebus.items.block.ItemBlockEnum;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPreservedBlock extends Block implements IHasCustomItem, ISubBlocksBlock {
	public static final PropertyEnum<EnumAmberType> TYPE = PropertyEnum.create("type", EnumAmberType.class);
	public BlockPreservedBlock() {
		super(Material.GLASS);
		setDefaultState(blockState.getBaseState().withProperty(TYPE, EnumAmberType.AMBER));
		setHardness(10F);
		setResistance(10.0F);
		setSoundType(SoundType.GLASS);
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { TYPE });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, EnumAmberType.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		EnumAmberType type = state.getValue(TYPE);
		return type.ordinal();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = world.getBlockState(pos.offset(side));
		Block block = iblockstate.getBlock();

		return block == this || block == ModBlocks.AMBER ? false : super.shouldSideBeRendered(state, world, pos, side);
	}

	@Override
	public ItemBlock getItemBlock() {
		return ItemBlockEnum.create(this, EnumAmberType.class);
	}

	@Override
	public List<String> getModels() {
		List<String> models = new ArrayList<String>();
		for (EnumAmberType type : EnumAmberType.values())
			models.add(type.getName());
		return models;
	}

	public enum EnumAmberType implements IErebusEnum {
		AMBER,
		AMBER_GLASS;

		@Override
		public ItemStack createStack(int size) {
			return new ItemStack(ModBlocks.PRESERVED_BLOCK, size, ordinal());
		}

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}
}