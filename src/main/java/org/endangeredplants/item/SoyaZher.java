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

public class SoyaZher {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    private static final FoodProperties SOYA_ZHER_FOOD = (new FoodProperties.Builder())
            .nutrition(2)
            .saturationMod(0.1f)
            .alwaysEat()
            .build();

    public static final RegistryObject<Item> SOYA_ZHER = ITEMS.register("soyazher",
            () -> new Item(new Item.Properties()
                    .food(SOYA_ZHER_FOOD)
                    .craftRemainder(Items.GLASS_BOTTLE)
                    .stacksTo(16)
            ) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
                    // 添加效果：60秒恶心效果（1级）
                    entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 1200)); // 1200 ticks = 60秒

                    // 添加效果：10秒发光效果
                    entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200)); // 200 ticks = 10秒

                    // 添加效果：30秒再生效果
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600)); // 600 ticks = 30秒

                    // 播放饮用音效（如果需要可以取消注释）
                    // entity.playSound(SoundEvents.GENERIC_DRINK, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);

                    // 处理玻璃瓶返还
                    ItemStack resultStack = super.finishUsingItem(stack, world, entity);

                    if (entity instanceof Player player && !player.getAbilities().instabuild) {
                        // 检查是否应该将玻璃瓶添加到库存或掉落
                        if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
                            // 如果库存已满，掉落玻璃瓶
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