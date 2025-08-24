package org.endangeredplants.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

public class RoastedGinkgoSeed {
    // 创建物品注册器
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 食物属性（2饱食度，2饱和度，5%凋零效果，100%抗性提升效果）
    private static final FoodProperties ROASTED_GINKGO_SEED_FOOD = (new FoodProperties.Builder())
            .nutrition(2)  // 2饱食度
            .saturationMod(2.0f)  // 2饱和度
            .effect(() -> new MobEffectInstance(MobEffects.WITHER, 30 * 20, 0), 0.05f)  // 5%几率30秒凋零Ⅰ效果
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 0), 1.0f)  // 100%几率30秒抗性提升Ⅰ效果
            .alwaysEat()  // 即使不饿也能吃
            .build();

    // 注册烤制银杏种子物品
    public static final RegistryObject<Item> ROASTED_GINKGO_SEED = ITEMS.register("roastedginkgoseed",
            () -> new Item(new Item.Properties().food(ROASTED_GINKGO_SEED_FOOD)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}