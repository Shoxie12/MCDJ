package com.shoxie.mcdj.gui;

import com.shoxie.mcdj.ModOptions;
import com.shoxie.mcdj.mcdj;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.network.chat.Component;

public final class ConfigScreen extends SimpleOptionsSubScreen {

    private static final OptionInstance<?>[] OPTIONS = {
        ModOptions.ENABLE_MUSIC_GENERATOR,
        ModOptions.ENABLE_SPAWN_IN_DUNGEONS,
        ModOptions.ENABLE_DROP_FROM_ZOMBIES,
        ModOptions.ENABLE_NODISCS_MODE,
        ModOptions.ENABLE_NUMBERS_IN_TRACK_NAMES,
        ModOptions.ENABLE_SCAN_SUBFOLDERS,
        ModOptions.FFMPEG_MAX_ACTIVE_THREADS
    };

    public ConfigScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, Component.translatable("gui.mcdj.configscreen.title",
                mcdj.NAME),OPTIONS);
    }
}