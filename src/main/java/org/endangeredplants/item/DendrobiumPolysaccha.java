package org.endangeredplants.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

public class DendrobiumPolysaccha {
    // Create a Deferred Register to hold Items which will all be registered under the "endangeredplants" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Food properties for Dendrobium Polysaccha (treated as a drinkable potion)
    private static final FoodProperties DENDROBIUM_POLYSACCHA_FOOD = (new FoodProperties.Builder())
            .nutrition(0)  // No hunger restoration
            .saturationMod(0.0f)  // No saturation
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 8 * 60 * 20, 0), 1.0f)  // 8 minutes of Resistance effect
            .alwaysEat()  // Can always be consumed
            .build();

    // Creates a new potion-like item with the specified food properties
    public static final RegistryObject<Item> DENDROBIUM_POLYSACCHA = ITEMS.register("dendrobiumpolysaccha",
            () -> new Item(new Item.Properties()
                    .food(DENDROBIUM_POLYSACCHA_FOOD)
                    .craftRemainder(Items.GLASS_BOTTLE)  // Returns a glass bottle after consumption
                    .stacksTo(1)  // Stack size of 1 like most potions
            ) {
                // You can add additional behavior here if needed
            });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}