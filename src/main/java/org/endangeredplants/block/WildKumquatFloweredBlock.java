package org.endangeredplants.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.WildKumquatBushWithFlowers;

public class WildKumquatFloweredBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 生长阶段属性 - 改为合理的范围
    public static final IntegerProperty GROWTH_STAGE = IntegerProperty.create("growth_stage", 0, 2);

    // 自定义开花金柑灌木方块类
    public static class KumquatFloweredBushBlock extends Block {
        public KumquatFloweredBushBlock(BlockBehaviour.Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(GROWTH_STAGE, 0));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(GROWTH_STAGE);
            super.createBlockStateDefinition(builder);
        }

        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return state.getValue(GROWTH_STAGE) < 2;
        }

        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            int currentStage = state.getValue(GROWTH_STAGE);
            if (currentStage < 2 && random.nextInt(10) > 0) { // 90%的概率生长
                // 随机生长
                level.setBlock(pos, state.setValue(GROWTH_STAGE, currentStage + 1), 2);
                // 如果达到最终阶段，转换为结果状态
                if (currentStage + 1 == 2) {
                    level.setBlock(pos, WildKumquatFruitedBlock.WILD_KUMQUAT_FRUITED_BLOCK.get().defaultBlockState(), 3);
                }
            }
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack heldItem = player.getItemInHand(hand);

            // 使用玻璃瓶右键 - 获得蜂蜜瓶
            if (heldItem.getItem() == Items.GLASS_BOTTLE) {
                if (!level.isClientSide) {
                    // 消耗玻璃瓶
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    // 给予蜂蜜瓶
                    ItemStack honeyBottle = new ItemStack(Items.HONEY_BOTTLE);
                    if (!player.getInventory().add(honeyBottle)) {
                        // 如果背包满了，掉落到地上
                        popResource(level, pos, honeyBottle);
                    }

                    // 播放蜂蜜收集音效
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BOTTLE_FILL,
                            net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            return super.use(state, level, pos, player, hand, hit);
        }

        @Override
        public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
            ItemStack heldItem = player.getMainHandItem();

            // 使用剪刀破坏 - 掉落开花金柑灌木
            if (heldItem.getItem() instanceof ShearsItem) {
                popResource(level, pos, new ItemStack(WildKumquatBushWithFlowers.WILD_KUMQUAT_BUSH_WITH_FLOWERS.get(), 1));
                return;
            }

            // 空手破坏 - 造成伤害且不掉落
            if (!level.isClientSide) {
                player.hurt(level.damageSources().cactus(), 2.0F);
            }

            super.playerWillDestroy(level, pos, state, player);
        }
    }

    // 注册方块
    public static final RegistryObject<Block> WILD_KUMQUAT_FLOWERED_BLOCK = BLOCKS.register("wildkumquatfloweredblock",
            () -> new KumquatFloweredBushBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2f)
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks() // 启用随机刻更新
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> WILD_KUMQUAT_FLOWERED_BLOCK_ITEM = ITEMS.register(
            "wildkumquatfloweredblock",
            () -> new BlockItem(
                    WILD_KUMQUAT_FLOWERED_BLOCK.get(),
                    new Item.Properties()
            )
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}