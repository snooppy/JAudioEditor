/*
 * Entagged Audio Tag library 
 * Copyright (c) 2003-2005 Raphaël Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jaudiotagger.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;
import java.util.ArrayList;

import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.asf.AsfTag;
import org.jaudiotagger.audio.wav.WavTag;
import org.jaudiotagger.audio.real.RealTag;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;
import org.jaudiotagger.tag.flac.FlacTag;

/**
 * <p>This is the main object manipulated by the user representing an audiofile, its properties and its tag.</p>
 * <p>The prefered way to obtain an <code>AudioFile</code> is to use the <code>AudioFileIO.read(File)</code> method.</p>
 * <p>The <code>AudioFile</code> contains every properties associated with the file itself (no meta-data), like the bitrate, the sampling rate, the encoding audioHeaders, etc.</p>
 * <p>To get the meta-data contained in this file you have to get the <code>Tag</code> of this <code>AudioFile</code></p>
 *
 * @author Raphael Slinckx
 * @version $Id: AudioFile.java 904 2010-05-14 13:46:39Z paultaylor $
 * @see AudioFileIO
 * @see Tag
 * @since v0.01
 */
public class AudioFile
{
    //Logger
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio");

    /**
     * The physical file that this instance represents.
     */
    protected File file;

    /**
     * The Audio header info
     */
    protected AudioHeader audioHeader;

    /**
     * The tag
     */
    protected Tag tag;

    public AudioFile()
    {

    }

    /**
     * <p>These constructors are used by the different readers, users should not use them, but use the <code>AudioFileIO.read(File)</code> method instead !.</p>
     * <p>Create the AudioFile representing file f, the encoding audio headers and containing the tag</p>
     *
     * @param f           The file of the audio file
     * @param audioHeader the encoding audioHeaders over this file
     * @param tag         the tag contained in this file or null if no tag exists
     */
    public AudioFile(File f, AudioHeader audioHeader, Tag tag)
    {
        this.file = f;
        this.audioHeader = audioHeader;
        this.tag = tag;
    }


    /**
     * <p>These constructors are used by the different readers, users should not use them, but use the <code>AudioFileIO.read(File)</code> method instead !.</p>
     * <p>Create the AudioFile representing file denoted by pathnames, the encoding audio Headers and containing the tag</p>
     *
     * @param s           The pathname of the audio file
     * @param audioHeader the encoding audioHeaders over this file
     * @param tag         the tag contained in this file
     */
    public AudioFile(String s, AudioHeader audioHeader, Tag tag)
    {
        this.file = new File(s);
        this.audioHeader = audioHeader;
        this.tag = tag;
    }

    /**
     * <p>Write the tag contained in this AudioFile in the actual file on the disk, this is the same as calling the <code>AudioFileIO.write(this)</code> method.</p>
     *
     * @throws CannotWriteException If the file could not be written/accessed, the extension wasn't recognized, or other IO error occured.
     * @see AudioFileIO
     */
    public void commit() throws CannotWriteException
    {
        AudioFileIO.write(this);
    }

    /**
     * Set the file to store the info in
     *
     * @param file
     */
    public void setFile(File file)
    {
        this.file = file;
    }

    /**
     * Retrieve the physical file
     *
     * @return
     */
    public File getFile()
    {
        return file;
    }

    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    /**
     * Return audio header
     * @return
     */
    public AudioHeader getAudioHeader()
    {
        return audioHeader;
    }

    /**
     * <p>Returns the tag contained in this AudioFile, the <code>Tag</code> contains any useful meta-data, like
     * artist, album, title, etc. If the file does not contain any tag the null is returned. Some audio formats do
     * not allow there to be no tag so in this case the reader would return an empty tag whereas for others such
     * as mp3 it is purely optional.
     *
     * @return Returns the tag contained in this AudioFile, or null if no tag exists.
     */
    public Tag getTag()
    {
        return tag;
    }

    /**
     * <p>Returns a multi-line string with the file path, the encoding audioHeader, and the tag contents.</p>
     *
     * @return A multi-line string with the file path, the encoding audioHeader, and the tag contents.
     *         TODO Maybe this can be changed ?
     */
    public String toString()
    {
        return "AudioFile " + getFile().getAbsolutePath()
                + "  --------\n" + audioHeader.toString() + "\n" + ((tag == null) ? "" : tag.toString()) + "\n-------------------";
    }

    /**
     * Check does file exist
     *
     * @param file
     * @throws FileNotFoundException
     */
    public void checkFileExists(File file)throws FileNotFoundException
    {
        logger.info("Reading file:" + "path" + file.getPath() + ":abs:" + file.getAbsolutePath());
        if (!file.exists())
        {
            logger.severe("Unable to find:" + file.getPath());
            throw new FileNotFoundException(ErrorMessage.UNABLE_TO_FIND_FILE.getMsg(file.getPath()));
        }
    }

    /**
     * Checks the file is accessible with the correct permissions, otherwise exception occurs
     *
     * @param file
     * @param readOnly
     * @throws ReadOnlyFileException
     * @throws FileNotFoundException
     * @return
     */
    protected RandomAccessFile checkFilePermissions(File file, boolean readOnly) throws ReadOnlyFileException, FileNotFoundException
    {
        RandomAccessFile newFile;

        checkFileExists(file);

        // Unless opened as readonly the file must be writable
        if (readOnly)
        {
            newFile = new RandomAccessFile(file, "r");
        }
        else
        {
            if (!file.canWrite())
            {
                logger.severe("Unable to write:" + file.getPath());
                throw new ReadOnlyFileException(ErrorMessage.NO_PERMISSIONS_TO_WRITE_TO_FILE.getMsg(file.getPath()));
            }
            newFile = new RandomAccessFile(file, "rws");
        }
        return newFile;
    }

    /**
     * Optional debugging method
     *
     * @return
     */
    public String displayStructureAsXML()
    {
        return "";
    }

    /**
     * Optional debugging method
     *
     * @return
     */
    public String displayStructureAsPlainText()
    {
        return "";
    }


    /** Create Default Tag
     *
     * @return
     */
    //TODO might be better to instantiate classes such as Mp4File,FlacFile ecetera
    //TODO Generic tag is very misleading because soem of these formats cannot actually save the tag
    public Tag createDefaultTag()
    {
        if(SupportedFileFormat.FLAC.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new FlacTag(VorbisCommentTag.createNewTag(), new ArrayList< MetadataBlockDataPicture >());
        }
        else if(SupportedFileFormat.OGG.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return VorbisCommentTag.createNewTag();
        }
        else if(SupportedFileFormat.MP4.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.M4A.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.M4P.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.WMA.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new AsfTag();
        }
        else if(SupportedFileFormat.WAV.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new WavTag();
        }
        else if(SupportedFileFormat.RA.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new RealTag();
        }
        else if(SupportedFileFormat.RM.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new RealTag();
        }
        else
        {
            throw new RuntimeException("Unable to create default tag for this file format");
        }

    }

    /**
     * Get the tag or if the file doesn't have one at all, create a default tag  and return
     *
     * @return
     */
    public Tag getTagOrCreateDefault()
    {
        Tag tag = getTag();
        if(tag==null)
        {
            return createDefaultTag();
        }
        return tag;
    }

     /**
     * Get the tag or if the file doesn't have one at all, create a default tag  and set it
     *
     * @return
     */
    public Tag getTagOrCreateAndSetDefault()
    {
        Tag tag = getTag();
        if(tag==null)
        {
            tag = createDefaultTag();
            setTag(tag);
            return tag;
        }
        return tag;
    }

    /**
     *
     * @param file
     * @return filename with audioFormat separator stripped of.
     */
    public static String getBaseFilename(File file)
    {
        int index=file.getName().toLowerCase().lastIndexOf(".");
        if(index>0)
        {
            return file.getName().substring(0,index);
        }
        return file.getName();
    }
}
