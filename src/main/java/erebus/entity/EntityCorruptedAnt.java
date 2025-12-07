package erebus.entity;

import erebus.ModSounds;
import erebus.core.handler.configs.ConfigHandler;
import erebus.entity.ai.EntityAIErebusAttackMelee;
import erebus.items.ItemMaterials;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class EntityCorruptedAnt extends EntityMob {

    public EntityCorruptedAnt(World world) {
        super(world);
        stepHeight = 0.1F;
        setSize(0.9F, 0.5F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIErebusAttackMelee(this, 0.5D, true));
        tasks.addTask(2, new EntityAILookIdle(this));
        tasks.addTask(3, new EntityAIWanderAvoidWater(this, 0.6D));
        targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ConfigHandler.INSTANCE.mobHealthMultipier < 2 ? 30D : 30D * ConfigHandler.INSTANCE.mobHealthMultipier);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ConfigHandler.INSTANCE.mobAttackDamageMultiplier < 2 ? 3D : 3D * ConfigHandler.INSTANCE.mobAttackDamageMultiplier);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
    }

    @Override
    public int getTotalArmorValue() {
        return 6;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FIRE_ANT_SOUND;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.FIRE_ANT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SQUISH;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean getCanSpawnHere() {
        float light = getBrightness();
        if (light >= 0F)
            return isNotColliding();
        return super.getCanSpawnHere();
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 5;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int amount = 1 + rand.nextInt(3) + rand.nextInt(1 + looting);
        for (int a = 0; a < amount; ++a)
            entityDropItem(ItemMaterials.EnumErebusMaterialsType.PLATE_ZOMBIE_ANT.createStack(), 0.0F);
        if (rand.nextInt(5) == 0)
            entityDropItem(ItemMaterials.EnumErebusMaterialsType.ANT_PHEROMONES.createStack(), 0.0F);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean attacked = super.attackEntityAsMob(entityIn);
        if (attacked && entityIn instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase) entityIn;
            target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 0));
        }
        return attacked;
    }
}
