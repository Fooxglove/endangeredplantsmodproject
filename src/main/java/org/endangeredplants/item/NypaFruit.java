package org.endangeredplants.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
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
import org.endangeredplants.block.Nypa;

public class NypaFruit {
    // Create a Deferred Register to hold Items which will all be registered under the "endangeredplants" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Food properties for the nypa fruit
    private static final FoodProperties NYPA_FRUIT_FOOD = (new FoodProperties.Builder())
            .nutrition(2)  // 2饱食度
            .saturationMod(2.0f)  // 2饱和度
            .build();

    // Custom Item class for Nypa Fruit
    public static class NypaFruitItem extends Item {
        public NypaFruitItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            // Check if we're on the server side
            if (!context.getLevel().isClientSide()) {
                BlockPos clickedPos = context.getClickedPos();
                BlockState clickedBlock = context.getLevel().getBlockState(clickedPos);

                // Check if the clicked block is mud
                if (clickedBlock.getBlock() == Blocks.MUD) {
                    // Get the position above the clicked block
                    BlockPos abovePos = clickedPos.above();
                    BlockState aboveBlock = context.getLevel().getBlockState(abovePos);

                    // Check if the space above is air (or replaceable)
                    if (aboveBlock.isAir() ) {
                        // Place the NYPA block above the mud
                        context.getLevel().setBlockAndUpdate(abovePos, Nypa.NYPA.get().defaultBlockState());

                        // Consume one item from the stack
                        context.getItemInHand().shrink(1);

                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return super.useOn(context);
        }
    }

    // Register the item with food properties and custom behavior
    public static final RegistryObject<Item> NYPA_FRUIT = ITEMS.register("nypafruit",
            () -> new NypaFruitItem(new Item.Properties().food(NYPA_FRUIT_FOOD)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}