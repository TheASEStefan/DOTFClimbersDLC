package net.ase.mixin.pod_infector;

import com.nyfaria.awcapi.ClimberHelper;
import com.nyfaria.awcapi.entity.ClimberComponent;
import com.nyfaria.awcapi.entity.IAdvancedClimber;
import com.nyfaria.awcapi.entity.movement.ClimberPathNavigator;
import net.asestefan.utils.SoundUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.phys.Vec3;
import net.teamabyssalofficial.entity.categories.Flammable;
import net.teamabyssalofficial.entity.custom.biomass.PodInfectorEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animatable.GeoEntity;

import javax.annotation.Nullable;
import java.util.function.Predicate;


@Mixin(PodInfectorEntity.class)
public abstract class PodInfectorFormMixin extends Monster implements IAdvancedClimber, GeoEntity, SoundUtils, Flammable
{
    @Unique
    private ClimberComponent dotfClimbersDLC$climberComponent;

    protected PodInfectorFormMixin(EntityType<? extends Monster> entityType, Level level)
    {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void dotfClimbersDLC$onConstructed(EntityType<? extends PodInfectorEntity> entityType, Level level, CallbackInfo ci)
    {
        this.dotfClimbersDLC$climberComponent = new ClimberComponent(this);
        ClimberHelper.initClimber(this);
    }

    @Inject(method = "createNavigation", at = @At("HEAD"), cancellable = true)
    private void dotfClimbersDLC$onCreateNavigation(Level level, CallbackInfoReturnable<PathNavigation> ci)
    {
        ClimberPathNavigator<PodInfectorFormMixin> navigator = new ClimberPathNavigator<>(this, level, false);
        navigator.setCanFloat(true);
        ci.setReturnValue(navigator);
    }

    @Override
    public ClimberComponent getClimberComponent()
    {
        return dotfClimbersDLC$climberComponent;
    }

    @Override
    public Mob asMob()
    {
        return this;
    }

    @Override
    public float getMovementSpeed()
    {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public float getBlockSlipperiness(BlockPos pos)
    {
        BlockState state = this.level().getBlockState(pos);
        float slipperiness = state.getBlock().getFriction() * 0.91f;
        return slipperiness;
    }

    @Override
    public boolean canClimbOnBlock(BlockState state, BlockPos pos)
    {
        return true;
    }

    @Override
    public void setLerpYRot(Float yRot)
    {
        this.lerpYRot = yRot != null ? yRot.doubleValue() : 0;
    }

    @Override
    public void setLerpXRot(Float xRot)
    {
        this.lerpXRot = xRot != null ? xRot.doubleValue() : 0;
    }

    @Override
    public void setLerpYHeadRot(Float yHeadRot)
    {

    }

    @Override
    public void setLerpHeadSteps(int steps)
    {
        this.lerpHeadSteps = steps;
    }


    @Override
    public Direction getGroundSide()
    {
        return getClimberComponent().getGroundSide();
    }

    @Override
    public void onPathingObstructed(Direction facing)
    {

    }

    @Override
    public int getMaxStuckCheckTicks()
    {
        return 40;
    }

    @Override
    public float getBridgePathingMalus(Mob entity, BlockPos pos, @Nullable Node fallPathPoint)
    {
        return -1.0f;
    }

    @Override
    public float getPathingMalus(BlockGetter cache, Mob entity, BlockPathTypes nodeType, BlockPos pos, Vec3i direction, Predicate<Direction> sides)
    {
        if (direction.getY() != 0)
        {
            boolean hasClimbableNeighbor = false;
            BlockPos.MutableBlockPos offsetPos = new BlockPos.MutableBlockPos();
            for (Direction offset : Direction.values())
            {
                if (sides.test(offset))
                {
                    offsetPos.set(pos.getX() + offset.getStepX(), pos.getY() + offset.getStepY(), pos.getZ() + offset.getStepZ());
                    BlockState state = cache.getBlockState(offsetPos);
                    if (this.canClimbOnBlock(state, offsetPos))
                    {
                        hasClimbableNeighbor = true;
                        break;
                    }
                }
            }

            if (!hasClimbableNeighbor)
            {
                return -1.0f;
            }
        }

        return entity.getPathfindingMalus(nodeType);
    }

    @Override
    public void pathFinderCleanup()
    {

    }


    /**
     * Override onClimbable to disable vanilla climbing behavior.
     * AWCAPI handles climbing differently.
     */

    @Override
    public boolean onClimbable()
    {
        return false;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void dotfClimbersDLC$onTick(CallbackInfo ci)
    {
        ClimberHelper.tickClimber(this);
    }

    @Override
    public void aiStep()
    {
        ClimberHelper.livingTickClimber(this);
        super.aiStep();
    }

    @Override
    public void move(MoverType type, Vec3 movement)
    {
        ClimberHelper.handleMove(this, type, movement, true);
        super.move(type, movement);
        ClimberHelper.handleMove(this, type, movement, false);
    }

    @Override
    public BlockPos getOnPos()
    {
        BlockPos pos = super.getOnPos();
        return ClimberHelper.getAdjustedOnPosition(this, pos);
    }

    @Override
    public void travel(Vec3 travelVector)
    {
        if (!ClimberHelper.handleTravel(this, travelVector))
        {
            super.travel(travelVector);
        }
        ClimberHelper.postTravel(this, travelVector);
    }

    @Override
    public void jumpFromGround()
    {
        if (!ClimberHelper.handleJump(this))
        {
            super.jumpFromGround();
        }
    }

    @Override
    public void lookAt(EntityAnchorArgument.Anchor anchor, Vec3 target)
    {
        Vec3 dir = target.subtract(this.position());
        dir = this.getOrientation().getLocal(dir);
        super.lookAt(anchor, this.position().add(dir));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        if (dotfClimbersDLC$climberComponent != null)
        {
            dotfClimbersDLC$climberComponent.writeToNbt(compound);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if (dotfClimbersDLC$climberComponent != null)
        {
            dotfClimbersDLC$climberComponent.readFromNbt(compound);
        }
    }
}
