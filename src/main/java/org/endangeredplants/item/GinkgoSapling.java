package org.endangeredplants.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.GinkgoSaplingBlock;

public class GinkgoSapling {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    public static final RegistryObject<Item> GINKGO_SAPLING = ITEMS.register("ginkgosapling",
            () -> new Item(new Item.Properties()) {
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
                        if (context.getLevel().getBlockState(posAbove).isAir()) {
                            if (!context.getLevel().isClientSide()) {
                                context.getLevel().setBlockAndUpdate(posAbove,
                                        GinkgoSaplingBlock.GINKGO_SAPLING_BLOCK.get().defaultBlockState());

                                // 非创造模式下消耗物品
                                if (!context.getPlayer().isCreative()) {
                                    context.getItemInHand().shrink(1);
                                }
                            }

                            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
                        }
                    }
                    return InteractionResult.PASS;
                }
            });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}