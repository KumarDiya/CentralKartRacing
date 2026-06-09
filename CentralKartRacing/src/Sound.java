import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Sound {
    Clip clip;
    URL soundURL[] = new URL[10];
    public Sound() {
        soundURL[0] = getClass().getResource("sound/song.wav");
        soundURL[1] = getClass().getResource("sound/engineSound.wav");
        soundURL[2] = getClass().getResource("sound/boostSound.wav");
        soundURL[3] = getClass().getResource("sound/driftSound.wav");
        soundURL[4] = getClass().getResource("sound/321go.wav");
        soundURL[5] = getClass().getResource("sound/button.wav");
    }

    public void setFile(int i){
        if (soundURL[i] == null) {
            System.out.println("Error loading sound: missing resource index " + i);
            clip = null;
            return;
        }

        try {
            if (clip != null) {
                clip.stop();
                clip.close();
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading sound: " + soundURL[i]);
            clip = null;
        }
    }

    public void play(){
        if (clip != null) {
            clip.start();
        }
    }
    public void playButton() {
        clip.stop();
        clip.setFramePosition(0); //rewinds audio sample
        clip.start();
    }

    public void loop(){
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop(){
        if (clip != null) {
            clip.stop();
        }
    }
    
}
