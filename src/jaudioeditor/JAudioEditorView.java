/*
 * JAudioEditorView.java
 */
package jaudioeditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledEditorKit;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;

/**
 * The application's main frame.
 */
public class JAudioEditorView extends FrameView {

    private String currDirectory;
    private Properties props;
    private AudioContainer audios = new AudioContainer();
    private AudioView audioView;
    private AudioTableRender render = new AudioTableRender();

    public JAudioEditorView(JAudioSingleFrameApp app) {
        super(app);

        initComponents();

        initJFrame();

        initTables();

        initProperties();

        initJTextPane();

        initJComboBoxGenre();


        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Override
    public JFrame getFrame() {
        return super.getFrame();
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = JAudioEditorApp.getApplication().getMainFrame();
            aboutBox = new JAudioEditorAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        JAudioEditorApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableArtist = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableAlbum = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableInfo = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelSummary = new javax.swing.JPanel();
        jLabelImg = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabelTitle1 = new javax.swing.JLabel();
        jLabelArtist1 = new javax.swing.JLabel();
        jLabelAlbumArtist1 = new javax.swing.JLabel();
        jLabelAlbum1 = new javax.swing.JLabel();
        jLabelLocation1 = new javax.swing.JLabel();
        jLabelTime1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jLabelArtist = new javax.swing.JLabel();
        jLabelAlbumArtist = new javax.swing.JLabel();
        jLabelAlbum = new javax.swing.JLabel();
        jLabelTime = new javax.swing.JLabel();
        jLabelLocation = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabelChannels1 = new javax.swing.JLabel();
        jLabelFormat = new javax.swing.JLabel();
        jLabelSize = new javax.swing.JLabel();
        jLabelBitRate = new javax.swing.JLabel();
        jLabelFrequency = new javax.swing.JLabel();
        jLabelChannels = new javax.swing.JLabel();
        jPanelEdit = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldTitle = new javax.swing.JTextField();
        jTextFieldArtist = new javax.swing.JTextField();
        jTextFieldAlbumArtist = new javax.swing.JTextField();
        jTextFieldAlbum = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldYear = new javax.swing.JTextField();
        jTextFieldTrackNo = new javax.swing.JTextField();
        jTextFieldDiscNo = new javax.swing.JTextField();
        jComboBoxGenre = new javax.swing.JComboBox();
        jScrollPaneComments = new javax.swing.JScrollPane();
        jTextPaneComments = new javax.swing.JTextPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextFieldProducer = new javax.swing.JTextField();
        jTextFieldComposer = new javax.swing.JTextField();
        jTextFieldRemixer = new javax.swing.JTextField();
        jTextFieldDjMixer = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTextFieldMixer = new javax.swing.JTextField();
        jTextFieldLyricist = new javax.swing.JTextField();
        jTextFieldWikiRelease = new javax.swing.JTextField();
        jTextFieldDIscogsRelease = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jTextFieldWikiArtistSite = new javax.swing.JTextField();
        jTextFieldOffArtistSite = new javax.swing.JTextField();
        jTextFieldDiscogsArtistUrl = new javax.swing.JTextField();
        jTextFieldOffReleaseSite = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPaneLyrics = new javax.swing.JTextPane();
        jTextFieldLyricsUrl = new javax.swing.JTextField();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        openFileMenuItem = new javax.swing.JMenuItem();
        openFolderMenuItem1 = new javax.swing.JMenuItem();
        saveMenuItem1 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jLabelId = new javax.swing.JLabel();

        mainPanel.setMinimumSize(new java.awt.Dimension(500, 400));
        mainPanel.setName("mainPanel"); // NOI18N

        jPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridLayout(1, 4, 10, 0));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(jaudioeditor.JAudioEditorApp.class).getContext().getResourceMap(JAudioEditorView.class);
        jTableArtist.setFont(resourceMap.getFont("jTableArtist.font")); // NOI18N
        jTableArtist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Artists"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableArtist.setInheritsPopupMenu(true);
        jTableArtist.setName("jTableArtist"); // NOI18N
        jTableArtist.setShowHorizontalLines(false);
        jTableArtist.setShowVerticalLines(false);
        jScrollPane1.setViewportView(jTableArtist);

        jPanel1.add(jScrollPane1);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTableAlbum.setFont(resourceMap.getFont("jTableAlbum.font")); // NOI18N
        jTableAlbum.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Album"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableAlbum.setName("jTableAlbum"); // NOI18N
        jTableAlbum.setShowHorizontalLines(false);
        jTableAlbum.setShowVerticalLines(false);
        jScrollPane2.setViewportView(jTableAlbum);

        jPanel1.add(jScrollPane2);

        jPanel2.setAutoscrolls(true);
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTableInfo.setFont(resourceMap.getFont("jTableInfo.font")); // NOI18N
        jTableInfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Status", "No", "Title", "Genre", "Artist", "Album", "Year", "Album Artist", "Disk", "Atrwork", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableInfo.setGridColor(resourceMap.getColor("jTableInfo.gridColor")); // NOI18N
        jTableInfo.setMinimumSize(new java.awt.Dimension(0, 0));
        jTableInfo.setName("jTableInfo"); // NOI18N
        jTableInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableInfoMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableInfo);

        jPanel2.add(jScrollPane3);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        jTabbedPane1.setFont(resourceMap.getFont("jTabbedPane1.font")); // NOI18N
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanelSummary.setAutoscrolls(true);
        jPanelSummary.setName("jPanelSummary"); // NOI18N
        jPanelSummary.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelImg.setText(resourceMap.getString("jLabelImg.text")); // NOI18N
        jLabelImg.setBorder(null);
        jLabelImg.setName("jLabelImg"); // NOI18N
        jLabelImg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelImgMouseClicked(evt);
            }
        });
        jPanelSummary.add(jLabelImg, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 148, 140));

        jPanel4.setBorder(null);
        jPanel4.setFont(resourceMap.getFont("jPanel4.font")); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        jLabelTitle1.setFont(resourceMap.getFont("jLabelTitle1.font")); // NOI18N
        jLabelTitle1.setText(resourceMap.getString("jLabelTitle1.text")); // NOI18N
        jLabelTitle1.setName("jLabelTitle1"); // NOI18N
        jLabelTitle1.setOpaque(true);

        jLabelArtist1.setFont(resourceMap.getFont("jLabelArtist1.font")); // NOI18N
        jLabelArtist1.setText(resourceMap.getString("jLabelArtist1.text")); // NOI18N
        jLabelArtist1.setName("jLabelArtist1"); // NOI18N

        jLabelAlbumArtist1.setFont(resourceMap.getFont("jLabelAlbumArtist1.font")); // NOI18N
        jLabelAlbumArtist1.setText(resourceMap.getString("jLabelAlbumArtist1.text")); // NOI18N
        jLabelAlbumArtist1.setName("jLabelAlbumArtist1"); // NOI18N

        jLabelAlbum1.setFont(resourceMap.getFont("jLabelAlbum1.font")); // NOI18N
        jLabelAlbum1.setText(resourceMap.getString("jLabelAlbum1.text")); // NOI18N
        jLabelAlbum1.setName("jLabelAlbum1"); // NOI18N

        jLabelLocation1.setFont(resourceMap.getFont("jLabelLocation1.font")); // NOI18N
        jLabelLocation1.setText(resourceMap.getString("jLabelLocation1.text")); // NOI18N
        jLabelLocation1.setName("jLabelLocation1"); // NOI18N

        jLabelTime1.setFont(resourceMap.getFont("jLabelTime1.font")); // NOI18N
        jLabelTime1.setText(resourceMap.getString("jLabelTime1.text")); // NOI18N
        jLabelTime1.setName("jLabelTime1"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelAlbum1)
                    .addComponent(jLabelAlbumArtist1)
                    .addComponent(jLabelTime1)
                    .addComponent(jLabelTitle1)
                    .addComponent(jLabelArtist1)
                    .addComponent(jLabelLocation1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabelTitle1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelArtist1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelAlbumArtist1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelAlbum1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelTime1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelLocation1)
                .addGap(10, 10, 10))
        );

        jPanelSummary.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 90, 140));

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel5.setName("jPanel5"); // NOI18N

        jLabelTitle.setFont(resourceMap.getFont("jLabelTitle.font")); // NOI18N
        jLabelTitle.setText(resourceMap.getString("jLabelTitle.text")); // NOI18N
        jLabelTitle.setName("jLabelTitle"); // NOI18N

        jLabelArtist.setFont(resourceMap.getFont("jLabelArtist.font")); // NOI18N
        jLabelArtist.setText(resourceMap.getString("jLabelArtist.text")); // NOI18N
        jLabelArtist.setName("jLabelArtist"); // NOI18N

        jLabelAlbumArtist.setFont(resourceMap.getFont("jLabelAlbumArtist.font")); // NOI18N
        jLabelAlbumArtist.setText(resourceMap.getString("jLabelAlbumArtist.text")); // NOI18N
        jLabelAlbumArtist.setName("jLabelAlbumArtist"); // NOI18N

        jLabelAlbum.setFont(resourceMap.getFont("jLabelAlbum.font")); // NOI18N
        jLabelAlbum.setText(resourceMap.getString("jLabelAlbum.text")); // NOI18N
        jLabelAlbum.setName("jLabelAlbum"); // NOI18N

        jLabelTime.setFont(resourceMap.getFont("jLabelTime.font")); // NOI18N
        jLabelTime.setText(resourceMap.getString("jLabelTime.text")); // NOI18N
        jLabelTime.setName("jLabelTime"); // NOI18N

        jLabelLocation.setFont(resourceMap.getFont("jLabelLocation.font")); // NOI18N
        jLabelLocation.setText(resourceMap.getString("jLabelLocation.text")); // NOI18N
        jLabelLocation.setName("jLabelLocation"); // NOI18N

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel7.setName("jPanel7"); // NOI18N

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabelChannels1.setFont(resourceMap.getFont("jLabelChannels1.font")); // NOI18N
        jLabelChannels1.setText(resourceMap.getString("jLabelChannels1.text")); // NOI18N
        jLabelChannels1.setName("jLabelChannels1"); // NOI18N

        jLabelFormat.setFont(resourceMap.getFont("jLabelFormat.font")); // NOI18N
        jLabelFormat.setText(resourceMap.getString("jLabelFormat.text")); // NOI18N
        jLabelFormat.setName("jLabelFormat"); // NOI18N

        jLabelSize.setFont(resourceMap.getFont("jLabelSize.font")); // NOI18N
        jLabelSize.setText(resourceMap.getString("jLabelSize.text")); // NOI18N
        jLabelSize.setName("jLabelSize"); // NOI18N

        jLabelBitRate.setFont(resourceMap.getFont("jLabelBitRate.font")); // NOI18N
        jLabelBitRate.setText(resourceMap.getString("jLabelBitRate.text")); // NOI18N
        jLabelBitRate.setName("jLabelBitRate"); // NOI18N

        jLabelFrequency.setFont(resourceMap.getFont("jLabelFrequency.font")); // NOI18N
        jLabelFrequency.setText(resourceMap.getString("jLabelFrequency.text")); // NOI18N
        jLabelFrequency.setName("jLabelFrequency"); // NOI18N

        jLabelChannels.setText(resourceMap.getString("jLabelChannels.text")); // NOI18N
        jLabelChannels.setName("jLabelChannels"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelSize, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                            .addComponent(jLabelFormat, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                            .addComponent(jLabelBitRate, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabelChannels1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelChannels, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addComponent(jLabelFrequency, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabelFormat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabelSize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabelBitRate))
                .addGap(7, 7, 7)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabelFrequency))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelChannels1)
                    .addComponent(jLabelChannels)))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                    .addComponent(jLabelArtist, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelAlbumArtist, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelAlbum, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                    .addComponent(jLabelTime, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabelLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 714, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabelTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelArtist)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelAlbumArtist)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelAlbum)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTime))
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelLocation)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelSummary.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 740, 140));

        jTabbedPane1.addTab(resourceMap.getString("jPanelSummary.TabConstraints.tabTitle"), jPanelSummary); // NOI18N

        jPanelEdit.setName("jPanelEdit"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jTextFieldTitle.setFont(resourceMap.getFont("jTextFieldTitle.font")); // NOI18N
        jTextFieldTitle.setText(resourceMap.getString("jTextFieldTitle.text")); // NOI18N
        jTextFieldTitle.setName("jTextFieldTitle"); // NOI18N
        jTextFieldTitle.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldTitleFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldTitleFocusLost(evt);
            }
        });

        jTextFieldArtist.setFont(resourceMap.getFont("jTextFieldArtist.font")); // NOI18N
        jTextFieldArtist.setText(resourceMap.getString("jTextFieldArtist.text")); // NOI18N
        jTextFieldArtist.setName("jTextFieldArtist"); // NOI18N
        jTextFieldArtist.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldArtistFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldArtistFocusLost(evt);
            }
        });

        jTextFieldAlbumArtist.setFont(resourceMap.getFont("jTextFieldAlbumArtist.font")); // NOI18N
        jTextFieldAlbumArtist.setText(resourceMap.getString("jTextFieldAlbumArtist.text")); // NOI18N
        jTextFieldAlbumArtist.setName("jTextFieldAlbumArtist"); // NOI18N
        jTextFieldAlbumArtist.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldAlbumArtistFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldAlbumArtistFocusLost(evt);
            }
        });

        jTextFieldAlbum.setFont(resourceMap.getFont("jTextFieldAlbum.font")); // NOI18N
        jTextFieldAlbum.setText(resourceMap.getString("jTextFieldAlbum.text")); // NOI18N
        jTextFieldAlbum.setName("jTextFieldAlbum"); // NOI18N
        jTextFieldAlbum.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldAlbumFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldAlbumFocusLost(evt);
            }
        });

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jTextFieldYear.setFont(resourceMap.getFont("jTextFieldYear.font")); // NOI18N
        jTextFieldYear.setText(resourceMap.getString("jTextFieldYear.text")); // NOI18N
        jTextFieldYear.setName("jTextFieldYear"); // NOI18N
        jTextFieldYear.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldYearFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldYearFocusLost(evt);
            }
        });

        jTextFieldTrackNo.setFont(resourceMap.getFont("jTextFieldTrackNo.font")); // NOI18N
        jTextFieldTrackNo.setText(resourceMap.getString("jTextFieldTrackNo.text")); // NOI18N
        jTextFieldTrackNo.setName("jTextFieldTrackNo"); // NOI18N
        jTextFieldTrackNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldTrackNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldTrackNoFocusLost(evt);
            }
        });

        jTextFieldDiscNo.setFont(resourceMap.getFont("jTextFieldDiscNo.font")); // NOI18N
        jTextFieldDiscNo.setText(resourceMap.getString("jTextFieldDiscNo.text")); // NOI18N
        jTextFieldDiscNo.setName("jTextFieldDiscNo"); // NOI18N
        jTextFieldDiscNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldDiscNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldDiscNoFocusLost(evt);
            }
        });

        jComboBoxGenre.setEditable(true);
        jComboBoxGenre.setFont(resourceMap.getFont("jComboBoxGenre.font")); // NOI18N
        jComboBoxGenre.setName("jComboBoxGenre"); // NOI18N
        jComboBoxGenre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jComboBoxGenreFocusLost(evt);
            }
        });

        jScrollPaneComments.setName("jScrollPaneComments"); // NOI18N

        jTextPaneComments.setFont(resourceMap.getFont("jTextPaneComments.font")); // NOI18N
        jTextPaneComments.setText(resourceMap.getString("jTextPaneComments.text")); // NOI18N
        jTextPaneComments.setCaretColor(resourceMap.getColor("jTextPaneComments.caretColor")); // NOI18N
        jTextPaneComments.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextPaneComments.setName("jTextPaneComments"); // NOI18N
        jTextPaneComments.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextPaneCommentsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextPaneCommentsFocusLost(evt);
            }
        });
        jScrollPaneComments.setViewportView(jTextPaneComments);

        javax.swing.GroupLayout jPanelEditLayout = new javax.swing.GroupLayout(jPanelEdit);
        jPanelEdit.setLayout(jPanelEditLayout);
        jPanelEditLayout.setHorizontalGroup(
            jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(5, 5, 5)
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                    .addComponent(jTextFieldArtist)
                    .addComponent(jTextFieldAlbumArtist)
                    .addComponent(jTextFieldAlbum))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel9)
                    .addComponent(jLabel12)
                    .addComponent(jLabel11))
                .addGap(2, 2, 2)
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jComboBoxGenre, 0, 163, Short.MAX_VALUE)
                    .addComponent(jTextFieldTrackNo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(jTextFieldYear, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(jTextFieldDiscNo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneComments, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelEditLayout.setVerticalGroup(
            jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPaneComments, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelEditLayout.createSequentialGroup()
                        .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextFieldTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jTextFieldYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelEditLayout.createSequentialGroup()
                                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(jTextFieldArtist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(jTextFieldAlbumArtist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11)
                                    .addComponent(jTextFieldDiscNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(jTextFieldAlbum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12)
                                    .addComponent(jComboBoxGenre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel10)
                                .addComponent(jTextFieldTrackNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(24, 24, 24))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanelEdit.TabConstraints.tabTitle"), jPanelEdit); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N

        jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel16.setFont(resourceMap.getFont("jLabel16.font")); // NOI18N
        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        jTextFieldProducer.setFont(resourceMap.getFont("jTextFieldProducer.font")); // NOI18N
        jTextFieldProducer.setText(resourceMap.getString("jTextFieldProducer.text")); // NOI18N
        jTextFieldProducer.setName("jTextFieldProducer"); // NOI18N
        jTextFieldProducer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldProducerFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldProducerFocusLost(evt);
            }
        });

        jTextFieldComposer.setFont(resourceMap.getFont("jTextFieldComposer.font")); // NOI18N
        jTextFieldComposer.setText(resourceMap.getString("jTextFieldComposer.text")); // NOI18N
        jTextFieldComposer.setName("jTextFieldComposer"); // NOI18N
        jTextFieldComposer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldComposerFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldComposerFocusLost(evt);
            }
        });

        jTextFieldRemixer.setFont(resourceMap.getFont("jTextFieldRemixer.font")); // NOI18N
        jTextFieldRemixer.setText(resourceMap.getString("jTextFieldRemixer.text")); // NOI18N
        jTextFieldRemixer.setName("jTextFieldRemixer"); // NOI18N
        jTextFieldRemixer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldRemixerFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldRemixerFocusLost(evt);
            }
        });

        jTextFieldDjMixer.setFont(resourceMap.getFont("jTextFieldDjMixer.font")); // NOI18N
        jTextFieldDjMixer.setText(resourceMap.getString("jTextFieldDjMixer.text")); // NOI18N
        jTextFieldDjMixer.setName("jTextFieldDjMixer"); // NOI18N
        jTextFieldDjMixer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldDjMixerFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldDjMixerFocusLost(evt);
            }
        });

        jLabel17.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        jLabel18.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        jLabel19.setFont(resourceMap.getFont("jLabel19.font")); // NOI18N
        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        jLabel20.setFont(resourceMap.getFont("jLabel20.font")); // NOI18N
        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jTextFieldMixer.setFont(resourceMap.getFont("jTextFieldMixer.font")); // NOI18N
        jTextFieldMixer.setText(resourceMap.getString("jTextFieldMixer.text")); // NOI18N
        jTextFieldMixer.setName("jTextFieldMixer"); // NOI18N
        jTextFieldMixer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldMixerFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldMixerFocusLost(evt);
            }
        });

        jTextFieldLyricist.setFont(resourceMap.getFont("jTextFieldLyricist.font")); // NOI18N
        jTextFieldLyricist.setText(resourceMap.getString("jTextFieldLyricist.text")); // NOI18N
        jTextFieldLyricist.setName("jTextFieldLyricist"); // NOI18N
        jTextFieldLyricist.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldLyricistFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldLyricistFocusLost(evt);
            }
        });

        jTextFieldWikiRelease.setFont(resourceMap.getFont("jTextFieldWikiRelease.font")); // NOI18N
        jTextFieldWikiRelease.setText(resourceMap.getString("jTextFieldWikiRelease.text")); // NOI18N
        jTextFieldWikiRelease.setName("jTextFieldWikiRelease"); // NOI18N
        jTextFieldWikiRelease.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldWikiReleaseFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldWikiReleaseFocusLost(evt);
            }
        });

        jTextFieldDIscogsRelease.setFont(resourceMap.getFont("jTextFieldDIscogsRelease.font")); // NOI18N
        jTextFieldDIscogsRelease.setText(resourceMap.getString("jTextFieldDIscogsRelease.text")); // NOI18N
        jTextFieldDIscogsRelease.setName("jTextFieldDIscogsRelease"); // NOI18N
        jTextFieldDIscogsRelease.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldDIscogsReleaseFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldDIscogsReleaseFocusLost(evt);
            }
        });

        jLabel22.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        jLabel23.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        jLabel24.setFont(resourceMap.getFont("jLabel24.font")); // NOI18N
        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        jTextFieldWikiArtistSite.setFont(resourceMap.getFont("jTextFieldWikiArtistSite.font")); // NOI18N
        jTextFieldWikiArtistSite.setText(resourceMap.getString("jTextFieldWikiArtistSite.text")); // NOI18N
        jTextFieldWikiArtistSite.setName("jTextFieldWikiArtistSite"); // NOI18N
        jTextFieldWikiArtistSite.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldWikiArtistSiteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldWikiArtistSiteFocusLost(evt);
            }
        });

        jTextFieldOffArtistSite.setFont(resourceMap.getFont("jTextFieldOffArtistSite.font")); // NOI18N
        jTextFieldOffArtistSite.setText(resourceMap.getString("jTextFieldOffArtistSite.text")); // NOI18N
        jTextFieldOffArtistSite.setName("jTextFieldOffArtistSite"); // NOI18N
        jTextFieldOffArtistSite.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldOffArtistSiteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldOffArtistSiteFocusLost(evt);
            }
        });

        jTextFieldDiscogsArtistUrl.setFont(resourceMap.getFont("jTextFieldDiscogsArtistUrl.font")); // NOI18N
        jTextFieldDiscogsArtistUrl.setText(resourceMap.getString("jTextFieldDiscogsArtistUrl.text")); // NOI18N
        jTextFieldDiscogsArtistUrl.setName("jTextFieldDiscogsArtistUrl"); // NOI18N
        jTextFieldDiscogsArtistUrl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldDiscogsArtistUrlFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldDiscogsArtistUrlFocusLost(evt);
            }
        });

        jTextFieldOffReleaseSite.setFont(resourceMap.getFont("jTextFieldOffReleaseSite.font")); // NOI18N
        jTextFieldOffReleaseSite.setText(resourceMap.getString("jTextFieldOffReleaseSite.text")); // NOI18N
        jTextFieldOffReleaseSite.setName("jTextFieldOffReleaseSite"); // NOI18N
        jTextFieldOffReleaseSite.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldOffReleaseSiteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldOffReleaseSiteFocusLost(evt);
            }
        });

        jLabel25.setFont(resourceMap.getFont("jLabel25.font")); // NOI18N
        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addGap(2, 2, 2)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextFieldRemixer, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldComposer, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldDjMixer, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldProducer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel19)
                    .addComponent(jLabel18)
                    .addComponent(jLabel20))
                .addGap(1, 1, 1)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldDIscogsRelease)
                    .addComponent(jTextFieldWikiRelease)
                    .addComponent(jTextFieldLyricist)
                    .addComponent(jTextFieldMixer, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jLabel23)
                    .addComponent(jLabel22)
                    .addComponent(jLabel25))
                .addGap(3, 3, 3)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldOffArtistSite, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jTextFieldWikiArtistSite, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jTextFieldDiscogsArtistUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jTextFieldOffReleaseSite, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jTextFieldProducer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jTextFieldComposer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18)
                            .addComponent(jTextFieldLyricist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(jTextFieldWikiArtistSite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jTextFieldRemixer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jTextFieldDjMixer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20)
                            .addComponent(jTextFieldDIscogsRelease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24)
                            .addComponent(jTextFieldOffReleaseSite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jTextFieldMixer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25)
                            .addComponent(jTextFieldOffArtistSite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(jTextFieldWikiRelease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23)
                            .addComponent(jTextFieldDiscogsArtistUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

        jPanel8.setName("jPanel8"); // NOI18N

        jLabel21.setFont(resourceMap.getFont("jLabel21.font")); // NOI18N
        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        jLabel26.setFont(resourceMap.getFont("jLabel26.font")); // NOI18N
        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jTextPaneLyrics.setFont(resourceMap.getFont("jTextPaneLyrics.font")); // NOI18N
        jTextPaneLyrics.setName("jTextPaneLyrics"); // NOI18N
        jTextPaneLyrics.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextPaneLyricsFocusLost(evt);
            }
        });
        jScrollPane4.setViewportView(jTextPaneLyrics);

        jTextFieldLyricsUrl.setFont(resourceMap.getFont("jTextFieldLyricsUrl.font")); // NOI18N
        jTextFieldLyricsUrl.setText(resourceMap.getString("jTextFieldLyricsUrl.text")); // NOI18N
        jTextFieldLyricsUrl.setName("jTextFieldLyricsUrl"); // NOI18N
        jTextFieldLyricsUrl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldLyricsUrlFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldLyricsUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE))
                .addGap(156, 156, 156))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jTextFieldLyricsUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel8.TabConstraints.tabTitle"), jPanel8); // NOI18N

        jPanel3.add(jTabbedPane1);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 186, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        openFileMenuItem.setText(resourceMap.getString("openFileMenuItem.text")); // NOI18N
        openFileMenuItem.setName("openFileMenuItem"); // NOI18N
        openFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openFileMenuItem);

        openFolderMenuItem1.setText(resourceMap.getString("openFolderMenuItem1.text")); // NOI18N
        openFolderMenuItem1.setName("openFolderMenuItem1"); // NOI18N
        openFolderMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFolderMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(openFolderMenuItem1);

        saveMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem1.setText(resourceMap.getString("saveMenuItem1.text")); // NOI18N
        saveMenuItem1.setName("saveMenuItem1"); // NOI18N
        saveMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(jaudioeditor.JAudioEditorApp.class).getContext().getActionMap(JAudioEditorView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        jLabelId.setText(resourceMap.getString("jLabelId.text")); // NOI18N
        jLabelId.setName("jLabelId"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1022, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(statusMessageLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 836, Short.MAX_VALUE))
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelId)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statusMessageLabel)
                            .addComponent(statusAnimationLabel)
                            .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12))
                    .addComponent(jLabelId)))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void openFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileMenuItemActionPerformed
        File[] files = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser = initFilters(fileChooser, JFileChooser.FILES_ONLY);
        int ret = fileChooser.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            files = fileChooser.getSelectedFiles();
            this.currDirectory = files[0].getParent();
            this.changeProperties();
            this.addAudioFiles(files);
        } else if (ret == JFileChooser.CANCEL_OPTION) {
            this.currDirectory = fileChooser.getCurrentDirectory().getAbsolutePath();
            this.changeProperties();
        } else if (ret == JFileChooser.ERROR_OPTION) {
            System.out.print("Error of choosen file");
            return;
        }
    }//GEN-LAST:event_openFileMenuItemActionPerformed

    private void openFolderMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFolderMenuItem1ActionPerformed
        // TODO add your handling code here:
        File[] files = null;
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser = initFilters(fileChooser, JFileChooser.DIRECTORIES_ONLY);
        int ret = fileChooser.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            java.io.FileFilter filter = new java.io.FileFilter() {

                public boolean accept(File file) {
                    String fileName = "";
                    fileName = file.getName();
                    if (fileName.endsWith(".mp3")
                            || fileName.endsWith(".flac")
                            || fileName.endsWith(".ogg")
                            || fileName.endsWith(".wma")
                            || fileName.endsWith(".m4a")
                            || fileName.endsWith(".mp4")
                            || fileName.endsWith(".m4p")
                            || fileName.endsWith(".m4b")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            files = file.listFiles(filter);
            this.currDirectory = file.getAbsolutePath();
            this.changeProperties();
            this.addAudioFiles(files);
        } else if (ret == JFileChooser.CANCEL_OPTION) {
            this.currDirectory = fileChooser.getCurrentDirectory().getAbsolutePath();
            this.changeProperties();
        } else if (ret == JFileChooser.ERROR_OPTION) {
            System.out.print("Error of choosen file");
            return;
        }
    }//GEN-LAST:event_openFolderMenuItem1ActionPerformed

private void jTextFieldTitleFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldTitleFocusLost
    this.audioView.setTags(jLabelId, jTextFieldTitle, jLabelTitle, FieldKey.TITLE, 2);
}//GEN-LAST:event_jTextFieldTitleFocusLost

private void jTextFieldArtistFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldArtistFocusLost
    this.audioView.setTags(jLabelId, jTextFieldArtist, jLabelArtist, FieldKey.ARTIST, 4);
}//GEN-LAST:event_jTextFieldArtistFocusLost

private void jTextFieldAlbumArtistFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldAlbumArtistFocusLost
    this.audioView.setTags(jLabelId, jTextFieldAlbumArtist, jLabelAlbumArtist, FieldKey.ALBUM_ARTIST, 7);
}//GEN-LAST:event_jTextFieldAlbumArtistFocusLost

private void jTextFieldAlbumFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldAlbumFocusLost
    this.audioView.setTags(jLabelId, jTextFieldAlbum, jLabelAlbum, FieldKey.ALBUM, 5);
}//GEN-LAST:event_jTextFieldAlbumFocusLost

private void jTextFieldYearFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldYearFocusLost
    this.audioView.setTags(jLabelId, jTextFieldYear, null, FieldKey.YEAR, 6);
}//GEN-LAST:event_jTextFieldYearFocusLost

private void jTextFieldTrackNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldTrackNoFocusLost
    this.audioView.setTags(jLabelId, jTextFieldTrackNo, null, FieldKey.TRACK, 1);
}//GEN-LAST:event_jTextFieldTrackNoFocusLost

private void jTextFieldDiscNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldDiscNoFocusLost
    this.audioView.setTags(jLabelId, jTextFieldDiscNo, null, FieldKey.DISC_NO, 8);
}//GEN-LAST:event_jTextFieldDiscNoFocusLost

private void jTextFieldTitleFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldTitleFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldTitleFocusGained

private void jTextFieldArtistFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldArtistFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldArtistFocusGained

private void jTextFieldAlbumArtistFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldAlbumArtistFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldAlbumArtistFocusGained

private void jTextFieldAlbumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldAlbumFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldAlbumFocusGained

private void jTextPaneCommentsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextPaneCommentsFocusLost
    if (jTableInfo.getSelectedRows().length == 0 || jLabelId.getText().equals("")) {
        jTextPaneComments.setForeground(Color.GRAY);
        jTextPaneComments.setText("Comments...");
        return;
    }
    Integer id = Integer.valueOf(jLabelId.getText());
    int row = this.audioView.getRowById(id);
    AudioContain audioC = this.audios.getAudios().get(id);
    audioC.setChanged(true);
    AudioFile audio = this.audios.getAudios().get(id).getAudioFile();
    if (!audio.getTag().getFirst(FieldKey.COMMENT).equals(jTextPaneComments.getText())) {
        jTableInfo.setValueAt(this.audioView.getChangeIcon(), row, 0);
        try {
            audio.getTag().setField(FieldKey.COMMENT, jTextPaneComments.getText());
        } catch (KeyNotFoundException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FieldDataInvalidException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    if (jTextPaneComments.getText().equals("")) {
        jTextPaneComments.setForeground(Color.GRAY);
        jTextPaneComments.setText("Comments...");
    }
}//GEN-LAST:event_jTextPaneCommentsFocusLost

private void jTextPaneCommentsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextPaneCommentsFocusGained
    if (jTextPaneComments.getForeground().equals(Color.GRAY)) {
        jTextPaneComments.setForeground(Color.BLACK);
        jTextPaneComments.setText("");
    }
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextPaneCommentsFocusGained

private void jTextFieldProducerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldProducerFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldProducer, FieldKey.PRODUCER);
}//GEN-LAST:event_jTextFieldProducerFocusLost

private void jTextFieldComposerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldComposerFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldComposer, FieldKey.COMPOSER);
}//GEN-LAST:event_jTextFieldComposerFocusLost

private void jTextFieldRemixerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldRemixerFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldRemixer, FieldKey.REMIXER);
}//GEN-LAST:event_jTextFieldRemixerFocusLost

private void jTextFieldDjMixerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldDjMixerFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldDjMixer, FieldKey.DJMIXER);
}//GEN-LAST:event_jTextFieldDjMixerFocusLost

private void jTextFieldMixerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldMixerFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldMixer, FieldKey.MIXER);
}//GEN-LAST:event_jTextFieldMixerFocusLost

private void jTextFieldLyricistFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldLyricistFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldLyricist, FieldKey.LYRICIST);
}//GEN-LAST:event_jTextFieldLyricistFocusLost

private void jTextFieldWikiReleaseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldWikiReleaseFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldWikiRelease, FieldKey.URL_WIKIPEDIA_RELEASE_SITE);
}//GEN-LAST:event_jTextFieldWikiReleaseFocusLost

private void jTextFieldDIscogsReleaseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldDIscogsReleaseFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldDIscogsRelease, FieldKey.URL_DISCOGS_RELEASE_SITE);
}//GEN-LAST:event_jTextFieldDIscogsReleaseFocusLost

private void jTextFieldOffArtistSiteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldOffArtistSiteFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldOffArtistSite, FieldKey.URL_OFFICIAL_ARTIST_SITE);
}//GEN-LAST:event_jTextFieldOffArtistSiteFocusLost

private void jTextFieldWikiArtistSiteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldWikiArtistSiteFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldWikiArtistSite, FieldKey.URL_WIKIPEDIA_ARTIST_SITE);
}//GEN-LAST:event_jTextFieldWikiArtistSiteFocusLost

private void jTextFieldDiscogsArtistUrlFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldDiscogsArtistUrlFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldDiscogsArtistUrl, FieldKey.URL_DISCOGS_ARTIST_SITE);
}//GEN-LAST:event_jTextFieldDiscogsArtistUrlFocusLost

private void jTextFieldOffReleaseSiteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldOffReleaseSiteFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldOffReleaseSite, FieldKey.URL_OFFICIAL_RELEASE_SITE);
}//GEN-LAST:event_jTextFieldOffReleaseSiteFocusLost

private void jTextFieldProducerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldProducerFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldProducerFocusGained

private void jTextFieldComposerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldComposerFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldComposerFocusGained

private void jTextFieldRemixerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldRemixerFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldRemixerFocusGained

private void jTextFieldDjMixerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldDjMixerFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldDjMixerFocusGained

private void jTextFieldMixerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldMixerFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldMixerFocusGained

private void jTextFieldLyricistFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldLyricistFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldLyricistFocusGained

private void jTextFieldWikiReleaseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldWikiReleaseFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldWikiReleaseFocusGained

private void jTextFieldDIscogsReleaseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldDIscogsReleaseFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldDIscogsReleaseFocusGained

private void jTextFieldOffArtistSiteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldOffArtistSiteFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldOffArtistSiteFocusGained

private void jTextFieldWikiArtistSiteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldWikiArtistSiteFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldWikiArtistSiteFocusGained

private void jTextFieldDiscogsArtistUrlFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldDiscogsArtistUrlFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldDiscogsArtistUrlFocusGained

private void jTextFieldOffReleaseSiteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldOffReleaseSiteFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldOffReleaseSiteFocusGained

private void jTextFieldYearFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldYearFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldYearFocusGained

private void jTextFieldTrackNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldTrackNoFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldTrackNoFocusGained

private void jTextFieldDiscNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldDiscNoFocusGained
    this.audioView.stopCellEditing();
}//GEN-LAST:event_jTextFieldDiscNoFocusGained

private void jTextFieldLyricsUrlFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldLyricsUrlFocusLost
    this.audioView.setOtherTags(jLabelId, jTextFieldLyricsUrl, FieldKey.URL_LYRICS_SITE);
}//GEN-LAST:event_jTextFieldLyricsUrlFocusLost

private void jTextPaneLyricsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextPaneLyricsFocusLost
    if (jTableInfo.getSelectedRows().length == 0 || jLabelId.getText().equals("")) {
        return;
    }
    Integer id = Integer.valueOf(jLabelId.getText());
    int row = this.audioView.getRowById(id);
    AudioContain audioC = this.audios.getAudios().get(id);
    audioC.setChanged(true);
    AudioFile audio = this.audios.getAudios().get(id).getAudioFile();
    if (!audio.getTag().getFirst(FieldKey.LYRICS).equals(jTextPaneLyrics.getText())) {
        jTableInfo.setValueAt(this.audioView.getChangeIcon(), row, 0);
        try {
            audio.getTag().setField(FieldKey.LYRICS, jTextPaneLyrics.getText());
        } catch (KeyNotFoundException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FieldDataInvalidException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}//GEN-LAST:event_jTextPaneLyricsFocusLost

private void jLabelImgMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelImgMouseClicked
    if (evt.getClickCount() == 2) {
        if (jLabelId.getText().equals("")) {
            return;
        }
        Integer id = Integer.valueOf(jLabelId.getText());
        Image img = this.audios.getAudios().get(id).getImage();
        if (img != null) {
            JFrameArtworkView j = new JFrameArtworkView(img);
            j.setVisible(true);
        }
    }
}//GEN-LAST:event_jLabelImgMouseClicked

private void jTableInfoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableInfoMouseClicked
    if (evt.getClickCount() == 2) {
        if (jTableInfo.getSelectedColumn() == 9) {
            Integer id = Integer.valueOf(jLabelId.getText());
            Image img = this.audios.getAudios().get(id).getImage();
            Integer size = this.audios.getAudios().get(id).getSizeBytes();
            String format = this.audios.getAudios().get(id).getFormat();
            JFrameArtworkEdit j = new JFrameArtworkEdit(img, format,
                    size, id, jLabelImg, jTableInfo, audios,
                    audioView.getChangeIcon());
            j.setVisible(true);
        }
    }
}//GEN-LAST:event_jTableInfoMouseClicked

private void saveMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItem1ActionPerformed
    this.audios.save();
    this.audioView.viewTracksByAlbums(jTableArtist.getSelectedRows(), jTableAlbum.getSelectedRows());
}//GEN-LAST:event_saveMenuItem1ActionPerformed

private void jComboBoxGenreFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jComboBoxGenreFocusLost
    System.out.print("sdasaaaaaa");
    /* if (jTableInfo.getSelectedRows().length == 0 || jLabelId.getText().equals("")) {
    return;
    }
    int row = this.audioView.getRowById(Integer.valueOf(jLabelId.getText()));
    Integer id = (Integer) jTableInfo.getValueAt(row, 10);
    AudioContain audioC = this.audios.getAudios().get(id);
    AudioFile audio = this.audios.getAudios().get(id).getAudioFile();
    String str = audio.getTag().getFirst(FieldKey.URL_OFFICIAL_RELEASE_SITE);
    System.out.print(jComboBoxGenre.getSelectedItem().toString());*/
    /*  if (!jComboBoxGenre.getSelectedItem().equals(str)) {
    jTableInfo.setValueAt(this.getChangeIcon(), row, 0);
    audioC.setChanged(true);
    try {
    audio.getTag().setField(key, jtextField.getText());
    } catch (KeyNotFoundException ex) {
    Logger.getLogger(AudioView.class.getName()).log(Level.SEVERE, null, ex);
    } catch (FieldDataInvalidException ex) {
    Logger.getLogger(AudioView.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    }*/
}//GEN-LAST:event_jComboBoxGenreFocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBoxGenre;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelAlbum;
    private javax.swing.JLabel jLabelAlbum1;
    private javax.swing.JLabel jLabelAlbumArtist;
    private javax.swing.JLabel jLabelAlbumArtist1;
    private javax.swing.JLabel jLabelArtist;
    private javax.swing.JLabel jLabelArtist1;
    private javax.swing.JLabel jLabelBitRate;
    private javax.swing.JLabel jLabelChannels;
    private javax.swing.JLabel jLabelChannels1;
    private javax.swing.JLabel jLabelFormat;
    private javax.swing.JLabel jLabelFrequency;
    private javax.swing.JLabel jLabelId;
    private javax.swing.JLabel jLabelImg;
    private javax.swing.JLabel jLabelLocation;
    private javax.swing.JLabel jLabelLocation1;
    private javax.swing.JLabel jLabelSize;
    private javax.swing.JLabel jLabelTime;
    private javax.swing.JLabel jLabelTime1;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelTitle1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelSummary;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPaneComments;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableAlbum;
    private javax.swing.JTable jTableArtist;
    private javax.swing.JTable jTableInfo;
    private javax.swing.JTextField jTextFieldAlbum;
    private javax.swing.JTextField jTextFieldAlbumArtist;
    private javax.swing.JTextField jTextFieldArtist;
    private javax.swing.JTextField jTextFieldComposer;
    private javax.swing.JTextField jTextFieldDIscogsRelease;
    private javax.swing.JTextField jTextFieldDiscNo;
    private javax.swing.JTextField jTextFieldDiscogsArtistUrl;
    private javax.swing.JTextField jTextFieldDjMixer;
    private javax.swing.JTextField jTextFieldLyricist;
    private javax.swing.JTextField jTextFieldLyricsUrl;
    private javax.swing.JTextField jTextFieldMixer;
    private javax.swing.JTextField jTextFieldOffArtistSite;
    private javax.swing.JTextField jTextFieldOffReleaseSite;
    private javax.swing.JTextField jTextFieldProducer;
    private javax.swing.JTextField jTextFieldRemixer;
    private javax.swing.JTextField jTextFieldTitle;
    private javax.swing.JTextField jTextFieldTrackNo;
    private javax.swing.JTextField jTextFieldWikiArtistSite;
    private javax.swing.JTextField jTextFieldWikiRelease;
    private javax.swing.JTextField jTextFieldYear;
    private javax.swing.JTextPane jTextPaneComments;
    private javax.swing.JTextPane jTextPaneLyrics;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openFileMenuItem;
    private javax.swing.JMenuItem openFolderMenuItem1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem saveMenuItem1;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;

    private void initTables() {
        this.audioView = new AudioView(jTableArtist, jTableAlbum, jTableInfo,
                this.audios);
        jTableArtist.getTableHeader().setReorderingAllowed(false);
        jTableAlbum.getTableHeader().setReorderingAllowed(false);
        jTableInfo.getTableHeader().setReorderingAllowed(false);
        jTableAlbum.getTableHeader().setFont(new Font("Verdana", Font.PLAIN, 13));
        jTableArtist.getTableHeader().setFont(new Font("Verdana", Font.PLAIN, 13));
        jTableInfo.getTableHeader().setFont(new Font("Verdana", Font.PLAIN, 13));

        jTableInfo.getColumnModel().getColumn(0).setMinWidth(15);
        jTableInfo.getColumnModel().getColumn(0).setPreferredWidth(20);

        jTableInfo.getColumnModel().getColumn(1).setMinWidth(15);
        jTableInfo.getColumnModel().getColumn(1).setPreferredWidth(35);

        jTableInfo.getColumnModel().getColumn(2).setMinWidth(15);
        jTableInfo.getColumnModel().getColumn(2).setPreferredWidth(200);

        jTableInfo.getColumnModel().getColumn(3).setMinWidth(15);
        jTableInfo.getColumnModel().getColumn(3).setPreferredWidth(200);

        jTableInfo.getColumnModel().getColumn(4).setMinWidth(15);
        jTableInfo.getColumnModel().getColumn(4).setPreferredWidth(200);

        jTableInfo.getColumnModel().getColumn(5).setMinWidth(15);
        jTableInfo.getColumnModel().getColumn(5).setPreferredWidth(200);

        jTableInfo.getColumnModel().getColumn(6).setMinWidth(15);
        jTableInfo.getColumnModel().getColumn(6).setMaxWidth(40);
        jTableInfo.getColumnModel().getColumn(6).setPreferredWidth(40);

        jTableInfo.getColumnModel().getColumn(7).setMinWidth(15);
        jTableInfo.getColumnModel().getColumn(7).setMaxWidth(150);
        jTableInfo.getColumnModel().getColumn(7).setPreferredWidth(150);

        jTableInfo.getColumnModel().getColumn(8).setMinWidth(15);
        jTableInfo.getColumnModel().getColumn(8).setMaxWidth(35);
        jTableInfo.getColumnModel().getColumn(8).setPreferredWidth(35);


        /*jTableInfo.getColumnModel().getColumn(10).setMinWidth(0);
        jTableInfo.getColumnModel().getColumn(10).setMaxWidth(0);
        jTableInfo.getColumnModel().getColumn(10).setResizable(false);
        jTableInfo.getColumnModel().getColumn(10).setPreferredWidth(0);*/

        AudioTableRender audioRender = new AudioTableRender();
        jTableInfo.getColumnModel().getColumn(0).setCellRenderer(audioRender);
        jTableInfo.getColumnModel().getColumn(9).setCellRenderer(audioRender);

        StringBuilder infoArtist = new StringBuilder("<html>");
        infoArtist.append("<b>All 0 artists (0 tracks)</b>");
        ((DefaultTableModel) jTableArtist.getModel()).addRow(new Object[]{infoArtist.toString()});
        StringBuilder infoAlbum = new StringBuilder("<html>");
        infoAlbum.append("<b>All 0 albums(0 tracks)</b>");
        ((DefaultTableModel) jTableAlbum.getModel()).addRow(new Object[]{infoAlbum.toString()});
        jTableArtist.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTableAlbum.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jTableArtist.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent lse) {
                if (lse.getValueIsAdjusting()) {
                    return;
                }
                int[] selectedRows = jTableArtist.getSelectedRows();
                if (selectedRows.length > 1 && selectedRows[0] == 0) {
                    jTableArtist.setRowSelectionAllowed(false);
                    jTableArtist.setRowSelectionAllowed(true);
                    jTableArtist.setRowSelectionInterval(0, 0);
                    jTableAlbum.setRowSelectionInterval(0, 0);
                } else if (selectedRows.length == 1 && selectedRows[0] == 0) {
                    audioView.viewAlbumsByArtists(selectedRows, true);
                    jTableAlbum.setRowSelectionInterval(0, 0);
                } else {
                    audioView.viewAlbumsByArtists(selectedRows, false);
                    jTableAlbum.setRowSelectionInterval(0, 0);
                }
            }
        });

        jTableAlbum.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent lse) {
                if (lse.getValueIsAdjusting()) {
                    return;
                }
                int[] selectedRows = jTableAlbum.getSelectedRows();
                if (selectedRows.length > 1 && selectedRows[0] == 0) {
                    jTableAlbum.setRowSelectionAllowed(false);
                    jTableAlbum.setRowSelectionAllowed(true);
                    jTableAlbum.setRowSelectionInterval(0, 0);
                } else {
                    audioView.viewTracksByAlbums(jTableArtist.getSelectedRows(), selectedRows);
                }
            }
        });

        jTableInfo.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent lse) {
                if (lse.getValueIsAdjusting()) {
                    return;
                }
                int[] selectedRows = jTableInfo.getSelectedRows();
                if (selectedRows.length > 1) {
                    jTableInfo.setRowSelectionAllowed(false);
                    jTableInfo.setRowSelectionAllowed(true);
                    jTableInfo.setRowSelectionInterval(selectedRows[0], selectedRows[0]);
                } else if (selectedRows.length == 1) {
                    viewAudioInfo(selectedRows);
                }
            }
        });

        jTableInfo.getDefaultEditor(String.class).addCellEditorListener(
                new CellEditorListener() {

                    public void editingStopped(ChangeEvent ce) {
                        int column = jTableInfo.getSelectedColumn();
                        int row = jTableInfo.getSelectedRow();
                        if (column >= 0 && row >= 0) {
                            String value = (String) jTableInfo.getValueAt(row, column);
                            switch (column) {
                                case 1: {
                                    if (!jTextFieldTrackNo.getText().equals(value)) {
                                        Integer id = (Integer) jTableInfo.getValueAt(row, 10);
                                        AudioContain audioC = audios.getAudios().get(id);
                                        AudioFile audio = audios.getAudios().get(id).getAudioFile();
                                        try {
                                            audio.getTagOrCreateAndSetDefault().setField(FieldKey.TRACK, value);
                                        } catch (KeyNotFoundException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (FieldDataInvalidException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        audioC.setChanged(true);
                                        jTextFieldTrackNo.setText(value);
                                        jTableInfo.setValueAt(audioView.getChangeIcon(), row, 0);
                                    }
                                    break;
                                }
                                case 2: {
                                    if (!jTextFieldTitle.getText().equals(value)) {
                                        Integer id = (Integer) jTableInfo.getValueAt(row, 10);
                                        AudioContain audioC = audios.getAudios().get(id);
                                        AudioFile audio = audios.getAudios().get(id).getAudioFile();
                                        try {
                                            audio.getTagOrCreateAndSetDefault().setField(FieldKey.TITLE, value);
                                        } catch (KeyNotFoundException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (FieldDataInvalidException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        jTextFieldTitle.setText(value);
                                        jLabelTitle.setText(value);
                                        audioC.setChanged(true);
                                        jTableInfo.setValueAt(audioView.getChangeIcon(), row, 0);
                                    }
                                    break;
                                }
                                case 3: {
                                    if (!jComboBoxGenre.getSelectedItem().toString().equals(value)) {
                                        Integer id = (Integer) jTableInfo.getValueAt(row, 10);
                                        AudioContain audioC = audios.getAudios().get(id);
                                        AudioFile audio = audios.getAudios().get(id).getAudioFile();
                                        try {
                                            audio.getTagOrCreateAndSetDefault().setField(FieldKey.GENRE, value);
                                        } catch (KeyNotFoundException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (FieldDataInvalidException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        jComboBoxGenre.setSelectedItem(value);
                                        audioC.setChanged(true);
                                        jTableInfo.setValueAt(audioView.getChangeIcon(), row, 0);
                                    }
                                    break;
                                }
                                case 4: {
                                    if (!jTextFieldArtist.getText().equals(value)) {
                                        Integer id = (Integer) jTableInfo.getValueAt(row, 10);
                                        AudioContain audioC = audios.getAudios().get(id);
                                        AudioFile audio = audios.getAudios().get(id).getAudioFile();
                                        try {
                                            audio.getTagOrCreateAndSetDefault().setField(FieldKey.ARTIST, value);
                                        } catch (KeyNotFoundException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (FieldDataInvalidException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        jTextFieldArtist.setText(value);
                                        jLabelArtist.setText(value);
                                        audioC.setChanged(true);
                                        jTableInfo.setValueAt(audioView.getChangeIcon(), row, 0);
                                    }
                                    break;
                                }
                                case 5: {
                                    if (!jTextFieldAlbum.getText().equals(value)) {
                                        Integer id = (Integer) jTableInfo.getValueAt(row, 10);
                                        AudioContain audioC = audios.getAudios().get(id);
                                        AudioFile audio = audios.getAudios().get(id).getAudioFile();
                                        try {
                                            audio.getTagOrCreateAndSetDefault().setField(FieldKey.ALBUM, value);
                                        } catch (KeyNotFoundException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (FieldDataInvalidException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        jTextFieldAlbum.setText(value);
                                        jLabelAlbum.setText(value);
                                        audioC.setChanged(true);
                                        jTableInfo.setValueAt(audioView.getChangeIcon(), row, 0);
                                    }
                                    break;
                                }
                                case 6: {
                                    if (!jTextFieldYear.getText().equals(value)) {
                                        Integer id = (Integer) jTableInfo.getValueAt(row, 10);
                                        AudioContain audioC = audios.getAudios().get(id);
                                        AudioFile audio = audios.getAudios().get(id).getAudioFile();
                                        try {
                                            audio.getTagOrCreateAndSetDefault().setField(FieldKey.YEAR, value);
                                        } catch (KeyNotFoundException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (FieldDataInvalidException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        jTextFieldYear.setText(value);
                                        audioC.setChanged(true);
                                        jTableInfo.setValueAt(audioView.getChangeIcon(), row, 0);
                                    }
                                    break;
                                }
                                case 7: {
                                    if (!jTextFieldAlbumArtist.getText().equals(value)) {
                                        Integer id = (Integer) jTableInfo.getValueAt(row, 10);
                                        AudioContain audioC = audios.getAudios().get(id);
                                        AudioFile audio = audios.getAudios().get(id).getAudioFile();
                                        try {
                                            audio.getTagOrCreateAndSetDefault().setField(FieldKey.ALBUM_ARTIST, value);
                                        } catch (KeyNotFoundException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (FieldDataInvalidException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        jTextFieldAlbumArtist.setText(value);
                                        jLabelAlbumArtist.setText(value);
                                        audioC.setChanged(true);
                                        jTableInfo.setValueAt(audioView.getChangeIcon(), row, 0);
                                    }
                                    break;
                                }
                                case 8: {
                                    if (!jTextFieldDiscNo.getText().equals(value)) {
                                        Integer id = (Integer) jTableInfo.getValueAt(row, 10);
                                        AudioContain audioC = audios.getAudios().get(id);
                                        AudioFile audio = audios.getAudios().get(id).getAudioFile();
                                        try {
                                            audio.getTagOrCreateAndSetDefault().setField(FieldKey.DISC_NO, value);
                                        } catch (KeyNotFoundException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (FieldDataInvalidException ex) {
                                            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        jTextFieldDiscNo.setText(value);
                                        audioC.setChanged(true);
                                        jTableInfo.setValueAt(audioView.getChangeIcon(), row, 0);
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    public void editingCanceled(ChangeEvent ce) {
                        System.out.print("Editing Canceled");
                    }
                });


        jTableArtist.getSelectionModel().setSelectionInterval(0, 0);
        jTableAlbum.getSelectionModel().setSelectionInterval(0, 0);
        // jLabelId.setVisible(false);
        /*TableRowSorter<TableModel> sorter = new TableRowSorter(jTableArtist.getModel());
        jTableArtist.setRowSorter(sorter);
        sorter.rowsUpdated(1, 3, 0);*/
    }

    private void initJTextPane() {
        StyledEditorKit kit = new StyledEditorKit();
        jTextPaneComments.setEditorKit(kit);
        StyleContext context = new StyleContext();
        MutableAttributeSet style = context.getStyle("default");
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_JUSTIFIED);
        DefaultStyledDocument doc = new DefaultStyledDocument(context);
        jTextPaneComments.setDocument(doc);
        jTextPaneComments.setForeground(Color.GRAY);
        jTextPaneComments.setText("Comments...");

        StyledEditorKit kitLyrics = new StyledEditorKit();
        jTextPaneLyrics.setEditorKit(kitLyrics);
        StyleContext contextLyrics = new StyleContext();
        MutableAttributeSet styleLyrics = contextLyrics.getStyle("default");
        StyleConstants.setAlignment(styleLyrics, StyleConstants.ALIGN_JUSTIFIED);
        DefaultStyledDocument docLyrics = new DefaultStyledDocument(contextLyrics);
        jTextPaneLyrics.setDocument(docLyrics);
    }

    private void initJComboBoxGenre() {
        jComboBoxGenre.removeAllItems();
        File f = new File("genres.properties");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(f)));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                jComboBoxGenre.addItem(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //!!!или начальная директория - пусто! проверить на Windows!
    private void initProperties() {
        this.props = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream("jaudioedtor.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.props.load(is);
        } catch (IOException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            is.close();
        } catch (IOException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.currDirectory = this.props.getProperty("dir");
        if (this.currDirectory.equals("")) {
            this.currDirectory = System.getProperty("user.dir");
            this.props.setProperty("dir", this.currDirectory);
            BufferedOutputStream out = null;
            try {
                out = new BufferedOutputStream(new FileOutputStream("jaudioedtor.properties"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(JAudioEditorView.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            try {
                this.props.store(out, null);
            } catch (IOException ex) {
                Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void changeProperties() {
        this.props.setProperty("dir", this.currDirectory);
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream("jaudioedtor.properties"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.props.store(out, null);
        } catch (IOException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initJFrame() {
        getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getFrame().addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                if (audios.isChanged()) {
                    int choise = JOptionPane.showConfirmDialog(null, "Some files have been "
                            + "changed but not saved\nSave changes before quit?");
                    if (choise == JOptionPane.YES_OPTION) {
                        audios.save();
                        System.exit(0);
                    } else if (choise == JOptionPane.NO_OPTION) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });
    }

    private JFileChooser initFilters(JFileChooser fileChooser, int fileOrDir) {
        fileChooser.setCurrentDirectory(new File(this.currDirectory));

        if (fileOrDir == JFileChooser.FILES_ONLY) {
            String[] extAll = {"mp3", "flac", "ogg", "wma", "m4a", "mp4", "m4p", "m4b"};
            String[] extM4 = {"m4a", "mp4", "m4p", "m4b"};

            AudioFileFilter fmp3 = new AudioFileFilter("mp3", "*.mp3");
            AudioFileFilter fflac = new AudioFileFilter("flac", "*.flac");
            AudioFileFilter fogg = new AudioFileFilter("ogg", "*.ogg");
            AudioFileFilter fwma = new AudioFileFilter("wma", "*.wma");
            AudioFileFilter fm4 = new AudioFileFilter(extM4, "*.m4a, *.mp4, *.m4p,"
                    + " *.m4b");
            AudioFileFilter fall = new AudioFileFilter(extAll, "*.mp3, "
                    + "*.flac, *.ogg, *.wma, *.m4a, *.mp4, *.m4p, *.m4b");

            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(fmp3);
            fileChooser.addChoosableFileFilter(fflac);
            fileChooser.addChoosableFileFilter(fogg);
            fileChooser.addChoosableFileFilter(fwma);
            fileChooser.addChoosableFileFilter(fm4);
            fileChooser.addChoosableFileFilter(fall);
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        } else if (fileOrDir == JFileChooser.DIRECTORIES_ONLY) {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        return fileChooser;
    }

    private void addAudioFiles(File[] files) {
        AudioFile audioFile = null;
        for (File file : files) {
            try {
                audioFile = AudioFileIO.read(file);
            } catch (CannotReadException ex) {
                Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TagException ex) {
                Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ReadOnlyFileException ex) {
                Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidAudioFrameException ex) {
                Logger.getLogger(JAudioEditorView.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.audios.addAudio(audioFile);
        }
        this.audioView.resetViews();
    }

    private void viewAudioInfo(int[] selectedRows) {
        Integer key = (Integer) jTableInfo.getValueAt(selectedRows[0], 10);
        Image image = this.audios.getAudios().get(key).getImage();
        image = this.render.createResizedCopy(image, 148, 148);
        jLabelImg.setIcon(new ImageIcon(image));
        String title = (String) jTableInfo.getValueAt(selectedRows[0], 2);
        if (title != null && !title.equals("")) {
            jLabelTitle.setText(title);
            jTextFieldTitle.setText(title);
        } else {
            jLabelTitle.setText(" ");
            jTextFieldTitle.setText("");
        }
        String artist = (String) jTableInfo.getValueAt(selectedRows[0], 4);
        if (artist != null && !artist.equals("")) {
            jLabelArtist.setText(artist);
            jTextFieldArtist.setText(artist);
        } else {
            jLabelArtist.setText(" ");
            jTextFieldArtist.setText("");
        }
        String album = (String) jTableInfo.getValueAt(selectedRows[0], 5);
        if (album != null && !album.equals("")) {
            jLabelAlbum.setText(album);
            jTextFieldAlbum.setText(album);
        } else {
            jLabelAlbum.setText(" ");
            jTextFieldAlbum.setText("");
        }
        String albumArtist = (String) jTableInfo.getValueAt(selectedRows[0], 7);
        if (albumArtist != null && !albumArtist.equals("")) {
            jLabelAlbumArtist.setText(albumArtist);
            jTextFieldAlbumArtist.setText(albumArtist);
        } else {
            jLabelAlbumArtist.setText(" ");
            jTextFieldAlbumArtist.setText("");
        }
        String time = this.audios.getAudioLength(this.audios.getAudios().get(key).getAudioFile());
        if (time != null && !time.equals("")) {
            jLabelTime.setText(time);
        } else {
            jLabelTime.setText(" ");
        }
        String format = this.audios.getAudios().get(key).getAudioFile().getAudioHeader().getFormat();
        if (format != null && !format.equals("")) {
            jLabelFormat.setText(format);
        } else {
            jLabelFormat.setText(" ");
        }
        String size = this.audios.getAudioSize(this.audios.getAudios().get(key).getAudioFile());
        if (size != null && !size.equals("")) {
            jLabelSize.setText(size);
        } else {
            jLabelSize.setText(" ");
        }

        String bitrate = this.audios.getAudioBitRate(this.audios.getAudios().get(key).getAudioFile());
        if (bitrate != null && !bitrate.equals("")) {
            jLabelBitRate.setText(bitrate);
        } else {
            jLabelBitRate.setText(" ");
        }
        String frequency = this.audios.getAudios().get(key).getAudioFile().getAudioHeader().getSampleRate();
        if (frequency != null && !frequency.equals(" ")) {
            jLabelFrequency.setText(frequency + " KHz");
        } else {
            jLabelFrequency.setText(" ");
        }
        String channels = this.audios.getAudios().get(key).getAudioFile().getAudioHeader().getChannels();
        if (channels != null && !channels.equals(" ")) {
            jLabelChannels.setText(channels);
        } else {
            jLabelChannels.setText(" ");
        }
        jLabelLocation.setText(this.audios.getAudios().get(key).getAudioFile().
                getFile().getAbsolutePath());
        String year = (String) jTableInfo.getValueAt(selectedRows[0], 6);
        if (year != null && !year.equals("")) {
            jTextFieldYear.setText(year);
        } else {
            jTextFieldYear.setText("");
        }
        String trackNo = (String) jTableInfo.getValueAt(selectedRows[0], 1);
        if (trackNo != null && !trackNo.equals("")) {
            jTextFieldTrackNo.setText(trackNo);
        } else {
            jTextFieldTrackNo.setText("");
        }
        String discNo = (String) jTableInfo.getValueAt(selectedRows[0], 8);
        if (discNo != null && !discNo.equals("")) {
            jTextFieldDiscNo.setText(discNo);
        } else {
            jTextFieldDiscNo.setText("");
        }
        String genre = (String) jTableInfo.getValueAt(selectedRows[0], 3);
        if (genre != null && !genre.equals("")) {
            jComboBoxGenre.setSelectedItem(genre);
        } else {
            jComboBoxGenre.setSelectedItem("");
        }
        if (this.audios.getAudios().get(key).getAudioFile().getTag() != null) {
            String comments = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.COMMENT);
            if (comments != null && !comments.equals("")) {
                jTextPaneComments.setForeground(Color.BLACK);
                jTextPaneComments.setText(comments);
            } else {
                jTextPaneComments.setForeground(Color.GRAY);
                jTextPaneComments.setText("Comments...");
            }
            String producer = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.PRODUCER);
            if (producer != null && !producer.equals("")) {
                jTextFieldProducer.setText(producer);
            } else {
                jTextFieldProducer.setText("");
            }
            String composer = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.COMPOSER);
            if (composer != null && !composer.equals("")) {
                jTextFieldComposer.setText(composer);
            } else {
                jTextFieldComposer.setText("");
            }
            String remixer = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.REMIXER);
            if (remixer != null && !remixer.equals("")) {
                jTextFieldRemixer.setText(remixer);
            } else {
                jTextFieldRemixer.setText("");
            }
            String djmixer = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.DJMIXER);
            if (djmixer != null && !djmixer.equals("")) {
                jTextFieldDjMixer.setText(djmixer);
            } else {
                jTextFieldDjMixer.setText("");
            }
            String mixer = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.MIXER);
            if (mixer != null && !mixer.equals("")) {
                jTextFieldMixer.setText(mixer);
            } else {
                jTextFieldMixer.setText("");
            }
            String lyricist = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.LYRICIST);
            if (lyricist != null && !lyricist.equals("")) {
                jTextFieldLyricist.setText(lyricist);
            } else {
                jTextFieldLyricist.setText("");
            }
            String wikiRelease = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.URL_WIKIPEDIA_RELEASE_SITE);
            if (wikiRelease != null && !wikiRelease.equals("")) {
                jTextFieldWikiRelease.setText(wikiRelease);
            } else {
                jTextFieldWikiRelease.setText("");
            }
            String discogRelease = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.URL_DISCOGS_RELEASE_SITE);
            if (discogRelease != null && !discogRelease.equals("")) {
                jTextFieldDIscogsRelease.setText(discogRelease);
            } else {
                jTextFieldDIscogsRelease.setText("");
            }
            String offArtistSite = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.URL_OFFICIAL_ARTIST_SITE);
            if (offArtistSite != null && !offArtistSite.equals("")) {
                jTextFieldOffArtistSite.setText(offArtistSite);
            } else {
                jTextFieldOffArtistSite.setText("");
            }
            String wikiArtistUrl = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.URL_WIKIPEDIA_ARTIST_SITE);
            if (wikiArtistUrl != null && !wikiArtistUrl.equals("")) {
                jTextFieldWikiArtistSite.setText(wikiArtistUrl);
            } else {
                jTextFieldWikiArtistSite.setText("");
            }
            String discogsArtistUrl = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.URL_DISCOGS_ARTIST_SITE);
            if (discogsArtistUrl != null && !discogsArtistUrl.equals("")) {
                jTextFieldDiscogsArtistUrl.setText(discogsArtistUrl);
            } else {
                jTextFieldDiscogsArtistUrl.setText("");
            }
            String offReleaseSite = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.URL_OFFICIAL_RELEASE_SITE);
            if (offReleaseSite != null && !offReleaseSite.equals("")) {
                jTextFieldOffReleaseSite.setText(offReleaseSite);
            } else {
                jTextFieldOffReleaseSite.setText("");
            }
            String lyricsUrl = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.URL_LYRICS_SITE);
            if (lyricsUrl != null && !lyricsUrl.equals("")) {
                jTextFieldLyricsUrl.setText(lyricsUrl);
            } else {
                jTextFieldLyricsUrl.setText("");
            }
            String lyrics = this.audios.getAudios().get(key).getAudioFile().
                    getTag().getFirst(FieldKey.LYRICS);
            if (lyrics != null && !lyrics.equals("")) {
                jTextPaneLyrics.setText(lyrics);
            } else {
                jTextPaneLyrics.setText("");
            }
        } else {
            jTextPaneComments.setForeground(Color.GRAY);
            jTextPaneComments.setText("Comments...");
            jTextFieldProducer.setText("");
            jTextFieldComposer.setText("");
            jTextFieldRemixer.setText("");
            jTextFieldDjMixer.setText("");
            jTextFieldMixer.setText("");
            jTextFieldLyricist.setText("");
            jTextFieldWikiRelease.setText("");
            jTextFieldDIscogsRelease.setText("");
            jTextFieldOffArtistSite.setText("");
            jTextFieldWikiArtistSite.setText("");
            jTextFieldDiscogsArtistUrl.setText("");
            jTextFieldOffReleaseSite.setText("");
            jTextFieldLyricsUrl.setText("");
            jTextPaneLyrics.setText("");
        }
        jLabelId.setText(String.valueOf(jTableInfo.getValueAt(selectedRows[0], 10)));
    }
}
