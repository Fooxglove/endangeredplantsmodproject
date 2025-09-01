package org.endangeredplants.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.entity.FallingPapeda;
import org.endangeredplants.item.HonghePapeda;

public class HonghePapedaBlock extends Block {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 布尔属性：hanging
    public static final BooleanProperty HANGING = BooleanProperty.create("hanging");

    // 碰撞箱形状：6x6x6像素 (0.375个方块单位)
    private static final VoxelShape HANGING_SHAPE = Block.box(5.0D, 10.0D, 5.0D, 11.0D, 16.0D, 11.0D); // 顶部中央
    private static final VoxelShape STANDING_SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);   // 底部中央

    // 注册PapedaBlock
    public static final RegistryObject<Block> HONGHE_PAPEDA_BLOCK = BLOCKS.register("honghepapedablock",
            () -> new HonghePapedaBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(0.5f) // 木质硬度
                    .sound(createCustomSoundType()) // 使用自定义声音类型
                    .randomTicks())); // 启用随机刻更新

    // 创建自定义声音类型（西瓜破坏声 + 木头其他声音）
    private static SoundType createCustomSoundType() {
        return new SoundType(1.0F, 1.0F,
                SoundEvents.SWEET_BERRY_BUSH_BREAK, // 破坏声音：浆果丛破坏声（类似西瓜）
                SoundEvents.WOOD_STEP,              // 脚步声：木头脚步声
                SoundEvents.SWEET_BERRY_BUSH_PLACE, // 放置声音：浆果丛放置声
                SoundEvents.WOOD_HIT,               // 击打声音：木头击打声
                SoundEvents.WOOD_FALL);             // 掉落声音：木头掉落声
    }

    // 注册对应的BlockItem
    public static final RegistryObject<Item> PAPEDA_BLOCK_ITEM = ITEMS.register("honghepapedablock",
            () -> new BlockItem(HONGHE_PAPEDA_BLOCK.get(), new Item.Properties()));

    public HonghePapedaBlock(BlockBehaviour.Properties properties) {
        super(properties);
        // 设置默认状态：hanging = false
        this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, false));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HANGING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        try {
            if (state.getValue(HANGING)) {
                // hanging为true时检查下方4格是否为空气
                if (checkBelowFourBlocksAir(level, pos)) {
                    // 1/200 概率掉落
                    if (random.nextInt(200) == 0) {
                        System.out.println("[DEBUG] Hanging papeda dropping at " + pos + " (4 blocks below are air)");
                        dropAsFallingEntity(level, pos, state);
                    }
                } else {
                    System.out.println("[DEBUG] Hanging papeda at " + pos + " not dropping (blocks below are not all air)");
                }
            } else {
                // hanging为false时检查下方支撑
                BlockPos belowPos = pos.below();
                BlockState belowState = level.getBlockState(belowPos);

                // 检查下方方块的上表面是否完整
                if (!hasFullTopSurface(level, belowPos, belowState)) {
                    System.out.println("[DEBUG] Non-hanging papeda losing support at " + pos);
                    dropAsFallingEntity(level, pos, state);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in randomTick: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 检查下方4格是否都是空气
     */
    private boolean checkBelowFourBlocksAir(Level level, BlockPos pos) {
        for (int i = 1; i <= 4; i++) {
            BlockPos checkPos = pos.below(i);
            BlockState checkState = level.getBlockState(checkPos);
            if (!checkState.isAir()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查方块的上表面是否完整（能够支撑其他方块）
     */
    private boolean hasFullTopSurface(Level level, BlockPos pos, BlockState state) {
        try {
            // 如果是空气方块，肯定不能支撑
            if (state.isAir()) {
                return false;
            }

            // 使用isFaceSturdy方法检查上表面是否坚固
            return state.isFaceSturdy(level, pos, Direction.UP);

        } catch (Exception e) {
            System.err.println("[ERROR] Exception in hasFullTopSurface: " + e.getMessage());
            // 发生异常时保守处理，认为不能支撑
            return false;
        }
    }

    /**
     * 将方块转换为掉落实体
     */
    private void dropAsFallingEntity(Level level, BlockPos pos, BlockState state) {
        try {
            if (!level.isClientSide) {
                System.out.println("[DEBUG] Converting block to falling entity at " + pos);

                // 创建掉落实体
                FallingPapeda fallingEntity = FallingPapeda.create(level, pos, state);

                if (fallingEntity != null) {
                    // 先移除方块再添加实体，避免冲突
                    level.removeBlock(pos, false);
                    level.addFreshEntity(fallingEntity);
                    System.out.println("[DEBUG] Successfully created falling entity");
                } else {
                    // 回退为掉落物品
                    System.out.println("[DEBUG] Failed to create entity, dropping item instead");
                    level.removeBlock(pos, false);
                    popResource(level, pos, new ItemStack(HonghePapeda.HONGHE_PAPEDA.get()));
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in dropAsFallingEntity: " + e.getMessage());
            e.printStackTrace();
            // 发生异常时直接掉落物品
            if (!level.isClientSide) {
                level.removeBlock(pos, false);
                popResource(level, pos, new ItemStack(HonghePapeda.HONGHE_PAPEDA.get()));
            }
        }
    }

    @Override
    public void playerDestroy(Level level, net.minecraft.world.entity.player.Player player, BlockPos pos, BlockState state,
                              net.minecraft.world.level.block.entity.BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        // 人为破坏时掉落Papeda物品
        if (!level.isClientSide) {
            popResource(level, pos, new ItemStack(HonghePapeda.HONGHE_PAPEDA.get()));
        }
    }

    /**
     * 检查方块是否会自然掉落（用于其他逻辑判断）
     */
    public boolean isHanging(BlockState state) {
        return state.getValue(HANGING);
    }

    /**
     * 设置hanging状态的便利方法
     */
    public BlockState setHanging(BlockState state, boolean hanging) {
        return state.setValue(HANGING, hanging);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}