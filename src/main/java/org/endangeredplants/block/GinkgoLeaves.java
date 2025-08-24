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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.GinkgoCone;
import org.endangeredplants.item.GinkgoLeaf;
import org.endangeredplants.item.GinkgoPollenBottle;

import java.util.Collections;
import java.util.List;

import static net.minecraft.world.level.block.LeavesBlock.DISTANCE;

public class GinkgoLeaves {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // New properties for states
    public static final BooleanProperty FRUITED = BooleanProperty.create("fruited");
    public static final BooleanProperty HAS_POLLEN = BooleanProperty.create("haspollen");

    // Ginkgo Leaves Block class
    public static class GinkgoLeavesBlock extends LeavesBlock {
        private static final int DECAY_RADIUS = 7;
        private static final int MAX_DISTANCE = 7;
        private final boolean isMale;


        public GinkgoLeavesBlock(BlockBehaviour.Properties properties, boolean isMale) {
            super(properties);
            this.isMale = isMale;
            this.registerDefaultState(this.stateDefinition.any()
                    .setValue(DISTANCE, 7)
                    .setValue(PERSISTENT, false)
                    .setValue(FRUITED, false)
                    .setValue(HAS_POLLEN, false));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(FRUITED, HAS_POLLEN);
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack itemInHand = player.getItemInHand(hand);

            // 检查是否是雄性银杏叶且玩家手持空玻璃瓶
            if (isMale && itemInHand.is(Items.GLASS_BOTTLE)) {
                if (!level.isClientSide) {
                    // 消耗一个玻璃瓶
                    itemInHand.shrink(1);

                    // 给予玩家银杏花粉瓶
                    ItemStack pollenBottle = new ItemStack(GinkgoPollenBottle.GINKGO_POLLEN_BOTTLE.get());
                    if (!player.getInventory().add(pollenBottle)) {
                        // 如果背包满了，掉落在地上
                        player.drop(pollenBottle, false);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            return InteractionResult.PASS;
        }

        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            LootParams lootparams = builder.withParameter(LootContextParams.BLOCK_STATE, state)
                    .create(LootContextParamSets.BLOCK);

            if (lootparams.getParameter(LootContextParams.TOOL) != null &&
                    lootparams.getParameter(LootContextParams.TOOL).is(Items.SHEARS)) {
                return Collections.singletonList(new ItemStack(this));
            }

            if (state.getValue(FRUITED)) {
                return Collections.singletonList(new ItemStack(GinkgoCone.GINKGO_CONE.get()));
            } else {
                return Collections.singletonList(new ItemStack(GinkgoLeaf.GINKGO_LEAF.get()));
            }
        }

        private boolean isGinkgoLog(BlockState state) {
            return state.is(GinkgoLog.GINKGO_LOG.get());
        }

        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return !state.getValue(PERSISTENT) || (!isMale && !state.getValue(FRUITED));
        }

        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            if (!state.getValue(PERSISTENT)) {
                int currentDistance = state.getValue(DISTANCE);
                if (currentDistance > MAX_DISTANCE) {
                    dropResources(state, level, pos);
                    level.removeBlock(pos, false);
                } else {
                    level.setBlock(pos, updateDistance(state, level, pos), 3);
                }
            }

            // 修改这部分逻辑 - 添加更严格的条件和延迟
            if (!isMale && !state.getValue(FRUITED)) {
                // 只有在明确有花粉的情况下才有可能结果
                if (state.getValue(HAS_POLLEN)) {
                    // 增加随机性，只有很小的概率会结果
                    if (random.nextFloat() < 0.5f) {
                        level.scheduleTick(pos, this, 200 + random.nextInt(800)); // 更长的延迟
                    }
                } else if (hasNearbyMaleLeaves(level, pos)) {
                    // 如果附近有雄性树叶，先设置为有花粉状态，但不立即结果
                    if (random.nextFloat() < 0.5f) { // 5% 的概率获得花粉
                        level.setBlock(pos, state.setValue(HAS_POLLEN, true), 3);
                    }
                }
            }
        }

        @Override
        public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            if (!isMale && !state.getValue(FRUITED) && state.getValue(HAS_POLLEN)) {
                // 再次检查随机性，确保不是每次都结果
                if (random.nextFloat() < 0.8f) { // 80% 的概率结果
                    level.setBlock(pos, state.setValue(FRUITED, true), 3);
                }
            }
        }

        private boolean hasNearbyMaleLeaves(Level level, BlockPos pos) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int x = -10; x <= 10; ++x) {
                for (int y = -10; y <= 10; ++y) {
                    for (int z = -10; z <= 10; ++z) {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) > 10) {
                            continue;
                        }

                        mutablePos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        BlockState blockState = level.getBlockState(mutablePos);

                        if (blockState.getBlock() == GINKGO_MALE_LEAVES.get()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private BlockState updateDistance(BlockState state, Level level, BlockPos pos) {
            int distance = getDistanceAt(level, pos) + 1;
            return state.setValue(DISTANCE, Math.min(distance, MAX_DISTANCE));
        }

        private int getDistanceAt(Level level, BlockPos pos) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            int distance = MAX_DISTANCE;

            for (int x = -DECAY_RADIUS; x <= DECAY_RADIUS; ++x) {
                for (int y = -DECAY_RADIUS; y <= DECAY_RADIUS; ++y) {
                    for (int z = -DECAY_RADIUS; z <= DECAY_RADIUS; ++z) {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) > DECAY_RADIUS) {
                            continue;
                        }

                        mutablePos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        BlockState blockState = level.getBlockState(mutablePos);

                        if (isGinkgoLog(blockState)) {
                            int currentDistance = Math.abs(x) + Math.abs(y) + Math.abs(z);
                            if (currentDistance < distance) {
                                distance = currentDistance;
                                if (distance == 0) {
                                    return 0;
                                }
                            }
                        }
                    }
                }
            }

            return distance;
        }
    }

    public static final RegistryObject<Block> GINKGO_MALE_LEAVES = BLOCKS.register("ginkgomaleleaves",
            () -> new GinkgoLeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2F)
                    .randomTicks()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false),
                    true));

    public static final RegistryObject<Block> GINKGO_FEMALE_LEAVES = BLOCKS.register("ginkgofemaleleaves",
            () -> new GinkgoLeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2F)
                    .randomTicks()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false),
                    false));

    public static final RegistryObject<Item> GINKGO_MALE_LEAVES_ITEM = ITEMS.register(
            "ginkgomaleleaves",
            () -> new BlockItem(GINKGO_MALE_LEAVES.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> GINKGO_FEMALE_LEAVES_ITEM = ITEMS.register(
            "ginkgofemaleleaves",
            () -> new BlockItem(GINKGO_FEMALE_LEAVES.get(), new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}