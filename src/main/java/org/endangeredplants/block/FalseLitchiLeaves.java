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
import org.endangeredplants.item.FalseLitchi;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import static net.minecraft.world.level.block.LeavesBlock.DISTANCE;
import static net.minecraft.world.level.block.LeavesBlock.PERSISTENT;

public class FalseLitchiLeaves {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 直接引用假荔枝原木
    // 无需额外设置，直接使用 FalseLitchiLog 中的注册对象

    // 假荔枝树叶方块类
    public static class FalseLitchiLeavesBlock extends LeavesBlock {
        private static final int DECAY_RADIUS = 7;
        private static final int MAX_DISTANCE = 7;

        public FalseLitchiLeavesBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        // 添加掉落物逻辑
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            LootParams lootparams = builder.withParameter(LootContextParams.BLOCK_STATE, state)
                    .create(LootContextParamSets.BLOCK);

            // 如果使用剪刀，直接掉落树叶本身
            if (lootparams.getParameter(LootContextParams.TOOL) != null &&
                    lootparams.getParameter(LootContextParams.TOOL).is(Items.SHEARS)) {
                return Collections.singletonList(new ItemStack(this));
            }

            // 非剪刀破坏的情况
            List<ItemStack> drops = new ArrayList<>();

            // 50% 几率掉落 FalseLitchi
            Random random = new Random();
            if (random.nextFloat() < 0.5f) {
                drops.add(new ItemStack(FalseLitchi.FALSE_LITCHI.get()));
            }

            // 也可以添加其他默认掉落物（如树苗等）
            // 这里调用父类方法获取默认掉落物，但排除剪刀的情况
            List<ItemStack> defaultDrops = super.getDrops(state, builder);
            drops.addAll(defaultDrops);

            return drops;
        }

        // 自定义原木检测 - 直接检测所有假荔枝原木类型
        private boolean isFalseLitchiLog(BlockState state) {
            // 检测普通假荔枝原木
            if (state.is(FalseLitchiLog.FALSE_LITCHI_LOG.get())) {
                return true;
            }
            // 检测剥皮假荔枝原木
            if (state.is(FalseLitchiLog.STRIPPED_FALSE_LITCHI_LOG.get())) {
                return true;
            }
            return false;
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
                    // Update distance periodically
                    level.setBlock(pos, updateDistance(state, level, pos), 3);
                }
            }
        }

        private BlockState updateDistance(BlockState state, Level level, BlockPos pos) {
            int distance = getDistanceAt(level, pos) + 1;
            return state.setValue(DISTANCE, Math.min(distance, MAX_DISTANCE));
        }

        private int getDistanceAt(Level level, BlockPos pos) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            int distance = MAX_DISTANCE;

            // Check all blocks within 7 blocks radius (Manhattan distance)
            for (int x = -DECAY_RADIUS; x <= DECAY_RADIUS; ++x) {
                for (int y = -DECAY_RADIUS; y <= DECAY_RADIUS; ++y) {
                    for (int z = -DECAY_RADIUS; z <= DECAY_RADIUS; ++z) {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) > DECAY_RADIUS) {
                            continue; // Skip positions outside our radius
                        }

                        mutablePos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        BlockState blockState = level.getBlockState(mutablePos);

                        if (isFalseLitchiLog(blockState)) {
                            // If we find a log, calculate Manhattan distance
                            int currentDistance = Math.abs(x) + Math.abs(y) + Math.abs(z);
                            if (currentDistance < distance) {
                                distance = currentDistance;
                                if (distance == 0) {
                                    return 0; // Early exit if we find a log at the same position
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
    public static final RegistryObject<Block> FALSE_LITCHI_LEAVES = BLOCKS.register("falselitchileaves",
            () -> new FalseLitchiLeavesBlock(BlockBehaviour.Properties.of()
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
    public static final RegistryObject<Item> FALSE_LITCHI_LEAVES_ITEM = ITEMS.register(
            "falselitchileaves",
            () -> new BlockItem(FALSE_LITCHI_LEAVES.get(), new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}