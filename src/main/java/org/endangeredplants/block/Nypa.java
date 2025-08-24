package org.endangeredplants.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.CyatheaSporeBottle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Nypa {
    // 定义结果属性（布尔值）
    public static final BooleanProperty FRUITED = BooleanProperty.create("fruited");

    // 定义果实位置属性
    public static final EnumProperty<FruitPosition> FRUITPOS = EnumProperty.create("fruitpos", FruitPosition.class);

    // 果实位置枚举
    public enum FruitPosition implements StringRepresentable {
        NORTH("north"),
        SOUTH("south"),
        EAST("east"),
        WEST("west"),
        NONE("none");

        private final String name;

        FruitPosition(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        // 从方向转换为果实位置
        public static FruitPosition fromDirection(Direction direction) {
            switch (direction) {
                case NORTH: return NORTH;
                case SOUTH: return SOUTH;
                case EAST: return EAST;
                case WEST: return WEST;
                default: return NONE;
            }
        }

        // 转换为方向
        public Direction toDirection() {
            switch (this) {
                case NORTH: return Direction.NORTH;
                case SOUTH: return Direction.SOUTH;
                case EAST: return Direction.EAST;
                case WEST: return Direction.WEST;
                default: return null;
            }
        }
    }

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Nypa植物方块类
    public static class PlantBlock extends Block {
        public PlantBlock(BlockBehaviour.Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any()
                    .setValue(FRUITED, false)
                    .setValue(FRUITPOS, FruitPosition.NONE));
        }

        // 重写掉落物逻辑 - 永远不返回任何掉落物
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            return Collections.emptyList();
        }

        // 添加状态定义
        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FRUITED, FRUITPOS);
        }

        // 随机刻逻辑 - 只有在未结果时才能随机生长
        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return !state.getValue(FRUITED);
        }

        // 随机刻更新逻辑
        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            super.randomTick(state, level, pos, random);

            boolean hasFruited = state.getValue(FRUITED);

            // 只有在未结果时才会生长
            if (!hasFruited) {
                // 5000-10000刻的随机生长间隔
                // 使用概率来模拟这个时间间隔
                if (random.nextInt(10000) < 5000) { // 大约每5000-10000刻生长一次
                    // 检测周围四个方向的邻近方块是否为空气
                    List<Direction> availableDirections = new ArrayList<>();
                    Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

                    for (Direction direction : directions) {
                        BlockPos neighborPos = pos.relative(direction);
                        if (level.getBlockState(neighborPos).isAir()) {
                            availableDirections.add(direction);
                        }
                    }

                    // 如果有可用的方向，随机选择一个
                    if (!availableDirections.isEmpty()) {
                        Direction selectedDirection = availableDirections.get(random.nextInt(availableDirections.size()));
                        FruitPosition selectedFruitPos = FruitPosition.fromDirection(selectedDirection);

                        // 更新方块状态
                        BlockState newState = state.setValue(FRUITED, true).setValue(FRUITPOS, selectedFruitPos);
                        level.setBlock(pos, newState, 3);

                        // 在选定方向的邻近格子上生成果实方块
                        BlockPos fruitPos = pos.relative(selectedDirection);
                        level.setBlock(fruitPos, NypaFruitBlock.NYPA_FRUIT_BLOCK.get().defaultBlockState(), 3);
                    }
                }
            }
            // 当已结果时，isRandomlyTicking返回false，此方法不会被调用
        }


    }

    // 注册Nypa方块
    public static final RegistryObject<Block> NYPA = BLOCKS.register("nypa",
            () -> new PlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks() // 启用随机刻
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> NYPA_ITEM = ITEMS.register(
            "nypa",
            () -> new BlockItem(
                    NYPA.get(),
                    new Item.Properties()
            )
    );

    // 注册到事件总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}