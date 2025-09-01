package org.endangeredplants.item;

import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.FalseLitchiSeedlingBlock;
import org.endangeredplants.effect.HalluEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FalseLitchiStone {
    // Create a Deferred Register to hold Items
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Food properties for False Litchi Stone
    private static final FoodProperties FALSE_LITCHI_STONE_FOOD = (new FoodProperties.Builder())
            .nutrition(0)
            .saturationMod(0.0f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 60 * 20, 0), 1.0f)
            .effect(() -> new MobEffectInstance(HalluEffect.HALLU.get(), 60 * 20, 0), 1.0f)
            .alwaysEat()
            .build();

    // 用于跟踪玩家效果
    private static final Map<UUID, Integer> playerTickCounters = new HashMap<>();
    private static final int TOTAL_DURATION_TICKS = 60 * 20; // 60秒
    private static final int DRAIN_INTERVAL = 20; // 每20游戏刻(1秒)一次
    private static final int FOOD_DRAIN = 1; // 每次扣除1点饱食度
    private static final float SATURATION_DRAIN = 1.0f; // 每次扣除1点饱和度

    // 事件处理器类
    public static class SaturationDrainHandler {
        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                // 创建一个副本来避免并发修改异常
                Map<UUID, Integer> copyMap = new HashMap<>(playerTickCounters);

                for (Map.Entry<UUID, Integer> entry : copyMap.entrySet()) {
                    UUID playerId = entry.getKey();
                    int tickCount = entry.getValue();

                    // 查找玩家
                    Player player = null;
                    for (Level level : event.getServer().getAllLevels()) {
                        player = level.getPlayerByUUID(playerId);
                        if (player != null) break;
                    }

                    if (player != null) {
                        // 每20tick执行一次扣除
                        if (tickCount > 0 && tickCount % DRAIN_INTERVAL == 0) {
                            // 扣除饱食度
                            int currentFood = player.getFoodData().getFoodLevel();
                            int newFood = Math.max(0, currentFood - FOOD_DRAIN);
                            player.getFoodData().setFoodLevel(newFood);

                            // 扣除饱和度
                            float currentSaturation = player.getFoodData().getSaturationLevel();
                            float newSaturation = Math.max(0.0f, currentSaturation - SATURATION_DRAIN);
                            player.getFoodData().setSaturation(newSaturation);


                        }

                        // 增加tick计数
                        tickCount++;

                        // 检查是否超过持续时间
                        if (tickCount >= TOTAL_DURATION_TICKS) {
                            playerTickCounters.remove(playerId);
                        } else {
                            playerTickCounters.put(playerId, tickCount);
                        }
                    } else {
                        // 玩家不在线，移除效果
                        playerTickCounters.remove(playerId);
                    }
                }
            }
        }
    }

    // 静态实例
    private static final SaturationDrainHandler DRAIN_HANDLER = new SaturationDrainHandler();
    private static boolean isEventHandlerRegistered = false;

    // 注册事件监听器
    public static void registerEventHandlers() {
        if (!isEventHandlerRegistered) {
            MinecraftForge.EVENT_BUS.register(DRAIN_HANDLER);
            isEventHandlerRegistered = true;
            System.out.println("FalseLitchiStone: Event handler registered successfully");
        }
    }

    // Register the False Litchi Stone item
    public static final RegistryObject<Item> FALSE_LITCHI_STONE = ITEMS.register("falselitchistone",
            () -> new Item(new Item.Properties().food(FALSE_LITCHI_STONE_FOOD)) {
                @Override
                public InteractionResult useOn(UseOnContext context) {
                    BlockState clickedBlock = context.getLevel().getBlockState(context.getClickedPos());

                    if (clickedBlock.is(Blocks.GRASS_BLOCK) ||
                            clickedBlock.is(Blocks.MOSS_BLOCK) ||
                            clickedBlock.is(Blocks.DIRT)) {

                        var posAbove = context.getClickedPos().above();

                        if (context.getLevel().getBlockState(posAbove).isAir() && !context.getLevel().isClientSide) {
                            context.getLevel().setBlockAndUpdate(posAbove,
                                    FalseLitchiSeedlingBlock.FALSE_LITCHI_SEEDLING_BLOCK.get().defaultBlockState());

                            if (!context.getPlayer().isCreative()) {
                                context.getItemInHand().shrink(1);
                            }

                            return InteractionResult.SUCCESS;
                        }
                    }
                    return InteractionResult.PASS;
                }

                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
                    ItemStack result = super.finishUsingItem(stack, level, livingEntity);

                    if (!level.isClientSide && livingEntity instanceof Player) {
                        Player player = (Player) livingEntity;
                        // 开始扣除效果，从tick 1开始
                        playerTickCounters.put(player.getUUID(), 1);

                    }

                    return result;
                }
            });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        // 立即注册事件处理器
        registerEventHandlers();
    }
}