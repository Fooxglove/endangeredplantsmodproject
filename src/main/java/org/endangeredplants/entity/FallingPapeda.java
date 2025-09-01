package org.endangeredplants.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.HonghePapedaBlock;
import org.endangeredplants.item.HonghePapeda;

public class FallingPapeda extends FallingBlockEntity {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Endangeredplants.MODID);

    public static final RegistryObject<EntityType<FallingPapeda>> FALLING_PAPEDA = ENTITIES.register("fallingpapeda",
            () -> EntityType.Builder.<FallingPapeda>of(FallingPapeda::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .build("fallingpapeda"));

    private BlockState storedBlockState;
    private boolean hasLanded = false;
    private boolean customHandling = false; // 标记是否使用自定义处理

    // 标准构造函数 - 用于实体类型注册和网络同步
    public FallingPapeda(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
        this.storedBlockState = HonghePapedaBlock.HONGHE_PAPEDA_BLOCK.get().defaultBlockState();
        this.customHandling = true; // 启用自定义处理
    }

    // 创建实体的静态方法 - 这是推荐的创建方式
    public static FallingPapeda create(Level level, BlockPos pos, BlockState blockState) {
        if (level.isClientSide) return null;

        System.out.println("[DEBUG] Creating FallingPapeda at " + pos + " with state: " + blockState);

        FallingPapeda entity = new FallingPapeda(FALLING_PAPEDA.get(), level);
        // 确保传入的blockState不为null，否则使用默认状态
        entity.storedBlockState = blockState != null ? blockState : HonghePapedaBlock.HONGHE_PAPEDA_BLOCK.get().defaultBlockState();
        entity.blocksBuilding = true;
        entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        entity.setDeltaMovement(0.0D, 0.0D, 0.0D);

        return entity;
    }

    @Override
    public BlockState getBlockState() {
        return this.storedBlockState;
    }

    @Override
    public void tick() {
        if (this.isRemoved() || hasLanded) {
            return;
        }

        // 检查是否撞击生物
        if (!this.level().isClientSide && checkEntityCollision()) {
            return; // 如果撞击生物，方法内部会处理掉落和移除
        }

        // 检查是否在液体中（水或熔岩）
        if (!this.level().isClientSide && isInLiquid()) {
            System.out.println("[DEBUG] FallingPapeda entered liquid, dropping as item");
            spawnDrops(this.blockPosition());
            this.discard();
            return;
        }

        // 如果使用自定义处理，则不调用父类的tick方法来避免自动方块放置
        if (customHandling) {
            // 手动处理物理运动
            this.handlePhysics();

            // 只在服务端处理落地逻辑
            if (!this.level().isClientSide && this.onGround() && !hasLanded) {
                handleLanding();
            }
        } else {
            // 调用父类的tick来处理物理
            super.tick();

            // 只在服务端处理落地逻辑
            if (!this.level().isClientSide && this.onGround() && !hasLanded) {
                handleLanding();
            }
        }
    }

    // 检查与生物的碰撞
    private boolean checkEntityCollision() {
        AABB boundingBox = this.getBoundingBox();

        // 获取周围的生物实体
        var livingEntities = this.level().getEntitiesOfClass(LivingEntity.class, boundingBox);

        for (LivingEntity entity : livingEntities) {
            // 确保不是自己（通过UUID比较），并且生物不是观察者模式
            if (!entity.getUUID().equals(this.getUUID()) && !entity.isSpectator()) {
                // 造成4点伤害
                DamageSource damageSource = this.damageSources().fallingBlock(this);
                entity.hurt(damageSource, 4.0F);

                System.out.println("[DEBUG] FallingPapeda hit entity: " + entity.getName().getString() + " at " + entity.blockPosition());

                // 在生物位置掉落HonghePapeda
                spawnDrops(entity.blockPosition());

                // 移除掉落实体
                this.discard();
                return true;
            }
        }

        return false;
    }

    // 检查是否在液体中
    private boolean isInLiquid() {
        BlockPos pos = this.blockPosition();
        BlockState blockState = this.level().getBlockState(pos);

        // 检查当前位置是否有液体
        return blockState.getFluidState().is(FluidTags.WATER) ||
                blockState.getFluidState().is(FluidTags.LAVA) ||
                this.isInWater() || this.isInLava();
    }

    // 手动处理物理运动（简化版）
    private void handlePhysics() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }

        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));

        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
        }
    }

    // 重写父类的方法以禁用自动方块放置
    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (customHandling) {
            // 不调用父类方法，避免自动方块放置
            return;
        }
        super.checkFallDamage(y, onGround, state, pos);
    }

    private void handleLanding() {
        hasLanded = true; // 防止重复处理

        BlockPos landingPos = this.blockPosition();
        BlockPos belowPos = landingPos.below();
        BlockState belowState = this.level().getBlockState(belowPos);

        System.out.println("[DEBUG] FallingPapeda landing at " + landingPos + ", below block: " + belowState);

        try {
            // 检查是否掉落在另一个HonghePapedaBlock上
            if (belowState.getBlock() instanceof HonghePapedaBlock) {
                spawnDrops(landingPos);
                System.out.println("[DEBUG] Landed on another Papeda, dropping as item");
            }
            // 检查下方是否是完整方块
            else if (belowState.isFaceSturdy(this.level(), belowPos, Direction.UP)) {
                // 确保目标位置是空气
                if (this.level().isEmptyBlock(landingPos)) {
                    BlockState placedState = HonghePapedaBlock.HONGHE_PAPEDA_BLOCK.get()
                            .defaultBlockState()
                            .setValue(HonghePapedaBlock.HANGING, false);

                    this.level().setBlockAndUpdate(landingPos, placedState);
                    System.out.println("[DEBUG] Successfully placed block at " + landingPos);
                } else {
                    spawnDrops(landingPos);
                    System.out.println("[DEBUG] Target position not empty, dropping as item");
                }
            } else {
                spawnDrops(landingPos);
                System.out.println("[DEBUG] Below block not sturdy, dropping as item");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in handleLanding: " + e.getMessage());
            e.printStackTrace();
            spawnDrops(landingPos);
        }

        this.discard();
    }

    private void spawnDrops(BlockPos pos) {
        try {
            ItemStack dropItem = new ItemStack(HonghePapeda.HONGHE_PAPEDA.get());
            Block.popResource(this.level(), pos, dropItem);
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in spawnDrops: " + e.getMessage());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.storedBlockState != null) {
            compound.put("StoredBlockState", NbtUtils.writeBlockState(this.storedBlockState));
        }
        compound.putBoolean("HasLanded", this.hasLanded);
        compound.putBoolean("CustomHandling", this.customHandling);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("StoredBlockState", 10)) {
            try {
                this.storedBlockState = NbtUtils.readBlockState(
                        this.level().holderLookup(net.minecraft.core.registries.Registries.BLOCK),
                        compound.getCompound("StoredBlockState")
                );
            } catch (Exception e) {
                this.storedBlockState = HonghePapedaBlock.HONGHE_PAPEDA_BLOCK.get().defaultBlockState();
            }
        }
        this.hasLanded = compound.getBoolean("HasLanded");
        this.customHandling = compound.getBoolean("CustomHandling");
    }

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}