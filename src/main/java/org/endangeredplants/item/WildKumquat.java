package org.endangeredplants.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.WildKumquatBlock;

public class WildKumquat {
    // Create a Deferred Register to hold Items which will all be registered under the "endangeredplants" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Food properties for Wild Kumquat
    private static final FoodProperties WILD_KUMQUAT_FOOD = (new FoodProperties.Builder())
            .nutrition(1)  // 1饱食度
            .saturationMod(1.0f)  // 1饱和度
            .build();

    // 自定义野生金柑物品类
    public static class WildKumquatItem extends Item {
        public WildKumquatItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState blockState = level.getBlockState(pos);
            Player player = context.getPlayer();
            ItemStack itemStack = context.getItemInHand();

            // 检查点击的方块是否为泥土或草方块
            if (blockState.is(net.minecraft.tags.BlockTags.DIRT) ||
                    blockState.getBlock() == net.minecraft.world.level.block.Blocks.GRASS_BLOCK) {

                BlockPos abovePos = pos.above();

                // 检查上方是否为空
                if (level.isEmptyBlock(abovePos)) {
                    if (!level.isClientSide) {
                        // 在上方生成野生金柑灌木
                        level.setBlock(abovePos, WildKumquatBlock.WILD_KUMQUAT_BLOCK.get().defaultBlockState(), 3);

                        // 消耗物品（创造模式下不消耗）
                        if (player != null && !player.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }

                        // 播放种植音效
                        level.playSound(null, abovePos, net.minecraft.sounds.SoundEvents.GRASS_PLACE,
                                net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 0.8F);
                    }

                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }

            return super.useOn(context);
        }
    }

    // Creates a new food item with the specified food properties and planting functionality
    public static final RegistryObject<Item> WILD_KUMQUAT = ITEMS.register("wildkumquat",
            () -> new WildKumquatItem(new Item.Properties().food(WILD_KUMQUAT_FOOD)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}