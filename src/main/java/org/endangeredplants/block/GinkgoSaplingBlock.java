package org.endangeredplants.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.worldgen.tree.MaleGinkgoGrower;
import org.endangeredplants.worldgen.tree.FemaleGinkgoGrower;
import org.endangeredplants.item.GinkgoSapling;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GinkgoSaplingBlock {
    // Deferred registers for blocks and items
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Custom sapling block with random grower selection
    public static class PlantBlock extends SaplingBlock {
        private static final Random RANDOM = new Random();

        public PlantBlock(BlockBehaviour.Properties properties) {
            // Start with null grower, we'll override advanceTree method
            super(null, properties);
        }

        @Override
        public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
            // Randomly select between male and female grower
            var grower = RANDOM.nextBoolean() ? new MaleGinkgoGrower() : new FemaleGinkgoGrower();
            grower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);
        }

        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            LootParams lootparams = builder.withParameter(LootContextParams.BLOCK_STATE, state)
                    .create(LootContextParamSets.BLOCK);

            ItemStack tool = lootparams.getParameter(LootContextParams.TOOL);
            if (tool != null && tool.is(Items.SHEARS)) {
                return Collections.singletonList(new ItemStack(
                        GinkgoSapling.GINKGO_SAPLING.get()
                ));
            }
            return Collections.emptyList();
        }
    }

    // Register the sapling block
    public static final RegistryObject<Block> GINKGO_SAPLING_BLOCK = BLOCKS.register("ginkgosaplingblock",
            () -> new PlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks()
            ));

    // Register the sapling item
    public static final RegistryObject<Item> GINKGO_SAPLING_BLOCK_ITEM = ITEMS.register("ginkgosaplingblock",
            () -> new BlockItem(GINKGO_SAPLING_BLOCK.get(),
                    new Item.Properties()));

    // Registration method
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}