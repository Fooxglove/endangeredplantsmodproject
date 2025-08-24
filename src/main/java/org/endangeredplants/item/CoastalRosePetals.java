package org.endangeredplants.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

public class CoastalRosePetals {
    // Create a Deferred Register to hold Items which will all be registered under the "endangeredplants" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Food properties for the coastal rosehip
    private static final FoodProperties COASTAL_ROSE_PETALS_FOOD = (new FoodProperties.Builder())
            .nutrition(1)  // 1饱食度
            .saturationMod(3.0f)  // 3饱和度
            .build();

    // Register the item with food properties
    public static final RegistryObject<Item> COASTAL_ROSE_PETALS = ITEMS.register("coastalrosepetals",
            () -> new Item(new Item.Properties().food(COASTAL_ROSE_PETALS_FOOD)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
