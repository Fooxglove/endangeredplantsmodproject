package org.endangeredplants.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

import java.util.function.Consumer;

public class HalluEffect {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Endangeredplants.MODID);

    public static final RegistryObject<MobEffect> HALLU = EFFECTS.register("hallu",
            () -> new HalluMobEffect(MobEffectCategory.HARMFUL, 0x4B0082)); // 靛青色

    public static void register(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
    }

    private static class HalluMobEffect extends MobEffect {
        public HalluMobEffect(MobEffectCategory category, int color) {
            super(category, color);
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity instanceof Player player) {
                // 每秒将玩家向上移动一个方块高度（20 ticks = 1秒）
                if (entity.level().getGameTime() % 2 == 0) {
                    player.setDeltaMovement(player.getDeltaMovement().x, 0.1, player.getDeltaMovement().z);
                }

                // 设置飞行速度
                player.getAbilities().setFlyingSpeed(0.05f * (amplifier + 1));
                player.onUpdateAbilities();
            }
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            // 每刻都执行效果，以实现平滑效果
            return true;
        }

        @Override
        public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
            consumer.accept(new IClientMobEffectExtensions() {
                @Override
                public boolean isVisibleInInventory(MobEffectInstance instance) {
                    return true;
                }

                @Override
                public boolean isVisibleInGui(MobEffectInstance instance) {
                    return true;
                }
            });
        }
    }
}