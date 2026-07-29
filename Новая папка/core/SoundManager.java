package core;

import enum.SoundType;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.FloatControl.Type;
import module.SoundsModule;

public class SoundManager {
   private static final SoundManager instance = new SoundManager();
   private final Map<SoundType, byte[]> soundData = new ConcurrentHashMap<SoundType, byte[]>();
   private final Map<SoundType, Boolean> soundEnabled = new ConcurrentHashMap<SoundType, Boolean>();
   private final ExecutorService executor = Executors.newCachedThreadPool(runnable -> {
      Thread thread = new Thread(runnable, "SoundPlayer");
      thread.setDaemon(true);
      return thread;
   });
   private volatile float volume = 1.0F;
   private volatile boolean enabled = true;
   private volatile boolean muted = false;

   private SoundManager() {
      for (SoundType soundtype : SoundType.values()) {
         this.soundEnabled.put(soundtype, true);
      }
   }

   public void loadSounds() {
      new Thread(this::loadSoundsAsync, "SoundLoader").start();
   }

   private void loadSoundsAsync() {
      gg gg = new gg();
      Map map = gg.a();

      for (SoundType soundtype : SoundType.values()) {
         byte[] abyte = (byte[])map.get(soundtype.getFileName());
         if (abyte != null && abyte.length > 0) {
            this.soundData.put(soundtype, abyte);
         }
      }
   }

   public void c(SoundType soundtype) {
      SoundsModule soundsmodule = ClientMain.getInstance().getModuleManager().<SoundsModule>getModule(SoundsModule.class);
      if (!this.muted && this.enabled) {
         if (soundsmodule != null && soundsmodule.isEnabled()) {
            if (this.g(soundtype)) {
               if (!(this.volume <= 0.0F)) {
                  byte[] abyte = this.soundData.get(soundtype);
                  if (abyte != null) {
                     float f = this.volume;
                     this.executor.execute(() -> {
                        this.playSoundData(abyte, f);
                     });
                  }
               }
            }
         }
      }
   }

   private void playSoundData(byte[] abyte, float f) {
      try {
         try (AudioInputStream audioinputstream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(abyte))) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioinputstream);
            if (clip.isControlSupported(Type.MASTER_GAIN)) {
               FloatControl floatcontrol = (FloatControl)clip.getControl(Type.MASTER_GAIN);
               float f1 = (float)(20.0 * Math.log10(f));
               floatcontrol.setValue(Math.min(Math.max(f1, floatcontrol.getMinimum()), floatcontrol.getMaximum()));
            }

            clip.start();
            clip.addLineListener(lineevent -> {
               if (lineevent.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                  clip.close();
               }
            });
         }
      } catch (Exception exception) {
      }
   }

   public void setVolume(float f) {
      this.volume = Math.max(0.0F, Math.min(1.0F, f));
   }

   public void f(SoundType soundtype, boolean flag) {
      this.soundEnabled.put(soundtype, flag);
   }

   public boolean g(SoundType soundtype) {
      return this.soundEnabled.getOrDefault(soundtype, true);
   }

   public void shutdown() {
      this.executor.shutdownNow();
      this.soundData.clear();
      this.soundEnabled.clear();
   }

   public static SoundManager getInstance() {
      return instance;
   }

   public float getVolume() {
      return this.volume;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean flag) {
      this.enabled = flag;
   }

   public boolean isMuted() {
      return this.muted;
   }

   public void setMuted(boolean flag) {
      this.muted = flag;
   }
}
