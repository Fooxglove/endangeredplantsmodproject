package org.endangeredplants.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.SandFritillariaBulb;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.List;

public class SandFritillariaBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    public static class PlantBlock extends Block {
        public PlantBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            // 无论以何种方式破坏，都掉落1个沙贝母鳞茎
            return Collections.singletonList(new ItemStack(
                    SandFritillariaBulb.SAND_FRITILLARIA_BULB.get()
            ));
        }
    }

    // 注册沙贝母方块
    public static final RegistryObject<Block> SAND_FRITILLARIA_BLOCK = BLOCKS.register("sandfritillariablock",
            () -> new PlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> SAND_FRITILLARIA_BLOCK_ITEM = ITEMS.register(
            "sand_fritillaria_block",
            () -> new BlockItem(
                    SAND_FRITILLARIA_BLOCK.get(),
                    new Item.Properties()
            )
    );

    // 注册到事件总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}