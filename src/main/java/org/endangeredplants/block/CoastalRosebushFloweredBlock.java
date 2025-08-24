package org.endangeredplants.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.CoastalRosePetals;
import org.endangeredplants.item.CoastalRosehip;
import org.endangeredplants.item.CoastalRoseSucker;
import org.endangeredplants.item.CoastalRoseBushWithRosehips;

import java.util.Random;

public class CoastalRosebushFloweredBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 自定义带伤害的方块类
    public static class ThornyBushBlock extends Block {
        private static final Random RANDOM = new Random();

        public ThornyBushBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
            if (entity instanceof Player) {
                entity.hurt(level.damageSources().cactus(), 1.0F);
            }
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack heldItem = player.getItemInHand(hand);

            // 使用剪刀右键 - 获取吸芽
            if (heldItem.getItem() instanceof ShearsItem) {
                if (!level.isClientSide) {
                    // 掉落1个吸芽
                    popResource(level, pos, new ItemStack(CoastalRoseSucker.COASTAL_ROSE_SUCKER.get(), 1));

                    // 播放剪刀使用音效
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.SHEEP_SHEAR, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

                    // 不破坏方块，不退化
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            // 空手右键 - 采集花瓣和果实
            if (heldItem.isEmpty()) {
                if (!level.isClientSide) {
                    // 随机掉落2-3个玫瑰花瓣
                    int petalCount = 2 + RANDOM.nextInt(2);
                    popResource(level, pos, new ItemStack(CoastalRosePetals.COASTAL_ROSE_PETALS.get(), petalCount));

                    // 随机掉落2-3个玫瑰果
                    int hipCount = 2 + RANDOM.nextInt(2);
                    popResource(level, pos, new ItemStack(CoastalRosehip.COASTAL_ROSEHIP.get(), hipCount));

                    // 播放声音效果
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 0.8F + RANDOM.nextFloat() * 0.4F);

                    // 采摘后变回基础灌木
                    level.setBlock(pos, CoastalRosebushBlock.COASTAL_ROSE_BUSH_BLOCK.get().defaultBlockState(), 3);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            return super.use(state, level, pos, player, hand, hit);
        }

        @Override
        public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
            ItemStack heldItem = player.getMainHandItem();

            // 使用剪刀破坏 - 掉落玫瑰植株
            if (heldItem.getItem() instanceof ShearsItem) {
                popResource(level, pos, new ItemStack(CoastalRoseBushWithRosehips.COASTAL_ROSE_BUSH_WITH_ROSEHIPS.get(), 1));
                return;
            }

            // 空手破坏 - 造成伤害且不掉落
            if (!level.isClientSide) {
                player.hurt(level.damageSources().cactus(), 4.0F);
            }

            super.playerWillDestroy(level, pos, state, player);
        }
    }

    // 注册方块（带伤害属性）
    public static final RegistryObject<Block> COASTAL_ROSE_BUSH_FLOWERED_BLOCK = BLOCKS.register("coastalrosebushfloweredblock",
            () -> new ThornyBushBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> COASTAL_ROSE_BUSH_FLOWERED_BLOCK_ITEM = ITEMS.register(
            "coastalrosebushfloweredblock",
            () -> new BlockItem(
                    COASTAL_ROSE_BUSH_FLOWERED_BLOCK.get(),
                    new Item.Properties()
            )
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}