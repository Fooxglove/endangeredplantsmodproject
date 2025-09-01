package org.endangeredplants.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.WildSoya;

public class WildSoyaBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 自定义大豆方块类
    public static class SoyaBlock extends Block {
        public SoyaBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
            // 破坏时掉落2个野生大豆
            if (!level.isClientSide) {
                popResource(level, pos, new ItemStack(WildSoya.WILD_SOYA.get(), 2));
            }
            super.playerWillDestroy(level, pos, state, player);
        }
    }

    // 注册方块
    public static final RegistryObject<Block> WILD_SOYA_BLOCK = BLOCKS.register("wildsoyablock",
            () -> new SoyaBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2f)
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> WILD_SOYA_BLOCK_ITEM = ITEMS.register(
            "wildsoyablock",
            () -> new BlockItem(
                    WILD_SOYA_BLOCK.get(),
                    new Item.Properties()
            )
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}