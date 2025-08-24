package org.endangeredplants.item;

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
import org.endangeredplants.block.CoastalRosebushBlock;

public class CoastalRoseBushWithoutRosehips {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 自定义物品类，实现与方块交互逻辑
    public static class CoastalRoseBushWithoutHipsItem extends Item {
        public CoastalRoseBushWithoutHipsItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);
            BlockPos abovePos = pos.above();

            // 检查目标方块是否是我们需要的类型，并且上方是空气
            if ((state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.SAND))) {
                if (level.getBlockState(abovePos).isAir()) {
                    // 放置基础玫瑰灌木（初始生长阶段为0）
                    level.setBlock(abovePos,
                            CoastalRosebushBlock.COASTAL_ROSE_BUSH_BLOCK.get()
                                    .defaultBlockState()
                                    .setValue(CoastalRosebushBlock.GROWTH_STAGE, 0),
                            3);

                    // 消耗物品（除非在创造模式）
                    if (!context.getPlayer().isCreative()) {
                        context.getItemInHand().shrink(1);
                    }

                    // 播放放置音效
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GRASS_PLACE,
                            net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }

            return super.useOn(context);
        }
    }

    // 注册物品（使用自定义物品类）
    public static final RegistryObject<Item> COASTAL_ROSE_BUSH_WITHOUT_ROSEHIPS = ITEMS.register(
            "coastalrosebushwithoutrosehips",
            () -> new CoastalRoseBushWithoutHipsItem(new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}