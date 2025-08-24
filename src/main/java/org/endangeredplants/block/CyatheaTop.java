package org.endangeredplants.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.CyatheaSporeBottle;

import java.util.Collections;
import java.util.List;

public class CyatheaTop {
    // 定义年龄属性，范围0-4
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 桫椤顶部方块类
    public static class PlantBlock extends Block {
        public PlantBlock(BlockBehaviour.Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
        }

        // 重写掉落物逻辑 - 永远不返回任何掉落物
        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            return Collections.emptyList();
        }

        // 添加状态定义
        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(AGE);
        }

        // 随机刻逻辑 - 只有在age<4时才能随机生长
        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return state.getValue(AGE) < 4;
        }

        // 随机刻更新逻辑
        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            super.randomTick(state, level, pos, random);

            int currentAge = state.getValue(AGE);

            // 只有在age<4时才会生长
            if (currentAge < 4) {
                // 10000-20000刻的随机生长间隔
                // 使用概率来模拟这个时间间隔
                if (random.nextInt(20000) < 10000) { // 大约每10000-20000刻生长一次
                    // 生长逻辑：将自身替换为log，在上方生成新的age+1的top
                    level.setBlock(pos, CyatheaLog.CYATHEA_LOG.get().defaultBlockState(), 3);
                    BlockPos abovePos = pos.above();
                    if (level.getBlockState(abovePos).isAir()) {
                        int newAge = currentAge + 1;
                        level.setBlock(abovePos, CYATHEA_TOP.get().defaultBlockState().setValue(AGE, newAge), 3);
                    }
                }
            }
            // 当age=4时，isRandomlyTicking返回false，此方法不会被调用，树木停滞
        }

        // 处理玩家右键交互
        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack itemInHand = player.getItemInHand(hand);

            // 检查玩家手中是否持有玻璃瓶
            if (itemInHand.is(Items.GLASS_BOTTLE)) {
                if (!level.isClientSide) {
                    // 减少玻璃瓶数量
                    itemInHand.shrink(1);

                    // 给玩家孢子瓶
                    ItemStack sporeBottle = new ItemStack(CyatheaSporeBottle.CYATHEA_SPORE_BOTTLE.get());
                    if (!player.getInventory().add(sporeBottle)) {
                        // 如果背包满了，掉落到地上
                        player.drop(sporeBottle, false);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            return super.use(state, level, pos, player, hand, hit);
        }
    }

    // 注册桫椤顶部方块
    public static final RegistryObject<Block> CYATHEA_TOP = BLOCKS.register("cyatheatop",
            () -> new PlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks() // 启用随机刻
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> CYATHEA_TOP_ITEM = ITEMS.register(
            "cyatheatop",
            () -> new BlockItem(
                    CYATHEA_TOP.get(),
                    new Item.Properties()
            )
    );

    // 注册到事件总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}