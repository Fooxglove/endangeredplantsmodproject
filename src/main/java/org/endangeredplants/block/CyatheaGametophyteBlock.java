package org.endangeredplants.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.CyatheaGametophyte;

import java.util.Collections;
import java.util.List;

public class CyatheaGametophyteBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 桫椤配子体方块类
    public static class PlantBlock extends Block {
        public PlantBlock(BlockBehaviour.Properties properties) {
            super(properties);
            // 注册随机刻行为
            this.registerDefaultState(this.stateDefinition.any());
        }

        // 添加掉落物逻辑 - 始终掉落一个桫椤配子体
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            return Collections.singletonList(new ItemStack(
                    CyatheaGametophyte.CYATHEA_GAMETOPHYTE.get()
            ));
        }

        // 检查是否可以进行随机刻
        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return true;
        }

        // 随机刻逻辑 - 生长为CyatheaTop
        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            super.randomTick(state, level, pos, random);

            // 检查上方是否有足够空间生长
            if (level.isEmptyBlock(pos.above())) {
                // 1000~2000随机刻后生长
                if (random.nextInt(2000) < 1000) {
                    // 替换为CyatheaTop方块
                    level.setBlockAndUpdate(pos, CyatheaTop.CYATHEA_TOP.get().defaultBlockState());
                }
            }
        }
    }

    // 注册桫椤配子体方块
    public static final RegistryObject<Block> CYATHEA_GAMETOPHYTE_BLOCK = BLOCKS.register("cyatheagametophyteblock",
            () -> new PlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks()  // 启用随机刻
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> CYATHEA_GAMETOPHYTE_BLOCK_ITEM = ITEMS.register(
            "cyatheagametophyteblock",
            () -> new BlockItem(
                    CYATHEA_GAMETOPHYTE_BLOCK.get(),
                    new Item.Properties()
            )
    );

    // 注册到事件总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}