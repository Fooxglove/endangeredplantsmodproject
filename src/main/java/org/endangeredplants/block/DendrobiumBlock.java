package org.endangeredplants.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.endangeredplants.item.Dendrobium;
import org.endangeredplants.item.DendrobiumFlower;

import java.util.Map;
import java.util.HashMap;

public class DendrobiumBlock {
    // 定义年龄属性，范围0-3
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    // 定义开花状态
    public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");

    // 六个面的朝向属性
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 石斛藤蔓方块类
    public static class VineBlock extends Block {
        // 只有四个水平方向的碰撞箱，石斛不能附着在上下表面
        private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
        private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
        private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);

        private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = createPropertyMap();

        private static Map<Direction, BooleanProperty> createPropertyMap() {
            Map<Direction, BooleanProperty> map = new HashMap<>();
            map.put(Direction.NORTH, NORTH);
            map.put(Direction.EAST, EAST);
            map.put(Direction.SOUTH, SOUTH);
            map.put(Direction.WEST, WEST);
            // 不包含UP和DOWN，石斛不能附着在上下表面
            return map;
        }

        public VineBlock(BlockBehaviour.Properties properties) {
            super(properties);
            // 默认状态只包含水平方向，不包含UP和DOWN
            this.registerDefaultState(this.stateDefinition.any()
                    .setValue(AGE, 0)
                    .setValue(BLOOMING, false)
                    .setValue(NORTH, false)
                    .setValue(EAST, false)
                    .setValue(SOUTH, false)
                    .setValue(WEST, false));
        }

        // 添加状态定义 - 只包含水平方向
        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(AGE, BLOOMING, NORTH, EAST, SOUTH, WEST);
        }

        // 启用随机刻
        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return true; // 总是启用随机刻以支持生长和蔓延
        }

        // 随机刻更新逻辑 - 修复蔓延问题
        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            int currentAge = state.getValue(AGE);

            // 年龄增长逻辑
            if (currentAge < 3 && random.nextInt(5) == 0) { // 提高年龄增长几率
                BlockState newState = state.setValue(AGE, currentAge + 1);
                if (currentAge + 1 == 3) {
                    newState = newState.setValue(BLOOMING, true);
                }
                level.setBlock(pos, newState, 3);
            }

            // 蔓延逻辑 - 修复关键问题
            if (random.nextInt(4) == 0) { // 25%的几率尝试蔓延
                this.trySpread(state, level, pos, random);
            }
        }

        // 修复蔓延逻辑
        private void trySpread(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            // 获取当前方块附着的面
            for (Direction attachedDirection : Direction.values()) {
                BooleanProperty property = PROPERTY_BY_DIRECTION.get(attachedDirection);
                if (property != null && state.getValue(property)) {
                    // 尝试沿着这个面蔓延
                    this.spreadAlongSurface(level, pos, attachedDirection, random);
                }
            }
        }

        // 沿着表面蔓延 - 只在水平方向蔓延
        private void spreadAlongSurface(ServerLevel level, BlockPos currentPos, Direction attachedDirection, RandomSource random) {
            // 石斛只能附着在水平方向（侧面），所以只检查水平附着面
            if (attachedDirection == Direction.UP || attachedDirection == Direction.DOWN) {
                return; // 不应该有上下附着，但保险起见
            }

            BlockPos surfacePos = currentPos.relative(attachedDirection);

            // 在水平方向寻找可以蔓延的位置
            Direction[] horizontalDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
            Direction[] verticalDirections = {Direction.UP, Direction.DOWN};

            // 首先尝试水平蔓延（同一高度）
            for (Direction spreadDirection : horizontalDirections) {
                if (spreadDirection == attachedDirection.getOpposite()) continue;

                BlockPos targetPos = currentPos.relative(spreadDirection);
                if (this.tryPlaceAt(level, targetPos, random)) {
                    return;
                }
            }

            // 然后尝试垂直蔓延（向上或向下一格，但仍然附着在侧面）
            for (Direction verticalDir : verticalDirections) {
                BlockPos targetPos = currentPos.relative(verticalDir);
                if (this.tryPlaceAt(level, targetPos, random)) {
                    return;
                }
            }
        }

        // 尝试在指定位置放置石斛
        private boolean tryPlaceAt(ServerLevel level, BlockPos pos, RandomSource random) {
            if (!level.getBlockState(pos).isAir()) {
                return false;
            }

            // 检查水平方向是否有可附着的表面
            Direction[] horizontalDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

            for (Direction attachDirection : horizontalDirections) {
                if (this.canAttachTo(level, pos, attachDirection)) {
                    this.placeDendrobium(level, pos, attachDirection, random);
                    return true;
                }
            }

            return false;
        }

        // 放置新的石斛方块 - 只能附着在水平方向
        private void placeDendrobium(ServerLevel level, BlockPos pos, Direction attachDirection, RandomSource random) {
            // 确保只在水平方向附着
            if (attachDirection == Direction.UP || attachDirection == Direction.DOWN) {
                return;
            }

            BlockState newState = this.defaultBlockState()
                    .setValue(AGE, 0)
                    .setValue(BLOOMING, false);

            BooleanProperty property = PROPERTY_BY_DIRECTION.get(attachDirection);
            if (property != null) {
                newState = newState.setValue(property, true);
            }

            // 检查是否还能附着到其他水平面
            Direction[] horizontalDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
            for (Direction direction : horizontalDirections) {
                if (direction != attachDirection && this.canAttachTo(level, pos, direction)) {
                    BooleanProperty otherProperty = PROPERTY_BY_DIRECTION.get(direction);
                    if (otherProperty != null && random.nextBoolean()) { // 50%几率附着到额外的面
                        newState = newState.setValue(otherProperty, true);
                    }
                }
            }

            level.setBlock(pos, newState, 3);
        }

        // 检查是否能附着到指定方向 - 只允许水平方向附着
        private boolean canAttachTo(LevelReader level, BlockPos pos, Direction direction) {
            // 石斛不能附着在上下表面
            if (direction == Direction.UP || direction == Direction.DOWN) {
                return false;
            }

            BlockPos adjacentPos = pos.relative(direction);
            BlockState adjacentState = level.getBlockState(adjacentPos);

            // 检查相邻方块是否是固体方块
            if (adjacentState.isAir()) return false;

            // 检查面是否坚固
            return adjacentState.isFaceSturdy(level, adjacentPos, direction.getOpposite());
        }

        // 修改碰撞箱逻辑 - 只包含水平方向
        @Override
        public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            VoxelShape shape = Shapes.empty();

            if (state.getValue(NORTH)) {
                shape = Shapes.or(shape, NORTH_AABB);
            }
            if (state.getValue(SOUTH)) {
                shape = Shapes.or(shape, SOUTH_AABB);
            }
            if (state.getValue(EAST)) {
                shape = Shapes.or(shape, EAST_AABB);
            }
            if (state.getValue(WEST)) {
                shape = Shapes.or(shape, WEST_AABB);
            }

            // 如果没有任何附着面，返回一个很小的中心碰撞箱
            return shape.isEmpty() ? Block.box(7.0D, 7.0D, 7.0D, 9.0D, 9.0D, 9.0D) : shape;
        }

        // 方块更新检查 - 改进附着检查
        @Override
        public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                      LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
            // 检查当前状态是否仍然有效
            BlockState updatedState = this.updateAttachmentStates(state, level, pos);

            // 如果没有任何附着面，方块应该被破坏
            if (!this.hasAnyAttachment(updatedState)) {
                return Blocks.AIR.defaultBlockState();
            }

            return updatedState;
        }

        // 更新附着状态 - 只检查水平方向
        private BlockState updateAttachmentStates(BlockState state, LevelAccessor level, BlockPos pos) {
            BlockState newState = state;

            // 只检查水平方向的附着状态
            Direction[] horizontalDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
            for (Direction direction : horizontalDirections) {
                BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction);
                if (property != null) {
                    boolean currentlyAttached = state.getValue(property);
                    boolean canStillAttach = this.canAttachTo(level, pos, direction);

                    if (currentlyAttached && !canStillAttach) {
                        newState = newState.setValue(property, false);
                    }
                }
            }

            return newState;
        }

        // 检查是否有任何附着面 - 只检查水平方向
        private boolean hasAnyAttachment(BlockState state) {
            Direction[] horizontalDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
            for (Direction direction : horizontalDirections) {
                BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction);
                if (property != null && state.getValue(property)) {
                    return true;
                }
            }
            return false;
        }

        // 检查方块是否能存活
        public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
            return this.hasAnyAttachment(state);
        }

        // 破坏掉落逻辑
        @Override
        public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
            if (!level.isClientSide) {
                ItemStack tool = player.getMainHandItem();
                boolean isBlooming = state.getValue(BLOOMING);

                if (tool.is(Items.SHEARS)) {
                    // 剪刀破坏掉落Dendrobium物品
                    popResource(level, pos, new ItemStack(Dendrobium.DENDROBIUM.get()));
                } else if (isBlooming) {
                    // 开花状态非剪刀破坏掉落花朵
                    popResource(level, pos, new ItemStack(DendrobiumFlower.DENDROBIUM_FLOWER.get()));
                }
                // 其他情况不掉落任何物品
            }
            super.playerWillDestroy(level, pos, state, player);
        }
    }

    // 注册石斛藤蔓方块
    public static final RegistryObject<Block> DENDROBIUM_BLOCK = BLOCKS.register("dendrobiumblock",
            () -> new VineBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .randomTicks() // 启用随机刻
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> DENDROBIUM_BLOCK_ITEM = ITEMS.register(
            "dendrobiumblock",
            () -> new BlockItem(
                    DENDROBIUM_BLOCK.get(),
                    new Item.Properties()
            )
    );

    // 注册到事件总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}