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
import org.endangeredplants.block.GlehniaBlock;

public class GlehniaRoot {
    // Create a Deferred Register to hold Items which will all be registered under the "endangeredplants" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Food properties for Glehnia Root
    private static final FoodProperties GLEHNIA_ROOT_FOOD = (new FoodProperties.Builder())
            .nutrition(2)  // 2饱食度
            .saturationMod(2.0f)  // 2饱和度
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 0), 1.0f)  // 15秒力量增强效果(1级)
            .build();

    // Creates a new food item with the specified food properties and right-click planting functionality
    public static final RegistryObject<Item> GLEHNIA_ROOT = ITEMS.register("glehniaroot",
            () -> new Item(new Item.Properties().food(GLEHNIA_ROOT_FOOD)) {
                @Override
                public InteractionResult useOn(UseOnContext context) {
                    // Check if the clicked block is a valid planting surface
                    if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.GRASS_BLOCK ||
                            context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.DIRT ||
                            context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.FARMLAND ||
                            context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.SAND) {

                        // Place the Glehnia block on top of the clicked block
                        context.getLevel().setBlockAndUpdate(
                                context.getClickedPos().above(),
                                GlehniaBlock.GLEHNIA_BLOCK.get().defaultBlockState()
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