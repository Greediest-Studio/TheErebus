package erebus.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired whenever a player collides with {@code BlockMud} on the server side.
 * Listeners can mark {@link #setApplyMud(boolean) applyMud} to false to bypass
 * the normal mud slowdown so that the block behaves like a regular full block
 * (similar to having Water Striders equipped).
 */
public class PlayerMudWalkEvent extends Event {

    private final EntityPlayer player;
    private final World world;
    private final BlockPos pos;
    private boolean applyMud = true;

    public PlayerMudWalkEvent(EntityPlayer player, World world, BlockPos pos) {
        this.player = player;
        this.world = world;
        this.pos = pos;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    /**
     * Returns true when BlockMud should still apply its slowing/bracing logic.
     */
    public boolean shouldApplyMud() {
        return applyMud;
    }

    /**
     * Controls whether the mud restriction applies. Set to false to effectively treat
     * the block like a normal solid surface (no slowdown, no damage, no web state).
     */
    public void setApplyMud(boolean applyMud) {
        this.applyMud = applyMud;
    }
}