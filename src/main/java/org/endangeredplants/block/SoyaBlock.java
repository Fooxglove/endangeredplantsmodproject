package org.endangeredplants.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.Soya;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SoyaBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 定义生长阶段属性
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

    // SoyaBlock方块类
    public static class SoyaPlantBlock extends Block {
        public SoyaPlantBlock(BlockBehaviour.Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(AGE);
        }

        // 随机刻生长逻辑
        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            super.randomTick(state, level, pos, random);

            int currentAge = state.getValue(AGE);
            if (currentAge < 3 && level.random.nextInt(30) == 0) { // 大约每600-1000刻生长一次
                level.setBlock(pos, state.setValue(AGE, currentAge + 1), 2);
            }
        }

        // 掉落物逻辑
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            int age = state.getValue(AGE);

            // 只有完全成熟(age=3)时才掉落大豆
            if (age == 3) {
                Random random = new Random();
                int dropCount = 3 + random.nextInt(4); // 3-6个
                return Collections.singletonList(new ItemStack(Soya.SOYA.get(), dropCount));
            }

            return Collections.emptyList();
        }

        // 是否使用随机刻
        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return state.getValue(AGE) < 3;
        }
    }

    // 注册SoyaBlock方块
    public static final RegistryObject<Block> SOYA_BLOCK = BLOCKS.register("soyablock",
            () -> new SoyaPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.CROP)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks()
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> SOYA_BLOCK_ITEM = ITEMS.register(
            "soyablock",
            () -> new BlockItem(
                    SOYA_BLOCK.get(),
                    new Item.Properties()
            )
    );

    // 注册到事件总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}