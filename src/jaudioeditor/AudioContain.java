package jaudioeditor;

import java.awt.Image;
import org.jaudiotagger.audio.AudioFile;

/**
 *
 * @author dimon
 */
public class AudioContain {

    private Image image;
    private Integer sizeBytes;
    private String format;
    private boolean changed = false;
    private AudioFile audioFile;

    public AudioContain(Image img, String format, Integer size, AudioFile audio) {
        this.image = img;
        this.format = format;
        this.sizeBytes = size;
        this.audioFile = audio;
    }

    /**
     * @return the image
     */
    public Image getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * @return the audioFile
     */
    public AudioFile getAudioFile() {
        return audioFile;
    }

    /**
     * @param audioFile the audioFile to set
     */
    public void setAudioFile(AudioFile audioFile) {
        this.audioFile = audioFile;
    }

    /**
     * @return the isChanged
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * @param isChanged the isChanged to set
     */
    public void setChanged(boolean isChanged) {
        this.changed = isChanged;
    }

    /**
     * @return the sizeBytes
     */
    public Integer getSizeBytes() {
        return sizeBytes;
    }

    /**
     * @param sizeBytes the sizeBytes to set
     */
    public void setSizeBytes(Integer sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    public void clearAll() {
        this.image = null;
        this.format = "";
        this.sizeBytes = null;
    }
}
