package org.endangeredplants.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.GinkgoSaplingBlock;

public class GinkgoSeed {
    // 创建物品注册器
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 食物属性（2饱食度，2饱和度，带有凋零效果）
    private static final FoodProperties GINKGO_SEED_FOOD = (new FoodProperties.Builder())
            .nutrition(2)  // 2饱食度
            .saturationMod(2.0f)  // 2饱和度
            .effect(() -> new MobEffectInstance(MobEffects.WITHER, 30 * 20, 0), 1.0f)  // 30秒凋零Ⅰ效果
            .alwaysEat()  // 即使不饿也能吃
            .build();

    // 注册银杏种子物品
    public static final RegistryObject<Item> GINKGO_SEED = ITEMS.register("ginkgoseed",
            () -> new Item(new Item.Properties().food(GINKGO_SEED_FOOD)) {
                @Override
                public InteractionResult useOn(UseOnContext context) {
                    BlockState clickedBlock = context.getLevel().getBlockState(context.getClickedPos());

                    // 检查点击的方块类型（草方块、苔藓块或泥土）
                    if (clickedBlock.is(Blocks.GRASS_BLOCK) ||
                            clickedBlock.is(Blocks.MOSS_BLOCK) ||
                            clickedBlock.is(Blocks.DIRT)) {

                        // 获取上方位置
                        var posAbove = context.getClickedPos().above();

                        // 如果上方是空气，放置GinkgoSaplingBlock
                        if (context.getLevel().getBlockState(posAbove).isAir() && !context.getLevel().isClientSide) {
                            // 放置银杏树苗方块
                            context.getLevel().setBlockAndUpdate(posAbove, GinkgoSaplingBlock.GINKGO_SAPLING_BLOCK.get().defaultBlockState());

                            // 非创造模式下消耗物品
                            if (!context.getPlayer().isCreative()) {
                                context.getItemInHand().shrink(1);
                            }

                            return InteractionResult.SUCCESS;
                        }
                    }
                    return InteractionResult.PASS;
                }
            });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}