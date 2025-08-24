package org.endangeredplants.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.block.DendrobiumBlock;

import java.util.HashMap;
import java.util.Map;

public class Dendrobium {
    // Create a Deferred Register to hold Items
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Food properties for Dendrobium (if applicable)
    private static final FoodProperties DENDROBIUM_FOOD = (new FoodProperties.Builder())
            .nutrition(1)  // 1 hunger point
            .saturationMod(0.5f)  // low saturation
            .build();

    // 方向到属性的映射
    private static final Map<Direction, BooleanProperty> DIRECTION_TO_PROPERTY = createDirectionMap();

    private static Map<Direction, BooleanProperty> createDirectionMap() {
        Map<Direction, BooleanProperty> map = new HashMap<>();
        map.put(Direction.NORTH, DendrobiumBlock.NORTH);
        map.put(Direction.EAST, DendrobiumBlock.EAST);
        map.put(Direction.SOUTH, DendrobiumBlock.SOUTH);
        map.put(Direction.WEST, DendrobiumBlock.WEST);
        map.put(Direction.UP, DendrobiumBlock.UP);
        map.put(Direction.DOWN, DendrobiumBlock.DOWN);
        return map;
    }

    // Custom Dendrobium item class
    public static class DendrobiumItem extends Item {
        public DendrobiumItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState blockState = level.getBlockState(pos);
            Player player = context.getPlayer();
            ItemStack itemStack = context.getItemInHand();
            Direction clickedFace = context.getClickedFace();

            // 计算放置位置
            BlockPos placePos = pos.relative(clickedFace);

            // 检查目标位置是否为空气
            if (level.isEmptyBlock(placePos)) {
                // 检查是否能附着到被点击的面
                if (canAttachToFace(level, pos, clickedFace)) {
                    if (!level.isClientSide) {
                        // 获取对应的朝向属性
                        BooleanProperty faceProperty = DIRECTION_TO_PROPERTY.get(clickedFace.getOpposite());

                        if (faceProperty != null) {
                            // 创建方块状态，设置对应面为true
                            BlockState placeState = DendrobiumBlock.DENDROBIUM_BLOCK.get().defaultBlockState()
                                    .setValue(faceProperty, true)
                                    .setValue(DendrobiumBlock.AGE, 0)
                                    .setValue(DendrobiumBlock.BLOOMING, false);

                            // 放置方块
                            level.setBlock(placePos, placeState, 3);

                            // 消耗物品（非创造模式）
                            if (player != null && !player.getAbilities().instabuild) {
                                itemStack.shrink(1);
                            }

                            // 播放放置音效
                            level.playSound(null, placePos, net.minecraft.sounds.SoundEvents.GRASS_PLACE,
                                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 0.8F);
                        }
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            return super.useOn(context);
        }

        // 检查是否能附着到指定面
        private boolean canAttachToFace(Level level, BlockPos pos, Direction face) {
            BlockState state = level.getBlockState(pos);

            if (face == Direction.UP) {
                return state.isFaceSturdy(level, pos, Direction.UP);
            } else if (face == Direction.DOWN) {
                return state.isFaceSturdy(level, pos, Direction.DOWN);
            } else {
                return state.isFaceSturdy(level, pos, face);
            }
        }
    }

    // Register the Dendrobium item
    public static final RegistryObject<Item> DENDROBIUM = ITEMS.register("dendrobium",
            () -> new DendrobiumItem(new Item.Properties().food(DENDROBIUM_FOOD)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}