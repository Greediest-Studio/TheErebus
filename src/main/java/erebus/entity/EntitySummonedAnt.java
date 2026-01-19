package erebus.entity;

import erebus.ModItems;
import erebus.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.google.common.base.Predicate;

public class EntitySummonedAnt extends EntityTameable {

    private static final int DESPAWN_TICKS = 20 * 60;

    public EntitySummonedAnt(World world) {
        super(world);
        setSize(0.9F, 0.4F);
        setPathPriority(PathNodeType.WATER, -8F);
        stepHeight = 1.0F;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIAttackMelee(this, 0.7D, true));
        tasks.addTask(2, new EntityAIWander(this, 0.6D));
        tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(4, new EntityAILookIdle(this));
        tasks.addTask(5, new EntityAITempt(this, 0.6D, ModItems.ANT_TAMING_AMULET, false));
        tasks.addTask(6, new EntityAITempt(this, 0.6D, Items.SUGAR, false));
        targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, new Predicate<EntityLivingBase>() {
            @Override
            public boolean apply(EntityLivingBase target) {
                if (target instanceof EntityPlayer)
                    return false;
                if (target instanceof EntitySummonedAnt)
                    return false;
                EntityLivingBase owner = EntitySummonedAnt.this.getOwner();
                if (owner != null && target == owner)
                    return false;
                if (owner != null && target instanceof EntityTameable) {
                    EntityTameable tameable = (EntityTameable) target;
                    if (owner.equals(tameable.getOwner()))
                        return false;
                }
                return true;
            }
        }));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
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
        this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!getEntityWorld().isRemote && ticksExisted >= DESPAWN_TICKS) {
            setDead();
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (entityIn instanceof EntityPlayer)
            return false;
        float damage = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        boolean attacked = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
        if (attacked) {
            applyEnchantments(this, entityIn);
        }
        return attacked;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItem() == ModItems.ANT_TAMING_AMULET && !isTamed()) {
            if (!getEntityWorld().isRemote) {
                setTamed(true);
                playTameEffect(true);
            }
            player.swingArm(hand);
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable baby) {
        return null;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }
}