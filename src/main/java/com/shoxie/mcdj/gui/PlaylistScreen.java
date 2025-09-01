package com.shoxie.mcdj.gui;

import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.mcdj.SupportedFormats;
import com.shoxie.mcdj.misc.MusicFile;
import com.shoxie.mcdj.Lib;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.sounds.SoundSource;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.nio.file.Path;

public final class PlaylistScreen extends Screen {
   private PlaylistScreen.Playlist playlist;
   private boolean showwarn = true;
   Button delbutton;
   Button playbutton;
   Button stopbutton;
   MusicFile lastPlayed;
   float lastAmbientVolume = 0;

   public PlaylistScreen() {
      super(Component.translatable("gui.mcdj.playlistscreen.title",
            mcdj.NAME));
   }

   @Override
   protected void init() {
      this.playlist = new PlaylistScreen.Playlist(this.minecraft);
      if(!mcdj.ffmpegfound) Lib.Checkffmpeg();
      this.addWidget(this.playlist);
      //delbutton
      delbutton = Button.builder(Component.translatable("gui.mcdj.button.deletetrack"),
              button -> playlist.deletetrack()
              ).bounds(this.width / 2 - 100, this.height - 48, 50,20).build();

      //playbutton
      playbutton = Button.builder(Component.translatable("gui.mcdj.button.playtrack"),
              button -> playlist.playmusic()
              ).bounds(this.width / 2 - 42, this.height - 48, 84,20).build();

      //stopbutton
      stopbutton = Button.builder(Component.translatable("gui.mcdj.button.stoptrack"),
              button -> playlist.stopmusic()
              ).bounds(this.width / 2 + 50, this.height - 48, 50,20).build();

      this.addRenderableWidget(delbutton);
      this.addRenderableWidget(playbutton);
      this.addRenderableWidget(stopbutton);
      super.init();

      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
              button -> this.onClose()
              ).bounds(this.width / 2 - 100,this.height - 26,200, 20).build());

      this.addRenderableWidget(Button.builder(Component.translatable("gui.mcdj.button.configscreen"),
              button -> this.minecraft.setScreen(new ConfigScreen(this, minecraft.options))
              ).bounds(this.width / 2 - 182,this.height - 26,75, 20).build());
    }
   @Override
   public void onClose() {
      this.playlist.stopmusic();
      super.onClose();
   }

   @Override
   public void onFilesDrop(List<Path> path) {
      ArrayList<Path> allpaths = new ArrayList<Path>();
      ArrayList<Path> songspaths = new ArrayList<Path>();
      for (Path iPath : path) {
         if(iPath == null) continue;
         if(iPath.toFile().isDirectory()) allpaths = scandir(allpaths, iPath.toFile());
         else allpaths.add(iPath);
      }

      for (Path iPath : allpaths) {
         if(iPath == null || iPath.toFile().isDirectory()) continue;
         if((mcdj.ffmpegfound && Lib.isSupportedMusicFile(iPath)) || Lib.getFileType(iPath.toFile()) == SupportedFormats.ogg)
            songspaths.add(iPath);
      }

      this.toConvert(songspaths);
   }

   private void toConvert(ArrayList<Path> songspaths){
      String smsg = !songspaths.isEmpty() ? "gui.mcdj.playlistscreen.dropConfirm" : "gui.mcdj.playlistscreen.nomusicfound";
      String size = !songspaths.isEmpty() ? Integer.toString(songspaths.size()) : "";
      Component msg = Component.literal(size + " " +Component.translatable(smsg).getString());
      this.minecraft.setScreen(new ConfirmScreen((p_170012_) -> {
         if (p_170012_) {
            if(!songspaths.isEmpty()) {
               mcdj.updateinprogress = true;
               this.minecraft.setScreen(new TrackConvertingScreen(songspaths, this, minecraft.options));
            }
            else this.minecraft.setScreen(this);
         }
         else this.minecraft.setScreen(this);
      }, msg, Component.empty()));
   }

   private ArrayList<Path> scandir (ArrayList<Path> paths, File dirpath){
      for (File file : dirpath.listFiles()) {
         if(file == null) continue;
         if (file.isDirectory()) {
            paths = scandir(paths, file);
         } else {
            paths.add(file.toPath().toAbsolutePath());
         }
      }
      return paths;
   }
    @Override
    public void render(GuiGraphics guigraphics, int X, int Y, float p_281886_) {
        Lib.Checkffmpeg();

         if(!mcdj.ffmpegfound && showwarn){
            showwarn = false;
            Component component = Component.translatable("message.mcdj.error.ffmpegnotfound1");
            Component component1 = Component.translatable("message.mcdj.error.ffmpegnotfound2");
            Component component2 = CommonComponents.GUI_PROCEED;
            Component component3 = Component.translatable("message.mcdj.error.installffmpeg");
            ConfirmScreen confirmScreen = new ConfirmScreen((p_170012_) -> {
               if (p_170012_) {
                  this.minecraft.setScreen(this);
               }
               else this.onClose();
            }, component, component1, component2, component3) {
               @Override
               protected void addButtons(int p_169252_) {
                  this.addExitButton(Button.builder(this.yesButton,
                          button -> this.callback.accept(true)
                          ).bounds(this.width / 2 - 155, p_169252_, 150, 20).build());

                   this.addExitButton(Button.builder(this.noButton,
                           button -> {
                               Util.getPlatform().openUri("https://ffmpeg.org/download.html");
                               this.callback.accept(false);
                           }
                   ).bounds(this.width / 2 - 155 + 160, p_169252_, 150, 20).build());
               }
            };
            this.minecraft.setScreen(confirmScreen);
         }
         this.playlist.render(guigraphics, X, Y, p_281886_);
         if(mcdj.needrestart) guigraphics.drawCenteredString(this.font, Component.translatable("gui.mcdj.playlistscreen.needrestart").getString(), this.width / 2, this.height - 56, 8421504);
        guigraphics.drawCenteredString(this.font, this.title.getString(),
               this.width / 2, 25, 0xFFFFFF);
         delbutton.visible = playbutton.visible = stopbutton.visible = false;
         if(this.playlist.getSelected() != null)
            if(this.playlist.getSelected().getMusicFile() != null)
               if(!this.playlist.getSelected().getMusicFile().isDeleted())
                  if(!this.playlist.getSelected().getMusicFile().isRegistered())
                     delbutton.visible = true;
                  else
                     delbutton.visible = playbutton.visible = stopbutton.visible = true;
        super.render(guigraphics, X, Y, p_281886_);

    }

    class Playlist extends ObjectSelectionList<PlaylistScreen.Playlist.Entry> {
       private Entry lastselected;

      public Playlist(Minecraft p_96103_) {
          super(p_96103_, PlaylistScreen.this.width, PlaylistScreen.this.height, 32, PlaylistScreen.this.height - 65 + 4,18);
          boolean flag = true;
          for(MusicFile iMus : mcdj.currentPlaylist) {
            PlaylistScreen.Playlist.Entry iEn = new PlaylistScreen.Playlist.Entry(iMus);
             this.addEntry(iEn);
             if (flag) {
                this.setSelected(iEn);
                flag = false;
             }
          }
 
          if (this.lastselected != null) {
            this.centerScrollOn(this.getSelected());
            lastselected = null;
          }
          else if (this.getSelected() != null) {
             this.centerScrollOn(this.getSelected());
          }
 
       }

      public void deletetrack() {
         if(this.getSelected().getMusicFile().isDeleted()) return;
         String dname = this.getSelected().getMusicFile().getDisplayName();
         this.minecraft.setScreen(new ConfirmScreen((p_170012_) -> {
            if (p_170012_) {
               this.getSelected().delete();
            }
            this.minecraft.setScreen(PlaylistScreen.this);
            this.nextEntry(ScreenDirection.DOWN);
            this.lastselected = this.getSelected();
         }, Component.translatable("gui.mcdj.playlistscreen.confirmdeletion"), Component.literal(dname)));
      }

      public void playmusic() {
         this.stopmusic();
         lastAmbientVolume=minecraft.options.getSoundSourceVolume(SoundSource.MUSIC);
         Minecraft.getInstance().getSoundManager().updateSourceVolume(SoundSource.MUSIC,0);
         if(!this.getSelected().getMusicFile().isDeleted()) this.getSelected().play();
      }

      public void stopmusic() {
         if(mcdj.is != null) minecraft.getSoundManager().stop(mcdj.is);
         lastPlayed = null;
         Minecraft.getInstance().getSoundManager().updateSourceVolume(SoundSource.MUSIC,lastAmbientVolume);
         for(Entry iEn : children())
            iEn.stop();
      }

      protected int getScrollbarPosition() {
          return super.getScrollbarPosition() + 20;
       }
 
       public int getRowWidth() {
          return super.getRowWidth() + 50;
       }
 
       public boolean isFocused() {
          return PlaylistScreen.this.getFocused() == this;
       }

       public class Entry extends ObjectSelectionList.Entry<PlaylistScreen.Playlist.Entry> {
          private final MusicFile musicfile;
          private boolean isPlaying = false;
 
          public Entry(MusicFile musicfile) {
             this.musicfile = musicfile;
             if(lastPlayed != null)
                 if(lastPlayed == this.musicfile)
                    if(this.musicfile.getSoundEvent().getLocation() == mcdj.is.getLocation() && minecraft.getSoundManager().isActive(mcdj.is))
                        isPlaying = true;
          }
 
          public void render(GuiGraphics p_96126_, int p_96127_, int p_96128_, int p_96129_, int p_96130_, int p_96131_, int p_96132_, int p_96133_, boolean p_96134_, float p_96135_) {
            MutableComponent msg = Component.literal(this.musicfile.getDisplayName());
            if(this.musicfile.isDeleted())
               msg = Component.literal(this.musicfile.getDisplayName()).withStyle(ChatFormatting.STRIKETHROUGH);
            else if(!this.musicfile.isRegistered())
               msg = Component.literal(this.musicfile.getDisplayName()+"*").withStyle(ChatFormatting.GRAY);
            else if(isPlaying)
               msg = Component.literal(this.musicfile.getDisplayName()+" \u23F5");
               if(!minecraft.getSoundManager().isActive(mcdj.is)) {
                   isPlaying=false;
                   lastPlayed=null;
               }
              p_96126_.drawString(PlaylistScreen.this.font, msg, (PlaylistScreen.this.width / 2 - PlaylistScreen.this.font.width(msg.getString()) / 2), (p_96128_ + 1), 16777215);
            }
          public MusicFile getMusicFile(){
            return musicfile;
          }
          public boolean mouseClicked(double p_96122_, double p_96123_, int p_96124_) {
             if (p_96124_ == 0) {
                this.select();
                return true;
             } else {
                return false;
             }
          }

          public void play(){
            if(musicfile.isRegistered() && musicfile.getSoundEvent() != null){
                  Minecraft.getInstance().getSoundManager().pause();
                  mcdj.is = SimpleSoundInstance.forUI(musicfile.getSoundEvent(), 1,1);
                  Minecraft.getInstance().getSoundManager().play(mcdj.is);
            }
            this.isPlaying = true;
            lastPlayed = this.musicfile;
          }

          public void stop(){
              if(this.isPlaying)
                  lastPlayed = null;

              this.isPlaying = false;

            Minecraft.getInstance().getSoundManager().resume();
          }

         public void delete() {
             if(this.isPlaying) {
                 lastPlayed = null;
                 minecraft.getSoundManager().stop(mcdj.is);
             }
             this.stop();
             this.isPlaying = false;
            this.musicfile.remove();
         }
 
          private void select() {
             Playlist.this.setSelected(this);
          }

          public Component getNarration() {
             return Component.translatable("narrator.select", musicfile.getDisplayName());
          }

       }
    }
}