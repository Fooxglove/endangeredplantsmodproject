package org.endangeredplants.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.endangeredplants.Endangeredplants;
import org.endangeredplants.item.SpecimenBag;

import javax.annotation.Nullable;
import java.util.List;

public class SpecimenPresser {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Endangeredplants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Endangeredplants.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Endangeredplants.MODID);
    public static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Endangeredplants.MODID);

    // 方块状态属性
    public static final BooleanProperty PROCESSING = BooleanProperty.create("processing");

    // SpecimenPresser方块类
    public static class SpecimenPresserBlock extends BaseEntityBlock {
        public SpecimenPresserBlock(BlockBehaviour.Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(PROCESSING, false));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(PROCESSING);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new SpecimenPresserBlockEntity(pos, state);
        }

        @Override
        public RenderShape getRenderShape(BlockState state) {
            return RenderShape.MODEL;
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (!level.isClientSide()) {
                BlockEntity entity = level.getBlockEntity(pos);
                if (entity instanceof SpecimenPresserBlockEntity) {
                    NetworkHooks.openScreen((ServerPlayer) player, (SpecimenPresserBlockEntity) entity,
                            buf -> buf.writeBlockPos(pos));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        @Override
        public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
            if (state.getBlock() != newState.getBlock()) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof SpecimenPresserBlockEntity) {
                    ((SpecimenPresserBlockEntity) blockEntity).drops();
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
            if (level.isClientSide()) {
                return null;
            }

            return createTickerHelper(blockEntityType, SPECIMEN_PRESSER_BLOCK_ENTITY.get(),
                    (level1, pos, state1, blockEntity) -> blockEntity.tick(level1, pos, state1));
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            return this.defaultBlockState().setValue(PROCESSING, false);
        }
    }

    // SpecimenPresser方块实体类
    public static class SpecimenPresserBlockEntity extends BlockEntity implements MenuProvider {
        private final ItemStackHandler itemHandler = new ItemStackHandler(11) { // 0:输入槽, 1:输出槽, 2-10:内部存储
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (slot == 0) { // 输入槽只能放标本袋
                    return stack.getItem() == SpecimenBag.SPECIMEN_BAG.get();
                }
                return slot != 1; // 输出槽不能直接放入物品
            }
        };

        private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
        private final ContainerData data;
        private int progress = 0;
        private int maxProgress = 100; // 压制时间（刻）

        public SpecimenPresserBlockEntity(BlockPos pos, BlockState blockState) {
            super(SPECIMEN_PRESSER_BLOCK_ENTITY.get(), pos, blockState);
            this.data = new SimpleContainerData(2) {
                @Override
                public int get(int index) {
                    return switch (index) {
                        case 0 -> SpecimenPresserBlockEntity.this.progress;
                        case 1 -> SpecimenPresserBlockEntity.this.maxProgress;
                        default -> 0;
                    };
                }

                @Override
                public void set(int index, int value) {
                    switch (index) {
                        case 0 -> SpecimenPresserBlockEntity.this.progress = value;
                        case 1 -> SpecimenPresserBlockEntity.this.maxProgress = value;
                    }
                }
            };
        }

        @Override
        public Component getDisplayName() {
            return Component.literal("标本压制机");
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            return new SpecimenPresserMenu(containerId, playerInventory, this, this.data);
        }

        @Override
        public void onLoad() {
            super.onLoad();
            lazyItemHandler = LazyOptional.of(() -> itemHandler);
        }

        @Override
        public void invalidateCaps() {
            super.invalidateCaps();
            lazyItemHandler.invalidate();
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            tag.put("inventory", itemHandler.serializeNBT());
            tag.putInt("progress", progress);
            super.saveAdditional(tag);
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            itemHandler.deserializeNBT(tag.getCompound("inventory"));
            progress = tag.getInt("progress");
        }

        public void drops() {
            SimpleContainer inventory = new SimpleContainer(2);
            inventory.setItem(0, itemHandler.getStackInSlot(0));
            inventory.setItem(1, itemHandler.getStackInSlot(1));

            Containers.dropContents(this.level, this.worldPosition, inventory);
        }

        public void tick(Level level, BlockPos pos, BlockState state) {
            if (level.isClientSide()) {
                return;
            }

            boolean isProcessing = hasObstacleAbove() && canStartProcessing();

            if (isProcessing != state.getValue(PROCESSING)) {
                level.setBlock(pos, state.setValue(PROCESSING, isProcessing), 3);
            }

            if (isProcessing) {
                progress++;
                setChanged();

                if (progress >= maxProgress) {
                    finishProcessing();
                    progress = 0;
                    setChanged();
                }
            } else {
                if (progress > 0) {
                    progress = 0;
                    setChanged();
                }
            }
        }

        private boolean hasObstacleAbove() {
            BlockPos abovePos = worldPosition.above();

            // 检查方块
            if (!level.isEmptyBlock(abovePos)) {
                return true;
            }

            // 检查实体
            AABB checkArea = new AABB(abovePos);
            return !level.getEntities(null, checkArea).isEmpty();
        }

        private boolean canStartProcessing() {
            ItemStack inputStack = itemHandler.getStackInSlot(0);
            ItemStack outputStack = itemHandler.getStackInSlot(1);

            // 检查输入槽是否有标本袋
            if (inputStack.isEmpty() || !(inputStack.getItem() == SpecimenBag.SPECIMEN_BAG.get())) {
                return false;
            }

            // 检查输出槽是否为空
            if (!outputStack.isEmpty()) {
                return false;
            }

            // 检查标本袋是否有内容
            SpecimenBag.SpecimenBagItem bagItem = (SpecimenBag.SpecimenBagItem) inputStack.getItem();
            List<ItemStack> storedItems = bagItem.getStoredItems(inputStack);

            if (storedItems.isEmpty()) {
                return false;
            }

            // 检查内部存储是否有足够空间
            int currentStoredCount = getCurrentInternalStorageCount();
            return currentStoredCount + storedItems.size() <= 9;
        }

        private void finishProcessing() {
            ItemStack inputStack = itemHandler.getStackInSlot(0);

            if (inputStack.getItem() == SpecimenBag.SPECIMEN_BAG.get()) {
                SpecimenBag.SpecimenBagItem bagItem = (SpecimenBag.SpecimenBagItem) inputStack.getItem();
                List<ItemStack> storedItems = bagItem.getStoredItems(inputStack);

                // 将标本袋中的物品转移到内部存储
                for (ItemStack item : storedItems) {
                    addToInternalStorage(item);
                }

                // 清空标本袋并放到输出槽
                ItemStack emptyBag = new ItemStack(SpecimenBag.SPECIMEN_BAG.get());
                itemHandler.setStackInSlot(1, emptyBag);
                itemHandler.setStackInSlot(0, ItemStack.EMPTY);
            }
        }

        private void addToInternalStorage(ItemStack item) {
            for (int i = 2; i < 11; i++) { // 槽位2-10为内部存储
                ItemStack existingStack = itemHandler.getStackInSlot(i);
                if (existingStack.isEmpty()) {
                    itemHandler.setStackInSlot(i, item.copy());
                    return;
                } else if (ItemStack.isSameItemSameTags(existingStack, item)) {
                    int newCount = existingStack.getCount() + item.getCount();
                    if (newCount <= existingStack.getMaxStackSize()) {
                        existingStack.setCount(newCount);
                        return;
                    }
                }
            }
        }

        private int getCurrentInternalStorageCount() {
            int count = 0;
            for (int i = 2; i < 11; i++) {
                if (!itemHandler.getStackInSlot(i).isEmpty()) {
                    count++;
                }
            }
            return count;
        }

        public ContainerData getContainerData() {
            return this.data;
        }

        public ItemStackHandler getItemHandler() {
            return this.itemHandler;
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.ITEM_HANDLER) {
                return lazyItemHandler.cast();
            }
            return super.getCapability(cap, side);
        }
    }

    // 压制机菜单类
    public static class SpecimenPresserMenu extends AbstractContainerMenu {
        private final SpecimenPresserBlockEntity blockEntity;
        private final Level level;
        private final ContainerData data;

        // 服务端构造函数
        public SpecimenPresserMenu(int containerId, Inventory playerInventory, SpecimenPresserBlockEntity entity, ContainerData data) {
            super(SPECIMEN_PRESSER_MENU.get(), containerId);
            this.blockEntity = entity;
            this.level = playerInventory.player.level();
            this.data = data;

            this.addDataSlots(data);

            // 添加方块实体的槽位
            // 输入槽位 (标本袋)
            this.addSlot(new net.minecraftforge.items.SlotItemHandler(entity.getItemHandler(), 0, 56, 35));
            // 输出槽位
            this.addSlot(new net.minecraftforge.items.SlotItemHandler(entity.getItemHandler(), 1, 116, 35));

            // 添加玩家背包槽位
            addPlayerInventory(playerInventory);
            addPlayerHotbar(playerInventory);
        }

        // 客户端构造函数（用于网络同步）
        public SpecimenPresserMenu(int containerId, Inventory playerInventory, net.minecraft.network.FriendlyByteBuf additionalData) {
            this(containerId, playerInventory,
                    (SpecimenPresserBlockEntity) playerInventory.player.level().getBlockEntity(additionalData.readBlockPos()),
                    new SimpleContainerData(2));
        }

        private void addPlayerInventory(Inventory playerInventory) {
            for (int i = 0; i < 3; ++i) {
                for (int l = 0; l < 9; ++l) {
                    this.addSlot(new net.minecraft.world.inventory.Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
                }
            }
        }

        private void addPlayerHotbar(Inventory playerInventory) {
            for (int i = 0; i < 9; ++i) {
                this.addSlot(new net.minecraft.world.inventory.Slot(playerInventory, i, 8 + i * 18, 142));
            }
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            ItemStack itemstack = ItemStack.EMPTY;
            net.minecraft.world.inventory.Slot slot = this.slots.get(index);

            if (slot != null && slot.hasItem()) {
                ItemStack itemstack1 = slot.getItem();
                itemstack = itemstack1.copy();

                if (index < 2) {
                    // 从机器槽位移动到玩家背包
                    if (!this.moveItemStackTo(itemstack1, 2, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    // 从玩家背包移动到机器槽位
                    if (itemstack1.getItem() == SpecimenBag.SPECIMEN_BAG.get()) {
                        if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 29) {
                        if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                if (itemstack1.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
            }

            return itemstack;
        }

        @Override
        public boolean stillValid(Player player) {
            return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                    player, SPECIMEN_PRESSER.get());
        }

        public boolean isProcessing() {
            return data.get(0) > 0;
        }

        public int getScaledProgress() {
            int progress = this.data.get(0);
            int maxProgress = this.data.get(1);
            int progressArrowSize = 26; // 箭头图标的像素宽度

            return maxProgress != 0 ? progress * progressArrowSize / maxProgress : 0;
        }
    }

    // 带存储的方块物品类
    public static class SpecimenPresserBlockItem extends BlockItem {
        public SpecimenPresserBlockItem(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
            boolean result = super.updateCustomBlockEntityTag(pos, level, player, stack, state);

            if (stack.hasTag() && level.getBlockEntity(pos) instanceof SpecimenPresserBlockEntity blockEntity) {
                CompoundTag stackTag = stack.getTag();
                if (stackTag.contains("BlockEntityTag")) {
                    CompoundTag beTag = stackTag.getCompound("BlockEntityTag");
                    if (beTag.contains("inventory")) {
                        blockEntity.getItemHandler().deserializeNBT(beTag.getCompound("inventory"));
                    }
                }
            }

            return result;
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, net.minecraft.world.item.TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
                CompoundTag beTag = stack.getTag().getCompound("BlockEntityTag");
                if (beTag.contains("inventory")) {
                    ItemStackHandler tempHandler = new ItemStackHandler(11);
                    tempHandler.deserializeNBT(beTag.getCompound("inventory"));

                    int storedCount = 0;
                    for (int i = 2; i < 11; i++) {
                        if (!tempHandler.getStackInSlot(i).isEmpty()) {
                            storedCount++;
                        }
                    }

                    if (storedCount > 0) {
                        tooltipComponents.add(Component.literal("存储的标本: " + storedCount + "/9"));
                    }
                }
            }
        }
    }

    // 注册方块
    public static final RegistryObject<Block> SPECIMEN_PRESSER = BLOCKS.register("specimenpresser",
            () -> new SpecimenPresserBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .requiresCorrectToolForDrops()
            ));

    // 注册方块物品
    public static final RegistryObject<Item> SPECIMEN_PRESSER_ITEM = ITEMS.register("specimenpresser",
            () -> new SpecimenPresserBlockItem(SPECIMEN_PRESSER.get(), new Item.Properties()));

    // 注册MenuType
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<SpecimenPresserMenu>> SPECIMEN_PRESSER_MENU =
            MENUS.register("specimenpresser_menu", () ->
                    net.minecraftforge.common.extensions.IForgeMenuType.create(SpecimenPresserMenu::new));

    // 注册方块实体类型
    public static final RegistryObject<BlockEntityType<SpecimenPresserBlockEntity>> SPECIMEN_PRESSER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("specimenpresser_block_entity",
                    () -> BlockEntityType.Builder.of(SpecimenPresserBlockEntity::new, SPECIMEN_PRESSER.get()).build(null));

    // 注册到事件总线
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        MENUS.register(eventBus);
    }
}