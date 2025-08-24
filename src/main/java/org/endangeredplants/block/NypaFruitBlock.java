package org.endangeredplants.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.NypaFruit;

import java.util.ArrayList;
import java.util.List;

public class NypaFruitBlock {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Nypa果实方块类
    public static class FruitBlock extends Block {
        public FruitBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        // 自定义掉落物逻辑
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            List<ItemStack> drops = new ArrayList<>();
            return drops; // 返回空列表，因为我们将在playerDestroy中处理掉落
        }

        // 玩家破坏方块时的处理
        @Override
        public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                                  @javax.annotation.Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity,
                                  ItemStack tool) {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);

            if (!level.isClientSide) {
                // 确保在服务端处理掉落物
                int dropCount;
                if (tool.is(ItemTags.AXES)) {
                    dropCount = 16;
                } else {
                    dropCount = 4 + level.random.nextInt(2); // 4-5个
                }

                // 生成掉落物
                ItemStack dropStack = new ItemStack(NypaFruit.NYPA_FRUIT.get(), dropCount);
                popResource(level, pos, dropStack);
            }
        }
    }

    // 注册Nypa果实方块
    public static final RegistryObject<Block> NYPA_FRUIT_BLOCK = BLOCKS.register("nypafruitblock",
            () -> new FruitBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0f, 3.0f) // 木头硬度
                    .sound(net.minecraft.world.level.block.SoundType.WOOD) // 木头音效
                    .noOcclusion()
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> NYPA_FRUIT_BLOCK_ITEM = ITEMS.register(
            "nypafruitblock",
            () -> new BlockItem(
                    NYPA_FRUIT_BLOCK.get(),
                    new Item.Properties()
            )
    );

    // 注册到事件总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}