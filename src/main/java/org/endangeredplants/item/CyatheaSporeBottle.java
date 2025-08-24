package org.endangeredplants.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.CyatheaGametophyteBlock;

public class CyatheaSporeBottle {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 孢子瓶自定义物品类
    public static class CyatheaSporeBottleItem extends Item {
        public CyatheaSporeBottleItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockPos abovePos = pos.above();

            // 只允许在苔藓块上使用
            if (level.getBlockState(pos).is(Blocks.MOSS_BLOCK)) {
                if (level.getBlockState(abovePos).isAir()) {
                    // 放置桫椤配子体方块
                    level.setBlock(abovePos,
                            CyatheaGametophyteBlock.CYATHEA_GAMETOPHYTE_BLOCK.get().defaultBlockState(),
                            3); // 使用flag 3触发方块更新和客户端同步

                    // 消耗物品（创造模式除外）
                    if (!context.getPlayer().isCreative()) {
                        context.getItemInHand().shrink(1);
                    }



                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }

            return InteractionResult.PASS;
        }
    }

    // 注册孢子瓶物品
    public static final RegistryObject<Item> CYATHEA_SPORE_BOTTLE = ITEMS.register(
            "cyatheasporebottle",
            () -> new CyatheaSporeBottleItem(
                    new Item.Properties()
                            .stacksTo(16) // 限制堆叠数量为16
            )
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}