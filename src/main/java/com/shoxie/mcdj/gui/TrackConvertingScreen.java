package com.shoxie.mcdj.gui;

import com.shoxie.mcdj.Config;
import com.shoxie.mcdj.Lib;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.misc.trackProcessingWorker;

import java.nio.file.Path;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.shoxie.mcdj.Lib.getTranslated;


public class TrackConvertingScreen extends OptionsSubScreen {
   ArrayList<Path> songspaths;
   public boolean stopped = false;
   private boolean started = true;
   private Button cancelButton;
   private Button doneButton;
   static int added = 0;
   static int errors = 0;
   trackProcessingWorker[] active;
private FFmpegThreadList thrlist;

   public TrackConvertingScreen(ArrayList<Path> songspaths, Screen lastScreen, Options options) {
      super(lastScreen, options, Component.literal("gui.mcdj.trackAddingScreen.title"));
      this.songspaths = songspaths;
   }

   @Override
   protected void init() {
        super.init();
        this.thrlist = new TrackConvertingScreen.FFmpegThreadList(this.minecraft);
        this.addWidget(this.thrlist);

       cancelButton = Button.builder(CommonComponents.GUI_CANCEL,
                       (button) ->  this.stop()
                       ).bounds((this.width / 2 - 75), 8, 150, 20).build();

       doneButton = Button.builder(CommonComponents.GUI_DONE,
               (button) -> {
                   this.minecraft.setScreen(this.lastScreen);
                   added = 0;
                   errors = 0;
               }
       ).bounds((this.width / 2 - 75),8,150, 20).build();

        this.addRenderableWidget(doneButton);
        this.addRenderableWidget(cancelButton);
        if(mcdj.updateinprogress){
            new Thread(this::doConverting).start();
            mcdj.updateinprogress = false;
        }
    }
    public void stop() {
        mcdj.logger.info(getTranslated("message.mcdj.stopconverting"));
        this.stopped = true;
        started = false;
    }

    private void doConverting(){
        this.started = true;
        this.stopped = false;
        int j = 0;
        active = new trackProcessingWorker[Config.getFFMpegMaxThreads()];
        for(Path iPath : songspaths) {
            if(this.stopped) {this.started = false; break;}
            if(j < active.length){
                active[j] = new trackProcessingWorker(iPath, 
                    Lib.fnamestringfix(
                        Lib.getDestinationFile(iPath.toFile())
                        ).toPath()
                );
                active[j].run();
                j++;
            }
            else{
                boolean fin = false;
                do{
                    if(this.stopped) {this.started = false; break;}
                    for(int i = 0;i<active.length;i++){
                        if(!active[i].getStatus()) {
                            if(!active[i].isError() && active[i].getDestPath().toFile().exists()){
                                added++;
                                Lib.addNewToPlaylist(active[i].getDestPath());
                                active[i].markFinished();
                            } else {errors++;}
                                
                            active[i] = null;
                            active[i] = new trackProcessingWorker(iPath, Lib.getDestinationFile(iPath.toFile()).toPath());
                            active[i].run();
                            fin = true;
                            break;
                        }
                    }
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {}
                } while(!fin);
            }
        }
        boolean stillalive = true;
        while(stillalive){
            stillalive = false;
            for(int i = 0;i<active.length;i++){
                if(this.stopped) {this.started = false;}
                if(active[i] != null){
                    if(active[i].getStatus()) {
                        stillalive = true;
                    }
                    else{
                        if (!active[i].isError() && active[i].getDestPath().toFile().exists()) {
                            Lib.addNewToPlaylist(active[i].getDestPath());
                            added++;
                            active[i].markFinished();
                        } else {
                            errors++;
                        }
                        active[i] = null;
                    }
               }
            }
        }
        this.started = false;
    }

 @Override
 public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

    this.thrlist.render(guiGraphics, mouseX, mouseY, partialTicks);

     if(!this.started){
        cancelButton.visible = false;
        doneButton.visible = true;
     }else{
        cancelButton.visible = true;
        doneButton.visible = false;
    }
     guiGraphics.drawCenteredString(this.font,
             getTranslated("gui.mcdj.trackconvertingscreen.added") + ": "+added+" / "+songspaths.size(), 60, 10 , 8421504);
     guiGraphics.drawCenteredString(this.font, getTranslated("gui.mcdj.trackconvertingscreen.errors") + ": "+errors, 60, 18, 8421504);
     if(!this.started) guiGraphics.drawCenteredString(this.font,  getTranslated("gui.mcdj.trackconvertingscreen."+(stopped ? "canceled" : "finished")) , this.width - 60, 13, 8421504);
     thrlist.updateList();
     super.render(guiGraphics, mouseX, mouseY, partialTicks);
 }

 
 class FFmpegThreadList extends ObjectSelectionList<TrackConvertingScreen.FFmpegThreadList.Entry> {
    public FFmpegThreadList(Minecraft p_96103_) {
       super(p_96103_, TrackConvertingScreen.this.width, TrackConvertingScreen.this.height, 32,TrackConvertingScreen.this.height - 65 + 4,18);

       if (this.getSelected() != null) {
          this.centerScrollOn(this.getSelected());
       }

    }

    protected void updateList(){
        if(TrackConvertingScreen.this.active == null || !started) return;
        int free = -1;
        int thisthr = -1;
        for(trackProcessingWorker iThr : TrackConvertingScreen.this.active) {
            boolean exists = false;
            if(iThr==null) continue;
            for(int i = 0; i<children().size();i++){
                if(children().get(i).tpthread.isFinished()) free = i;
                if(children().get(i).tpthread == iThr) {exists = true;break;}
            }
            if(!exists){
                if(children().size() >= Config.getFFMpegMaxThreads()){
                    if(free > -1 && free < children().size())
                        children().get(free).tpthread = iThr;
                } else {
                    this.addEntry(new TrackConvertingScreen.FFmpegThreadList.Entry(iThr,thisthr));
                }
            }
        }
    }

   protected int getScrollbarPosition() {
       return super.getScrollbarPosition() + 20;
    }

    public int getRowWidth() {
       return super.getRowWidth() + 50;
    }

    
    public class Entry extends ObjectSelectionList.Entry<TrackConvertingScreen.FFmpegThreadList.Entry> {
       private trackProcessingWorker tpthread;

       public Entry(trackProcessingWorker tpw, int i) {
          this.tpthread = tpw;
       }

       public void render(GuiGraphics guiGraphics, int p_96127_, int p_96128_, int p_96129_, int p_96130_, int p_96131_, int p_96132_, int p_96133_, boolean p_96134_, float p_96135_) {
        String msg;
        if(this.tpthread == null){
            msg = getTranslated("gui.mcdj.trackconvertingscreen.idle");
        } else{
            msg = this.tpthread.getSongNameFromSource();
            String prc = this.tpthread.getprogress();
            msg = this.tpthread.getSongNameFromSource() + " " + prc;
        }
         guiGraphics.drawString(TrackConvertingScreen.this.font, msg,
                 (TrackConvertingScreen.this.width / 2 - TrackConvertingScreen.this.font.width(msg) / 2), (p_96128_), 16777215);

       }

    @Override
    public Component getNarration() {
        return Component.literal(this.tpthread.getSongNameFromSource());
    }

    }
 }


}