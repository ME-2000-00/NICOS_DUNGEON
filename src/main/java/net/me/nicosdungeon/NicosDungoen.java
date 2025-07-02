package net.me.nicosdungeon;

import net.fabricmc.api.ModInitializer;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NicosDungoen implements ModInitializer {
	public static Vector3f Triangle_position = new Vector3f(0.0f, 57.0f, 0.0f);
	public static boolean Global_render = true;
	public static final String MOD_ID = "nicosdungoen";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

	}
}