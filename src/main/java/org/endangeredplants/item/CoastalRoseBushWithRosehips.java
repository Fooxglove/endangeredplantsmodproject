package org.endangeredplants.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.CoastalRosebushFloweredBlock;

public class CoastalRoseBushWithRosehips {
    // Create a Deferred Register to hold Items
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 自定义物品类，实现与方块交互逻辑
    public static class CoastalRoseBushItem extends Item {
        public CoastalRoseBushItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);
            BlockPos abovePos = pos.above();

            // 检查目标方块是否是我们需要的类型，并且上方是空气
            if ((state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.SAND)) &&
                    level.getBlockState(abovePos).isAir()) {

                // 放置带花的玫瑰灌木
                level.setBlock(abovePos, CoastalRosebushFloweredBlock.COASTAL_ROSE_BUSH_FLOWERED_BLOCK.get().defaultBlockState(), 3);

                // 消耗物品（除非在创造模式）
                if (!context.getPlayer().isCreative()) {
                    context.getItemInHand().shrink(1);
                }

                // 播放放置音效
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GRASS_PLACE,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            return super.useOn(context);
        }
    }

    // 注册物品（使用自定义物品类）
    public static final RegistryObject<Item> COASTAL_ROSE_BUSH_WITH_ROSEHIPS = ITEMS.register(
            "coastalrosebushwithrosehips",
            () -> new CoastalRoseBushItem(new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}