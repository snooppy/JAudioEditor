/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JFrameArtworkEdit.java
 *
 * Created on 11.08.2011, 17:52:20
 */
package jaudioeditor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.datatype.Artwork;

/**
 *
 * @author dimon
 */
public class JFrameArtworkEdit extends javax.swing.JFrame {

    private int id;
    private JLabel img;
    private JTable info;
    private ImageIcon change;
    private AudioContainer audios;

    /** Creates new form JFrameArtworkEdit */
    public JFrameArtworkEdit() {
        initComponents();
    }

    public JFrameArtworkEdit(Image img, String format, Integer size, int id,
            JLabel image, JTable info, AudioContainer audios, ImageIcon change) {
        initComponents();
        this.viewArtwork(img, format, size);
        this.id = id;
        this.img = image;
        this.info = info;
        this.change = change;
        this.audios = audios;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabelArtwork = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabelSizePixels = new javax.swing.JLabel();
        jLabelSize = new javax.swing.JLabel();
        jLabelSizeBytes = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabelFormat = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(jaudioeditor.JAudioEditorApp.class).getContext().getResourceMap(JFrameArtworkEdit.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(530, 355));
        setName("Form"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(300, 300));
        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        jLabelArtwork.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelArtwork.setText(resourceMap.getString("jLabelArtwork.text")); // NOI18N
        jLabelArtwork.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jLabelArtwork.setName("jLabelArtwork"); // NOI18N
        jPanel2.add(jLabelArtwork);

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabelSizePixels.setFont(resourceMap.getFont("jLabelSizePixels.font")); // NOI18N
        jLabelSizePixels.setText(resourceMap.getString("jLabelSizePixels.text")); // NOI18N
        jLabelSizePixels.setName("jLabelSizePixels"); // NOI18N

        jLabelSize.setFont(resourceMap.getFont("jLabelSize.font")); // NOI18N
        jLabelSize.setText(resourceMap.getString("jLabelSize.text")); // NOI18N
        jLabelSize.setName("jLabelSize"); // NOI18N

        jLabelSizeBytes.setFont(resourceMap.getFont("jLabelSizeBytes.font")); // NOI18N
        jLabelSizeBytes.setText(resourceMap.getString("jLabelSizeBytes.text")); // NOI18N
        jLabelSizeBytes.setName("jLabelSizeBytes"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabelFormat.setFont(resourceMap.getFont("jLabelFormat.font")); // NOI18N
        jLabelFormat.setText(resourceMap.getString("jLabelFormat.text")); // NOI18N
        jLabelFormat.setName("jLabelFormat"); // NOI18N

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabelSize)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelSizePixels, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                            .addComponent(jLabelSizeBytes, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                            .addComponent(jLabelFormat, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabelSizePixels))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelSize)
                            .addComponent(jLabelSizeBytes))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabelFormat))
                        .addGap(52, 52, 52)
                        .addComponent(jButton1)
                        .addGap(45, 45, 45)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    this.dispose();
}//GEN-LAST:event_jButton3ActionPerformed

    private JFileChooser initFilters(JFileChooser fileChooser) {

        String[] extAll = {"jpg", "jpeg", "JPG", "JPEG", "png", "PNG", "gif", "GIF"};
        String[] extJPEG = {"jpg", "jpeg", "JPG", "JPEG"};
        String[] extPNG = {"png", "PNG"};
        String[] extGIF = {"gif", "GIF"};

        AudioFileFilter fjpg = new AudioFileFilter(extJPEG, "JPEG Files(JPG,"
                + " JPEG)");
        AudioFileFilter fpng = new AudioFileFilter(extPNG, "PNG(PNG)");
        AudioFileFilter fgif = new AudioFileFilter(extGIF, "GIF(GIF)");
        AudioFileFilter fall = new AudioFileFilter(extAll, "Image Files(JPG,"
                + " JPEG, PNG, GIF )");

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(fjpg);
        fileChooser.addChoosableFileFilter(fpng);
        fileChooser.addChoosableFileFilter(fgif);
        fileChooser.addChoosableFileFilter(fall);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        return fileChooser;
    }

    private int getRowById(Integer id) {
        for (int i = 0; i < info.getRowCount(); i++) {
            if (((Integer) info.getValueAt(i, 10)).equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void viewArtwork(Image img, String format, Integer size) {
        if (img != null) {
            AudioTableRender render = new AudioTableRender();
            BufferedImage bimg = (BufferedImage) img;
            int imgWidth = bimg.getWidth();
            int imgHight = bimg.getHeight();
            int jPanelWidth = jPanel2.getWidth();
            int jPnelHight = jPanel2.getHeight();
            jLabelSizePixels.setText(imgWidth + "x" + imgHight);
            jLabelFormat.setText(format);
            jLabelSizeBytes.setText(size.toString());
            if (imgWidth < jPanelWidth && imgHight >= jPnelHight) {
                img = render.createResizedCopy(img, imgWidth, jPnelHight);
            } else if (imgWidth >= jPanelWidth && imgHight < jPnelHight) {
                img = render.createResizedCopy(img, jPanelWidth, imgHight);
            } else if (imgWidth > jPanelWidth && imgHight > jPnelHight) {
                img = render.createResizedCopy(img, jPanelWidth, jPnelHight);
            }
            jLabelArtwork.setIcon(new ImageIcon(img));
        } else {
            jLabelArtwork.setIcon(null);
        }
    }

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    if (jLabelArtwork.getIcon() != null) {
        AudioContain audioC = this.audios.getAudios().get(id);
        audioC.getAudioFile().getTag().deleteArtworkField();
        audioC.clearAll();
        jLabelArtwork.setIcon(null);
        jLabelFormat.setText("");
        jLabelSizeBytes.setText("");
        jLabelSizePixels.setText("");
        img.setIcon(null);
        int row = this.getRowById(id);
        info.setValueAt(null, row, 9);
        info.setValueAt(change, row, 0);
        audioC.setChanged(true);
    }
}//GEN-LAST:event_jButton1ActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    File file = null;
    JFileChooser fileChooser = new JFileChooser();
    fileChooser = initFilters(fileChooser);
    int ret = fileChooser.showDialog(null, "Open file");
    if (ret == JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
    } else if (ret == JFileChooser.ERROR_OPTION) {
        System.out.print("Error of choosen file");
        return;
    }
    if (file != null) {
        Image artwork = null;
        try {
            artwork = ImageIO.read(file);
        } catch (IOException ex) {
            Logger.getLogger(JFrameArtworkEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        Artwork art = new Artwork();
        AudioContain audioC = this.audios.getAudios().get(id);
        try {
            art.setFromFile(file);
        } catch (IOException ex) {
            Logger.getLogger(JFrameArtworkEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            audioC.getAudioFile().getTagOrCreateAndSetDefault().setField(art);
        } catch (FieldDataInvalidException ex) {
            Logger.getLogger(JFrameArtworkEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        audioC.setImage(artwork);
        audioC.setSizeBytes(art.getBinaryData().length);
        audioC.setFormat(art.getMimeType());
        this.viewArtwork(artwork, audioC.getFormat(), audioC.getSizeBytes());
        audioC.setChanged(true);
        int row = this.getRowById(id);
        AudioTableRender render = new AudioTableRender();
        Image image = render.createResizedCopy(artwork, 148, 148);
        artwork = render.createResizedCopy(artwork, 18, 17);
        ImageIcon iconAtrwork = new ImageIcon(artwork);
        img.setIcon(new ImageIcon(image));
        info.setValueAt(iconAtrwork, row, 9);
        info.setValueAt(change, row, 0);
    }
}//GEN-LAST:event_jButton2ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelArtwork;
    private javax.swing.JLabel jLabelFormat;
    private javax.swing.JLabel jLabelSize;
    private javax.swing.JLabel jLabelSizeBytes;
    private javax.swing.JLabel jLabelSizePixels;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
