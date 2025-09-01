package org.endangeredplants.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

public class SoyaMilk {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    private static final FoodProperties SOYA_MILK_FOOD = (new FoodProperties.Builder())
            .nutrition(2)
            .saturationMod(0.1f)
            .alwaysEat()
            .build();

    public static final RegistryObject<Item> SOYA_MILK = ITEMS.register("soyamilk",
            () -> new Item(new Item.Properties()
                    .food(SOYA_MILK_FOOD)
                    .craftRemainder(Items.GLASS_BOTTLE)
                    .stacksTo(16)
            ) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
                    // Clear all effects like milk
                    entity.removeAllEffects();

                    // Play sound effect (you'll need to register this sound)
                    // entity.playSound(SoundEvents.GENERIC_DRINK, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);

                    // Handle glass bottle return
                    ItemStack resultStack = super.finishUsingItem(stack, world, entity);

                    if (entity instanceof Player player && !player.getAbilities().instabuild) {
                        // Check if we should add a glass bottle to inventory or drop it
                        if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
                            // If inventory is full, drop the glass bottle
                            player.drop(new ItemStack(Items.GLASS_BOTTLE), false);
                        }
                    }

                    return resultStack;
                }
            });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}