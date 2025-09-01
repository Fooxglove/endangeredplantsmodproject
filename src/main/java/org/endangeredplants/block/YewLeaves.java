package org.endangeredplants.block;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.YewCone;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.minecraft.world.level.block.LeavesBlock.DISTANCE;

public class YewLeaves {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 自定义属性
    public static final BooleanProperty FRUITED = BooleanProperty.create("fruited");

    // 静态引用到原木方块
    private static RegistryObject<Block> YEW_LOG;

    public static void setLogReference(RegistryObject<Block> logBlock) {
        YEW_LOG = logBlock;
    }

    // 紫杉树叶方块类
    public static class YewLeavesBlock extends LeavesBlock {
        private static final int DECAY_RADIUS = 7;
        private static final int MAX_DISTANCE = 7;

        public YewLeavesBlock(BlockBehaviour.Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any()
                    .setValue(DISTANCE, 7)
                    .setValue(PERSISTENT, false)
                    .setValue(FRUITED, false));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(FRUITED);
        }

        // 添加掉落物逻辑
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            LootParams lootparams = builder.withParameter(LootContextParams.BLOCK_STATE, state)
                    .create(LootContextParamSets.BLOCK);

            // 使用剪刀破坏时掉落方块本身
            if (lootparams.getParameter(LootContextParams.TOOL) != null &&
                    lootparams.getParameter(LootContextParams.TOOL).is(Items.SHEARS)) {
                return Collections.singletonList(new ItemStack(this));
            }

            // 非剪刀破坏且有果实状态时掉落1-2个YewCone
            if (state.getValue(FRUITED)) {
                Random random = new Random();
                int count = 1 + random.nextInt(2); // 1-2个
                return Collections.singletonList(new ItemStack(YewCone.YEW_CONE.get(), count));
            }

            // 其他情况不掉落
            return Collections.emptyList();
        }

        // 自定义原木检测
        private boolean isYewLog(BlockState state) {
            return YEW_LOG != null && state.is(YEW_LOG.get());
        }

        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return !state.getValue(PERSISTENT);
        }

        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            if (!state.getValue(PERSISTENT)) {
                int currentDistance = state.getValue(DISTANCE);
                if (currentDistance > MAX_DISTANCE) {
                    dropResources(state, level, pos);
                    level.removeBlock(pos, false);
                } else {
                    // 更新距离
                    level.setBlock(pos, updateDistance(state, level, pos), 3);
                }
            }

            // 随机设置果实状态
            if (random.nextFloat() < 0.05f) {
                level.setBlock(pos, state.setValue(FRUITED, !state.getValue(FRUITED)), 3);
            }
        }

        private BlockState updateDistance(BlockState state, Level level, BlockPos pos) {
            int distance = getDistanceAt(level, pos) + 1;
            return state.setValue(DISTANCE, Math.min(distance, MAX_DISTANCE));
        }

        private int getDistanceAt(Level level, BlockPos pos) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            int distance = MAX_DISTANCE;

            // 检查7格半径内的所有方块(曼哈顿距离)
            for (int x = -DECAY_RADIUS; x <= DECAY_RADIUS; ++x) {
                for (int y = -DECAY_RADIUS; y <= DECAY_RADIUS; ++y) {
                    for (int z = -DECAY_RADIUS; z <= DECAY_RADIUS; ++z) {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) > DECAY_RADIUS) {
                            continue; // 跳过半径外的位置
                        }

                        mutablePos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        BlockState blockState = level.getBlockState(mutablePos);

                        if (isYewLog(blockState)) {
                            // 如果找到原木，计算曼哈顿距离
                            int currentDistance = Math.abs(x) + Math.abs(y) + Math.abs(z);
                            if (currentDistance < distance) {
                                distance = currentDistance;
                                if (distance == 0) {
                                    return 0; // 如果在同一位置找到原木则提前返回
                                }
                            }
                        }
                    }
                }
            }

            return distance;
        }
    }

    // 注册树叶方块
    public static final RegistryObject<Block> YEW_LEAVES = BLOCKS.register("yewleaves",
            () -> new YewLeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2F)
                    .randomTicks()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)
            ));

    // 注册物品形式
    public static final RegistryObject<Item> YEW_LEAVES_ITEM = ITEMS.register(
            "yewleaves",
            () -> new BlockItem(YEW_LEAVES.get(), new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}