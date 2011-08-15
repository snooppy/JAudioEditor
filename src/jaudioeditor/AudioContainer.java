/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaudioeditor;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.datatype.Artwork;

/**
 *
 * @author dimon
 */
public class AudioContainer {

    public static final String UNKNOW_ARTIST = "Uuunnnknowww Aaartisttt";
    public static final String UNKNOW_ALBUM = "Uunnnknowww Aaalbummm";
    private HashMap<String, HashMap<String, ArrayList<AudioFile>>> artists;
    private HashMap<Integer, AudioContain> audios;
    private int counter = 0;

    public AudioContainer() {
        artists = new HashMap<String, HashMap<String, ArrayList<AudioFile>>>();
        audios = new HashMap<Integer, AudioContain>();
    }

    /**
     * @return the artists
     */
    public HashMap<String, HashMap<String, ArrayList<AudioFile>>> getArtists() {
        return artists;
    }

    /**
     * @param artists the artists to set
     */
    public void setArtists(HashMap<String, HashMap<String, ArrayList<AudioFile>>> artists) {
        this.artists = artists;
    }

    /**
     * @return the audios
     */
    public HashMap<Integer, AudioContain> getAudios() {
        return audios;
    }

    /**
     * @param audios the audios to set
     */
    public void setAudios(HashMap<Integer, AudioContain> audios) {
        this.audios = audios;
    }

    /**
     * 
     * @param audiofile 
     */
    public void addAudio(AudioFile audiofile) {
        String artist = "";
        String album = "";
        Image img = null;
        Integer sizeBytes = 0;
        String format = "";
        if (audiofile.getTag() != null) {
            if (audiofile.getTag().getFirst(FieldKey.ARTIST).equals("")) {
                artist = UNKNOW_ARTIST;
            } else {
                artist = audiofile.getTag().getFirst(FieldKey.ARTIST);
            }
            if (audiofile.getTag().getFirst(FieldKey.ALBUM).equals("")) {
                album = UNKNOW_ALBUM;
            } else {
                album = audiofile.getTag().getFirst(FieldKey.ALBUM);
            }
            try {
                Artwork art = audiofile.getTag().getFirstArtwork();
                if (art != null) {
                    img = art.getImage();
                    sizeBytes = art.getBinaryData().length;
                    format = art.getMimeType();
                }
            } catch (IOException ex) {
                Logger.getLogger(AudioContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            artist = UNKNOW_ARTIST;
            album = UNKNOW_ALBUM;
        }

        if (!this.artists.containsKey(artist)) {
            this.artists.put(artist, new HashMap<String, ArrayList<AudioFile>>());
            if (!this.artists.get(artist).containsKey(album)) {
                this.artists.get(artist).put(album, new ArrayList<AudioFile>());
                this.artists.get(artist).get(album).add(audiofile);
                AudioContain container = new AudioContain(img, format, sizeBytes, audiofile);
                this.audios.put(counter++, container);
            } else if (!this.containsAudio(this.artists.get(artist).get(album), audiofile)) {
                this.artists.get(artist).get(album).add(audiofile);
                AudioContain container = new AudioContain(img, format, sizeBytes, audiofile);
                this.audios.put(counter++, container);
            }
        } else if (!this.artists.get(artist).containsKey(album)) {
            this.artists.get(artist).put(album, new ArrayList<AudioFile>());
            this.artists.get(artist).get(album).add(audiofile);
            AudioContain container = new AudioContain(img, format, sizeBytes, audiofile);
            this.audios.put(counter++, container);
        } else if (!this.containsAudio(this.artists.get(artist).get(album), audiofile)) {
            this.artists.get(artist).get(album).add(audiofile);
            AudioContain container = new AudioContain(img, format, sizeBytes, audiofile);
            this.audios.put(counter++, container);
        }
    }

    /**
     * Contains artist or album
     * @param artOrAlb
     * @param jTable
     * @return 
     */
    public boolean containsArtOrAlb(String artOrAlb, JTable jTable) {
        Vector v = ((DefaultTableModel) jTable.getModel()).getDataVector();
        for (int i = 0; i < v.size(); i++) {
            if (((Vector) v.get(i)).get(0).equals(artOrAlb)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param list
     * @param audio
     * @return 
     */
    public boolean containsAudio(ArrayList<AudioFile> list, AudioFile audio) {
        for (AudioFile audioFile : list) {
            if (audioFile.getTag() != null && audio.getTag() != null) {
                if (audio.getTag().getFirst(FieldKey.ALBUM_ARTIST).
                        equals(audioFile.getTag().getFirst(FieldKey.ALBUM_ARTIST))
                        && audio.getTag().getFirst(FieldKey.TITLE).
                        equals(audioFile.getTag().getFirst(FieldKey.TITLE))
                        && audio.getTag().getFirst(FieldKey.GENRE).
                        equals(audioFile.getTag().getFirst(FieldKey.GENRE))
                        && audio.getTag().getFirst(FieldKey.YEAR).
                        equals(audioFile.getTag().getFirst(FieldKey.YEAR))
                        && audio.getTag().getFirst(FieldKey.TRACK).
                        equals(audioFile.getTag().getFirst(FieldKey.TRACK))
                        && audio.getFile().getAbsolutePath().
                        equals(audioFile.getFile().getAbsolutePath())) {
                    return true;
                }
            } else {
                if (audio.getFile().getAbsolutePath().equals(audioFile.getFile().getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @param albums
     * @param album
     * @return 
     */
    public boolean containsAlbum(ArrayList<String> albums, String album) {
        for (String alb : albums) {
            if (alb.equals(album)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return 
     */
    public int getArtisrtsLength() {
        int size = 0;
        Iterator itArtist = this.artists.keySet().iterator();
        while (itArtist.hasNext()) {
            String artist = (String) itArtist.next();
            if (!artist.equals(UNKNOW_ARTIST)) {
                size++;
            }
        }
        return size;
    }

    /**
     * 
     * @param artist
     * @return 
     */
    public int getAlbumsLength(String artist) {
        ArrayList<String> albums = new ArrayList<String>();

        if (artist != null && !artist.equals(UNKNOW_ALBUM)) {
            return this.artists.get(artist).size();
        } else {
            int size = 0;
            Iterator itArtists = this.artists.keySet().iterator();
            while (itArtists.hasNext()) {
                artist = (String) itArtists.next();
                if (!artist.equals(UNKNOW_ARTIST)) {
                    Iterator itAlbums = this.artists.get(artist).keySet().iterator();
                    while (itAlbums.hasNext()) {
                        String album = (String) itAlbums.next();
                        if (!this.containsAlbum(albums, album) && !album.equals(UNKNOW_ALBUM)) {
                            albums.add(album);
                            size++;
                        }
                    }
                }
            }
            return size;
        }
    }

    /**
     * 
     * @param artist
     * @param album
     * @return 
     */
    public int getTracksLength(String artist, String album) {
        if (artist == null && album == null) {
            int size = 0;
            Iterator itArtists = this.artists.keySet().iterator();
            while (itArtists.hasNext()) {
                artist = (String) itArtists.next();
                Iterator itAlbums = this.artists.get(artist).keySet().iterator();
                while (itAlbums.hasNext()) {
                    album = (String) itAlbums.next();
                    size += this.artists.get(artist).get(album).size();
                }
            }
            return size;
        } else if (artist != null && album == null) {
            int size = 0;
            Iterator itAlbums = this.artists.get(artist).keySet().iterator();
            while (itAlbums.hasNext()) {
                album = (String) itAlbums.next();
                size += this.artists.get(artist).get(album).size();
            }
            return size;
        }
        return 0;
    }

    public int getAudioId(AudioFile audioFile) {
        Set<Integer> itAudio = this.audios.keySet();
        for (Integer key : itAudio) {
            AudioContain audio = this.audios.get(key);
            if (audio.getAudioFile() == audioFile) {
                return key;
            }
        }
        return -1;
    }

    public String getAudioLength(AudioFile audio) {
        int length = audio.getAudioHeader().getTrackLength();
        int min = length / 60;
        int sec = length % 60;
        String minute = "";
        String second = "";
        if (min < 10) {
            minute = "0" + String.valueOf(min);
        } else {
            minute = String.valueOf(min);
        }
        if (sec < 10) {
            second = "0" + String.valueOf(sec);
        } else {
            second = String.valueOf(sec);
        }
        return minute + ":" + second;
    }

    public String getAudioSize(AudioFile audio) {
        long size = audio.getFile().length();
        if (size / (1024 * 1024) > 0) {
            return String.format("%.2f", (double) size / (1024 * 1024)) + " MB";
        } else {
            return String.format("%.2f", (double) size / 1024) + " KB";
        }
    }

    public String getAudioBitRate(AudioFile audio) {
        String bitrate = audio.getAudioHeader().getBitRate();
        if (audio.getAudioHeader().isVariableBitRate()) {
            return bitrate + " kbps (VBR)";
        } else {
            return bitrate + " kbps";
        }
    }

    public boolean isChanged() {
        Iterator itAudio = this.audios.keySet().iterator();
        while (itAudio.hasNext()) {
            Integer key = (Integer) itAudio.next();
            AudioContain audioC = this.audios.get(key);
            if (audioC.isChanged()) {
                return true;
            }
        }
        return false;
    }
    
    public void save(){
     Iterator itAudio = this.audios.keySet().iterator();
        while (itAudio.hasNext()) {
            Integer key = (Integer) itAudio.next();
            AudioContain audioC = this.audios.get(key);
            if (audioC.isChanged()) {
                try {
                    audioC.getAudioFile().commit();
                } catch (CannotWriteException ex) {
                    Logger.getLogger(AudioContainer.class.getName()).log(Level.SEVERE, null, ex);
                }
                audioC.setChanged(false);
            }
        }
    }
}
