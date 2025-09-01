package org.endangeredplants.block;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;

import java.util.function.Supplier;

public class ZangnanCupressusLog {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);

    // Block registrations
    public static final RegistryObject<Block> ZANGNAN_CUPRESSUS_LOG = BLOCKS.register(
            "zangnancupressuslog",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F)
                    .sound(net.minecraft.world.level.block.SoundType.WOOD))
    );

    public static final RegistryObject<Block> STRIPPED_ZANGNAN_CUPRESSUS_LOG = BLOCKS.register(
            "strippedzangnancupressuslog",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F)
                    .sound(net.minecraft.world.level.block.SoundType.WOOD))
    );

    // Item registrations
    public static final RegistryObject<Item> ZANGNAN_CUPRESSUS_LOG_ITEM = ITEMS.register(
            "zangnancupressuslog",
            () -> new BlockItem(ZANGNAN_CUPRESSUS_LOG.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> STRIPPED_ZANGNAN_CUPRESSUS_LOG_ITEM = ITEMS.register(
            "strippedzangnancupressuslog",
            () -> new BlockItem(STRIPPED_ZANGNAN_CUPRESSUS_LOG.get(), new Item.Properties())
    );

    // Initialization method
    public static void initialize() {
        registerInteractionHandler(
                ZANGNAN_CUPRESSUS_LOG.get(),
                ZangnanCupressusLog::onLogRightClick
        );

        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock.class, event -> {
            if (event.getLevel().isClientSide()) return;

            BlockState state = event.getLevel().getBlockState(event.getPos());
            InteractionResult result = LogInteractionRegistry.handleInteraction(
                    state,
                    event.getLevel(),
                    event.getPos(),
                    event.getEntity(),
                    event.getHand()
            );

            if (result.consumesAction()) {
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        });
    }

    private static void registerInteractionHandler(Block block, LogInteractionHandler handler) {
        block.getStateDefinition().getPossibleStates().forEach(state -> {
            LogInteractionRegistry.register(state, handler);
        });
    }

    private static InteractionResult onLogRightClick(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof AxeItem) {
            level.setBlock(pos, STRIPPED_ZANGNAN_CUPRESSUS_LOG.get().defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)), 11);
            itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            level.playSound(player, pos, net.minecraft.sounds.SoundEvents.AXE_STRIP, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);

        eventBus.addListener(EventPriority.LOWEST, (FMLCommonSetupEvent event) -> {
            initialize();
        });
    }

    @FunctionalInterface
    private interface LogInteractionHandler {
        InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand);
    }

    private static class LogInteractionRegistry {
        private static final java.util.Map<BlockState, LogInteractionHandler> HANDLERS = new java.util.HashMap<>();

        static void register(BlockState state, LogInteractionHandler handler) {
            HANDLERS.put(state, handler);
        }

        static InteractionResult handleInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
            LogInteractionHandler handler = HANDLERS.get(state);
            return handler != null ? handler.interact(state, level, pos, player, hand) : InteractionResult.PASS;
        }
    }
}