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
import org.endangeredplants.item.BaishanzuFirSapling;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.minecraft.world.level.block.LeavesBlock.DISTANCE;

public class BaishanzuFirLeaves {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 静态引用到原木方块（需要在注册后初始化）
    private static RegistryObject<Block> BAISHANZU_FIR_LOG;

    public static void setLogReference(RegistryObject<Block> logBlock) {
        BAISHANZU_FIR_LOG = logBlock;
    }

    // 百山祖冷杉树叶方块类
    public static class BaishanzuLeavesBlock extends LeavesBlock {
        private static final int DECAY_RADIUS = 7;
        private static final int MAX_DISTANCE = 7;

        public BaishanzuLeavesBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        // 添加掉落物逻辑
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            LootParams lootparams = builder.withParameter(LootContextParams.BLOCK_STATE, state)
                    .create(LootContextParamSets.BLOCK);
            if (lootparams.getParameter(LootContextParams.TOOL) != null &&
                    lootparams.getParameter(LootContextParams.TOOL).is(Items.SHEARS)) {
                return Collections.singletonList(new ItemStack(this));
            }
            List<ItemStack> drops = super.getDrops(state, builder);
            if (new Random().nextFloat() < 0.05f) {
                drops.add(new ItemStack(BaishanzuFirSapling.BAISHANZU_FIR_SAPLING.get()));
            }
            return drops;
        }

        // 自定义原木检测
        private boolean isBaishanzuFirLog(BlockState state) {
            return BAISHANZU_FIR_LOG != null && state.is(BAISHANZU_FIR_LOG.get());
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

                        if (isBaishanzuFirLog(blockState)) {
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
    public static final RegistryObject<Block> BAISHANZU_FIR_LEAVES = BLOCKS.register("baishanzufirleaves",
            () -> new BaishanzuLeavesBlock(BlockBehaviour.Properties.of()
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
    public static final RegistryObject<Item> BAISHANZU_FIR_LEAVES_ITEM = ITEMS.register(
            "baishanzufirleaves",
            () -> new BlockItem(BAISHANZU_FIR_LEAVES.get(), new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}