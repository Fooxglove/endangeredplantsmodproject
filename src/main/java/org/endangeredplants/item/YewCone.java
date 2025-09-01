package org.endangeredplants.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

public class YewCone {
    // 注册 YewCone 物品
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 食物属性：1 饱食度，0 饱和度
    private static final FoodProperties YEW_CONE_FOOD = (new FoodProperties.Builder())
            .nutrition(1)  // 1 点饱食度
            .saturationMod(0.0f)  // 0 饱和度
            .alwaysEat()  // 即使不饿也能吃
            .build();

    // 注册 YewCone - 使用自定义的 YewConeItem 类
    public static final RegistryObject<Item> YEW_CONE = ITEMS.register("yewcone",
            () -> new YewConeItem(new Item.Properties().food(YEW_CONE_FOOD)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    // 自定义的 YewConeItem 类，正确重写 finishUsingItem 方法
    public static class YewConeItem extends Item {
        public YewConeItem(Properties properties) {
            super(properties);
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
            // 调用父类方法处理食物消费逻辑
            ItemStack result = super.finishUsingItem(stack, level, livingEntity);

            // 检查是否是玩家并且在服务器端
            if (!level.isClientSide && livingEntity instanceof Player player) {
                // 给予玩家一个 YewSeed
                ItemStack seedStack = new ItemStack(YewSeed.YEW_SEED.get());

                // 尝试添加到玩家背包，如果背包满了就掉落在地上
                if (!player.getInventory().add(seedStack)) {
                    player.drop(seedStack, false);
                }
            }

            return result;
        }
    }
}