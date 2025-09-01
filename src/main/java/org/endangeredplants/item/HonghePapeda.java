package org.endangeredplants.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.HonghePapedaBlock;

public class HonghePapeda {
    // Create a Deferred Register to hold Items which will all be registered under the "endangeredplants" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Food properties for Papeda
    private static final FoodProperties PAPEDA_FOOD = (new FoodProperties.Builder())
            .nutrition(4)  // 4饱食度
            .saturationMod(4.0f)  // 4饱和度
            .build();

    // Creates a new food item with the specified food properties and right-click planting functionality
    public static final RegistryObject<Item> HONGHE_PAPEDA = ITEMS.register("honghepapeda",
            () -> new Item(new Item.Properties().food(PAPEDA_FOOD)) {
                @Override
                public InteractionResult useOn(UseOnContext context) {
                    // Check if the clicked block has a full/solid top surface and the space above is empty
                    if (context.getLevel().getBlockState(context.getClickedPos()).isFaceSturdy(
                            context.getLevel(),
                            context.getClickedPos(),
                            net.minecraft.core.Direction.UP) &&
                            context.getLevel().isEmptyBlock(context.getClickedPos().above())) {

                        // Create PapedaBlock with hanging=false
                        BlockState papedaBlockState = HonghePapedaBlock.HONGHE_PAPEDA_BLOCK.get()
                                .defaultBlockState()
                                .setValue(HonghePapedaBlock.HANGING, false);

                        // Place the Papeda block on top of the clicked block
                        context.getLevel().setBlockAndUpdate(
                                context.getClickedPos().above(),
                                papedaBlockState
                        );

                        // Consume one item from the stack
                        if (!context.getPlayer().isCreative()) {
                            context.getItemInHand().shrink(1);
                        }

                        return InteractionResult.SUCCESS;
                    }
                    return super.useOn(context);
                }
            });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}