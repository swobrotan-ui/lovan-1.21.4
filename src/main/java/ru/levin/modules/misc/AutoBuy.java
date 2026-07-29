package ru.levin.modules.misc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import ru.levin.events.Event;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionAnnotation(name = "AutoBuy", desc = "Автоматическая покупка предметов на аукционе", type = Type.Misc)
public class AutoBuy extends Function {

    private volatile boolean running;

    @Override
    public void onEvent(Event event) {
    }

    public void startBuying() {
        if (running) return;
        running = true;
        Thread thread = new Thread(() -> {
            try {
                startFind();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private boolean checkDon(int slotitem) {
        return mc.player.currentScreenHandler.getSlot(slotitem).getStack().getName().getString().contains("★");
    }

    private int getStackInSlotOpenContainer(int itemslot) {
        return mc.player.currentScreenHandler.getSlot(itemslot).getStack().getCount();
    }

    private String getNBT(int itemslot) {
        ItemStack stack = mc.player.currentScreenHandler.getSlot(itemslot).getStack();
        RegistryWrapper.WrapperLookup registries = mc.getNetworkHandler().getRegistryManager();
        NbtCompound nbt = (NbtCompound) stack.toNbtAllowEmpty(registries);
        NbtCompound tag = nbt.getCompound("tag");
        return tag == null ? "" : tag.toString();
    }

    private Item getItem(int itemslot) {
        return mc.player.currentScreenHandler.getSlot(itemslot).getStack().getItem();
    }

    private void startFind() {
        Thread thread = new Thread(() -> {
            try {
                for (int i = 0; i < 44; i++) {
                    String nbt = getNBT(i);
                    Item item = getItem(i);
                    int price = getPrice(nbt);
                    if (checkValid(item, price, i)) {
                        String[] parts = nbt.split("Прoдaвeц:\"");
                        if (parts.length > 1) {
                            String sellerName = parts[1].split("\"")[7].replace(" ", "");
                            mc.player.networkHandler.sendChatMessage("/ah " + sellerName);
                            Thread.sleep(300);
                            mc.player.networkHandler.sendChatMessage("/ah " + sellerName);
                            Thread.sleep(500);
                            for (int i2 = 0; i2 < 44; i2++) {
                                if (mc.player.currentScreenHandler.getSlot(i2).getStack().getItem() != net.minecraft.item.Items.AIR) {
                                    String nbt2 = getNBT(i2);
                                    Item item2 = getItem(i2);
                                    int price2 = getPrice(nbt2);
                                    if (checkValid(item2, price2, i2)) {
                                        quickBuy(i2);
                                        Thread.sleep((int) (Math.random() * (450 - 400 + 1)) + 450);
                                        mc.player.networkHandler.sendChatMessage("/ah");
                                        Thread.sleep(200);
                                        mc.player.networkHandler.sendChatMessage("/ah");
                                        Thread.sleep((int) (Math.random() * (450 - 400 + 1)) + 450);
                                        startFind();
                                        return;
                                    }
                                }
                            }
                            Thread.sleep(300);
                            mc.player.networkHandler.sendChatMessage("/ah");
                            Thread.sleep(500);
                            mc.player.networkHandler.sendChatMessage("/ah");
                            Thread.sleep((int) (Math.random() * (400 - 350 + 1)) + 400);
                            startFind();
                            return;
                        } else {
                            send(nbt);
                        }

                    }
                }
                Thread.sleep((int) (Math.random() * (450 - 400 + 1)) + 450);
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 49, 0, SlotActionType.QUICK_MOVE, mc.player);
                Thread.sleep((int) (Math.random() * (450 - 400 + 1)) + 450);
                startFind();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }


    private void send(String msg) {
        mc.player.sendMessage(Text.literal("" + msg), false);
    }

    private boolean checkValid(Item item, int price, int slot) {
        return (BuyList.itemsMapDonFt.containsKey(item) && checkDon(slot) && price <= BuyList.itemsMapDonFt.get(item))
                || (BuyList.itemsMapFt.containsKey(item) && price <= BuyList.itemsMapFt.get(item))
                || (BuyList.itemsMapneed16stackft.containsKey(item) && getStackInSlotOpenContainer(slot) >= 16 && price <= BuyList.itemsMapneed16stackft.get(item))
                || (BuyList.itemsMapneed32stackft.containsKey(item) && getStackInSlotOpenContainer(slot) >= 32 && price <= BuyList.itemsMapneed32stackft.get(item))
                && !mc.player.currentScreenHandler.getSlot(slot).getStack().getName().getString().contains("Сатан");
    }

    private int getPrice(String nbt) {
        String regex = "\\$(\\d+(?:\\s\\d{3})*(?:\\.\\d{2})?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nbt);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1).replace(" ", "").replace("$", ""));
        }
        return 0;
    }

    private void quickBuy(int slot) {
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
    }
}
