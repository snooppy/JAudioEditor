/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaudioeditor;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;

/**
 *
 * @author dimon
 */
public class AudioView {

    private JTable jTableArtist;
    private JTable jTableAlbum;
    private JTable jTableInfo;
    private ImageIcon changeIcon = null;
    private AudioContainer audios;
    private AudioTableRender render;

    public AudioView(JTable jTableArtist, JTable jTableAlbum, JTable jTableInfo,
            AudioContainer audios) {
        this.jTableArtist = jTableArtist;
        this.jTableAlbum = jTableAlbum;
        this.jTableInfo = jTableInfo;
        this.audios = audios;
        this.render = new AudioTableRender();
        Image img = null;
        try {
            img = ImageIO.read(new File("icons/edit.png"));
        } catch (IOException ex) {
            Logger.getLogger(AudioView.class.getName()).log(Level.SEVERE, null, ex);
        }
        img = render.createResizedCopy(img, 17, 17);
        this.changeIcon = new ImageIcon(img);
    }

    public void clearJTableInfo() {
        while (this.jTableInfo.getModel().getRowCount() > 0) {
            ((DefaultTableModel) this.jTableInfo.getModel()).removeRow(0);
        }
    }

    public void viewTracksByAlbum(String artist, String album) {
        ArrayList<AudioFile> audioFiles = this.audios.getArtists().
                get(artist).get(album);
        ImageIcon icon = new ImageIcon();
        icon = getChangeIcon();
        for (AudioFile audioFile : audioFiles) {
            Tag tag = audioFile.getTag();
            int id = this.audios.getAudioId(audioFile);
            if (!this.audios.getAudios().get(id).isChanged()) {
                icon = null;
            }
            Image artwork = this.audios.getAudios().get(id).getImage();
            artwork = render.createResizedCopy(artwork, 18, 17);
            ImageIcon iconAtrwork = new ImageIcon(artwork);
            if (tag != null) {
                ((DefaultTableModel) jTableInfo.getModel()).addRow(new Object[]{
                            icon, tag.getFirst(FieldKey.TRACK),
                            tag.getFirst(FieldKey.TITLE), tag.getFirst(FieldKey.GENRE),
                            tag.getFirst(FieldKey.ARTIST), tag.getFirst(FieldKey.ALBUM),
                            tag.getFirst(FieldKey.YEAR), tag.getFirst(FieldKey.ALBUM_ARTIST),
                            tag.getFirst(FieldKey.DISC_NO), iconAtrwork, id});
            } else {
                ((DefaultTableModel) jTableInfo.getModel()).addRow(new Object[]{
                            icon, "", "", "", "", "", "", "", "", null, id});
            }
        }
    }

    public void viewTracksByAlbums(int[] selectedRowArtist, int[] selectedRowAlbum) {
        this.clearJTableInfo();
        /*If selected All artists and All albums*/
        if (selectedRowArtist.length == 1 && selectedRowAlbum.length == 1
                && selectedRowArtist[0] == 0 && selectedRowAlbum[0] == 0) {
            Iterator itArtists = this.audios.getArtists().keySet().iterator();
            while (itArtists.hasNext()) {
                String artist = (String) itArtists.next();
                Iterator itAlbums = this.audios.getArtists().get(artist).keySet().iterator();
                while (itAlbums.hasNext()) {
                    String album = (String) itAlbums.next();
                    this.viewTracksByAlbum(artist, album);
                }
            }
            /*If selected All artists and Some albums*/
        } else if (selectedRowArtist.length == 1 && selectedRowArtist[0] == 0) {
            Iterator itArtists = this.audios.getArtists().keySet().iterator();
            while (itArtists.hasNext()) {
                String artist = (String) itArtists.next();
                Iterator itAlbums = this.audios.getArtists().get(artist).keySet().iterator();
                while (itAlbums.hasNext()) {
                    String album = (String) itAlbums.next();
                    for (int i = 0; i < selectedRowAlbum.length; i++) {
                        if (album.equals((String) jTableAlbum.getValueAt(selectedRowAlbum[i], 0))) {
                            this.viewTracksByAlbum(artist, album);
                        }
                    }
                }
            }
            /*If selected Some artists and Some albums*/
        } else if (selectedRowAlbum.length != 0 && selectedRowAlbum[0] != 0) {
            Iterator itArtists = this.audios.getArtists().keySet().iterator();
            while (itArtists.hasNext()) {
                String artist = (String) itArtists.next();
                for (int i = 0; i < selectedRowArtist.length; i++) {
                    if (artist.equals((String) jTableArtist.getValueAt(selectedRowArtist[i], 0))) {
                        Iterator itAlbums = this.audios.getArtists().get(artist).keySet().iterator();
                        while (itAlbums.hasNext()) {
                            String album = (String) itAlbums.next();
                            for (int j = 0; j < selectedRowAlbum.length; j++) {
                                if (album.equals((String) jTableAlbum.getValueAt(selectedRowAlbum[j], 0))) {
                                    this.viewTracksByAlbum(artist, album);
                                }
                            }
                        }
                    }
                }
            }
            /*If selected Some artists and All albums*/
        } else if (selectedRowAlbum.length == 1 && selectedRowAlbum[0] == 0) {
            Iterator itArtists = this.audios.getArtists().keySet().iterator();
            while (itArtists.hasNext()) {
                String artist = (String) itArtists.next();
                for (int i = 0; i < selectedRowArtist.length; i++) {
                    if (artist.equals((String) jTableArtist.getValueAt(selectedRowArtist[i], 0))) {
                        Iterator itAlbums = this.audios.getArtists().get(artist).keySet().iterator();
                        while (itAlbums.hasNext()) {
                            String album = (String) itAlbums.next();
                            this.viewTracksByAlbum(artist, album);
                        }
                    }
                }
            }
        }
    }

    public void viewAlbumsByArtists(int[] selectedRows, boolean all) {
        int numAlbums = 0;
        int numTracks = 0;
        while (jTableAlbum.getRowCount() > 1) {
            ((DefaultTableModel) jTableAlbum.getModel()).removeRow(1);
        }
        this.clearJTableInfo();
        if (all) {
            Iterator itArtists = this.audios.getArtists().keySet().iterator();
            while (itArtists.hasNext()) {
                String artist = (String) itArtists.next();

                Iterator itAlbums = this.audios.getArtists().get(artist).keySet().iterator();
                while (itAlbums.hasNext()) {
                    String album = (String) itAlbums.next();
                    if (!this.audios.containsArtOrAlb(album, jTableAlbum)
                            && !album.equals(AudioContainer.UNKNOW_ALBUM)) {
                        ((DefaultTableModel) jTableAlbum.getModel()).addRow(new Object[]{album});
                        numAlbums++;
                    }
                    this.viewTracksByAlbum(artist, album);
                    numTracks += this.audios.getArtists().get(artist).get(album).size();
                }
            }
        } else {
            for (int i = 0; i < selectedRows.length; i++) {
                int selIndex = selectedRows[i];
                String artist = (String) ((DefaultTableModel) jTableArtist.getModel()).getValueAt(selIndex, 0);
                Iterator itAlbums = this.audios.getArtists().get(artist).keySet().
                        iterator();
                while (itAlbums.hasNext()) {
                    String album = (String) itAlbums.next();
                    if (!this.audios.containsArtOrAlb(album, jTableAlbum)
                            && !album.equals(AudioContainer.UNKNOW_ALBUM)) {
                        ((DefaultTableModel) jTableAlbum.getModel()).addRow(new Object[]{album});
                        numAlbums++;
                    }
                    this.viewTracksByAlbum(artist, album);
                    numTracks += this.audios.getArtists().get(artist).get(album).size();
                }
            }
        }
        StringBuilder albumsInfo = new StringBuilder("<html>");
        albumsInfo.append("<b>All ").append(numAlbums).append(" albums(").
                append(numTracks).append(" tracks)</b>");
        ((DefaultTableModel) jTableAlbum.getModel()).setValueAt(albumsInfo, 0, 0);
    }

    public void resetViews() {
        Iterator itArtists = this.audios.getArtists().keySet().iterator();
        String keyArtist = "";
        String keyAlbum = "";
        this.clearJTableInfo();
        while (itArtists.hasNext()) {
            keyArtist = (String) itArtists.next();
            if (!keyArtist.equals(AudioContainer.UNKNOW_ARTIST)
                    && !this.audios.containsArtOrAlb(keyArtist, jTableArtist)) {
                ((DefaultTableModel) jTableArtist.getModel()).addRow(new Object[]{keyArtist});
            }
            Iterator itAlbums = this.audios.getArtists().get(keyArtist).keySet().
                    iterator();
            while (itAlbums.hasNext()) {
                keyAlbum = (String) itAlbums.next();
                if (!keyAlbum.equals(AudioContainer.UNKNOW_ALBUM)
                        && !this.audios.containsArtOrAlb(keyAlbum, jTableAlbum)) {
                    ((DefaultTableModel) jTableAlbum.getModel()).addRow(new Object[]{keyAlbum});
                }
                this.viewTracksByAlbum(keyArtist, keyAlbum);
            }
        }
        resetLengthInfo(null, null);
    }

    public void resetLengthInfo(String artist, String album) {
        StringBuilder infoArtist = new StringBuilder("<html>");
        infoArtist.append("<b>All ").append(this.audios.getArtisrtsLength()).
                append(" artists(").append(this.audios.getTracksLength(artist, album)).
                append(" tracks)</b>");
        ((DefaultTableModel) jTableArtist.getModel()).setValueAt(infoArtist, 0, 0);

        StringBuilder infoAlbum = new StringBuilder("<html>");
        infoAlbum.append("<b>All ").append(this.audios.getAlbumsLength(artist)).
                append(" albums(").append(this.audios.getTracksLength(artist, album)).
                append(" tracks)</b>");
        ((DefaultTableModel) jTableAlbum.getModel()).setValueAt(infoAlbum, 0, 0);
    }

    /**
     * @return the changeIcon
     */
    public ImageIcon getChangeIcon() {
        return changeIcon;
    }

    /**
     * @param changeIcon the changeIcon to set
     */
    public void setChangeIcon(ImageIcon changeIcon) {
        this.changeIcon = changeIcon;
    }

    public int getRowById(Integer id) {
        for (int i = 0; i < jTableInfo.getRowCount(); i++) {
            if (((Integer) jTableInfo.getValueAt(i, 10)).equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public void setTags(JLabel jLabelId, JTextField jtextField, JLabel label, FieldKey key, int column) {
        if (jTableInfo.getSelectedRows().length == 0 || jLabelId.getText().equals("")) {
            return;
        }
        int row = this.getRowById(Integer.valueOf(jLabelId.getText()));
        if (!jtextField.getText().equals((String) jTableInfo.getValueAt(row, column))) {
            jTableInfo.setValueAt(jtextField.getText(), row, column);
            if (label != null) {
                label.setText(jtextField.getText());
            }
            Integer id = Integer.valueOf(jLabelId.getText());
            AudioContain audioC = this.audios.getAudios().get(id);
            AudioFile audio = this.audios.getAudios().get(id).getAudioFile();
            try {
                audio.getTagOrCreateAndSetDefault().setField(key, jtextField.getText());
            } catch (KeyNotFoundException ex) {
                Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FieldDataInvalidException ex) {
                Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
            }
            jTableInfo.setValueAt(this.getChangeIcon(), row, 0);
            audioC.setChanged(true);
        }
    }

    public void setOtherTags(JLabel jLabelId, JTextField jtextField, FieldKey key) {
        if (jTableInfo.getSelectedRows().length == 0 || jLabelId.getText().equals("")) {
            return;
        }
        Integer id = Integer.valueOf(jLabelId.getText());
        int row = this.getRowById(id);
        AudioContain audioC = this.audios.getAudios().get(id);
        AudioFile audio = this.audios.getAudios().get(id).getAudioFile();
        String str = audio.getTag().getFirst(key);
        if (!jtextField.getText().equals(str)) {
            try {
                audio.getTagOrCreateAndSetDefault().setField(key, jtextField.getText());
            } catch (KeyNotFoundException ex) {
                Logger.getLogger(AudioView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FieldDataInvalidException ex) {
                Logger.getLogger(AudioView.class.getName()).log(Level.SEVERE, null, ex);
            }
            jTableInfo.setValueAt(this.getChangeIcon(), row, 0);
            audioC.setChanged(true);
        }
    }

    public void stopCellEditing() {
        if (jTableInfo.isEditing()) {
            jTableInfo.getDefaultEditor(String.class).stopCellEditing();
        }
    }
}
