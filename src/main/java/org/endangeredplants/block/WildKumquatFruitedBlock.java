package org.endangeredplants.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.WildKumquat;
import org.endangeredplants.item.WildKumquatBushWithFruits;

import java.util.Random;

public class WildKumquatFruitedBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // 自定义结果金柑灌木方块类
    public static class KumquatFruitedBushBlock extends Block {
        private static final Random RANDOM = new Random();

        public KumquatFruitedBushBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack heldItem = player.getItemInHand(hand);

            // 空手右键 - 采集野生金柑
            if (heldItem.isEmpty()) {
                if (!level.isClientSide) {
                    // 随机掉落2-4个野生金柑
                    int fruitCount = 2 + RANDOM.nextInt(3);
                    popResource(level, pos, new ItemStack(WildKumquat.WILD_KUMQUAT.get(), fruitCount));

                    // 播放采摘音效
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                            net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 0.8F + RANDOM.nextFloat() * 0.4F);

                    // 采摘后变回基础灌木
                    level.setBlock(pos, WildKumquatBlock.WILD_KUMQUAT_BLOCK.get().defaultBlockState(), 3);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            return super.use(state, level, pos, player, hand, hit);
        }

        @Override
        public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
            ItemStack heldItem = player.getMainHandItem();

            // 使用剪刀破坏 - 掉落结果金柑灌木
            if (heldItem.getItem() instanceof ShearsItem) {
                popResource(level, pos, new ItemStack(WildKumquatBushWithFruits.WILD_KUMQUAT_BUSH_WITH_FRUITS.get(), 1));
                return;
            }

            // 空手破坏 - 造成伤害且不掉落
            if (!level.isClientSide) {
                player.hurt(level.damageSources().cactus(), 2.0F);
            }

            super.playerWillDestroy(level, pos, state, player);
        }
    }

    // 注册方块
    public static final RegistryObject<Block> WILD_KUMQUAT_FRUITED_BLOCK = BLOCKS.register("wildkumquatfruitedblock",
            () -> new KumquatFruitedBushBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2f)
                    .sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .noCollission()
                    .noOcclusion()
            ));

    // 注册方块的物品形式
    public static final RegistryObject<Item> WILD_KUMQUAT_FRUITED_BLOCK_ITEM = ITEMS.register(
            "wildkumquatfruitedblock",
            () -> new BlockItem(
                    WILD_KUMQUAT_FRUITED_BLOCK.get(),
                    new Item.Properties()
            )
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}