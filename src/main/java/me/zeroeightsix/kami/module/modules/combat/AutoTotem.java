package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

/**
 * Created by 086 on 22/01/2018.
 * Updated by S-B99 on 24/02/20
 */
@Module.Info(name = "AutoTotem", category = Module.Category.COMBAT, description = "Refills your offhand with totems")
public class AutoTotem extends Module {
    private Setting<Mode> modeSetting = register(Settings.e("Mode", Mode.REPLACE_OFFHAND));
    private Setting<Double> healthSetting = register(Settings.doubleBuilder("Max replace health").withValue(20.0).withVisibility(v -> modeSetting.getValue().equals(Mode.REPLACE_OFFHAND)));

    private enum Mode {
        NEITHER, REPLACE_OFFHAND, INVENTORY
    }

    int totems;
    boolean moving = false;
    boolean returnI = false;

    @Override
    public void onUpdate() {
        if (!passHealthCheck()) return;
        if (!modeSetting.getValue().equals(Mode.INVENTORY) && mc.currentScreen instanceof GuiContainer)
            return; // this stops autototem from running if you're in a chest or something
        if (returnI) {
            int t = -1;
            for (int i = 0; i < 45; i++)
                if (mc.player.inventory.getStackInSlot(i).isEmpty) {
                    t = i;
                    break;
                }
            if (t == -1) return;
            mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            returnI = false;
        }
        totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) totems++;
        else {
            if (!modeSetting.getValue().equals(Mode.REPLACE_OFFHAND) && !mc.player.getHeldItemOffhand().isEmpty) return;
            if (moving) {
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                moving = false;
                if (!mc.player.inventory.itemStack.isEmpty()) returnI = true;
                return;
            }
            if (mc.player.inventory.itemStack.isEmpty()) {
                if (totems == 0) return;
                int t = -1;
                for (int i = 0; i < 45; i++)
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                        t = i;
                        break;
                    }
                if (t == -1) return; // Should never happen!
                mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
                moving = true;
            } else if (modeSetting.getValue().equals(Mode.REPLACE_OFFHAND)) {
                int t = -1;
                for (int i = 0; i < 45; i++)
                    if (mc.player.inventory.getStackInSlot(i).isEmpty) {
                        t = i;
                        break;
                    }
                if (t == -1) return;
                mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    private boolean passHealthCheck() {
        if (modeSetting.getValue().equals(Mode.REPLACE_OFFHAND)) {
            return mc.player.getHealth() + mc.player.getAbsorptionAmount() <= healthSetting.getValue();
        }
        return true;
    }

    @Override
    public String getHudInfo() {
        return String.valueOf(totems);
    }
}
