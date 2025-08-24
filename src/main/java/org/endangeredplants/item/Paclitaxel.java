package org.endangeredplants.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

public class Paclitaxel {
    // 创建物品注册器
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 紫杉醇的食物属性（作为可饮用药水）
    private static final FoodProperties PACLITAXEL_FOOD = (new FoodProperties.Builder())
            .nutrition(0)  // 无饱食度恢复
            .saturationMod(0.0f)  // 无饱和度
            .alwaysEat()  // 随时可以饮用
            .build();

    // 注册紫杉醇物品
    public static final RegistryObject<Item> PACLITAXEL = ITEMS.register("paclitaxel",
            () -> new Item(new Item.Properties()
                    .food(PACLITAXEL_FOOD)
                    .craftRemainder(Items.GLASS_BOTTLE)  // 饮用后返回玻璃瓶
                    .stacksTo(1)  // 和药水一样只能堆叠1个
            ) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
                    // 移除凋零效果
                    if (entity.hasEffect(MobEffects.WITHER)) {
                        entity.removeEffect(MobEffects.WITHER);
                    }

                    // 调用父类方法处理基础逻辑（包括返回玻璃瓶）
                    return super.finishUsingItem(stack, level, entity);
                }
            });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}