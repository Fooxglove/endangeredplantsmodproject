package org.endangeredplants.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.SandFritillariaBlock;

public class SandFritillariaBulb {
    // Create a Deferred Register to hold Items which will all be registered under the "endangeredplants" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Food properties for Sand Fritillaria Bulb
    public static final FoodProperties SAND_FRITILLARIA_BULB_FOOD = (new FoodProperties.Builder())
            .nutrition(0)  // 0饱食度，因为只恢复生命值
            .saturationMod(0.0f)  // 0饱和度
            .fast()  // 快速食用
            .effect(() -> new MobEffectInstance(MobEffects.HEAL, 2, 0), 1.0f)  // 瞬间恢复效果(恢复4生命值)
            .alwaysEat()  // 即使饱食度满也可以食用
            .build();

    // Creates a new food item with the specified food properties and right-click planting functionality
    public static final RegistryObject<Item> SAND_FRITILLARIA_BULB = ITEMS.register("sandfritillariabulb",
            () -> new Item(new Item.Properties().food(SAND_FRITILLARIA_BULB_FOOD)) {
                @Override
                public InteractionResult useOn(UseOnContext context) {
                    // Check if the clicked block is sand (only sand)
                    if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.SAND) {

                        // Place the Sand Fritillaria block on top of the clicked block
                        context.getLevel().setBlockAndUpdate(
                                context.getClickedPos().above(),
                                SandFritillariaBlock.SAND_FRITILLARIA_BLOCK.get().defaultBlockState()
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