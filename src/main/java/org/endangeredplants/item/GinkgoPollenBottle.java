package org.endangeredplants.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.GinkgoLeaves;

public class GinkgoPollenBottle {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 花粉瓶自定义物品类
    public static class GinkgoPollenBottleItem extends Item {
        public GinkgoPollenBottleItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState blockState = level.getBlockState(pos);

            // 检查是否点击的是雌性银杏叶
            if (blockState.getBlock() == GinkgoLeaves.GINKGO_FEMALE_LEAVES.get()) {
                // 设置haspollen属性为true
                BlockState newState = blockState.setValue(GinkgoLeaves.HAS_POLLEN, true);
                level.setBlock(pos, newState, 3); // 使用flag 3触发方块更新和客户端同步

                // 消耗花粉瓶物品（创造模式除外）
                if (!context.getPlayer().isCreative()) {
                    context.getItemInHand().shrink(1);
                }

                // 给予玩家空玻璃瓶
                if (!context.getPlayer().getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
                    // 如果背包满了，掉落在地上
                    context.getPlayer().drop(new ItemStack(Items.GLASS_BOTTLE), false);
                }

                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            return InteractionResult.PASS;
        }
    }

    // 注册花粉瓶物品
    public static final RegistryObject<Item> GINKGO_POLLEN_BOTTLE = ITEMS.register(
            "ginkgopollenbottle",
            () -> new GinkgoPollenBottleItem(
                    new Item.Properties()
                            .stacksTo(16) // 限制堆叠数量为16
            )
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}