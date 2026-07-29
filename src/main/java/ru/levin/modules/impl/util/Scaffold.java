package ru.levin.modules.impl.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.util.player.TimerUtil;

import java.util.Arrays;
import java.util.List;

@FunctionAnnotation(
        name = "Scaffold",
        type = Type.Misc
)
public class Scaffold extends Function {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    private static final List<Block> BLACKLIST = Arrays.asList(
            Blocks.CHEST,
            Blocks.ENDER_CHEST,
            Blocks.TRAPPED_CHEST,
            Blocks.SAND,
            Blocks.CRAFTING_TABLE,
            Blocks.FURNACE,
            Blocks.STONE_PRESSURE_PLATE,
            Blocks.OAK_PRESSURE_PLATE,
            Blocks.BIRCH_PRESSURE_PLATE,
            Blocks.SPRUCE_PRESSURE_PLATE,
            Blocks.JUNGLE_PRESSURE_PLATE,
            Blocks.ACACIA_PRESSURE_PLATE,
            Blocks.DARK_OAK_PRESSURE_PLATE,
            Blocks.CRIMSON_PRESSURE_PLATE,
            Blocks.WARPED_PRESSURE_PLATE
    );

    private final BooleanSetting useInventory = new BooleanSetting("Исп. из инв", true);
    private final TimerUtil placeTimer = new TimerUtil();

    public Scaffold() {
        addSettings(useInventory);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
            BlockPos below = getPredictedPos();
            if (!mc.world.getBlockState(below).isAir()) return;

            int slot = findBlockSlot();
            if (slot == -1 && useInventory.get()) {
                int invSlot = findInventoryBlock();
                if (invSlot != -1) {
                    int selected = mc.player.getInventory().selectedSlot;
                    mc.interactionManager.clickSlot(0, invSlot, selected, SlotActionType.SWAP, mc.player);
                    slot = selected;
                }
            }

            if (slot == -1) return;

            if (mc.player.getInventory().selectedSlot != slot) {
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                mc.player.getInventory().selectedSlot = slot;
            }

            if (placeTimer.hasTimeElapsed(50L, true)) {
                BlockHitResult hit = findHit(below);
                if (hit == null) return;

                Vec3d hitVec = hit.getPos();
                float[] rot = getRotationsTo(hitVec);
                if (rot != null) {
                    float yawDiff = Math.abs(MathHelper.wrapDegrees(rot[0] - mc.player.getYaw()));
                    float pitchDiff = Math.abs(rot[1] - mc.player.getPitch());
                    if (yawDiff > 10.0f || pitchDiff > 10.0f) {
                        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(rot[0], rot[1], mc.player.isOnGround(), mc.player.horizontalCollision));
                    }
                }

                ActionResult result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                if (result.isAccepted()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }

    private int findInventoryBlock() {
        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getCount() > 0 && stack.getItem() instanceof BlockItem blockItem && !BLACKLIST.contains(blockItem.getBlock())) {
                return i;
            }
        }
        return -1;
    }

    private BlockPos getPredictedPos() {
        Vec3d vel = mc.player.getVelocity();
        int dx = (int) Math.round(vel.x);
        int dz = (int) Math.round(vel.z);
        BlockPos pos = mc.player.getBlockPos().add(dx, 0, dz);
        return pos.down();
    }

    private int findBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getCount() > 0 && stack.getItem() instanceof BlockItem blockItem && !BLACKLIST.contains(blockItem.getBlock())) {
                return i;
            }
        }
        return -1;
    }

    private BlockHitResult findHit(BlockPos target) {
        Direction[] faces = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
        for (Direction face : faces) {
            BlockPos neighbour = target.offset(face);
            if (!mc.world.getBlockState(neighbour).isAir()) {
                Vec3d hitVec = Vec3d.ofCenter(neighbour).add(Vec3d.of(face.getVector()).multiply(0.5));
                return new BlockHitResult(hitVec, face.getOpposite(), neighbour, false);
            }
        }
        return null;
    }

    private float[] getRotationsTo(Vec3d point) {
        Vec3d eye = mc.player.getEyePos().subtract(0.0, 0.3, 0.0);
        double dx = point.x - eye.x;
        double dy = point.y - eye.y;
        double dz = point.z - eye.z;
        double distXZ = Math.sqrt(dx * dx + dz * dz);
        if (distXZ < 1.0E-6) return null;
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, distXZ)));
        yaw = MathHelper.wrapDegrees(yaw);
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        return new float[]{yaw, pitch};
    }
}
