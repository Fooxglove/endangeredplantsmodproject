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
import org.endangeredplants.item.ZangnanCupressusSapling;
import org.endangeredplants.item.ZangnanCupressusCone; // 假设ZangnanCupressusCone在ModItems中

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.minecraft.world.level.block.LeavesBlock.DISTANCE;

public class ZangnanCupressusLeaves {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Static reference to log block (needs to be initialized after registration)
    private static RegistryObject<Block> ZANGNAN_CUPRESSUS_LOG;

    public static void setLogReference(RegistryObject<Block> logBlock) {
        ZANGNAN_CUPRESSUS_LOG = logBlock;
    }

    // Zangnan Cupressus Leaves Block class
    public static class ZangnanCupressusLeavesBlock extends LeavesBlock {
        private static final int DECAY_RADIUS = 7;
        private static final int MAX_DISTANCE = 7;

        public ZangnanCupressusLeavesBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        // Add drop logic
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            LootParams lootparams = builder.withParameter(LootContextParams.BLOCK_STATE, state)
                    .create(LootContextParamSets.BLOCK);

            // 使用剪刀破坏时掉落树叶方块
            if (lootparams.getParameter(LootContextParams.TOOL) != null &&
                    lootparams.getParameter(LootContextParams.TOOL).is(Items.SHEARS)) {
                return Collections.singletonList(new ItemStack(this));
            }

            // 不使用剪刀时，20%几率掉落ZangnanCupressusCone
            List<ItemStack> drops = super.getDrops(state, builder);
            if (new Random().nextFloat() < 0.2f) {
                drops.add(new ItemStack(ZangnanCupressusCone.ZANGNAN_CUPRESSUS_CONE.get()));
            }
            // 移除树苗掉落
            drops.removeIf(stack -> stack.getItem() == ZangnanCupressusSapling.ZANGNAN_CUPRESSUS_SAPLING.get());
            return drops;
        }

        // Custom log detection
        private boolean isZangnanCupressusLog(BlockState state) {
            return ZANGNAN_CUPRESSUS_LOG != null && state.is(ZANGNAN_CUPRESSUS_LOG.get());
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

                        if (isZangnanCupressusLog(blockState)) {
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

    // Register leaves block
    public static final RegistryObject<Block> ZANGNAN_CUPRESSUS_LEAVES = BLOCKS.register("zangnancupressusleaves",
            () -> new ZangnanCupressusLeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2F)
                    .randomTicks()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)
            ));

    // Register item form
    public static final RegistryObject<Item> ZANGNAN_CUPRESSUS_LEAVES_ITEM = ITEMS.register(
            "zangnancupressusleaves",
            () -> new BlockItem(ZANGNAN_CUPRESSUS_LEAVES.get(), new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}