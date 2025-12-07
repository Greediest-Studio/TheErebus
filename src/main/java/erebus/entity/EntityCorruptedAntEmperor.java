package erebus.entity;

import java.util.List;

import erebus.ModSounds;
import erebus.core.handler.configs.ConfigHandler;
import erebus.entity.ai.EntityAIErebusAttackMelee;
import erebus.items.ItemMaterials;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityCorruptedAntEmperor extends EntityMob {

    private final BossInfoServer bossInfo = (BossInfoServer) (new BossInfoServer(this.getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS)).setDarkenSky(false);

    private int miasmaCooldown = 160;
    private int summonCooldown = 240;
    private int chargeCooldown = 80;
    private int chargeTicks;
    private boolean pendingSlam;

    public EntityCorruptedAntEmperor(World world) {
        super(world);
        setSize(3.6F, 2.0F);
        stepHeight = 1.0F;
        experienceValue = 500;
        isImmuneToFire = true;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIErebusAttackMelee(this, 0.4D, true));
        tasks.addTask(2, new EntityAIWanderAvoidWater(this, 0.4D));
        targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        double health = ConfigHandler.INSTANCE.mobHealthMultipier < 2 ? 600D : 600D * ConfigHandler.INSTANCE.mobHealthMultipier;
        double damage = ConfigHandler.INSTANCE.mobAttackDamageMultiplier < 2 ? 12D : 12D * ConfigHandler.INSTANCE.mobAttackDamageMultiplier;
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(damage);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (getAttackTarget() != null && getAttackTarget().isEntityAlive())
            faceEntity(getAttackTarget(), 30.0F, 30.0F);

        if (!getEntityWorld().isRemote) {
            tickCooldowns();
            if (pendingSlam && onGround) {
                pendingSlam = false;
                slamAttack();
            }
        }
    }

    private void tickCooldowns() {
        if (miasmaCooldown > 0)
            --miasmaCooldown;
        if (summonCooldown > 0)
            --summonCooldown;
        if (chargeCooldown > 0)
            --chargeCooldown;
        if (chargeTicks > 0)
            --chargeTicks;

        if (miasmaCooldown <= 0) {
            performMiasmaBurst();
            miasmaCooldown = isEnraged() ? 120 : 180;
        }

        if (isEnraged() && summonCooldown <= 0) {
            summonReinforcements();
            summonCooldown = 260;
        }

        EntityLivingBase target = getAttackTarget();
        if (target != null && target.isEntityAlive() && chargeCooldown <= 0 && chargeTicks == 0) {
            double distanceSq = getDistanceSq(target);
            if (distanceSq > 36.0D && distanceSq < 225.0D && onGround) {
                performCharge(target);
                chargeCooldown = isEnraged() ? 100 : 140;
            }
        }
    }

    private boolean isEnraged() {
        return getHealth() <= getMaxHealth() * 0.5F;
    }

    private void performMiasmaBurst() {
        AxisAlignedBB area = getEntityBoundingBox().grow(12.0D, 4.0D, 12.0D);
        List<EntityLivingBase> entities = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, area);
        for (EntityLivingBase entity : entities) {
            if (entity == this)
                continue;
            if (entity instanceof EntityCorruptedAntEmperor)
                continue;
            if (entity instanceof EntityCorruptedAnt && entity.getDistanceSq(this) < 4.0D)
                continue;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 4.0F);
            entity.addPotionEffect(new PotionEffect(MobEffects.WITHER, 120, isEnraged() ? 1 : 0));
            double dx = entity.posX - posX;
            double dz = entity.posZ - posZ;
            double scale = MathHelper.sqrt(dx * dx + dz * dz);
            if (scale > 0.0D)
                entity.addVelocity(dx / scale * 0.4D, 0.2D, dz / scale * 0.4D);
        }
        playSound(ModSounds.SQUISH, 1.0F, 0.6F + rand.nextFloat() * 0.2F);
    }

    private void summonReinforcements() {
        int count = 2 + rand.nextInt(2);
        for (int i = 0; i < count; i++) {
            EntityCorruptedAnt minion = new EntityCorruptedAnt(getEntityWorld());
            double offsetX = (rand.nextDouble() - 0.5D) * 4.0D;
            double offsetZ = (rand.nextDouble() - 0.5D) * 4.0D;
            minion.setLocationAndAngles(posX + offsetX, posY, posZ + offsetZ, rand.nextFloat() * 360.0F, 0.0F);
            getEntityWorld().spawnEntity(minion);
        }
        playSound(ModSounds.FIRE_ANT_SOUND, 1.0F, 0.5F);
    }

    private void performCharge(EntityLivingBase target) {
        double dx = target.posX - posX;
        double dz = target.posZ - posZ;
        double scale = MathHelper.sqrt(dx * dx + dz * dz);
        if (scale == 0.0D)
            return;
        motionX = dx / scale * 1.35D;
        motionZ = dz / scale * 1.35D;
        motionY = 0.6D;
        pendingSlam = true;
        chargeTicks = 20;
        playSound(ModSounds.FIRE_ANT_SOUND, 1.0F, 0.4F);
    }

    private void slamAttack() {
        AxisAlignedBB area = getEntityBoundingBox().grow(3.5D, 1.0D, 3.5D);
        List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, area);
        for (EntityLivingBase entity : list) {
            if (entity == this)
                continue;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0F);
            entity.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 1));
        }
        playSound(ModSounds.SQUISH, 1.0F, 0.5F + rand.nextFloat() * 0.2F);
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        bossInfo.setPercent(getHealth() / getMaxHealth());
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean result = super.attackEntityAsMob(entityIn);
        if (result && entityIn instanceof EntityLivingBase) {
            int duration = getEntityWorld().getDifficulty() == EnumDifficulty.HARD ? 160 : 100;
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.WITHER, duration, 1));
        }
        return result;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.canHarmInCreative())
            return super.attackEntityFrom(source, amount);
        if (source.isFireDamage())
            amount *= 0.7F;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean getCanSpawnHere() {
        return false;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    public int getTotalArmorValue() {
        return 15;
    }

    @Override
    public boolean isNonBoss() {
        return false;
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
        playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.3F, 0.6F);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if (!wasRecentlyHit)
            return;
        ItemStack plates = ItemMaterials.EnumErebusMaterialsType.PLATE_ZOMBIE_ANT.createStack();
        plates.setCount(8 + rand.nextInt(6) + lootingModifier * 2);
        entityDropItem(plates, 0.0F);
        ItemStack soul = ItemMaterials.EnumErebusMaterialsType.SOUL_CRYSTAL.createStack();
        soul.setCount(1);
        entityDropItem(soul, 0.0F);
        if (rand.nextBoolean())
            entityDropItem(ItemMaterials.EnumErebusMaterialsType.ANT_PHEROMONES.createStack(), 0.0F);
    }

    @Override
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        bossInfo.removePlayer(player);
    }
}
