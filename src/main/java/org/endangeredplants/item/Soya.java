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
import org.endangeredplants.block.SoyaBlock;

public class Soya {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 自定义Soya物品类
    public static class SoyaItem extends Item {
        public SoyaItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockPos abovePos = pos.above();

            // 检查目标方块是否是耕地且上方是空气
            if (level.getBlockState(pos).is(Blocks.FARMLAND) &&
                    level.getBlockState(abovePos).isAir()) {

                // 在耕地上方生成SoyaBlock
                level.setBlock(abovePos, SoyaBlock.SOYA_BLOCK.get().defaultBlockState(), 1);

                // 消耗物品（创造模式除外）
                if (!context.getPlayer().isCreative()) {
                    context.getItemInHand().shrink(1);
                }

                // 播放放置音效
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GRASS_PLACE,
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 1.2F);

                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            return super.useOn(context);
        }
    }

    // 注册物品
    public static final RegistryObject<Item> SOYA = ITEMS.register(
            "soya",
            () -> new SoyaItem(new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}