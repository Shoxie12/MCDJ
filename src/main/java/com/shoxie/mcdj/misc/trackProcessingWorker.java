package com.shoxie.mcdj.misc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.shoxie.mcdj.gui.TrackConvertingScreen;
import org.apache.commons.io.FileUtils;

import com.shoxie.mcdj.Lib;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.mcdj.SupportedFormats;

public class trackProcessingWorker extends Thread{

    SupportedFormats sformat;
    Process p;
    Path source;
    Path dest;
    boolean ioerror = false;
    double totalSecs = 0;
    double progress = 0;
    private boolean done;
    public boolean stopsig = false;
    public boolean stopsended = false;

    @Override
    public void run()  
    {
        mcdj.activeThreads++;
        if(sformat == null) {
            mcdj.activeThreads--;
            return;
        } 
        else if(sformat == SupportedFormats.ogg)
            try {
                FileUtils.copyFile(source.toFile(), dest.toFile());
            } catch (IOException e) {
                ioerror = true;
                mcdj.logger.error("message.mcdj.error.io "+source.getFileName());
            }
        else
            try {
                sleep(200);
                dest = dest.toFile().exists() ? Lib.ExistPathAppend(dest) : dest;
            p = new ProcessBuilder("ffmpeg", "-i",source.toAbsolutePath().toString()
                , "-ac", "1", "-map", "0:a", "-c:a", "libvorbis", "-q:a", "10",dest.toAbsolutePath().toString()
                , "-hide_banner", "-loglevel", "info", "-y"
                ).start();

                 //https://stackoverflow.com/questions/10927718/how-to-read-ffmpeg-response-from-java-and-use-it-to-create-a-progress-bar
                new Thread(() -> {

                  Scanner sc = new Scanner(p.getErrorStream());

                  // Find duration

                    Pattern durPattern = Pattern.compile("(?<=Duration: )[^,]*");
                    String dur = sc.findWithinHorizon(durPattern, 0);
                    if (dur == null) {
                        sc.close();
                        return;
                    }
                while(p.isAlive()){
                    if(stopsig && !stopsended) {
                        kill();
                    }
                    String[] hms = dur.split(":");
                    totalSecs = Integer.parseInt(hms[0]) * 3600
                                    + Integer.parseInt(hms[1]) *   60
                                    + Double.parseDouble(hms[2]);
                    // Find time as long as possible.
                    Pattern timePattern = Pattern.compile("(?<=time=)[\\d:]*");
                    String tm = sc.findWithinHorizon(timePattern, 0);
                    if (tm == null) {
                        sc.close();
                        return;
                    }
                    String[] hmsnow = tm.split(":");
                    double curSecs = Integer.parseInt(hmsnow[0]) * 3600
                                    + Integer.parseInt(hmsnow[1]) *   60
                                    + Double.parseDouble(hmsnow[2]);
                    progress = (100 / totalSecs * curSecs);
                }
            }).start();
            ioerror = dest.toFile().exists();
            } catch (Exception e) {
                mcdj.logger.error("message.mcdj.error.convering "+e.getMessage());
            }
        mcdj.activeThreads--;
   }

   public trackProcessingWorker(Path source, Path dest){
        this.sformat = Lib.getFileType(source.toFile());
        this.source = source;
        this.dest = dest;
   }

   public Path getSourcePath(){
    return source;
   }

   public Path getDestPath(){
    return dest;
   }

   public boolean getStatus(){
    if(sformat != SupportedFormats.ogg) return p.isAlive();
    return false;
   }

   public void markFinished(){
    this.done = true;
   }

   public boolean isFinished(){
    return this.done;
   }

   public boolean isError(){
    if(sformat != SupportedFormats.ogg && this.p != null)
        if(!p.isAlive())
            return ! (isFinished() || (p.exitValue()==0 && dest.toFile().exists()));
    return ioerror;
   }

   public String getSongNameFromSource(){
    return source.toFile().getName();
   }

   public String getprogress() {
       String ret = "-";

       if (this.p != null)
           if (this.p.isAlive())
               return (int) this.progress + "%";
       return this.isError() ? "ERROR" : "OK";
   }



    public void kill() {
        if(sformat != SupportedFormats.ogg  && p != null){
            this.p.destroy();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.dest.toFile().delete();
            stopsended = true;
        }
    }
    
}
