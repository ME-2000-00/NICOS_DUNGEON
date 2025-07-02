package net.me.nicosdungeon;

import net.fabricmc.api.ClientModInitializer;
import net.me.nicosdungeon.rendering.ScreenUV;


public class NicosDungeonClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NicosDungoen.LOGGER.info("[Client]: INITALIZED CLIENT");
        ScreenUV.init();
    }
}
