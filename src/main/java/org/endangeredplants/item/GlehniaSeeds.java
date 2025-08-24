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
import org.endangeredplants.block.CoastalRoseSeedling;
import org.endangeredplants.block.GlehniaBlock;

public class GlehniaSeeds {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 自定义种子物品类（保持相同逻辑）
    public static class GlehniaSeedsItem extends Item {
        public GlehniaSeedsItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockPos abovePos = pos.above();

            // 检查目标方块是否有效且上方是空气
            if ((level.getBlockState(pos).is(Blocks.DIRT) ||
                    level.getBlockState(pos).is(Blocks.GRASS_BLOCK) ||
                    level.getBlockState(pos).is(Blocks.FARMLAND) ||
                    level.getBlockState(pos).is(Blocks.SAND))) {

                if (level.getBlockState(abovePos).isAir()) {
                    // 放置方块
                    level.setBlock(abovePos, GlehniaBlock.GLEHNIA_BLOCK.get().defaultBlockState(),1);

                    // 消耗物品（创造模式除外）
                    if (!context.getPlayer().isCreative()) {
                        context.getItemInHand().shrink(1);
                    }

                    // 播放放置音效（使用更轻柔的声音）
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GRASS_PLACE,
                            net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 1.2F);

                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }

            return super.useOn(context);
        }
    }

    // 注册物品（使用自定义物品类，改为COASTAL_ROSE_SEEDS）
    public static final RegistryObject<Item> GLEHNIA_SEEDS = ITEMS.register(
            "glehniaseeds",
            () -> new GlehniaSeedsItem(new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}