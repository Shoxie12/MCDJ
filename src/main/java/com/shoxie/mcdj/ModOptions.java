package com.shoxie.mcdj;

import com.mojang.serialization.Codec;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class ModOptions {
   public static final OptionInstance<Boolean> ENABLE_MUSIC_GENERATOR =
           OptionInstance.createBoolean(
                   "options.mcdj.enablemg", OptionInstance.cachedConstantTooltip(Component.translatable("options.mcdj.enablemg.tooltip")),
                   Config.isMGenabled(), (a) -> Config.setMGenabled(!Config.isMGenabled()));

   public static final OptionInstance<Boolean> ENABLE_SPAWN_IN_DUNGEONS =
           OptionInstance.createBoolean(
                   "options.mcdj.dungeonspawn", OptionInstance.cachedConstantTooltip(Component.translatable("options.mcdj.dungeonspawn.tooltip")),
                   Config.isDungeonSpawnEnabled(), (a) -> Config.setDungeonSpawn(!Config.isDungeonSpawnEnabled())
           );

   public static final OptionInstance<Boolean> ENABLE_DROP_FROM_ZOMBIES =
           OptionInstance.createBoolean(
                   "options.mcdj.zombiedrop", OptionInstance.cachedConstantTooltip(Component.translatable("options.mcdj.zombiedrop.tooltip")),
                   Config.isZombieDropEnabled(), (a) -> Config.setZombieDrop(!Config.isZombieDropEnabled())
           );

   public static final OptionInstance<Boolean> ENABLE_NODISCS_MODE =
           OptionInstance.createBoolean(
                   "options.mcdj.nodiscsmode", OptionInstance.cachedConstantTooltip(Component.translatable("options.mcdj.nodiscsmode.tooltip")),
                   Config.isNoDiscsModEnabled(), (a) -> Config.setNoDiscsMode(!Config.isNoDiscsModEnabled())
           );

   public static final OptionInstance<Boolean> ENABLE_NUMBERS_IN_TRACK_NAMES =
           OptionInstance.createBoolean(
                   "options.mcdj.showtracknumbers", OptionInstance.cachedConstantTooltip(Component.translatable("options.mcdj.showtracknumbers.tooltip")),
                   Config.isTrackNumbersEnabled(), (a) -> Config.setTrackNumbers(!Config.isTrackNumbersEnabled())
           );

   public static final OptionInstance<Boolean> ENABLE_SCAN_SUBFOLDERS =
           OptionInstance.createBoolean(
                   "options.mcdj.creeperdrop", OptionInstance.cachedConstantTooltip(Component.translatable("options.mcdj.creeperdrop.tooltip")),
                   Config.isCreeperDropEnabled(), (a) -> Config.setCreeperDrop(!Config.isCreeperDropEnabled())
           );

   public static final OptionInstance<Integer> FFMPEG_MAX_ACTIVE_THREADS = new OptionInstance<>("options.mcdj.convmaxthreads",
           OptionInstance.cachedConstantTooltip(Component.translatable("options.mcdj.convmaxthreads.tooltip")),
           (p_232048_, p_232049_) -> Component.literal(Lib.getTranslated("options.mcdj.convmaxthreads")+": "+ p_232049_),
           (new OptionInstance.IntRange(1, 32)).xmap((p_232003_) -> p_232003_, (p_232094_) -> p_232094_),
           Codec.intRange(1, 32), Config.getFFMpegMaxThreads(), Config::setFFMpegMaxThreads);
}
