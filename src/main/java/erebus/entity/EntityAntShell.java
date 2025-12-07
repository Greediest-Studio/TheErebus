package erebus.entity;

import erebus.ModSounds;
import erebus.core.handler.configs.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAntShell extends EntityMob {

    public EntityAntShell(World world) {
        super(world);
        setSize(0.9F, 0.5F);
        stepHeight = 0.1F;
        setNoAI(true);
        enablePersistence();
    }

    @Override
    protected void initEntityAI() {
        // No AI - behaves as a stationary shell
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        double health = ConfigHandler.INSTANCE.mobHealthMultipier < 2 ? 1000D : 1000D * ConfigHandler.INSTANCE.mobHealthMultipier;
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        return false;
    }

    @Override
    public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
        // immune to knockback
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == DamageSource.FALL || source == DamageSource.DROWN || source == DamageSource.LIGHTNING_BOLT)
            return false;
        if (source.isMagicDamage())
            return false;
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
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        // silent
    }
}
