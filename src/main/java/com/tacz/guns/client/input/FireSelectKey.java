package com.tacz.guns.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class FireSelectKey {
    public static final KeyMapping FIRE_SELECT_KEY = new KeyMapping("key.tacz.fire_select.desc",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.category.tacz");

    @SubscribeEvent
    public static void onFireSelectPress(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && FIRE_SELECT_KEY.matches(event.getKey(), event.getScanCode())) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return;
            }
            if (IGun.mainhandHoldGun(player)) {
                IClientPlayerGunOperator.fromLocalPlayer(player).fireSelect();
            }
        }
    }
}
