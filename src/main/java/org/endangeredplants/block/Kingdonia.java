package org.endangeredplants.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

import java.util.Collections;
import java.util.List;

public class Kingdonia {
    // Deferred registers for blocks and items
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Kingdonia block implementation
    public static class PlantBlock extends Block {
        private int tickCounter = 0;
        private int nextSpawnTick;

        public PlantBlock(BlockBehaviour.Properties properties) {
            super(properties);
            this.nextSpawnTick = getRandomSpawnInterval();
        }

        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            LootParams lootparams = builder.withParameter(LootContextParams.BLOCK_STATE, state)
                    .create(LootContextParamSets.BLOCK);

            ItemStack tool = lootparams.getParameter(LootContextParams.TOOL);
            if (tool != null && tool.is(Items.SHEARS)) {
                return Collections.singletonList(new ItemStack(
                        KINGDONIA_ITEM.get()
                ));
            }
            return Collections.emptyList();
        }

        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return true;
        }

        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            super.randomTick(state, level, pos, random);

            tickCounter++;

            // 检查是否到了生成时间
            if (tickCounter >= nextSpawnTick) {
                // 寻找附近曼哈顿距离为7的范围内的牛
                if (hasCowNearby(level, pos)) {
                    // 尝试在水平相邻位置生成新的Kingdonia方块
                    trySpawnNewKingdonia(level, pos, random);
                }

                // 重置计时器和下次生成时间
                tickCounter = 0;
                nextSpawnTick = getRandomSpawnInterval();
            }
        }

        private boolean hasCowNearby(Level level, BlockPos centerPos) {
            // 创建曼哈顿距离为7的搜索范围
            AABB searchArea = new AABB(
                    centerPos.getX() - 7, centerPos.getY() - 7, centerPos.getZ() - 7,
                    centerPos.getX() + 7, centerPos.getY() + 7, centerPos.getZ() + 7
            );

            // 搜索范围内的牛
            List<Cow> cows = level.getEntitiesOfClass(Cow.class, searchArea);

            // 过滤出真正在曼哈顿距离7以内的牛
            for (Cow cow : cows) {
                BlockPos cowPos = cow.blockPosition();
                int manhattanDistance = Math.abs(cowPos.getX() - centerPos.getX()) +
                        Math.abs(cowPos.getY() - centerPos.getY()) +
                        Math.abs(cowPos.getZ() - centerPos.getZ());
                if (manhattanDistance <= 7) {
                    return true;
                }
            }
            return false;
        }

        private void trySpawnNewKingdonia(ServerLevel level, BlockPos pos, RandomSource random) {
            // 水平相邻位置（东西南北）
            BlockPos[] adjacentPositions = {
                    pos.east(),
                    pos.west(),
                    pos.north(),
                    pos.south()
            };

            // 随机打乱数组顺序
            for (int i = adjacentPositions.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                BlockPos temp = adjacentPositions[i];
                adjacentPositions[i] = adjacentPositions[j];
                adjacentPositions[j] = temp;
            }

            // 尝试在每个位置生成
            for (BlockPos targetPos : adjacentPositions) {
                if (canPlaceKingdonia(level, targetPos)) {
                    level.setBlockAndUpdate(targetPos, KINGDONIA_BLOCK.get().defaultBlockState());
                    break; // 只生成一个
                }
            }
        }

        private boolean canPlaceKingdonia(Level level, BlockPos pos) {
            // 检查目标位置是否为空气
            if (!level.getBlockState(pos).isAir()) {
                return false;
            }

            // 检查下方方块是否为podzol、草方块或泥土
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            Block belowBlock = belowState.getBlock();

            return belowBlock == Blocks.PODZOL ||
                    belowBlock == Blocks.GRASS_BLOCK ||
                    belowBlock == Blocks.DIRT;
        }

        private int getRandomSpawnInterval() {
            // 生成600-1000之间的随机数
            return 6 + (int)(Math.random() * 4); // 401 = 1000 - 600 + 1
        }
    }

    // Register the Kingdonia block
    public static final RegistryObject<Block> KINGDONIA_BLOCK = BLOCKS.register("kingdonia",
            () -> new PlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks() // 启用随机tick
            ));

    // Register the Kingdonia item
    public static final RegistryObject<Item> KINGDONIA_ITEM = ITEMS.register("kingdonia",
            () -> new BlockItem(KINGDONIA_BLOCK.get(),
                    new Item.Properties()));

    // Registration method
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}