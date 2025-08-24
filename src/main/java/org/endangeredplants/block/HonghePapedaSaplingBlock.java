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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import net.minecraft.world.level.block.grower.OakTreeGrower;
import org.endangeredplants.worldgen.tree.HongheGrower;

import java.util.Collections;
import java.util.List;

import static org.endangeredplants.item.HonghePapedaSapling.HONGHE_PAPEDA_SAPLING;

public class HonghePapedaSaplingBlock {
    // Deferred registers for blocks and items
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Tree sapling block implementation using vanilla SaplingBlock as base
    public static class PlantBlock extends SaplingBlock {
        public PlantBlock(BlockBehaviour.Properties properties) {
            super(new HongheGrower(), properties);
        }

        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            LootParams lootparams = builder.withParameter(LootContextParams.BLOCK_STATE, state)
                    .create(LootContextParamSets.BLOCK);

            ItemStack tool = lootparams.getParameter(LootContextParams.TOOL);
            if (tool != null && tool.is(Items.SHEARS)) {
                return Collections.singletonList(new ItemStack(
                        HONGHE_PAPEDA_SAPLING.get()
                ));
            }
            return Collections.emptyList();
        }
    }

    // Register the sapling block
    public static final RegistryObject<Block> HONGHE_PAPEDA_SAPLING_BLOCK = BLOCKS.register("honghepapedasaplingblock",
            () -> new PlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks()
            ));

    // Register the sapling item
    public static final RegistryObject<Item> HONGHE_PAPEDA_SAPLING_ITEM = ITEMS.register("honghepapedasaplingblock",
            () -> new BlockItem(HONGHE_PAPEDA_SAPLING_BLOCK.get(),
                    new Item.Properties()));

    // Registration method
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}