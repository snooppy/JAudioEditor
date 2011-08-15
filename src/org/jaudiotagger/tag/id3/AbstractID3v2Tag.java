/*
 *  MusicTag Copyright (C)2003,2004
 *
 *  This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 *  General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 *  or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 *  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 *  you can getFields a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jaudiotagger.tag.id3;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.UnableToCreateFileException;
import org.jaudiotagger.audio.exceptions.UnableToModifyFileException;
import org.jaudiotagger.audio.exceptions.UnableToRenameFileException;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.logging.FileSystemMessage;
import org.jaudiotagger.tag.*;
import org.jaudiotagger.tag.datatype.*;
import org.jaudiotagger.tag.id3.framebody.*;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;
import org.jaudiotagger.tag.reference.Languages;
import org.jaudiotagger.tag.reference.PictureTypes;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.WritableByteChannel;
import java.util.*;
import java.util.logging.Level;

/**
 * This is the abstract base class for all ID3v2 tags.
 *
 * @author : Paul Taylor
 * @author : Eric Farng
 * @version $Id: AbstractID3v2Tag.java 923 2010-10-16 21:59:49Z paultaylor $
 */
public abstract class AbstractID3v2Tag extends AbstractID3Tag implements Tag
{
    protected static final String TYPE_HEADER = "header";
    protected static final String TYPE_BODY = "body";

    //Tag ID as held in file
    protected static final byte[] TAG_ID = {'I', 'D', '3'};

    //The tag header is the same for ID3v2 versions
    public static final int TAG_HEADER_LENGTH = 10;
    protected static final int FIELD_TAGID_LENGTH = 3;
    protected static final int FIELD_TAG_MAJOR_VERSION_LENGTH = 1;
    protected static final int FIELD_TAG_MINOR_VERSION_LENGTH = 1;
    protected static final int FIELD_TAG_FLAG_LENGTH = 1;
    protected static final int FIELD_TAG_SIZE_LENGTH = 4;

    protected static final int FIELD_TAGID_POS = 0;
    protected static final int FIELD_TAG_MAJOR_VERSION_POS = 3;
    protected static final int FIELD_TAG_MINOR_VERSION_POS = 4;
    protected static final int FIELD_TAG_FLAG_POS = 5;
    protected static final int FIELD_TAG_SIZE_POS = 6;

    protected static final int TAG_SIZE_INCREMENT = 100;

    //The max size we try to write in one go to avoid out of memory errors (10mb)
    private static final long MAXIMUM_WRITABLE_CHUNK_SIZE = 10000000;

    /**
     * Map of all frames for this tag
     */
    public HashMap frameMap = null;

    /**
     * Map of all encrypted frames, these cannot be unencrypted by jaudiotagger
     */
    public HashMap encryptedFrameMap = null;

    /**
     * Holds the ids of invalid duplicate frames
     */
    protected static final String TYPE_DUPLICATEFRAMEID = "duplicateFrameId";
    protected String duplicateFrameId = "";

    /**
     * Holds count the number of bytes used up by invalid duplicate frames
     */
    protected static final String TYPE_DUPLICATEBYTES = "duplicateBytes";
    protected int duplicateBytes = 0;

    /**
     * Holds count the number bytes used up by empty frames
     */
    protected static final String TYPE_EMPTYFRAMEBYTES = "emptyFrameBytes";
    protected int emptyFrameBytes = 0;

    /**
     * Holds the size of the tag as reported by the tag header
     */
    protected static final String TYPE_FILEREADSIZE = "fileReadSize";
    protected int fileReadSize = 0;

    /**
     * Holds count of invalid frames, (frames that could not be read)
     */
    protected static final String TYPE_INVALIDFRAMES = "invalidFrames";
    protected int invalidFrames = 0;

    /**
     * Empty Constructor
     */
    public AbstractID3v2Tag()
    {
    }

    /**
     * This constructor is used when a tag is created as a duplicate of another
     * tag of the same type and version.
     *
     * @param copyObject
     */
    protected AbstractID3v2Tag(AbstractID3v2Tag copyObject)
    {
    }

    /**
     * Copy primitives apply to all tags
     *
     * @param copyObject
     */
    protected void copyPrimitives(AbstractID3v2Tag copyObject)
    {
        logger.info("Copying Primitives");
        //Primitives type variables common to all IDv2 Tags
        this.duplicateFrameId = copyObject.duplicateFrameId;
        this.duplicateBytes = copyObject.duplicateBytes;
        this.emptyFrameBytes = copyObject.emptyFrameBytes;
        this.fileReadSize = copyObject.fileReadSize;
        this.invalidFrames = copyObject.invalidFrames;
    }

    /**
     * Copy frames from another tag,
     *
     * @param copyObject
     */
    //TODO Copy Encrypted frames needs implementing
    protected void copyFrames(AbstractID3v2Tag copyObject)
    {
        frameMap = new LinkedHashMap();
        encryptedFrameMap = new LinkedHashMap();
        //Copy Frames that are a valid 2.4 type

        for (Object o1 : copyObject.frameMap.keySet())
        {
            String id = (String) o1;
            Object o = copyObject.frameMap.get(id);
            //SingleFrames
            if (o instanceof AbstractID3v2Frame)
            {
                addFrame((AbstractID3v2Frame) o);
            }
            //MultiFrames
            else if (o instanceof ArrayList)
            {
                for (AbstractID3v2Frame frame : (ArrayList<AbstractID3v2Frame>) o)
                {
                    addFrame(frame);
                }
            }
        }
    }

    protected abstract void addFrame(AbstractID3v2Frame frame);

    /**
     * Returns the number of bytes which come from duplicate frames
     *
     * @return the number of bytes which come from duplicate frames
     */
    public int getDuplicateBytes()
    {
        return duplicateBytes;
    }

    /**
     * Return the string which holds the ids of all
     * duplicate frames.
     *
     * @return the string which holds the ids of all duplicate frames.
     */
    public String getDuplicateFrameId()
    {
        return duplicateFrameId;
    }

    /**
     * Returns the number of bytes which come from empty frames
     *
     * @return the number of bytes which come from empty frames
     */
    public int getEmptyFrameBytes()
    {
        return emptyFrameBytes;
    }

    /**
     * Return  byte count of invalid frames
     *
     * @return byte count of invalid frames
     */
    public int getInvalidFrames()
    {
        return invalidFrames;
    }

    /**
     * Returns the tag size as reported by the tag header
     *
     * @return the tag size as reported by the tag header
     */
    public int getFileReadBytes()
    {
        return fileReadSize;
    }

    /**
     * Return whether tag has frame with this identifier
     * <p/>
     * Warning the match is only done against the identifier so if a tag contains a frame with an unsuported body
     * but happens to have an identifier that is valid for another version of the tag it will return true
     *
     * @param identifier frameId to lookup
     * @return true if tag has frame with this identifier
     */
    public boolean hasFrame(String identifier)
    {
        return frameMap.containsKey(identifier);
    }


    /**
     * Return whether tag has frame with this identifier and a related body. This is required to protect
     * against circumstances whereby a tag contains a frame with an unsupported body
     * but happens to have an identifier that is valid for another version of the tag which it has been converted to
     * <p/>
     * e.g TDRC is an invalid frame in a v23 tag but if somehow a v23tag has been created by another application
     * with a TDRC frame we construct an UnsupportedFrameBody to hold it, then this library constructs a
     * v24 tag, it will contain a frame with id TDRC but it will not have the expected frame body it is not really a
     * TDRC frame.
     *
     * @param identifier frameId to lookup
     * @return true if tag has frame with this identifier
     */
    public boolean hasFrameAndBody(String identifier)
    {
        if (hasFrame(identifier))
        {
            Object o = getFrame(identifier);
            if (o instanceof AbstractID3v2Frame)
            {
                return !(((AbstractID3v2Frame) o).getBody() instanceof FrameBodyUnsupported);
            }
            return true;
        }
        return false;
    }

    /**
     * Return whether tag has frame starting with this identifier
     * <p/>
     * Warning the match is only done against the identifier so if a tag contains a frame with an unsupported body
     * but happens to have an identifier that is valid for another version of the tag it will return true
     *
     * @param identifier start of frameId to lookup
     * @return tag has frame starting with this identifier
     */
    public boolean hasFrameOfType(String identifier)
    {
        Iterator<String> iterator = frameMap.keySet().iterator();
        String key;
        boolean found = false;
        while (iterator.hasNext() && !found)
        {
            key = iterator.next();
            if (key.startsWith(identifier))
            {
                found = true;
            }
        }
        return found;
    }


    /**
     * For single frames return the frame in this tag with given identifier if it exists, if multiple frames
     * exist with the same identifier it will return a list containing all the frames with this identifier
     * <p/>
     * Warning the match is only done against the identifier so if a tag contains a frame with an unsupported body
     * but happens to have an identifier that is valid for another version of the tag it will be returned.
     * <p/>
     *
     * @param identifier is an ID3Frame identifier
     * @return matching frame, or list of matching frames
     */
    //TODO:This method is problematic because sometimes it returns a list and sometimes a frame, we need to
    //replace with two separate methods as in the tag interface.
    public Object getFrame(String identifier)
    {
        return frameMap.get(identifier);
    }

    /**
     * Return any encrypted frames with this identifier
     * <p/>
     * <p>For single frames return the frame in this tag with given identifier if it exists, if multiple frames
     * exist with the same identifier it will return a list containing all the frames with this identifier
     *
     * @param identifier
     * @return
     */
    public Object getEncryptedFrame(String identifier)
    {
        return encryptedFrameMap.get(identifier);
    }

    /**
     * Retrieve the first value that exists for this identifier
     * <p/>
     * If the value is a String it returns that, otherwise returns a summary of the fields information
     * <p/>
     *
     * @param identifier
     * @return
     */
    public String getFirst(String identifier)
    {
        AbstractID3v2Frame frame = getFirstField(identifier);
        if (frame == null)
        {
            return "";
        }
        return getTextValueForFrame(frame);
    }

    /**
     * @param frame
     * @return
     */
    private String getTextValueForFrame(AbstractID3v2Frame frame)
    {
        return frame.getBody().getUserFriendlyValue();
    }

    public TagField getFirstField(FieldKey genericKey) throws KeyNotFoundException
    {
        List<TagField> fields = getFields(genericKey);
        if (fields.size() > 0)
        {
            return fields.get(0);
        }
        return null;
    }


    /**
     * Retrieve the first tag field that exists for this identifier
     *
     * @param identifier
     * @return tag field or null if doesn't exist
     */
    public AbstractID3v2Frame getFirstField(String identifier)
    {
        Object object = getFrame(identifier);
        if (object == null)
        {
            return null;
        }
        if (object instanceof List)
        {
            return ((List<AbstractID3v2Frame>) object).get(0);
        }
        else
        {
            return (AbstractID3v2Frame) object;
        }
    }

    /**
     * Add a frame to this tag
     *
     * @param frame the frame to add
     *              <p/>
     *              <p/>
     *              Warning if frame(s) already exists for this identifier that they are overwritten
     *              <p/>
     */
    //TODO needs to ensure do not addField an invalid frame for this tag
    //TODO what happens if already contains a list with this ID
    public void setFrame(AbstractID3v2Frame frame)
    {
        frameMap.put(frame.getIdentifier(), frame);
    }

    protected abstract ID3Frames getID3Frames();

    public void setField(FieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException
    {
        TagField tagfield = createField(genericKey, value);
        setField(tagfield);
    }

    public void addField(FieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException
    {
        TagField tagfield = createField(genericKey, value);
        addField(tagfield);
    }


    /**
     * Add frame taking into account existing frames of the same type
     *
     * @param newFrame
     * @param frames
     */
    public void mergeDuplicateFrames(AbstractID3v2Frame newFrame, List<AbstractID3v2Frame> frames)
    {
        for (ListIterator<AbstractID3v2Frame> li = frames.listIterator(); li.hasNext();)
        {
            AbstractID3v2Frame nextFrame = li.next();

            if (newFrame.getBody() instanceof FrameBodyTXXX)
            {
                //Value with matching key exists so replace
                if (((FrameBodyTXXX) newFrame.getBody()).getDescription().equals(((FrameBodyTXXX) nextFrame.getBody()).getDescription()))
                {
                    li.set(newFrame);
                    frameMap.put(newFrame.getId(), frames);
                    return;
                }
            }
            else if (newFrame.getBody() instanceof FrameBodyWXXX)
            {
                //Value with matching key exists so replace
                if (((FrameBodyWXXX) newFrame.getBody()).getDescription().equals(((FrameBodyWXXX) nextFrame.getBody()).getDescription()))
                {
                    li.set(newFrame);
                    frameMap.put(newFrame.getId(), frames);
                    return;
                }
            }
            else if (newFrame.getBody() instanceof FrameBodyCOMM)
            {
                if (((FrameBodyCOMM) newFrame.getBody()).getDescription().equals(((FrameBodyCOMM) nextFrame.getBody()).getDescription()))
                {
                    li.set(newFrame);
                    frameMap.put(newFrame.getId(), frames);
                    return;
                }
            }
            else if (newFrame.getBody() instanceof FrameBodyUFID)
            {
                if (((FrameBodyUFID) newFrame.getBody()).getOwner().equals(((FrameBodyUFID) nextFrame.getBody()).getOwner()))
                {
                    li.set(newFrame);
                    frameMap.put(newFrame.getId(), frames);
                    return;
                }
            }
            else if (newFrame.getBody() instanceof FrameBodyUSLT)
            {
                if (((FrameBodyUSLT) newFrame.getBody()).getDescription().equals(((FrameBodyUSLT) nextFrame.getBody()).getDescription()))
                {
                    li.set(newFrame);
                    frameMap.put(newFrame.getId(), frames);
                    return;
                }
            }
            else if (newFrame.getBody() instanceof FrameBodyPOPM)
            {
                if (((FrameBodyPOPM) newFrame.getBody()).getEmailToUser().equals(((FrameBodyPOPM) nextFrame.getBody()).getEmailToUser()))
                {
                    li.set(newFrame);
                    frameMap.put(newFrame.getId(), frames);
                    return;
                }
            }
            //Just grab any additional info from new TRCK Frame and add to the existing one
            else if (newFrame.getBody() instanceof FrameBodyTRCK)
            {
                FrameBodyTRCK newBody = (FrameBodyTRCK) newFrame.getBody();
                FrameBodyTRCK oldBody = (FrameBodyTRCK) nextFrame.getBody();

                if (newBody.getTrackNo() != null && newBody.getTrackNo() > 0)
                {
                    oldBody.setTrackNo(newBody.getTrackNo());
                }

                if (newBody.getTrackTotal() != null && newBody.getTrackTotal() > 0)
                {
                    oldBody.setTrackTotal(newBody.getTrackTotal());
                }
                return;
            }
            //Just grab any additional info from new TPOS Frame and add to the existing one
            else if (newFrame.getBody() instanceof FrameBodyTPOS)
            {
                FrameBodyTPOS newBody = (FrameBodyTPOS) newFrame.getBody();
                FrameBodyTPOS oldBody = (FrameBodyTPOS) nextFrame.getBody();

                Integer newDiscNo = newBody.getDiscNo();
                if ((newDiscNo != null) && (newDiscNo > 0))
                {
                    oldBody.setDiscNo(newDiscNo);
                }

                Integer newDiscTotal = newBody.getDiscTotal();
                if ((newDiscTotal != null) && (newDiscTotal > 0))
                {
                    oldBody.setDiscTotal(newDiscTotal);
                }
                return;
            }
            else if (newFrame.getBody() instanceof FrameBodyIPLS)
            {
                FrameBodyIPLS frameBody         = (FrameBodyIPLS) newFrame.getBody();
                FrameBodyIPLS existingFrameBody = (FrameBodyIPLS) nextFrame.getBody();
                existingFrameBody.addPair(frameBody.getText());
                return;
            }
            else if (newFrame.getBody() instanceof FrameBodyTIPL)
            {
                FrameBodyTIPL frameBody         = (FrameBodyTIPL) newFrame.getBody();
                FrameBodyTIPL existingFrameBody = (FrameBodyTIPL) nextFrame.getBody();
                existingFrameBody.addPair(frameBody.getText());
                return;
            }
        }

        if(!getID3Frames().isMultipleAllowed(newFrame.getId()))
        {
            frameMap.put(newFrame.getId(), newFrame);
        }
        else
        {
            //No match found so addField new one
            frames.add(newFrame);
            frameMap.put(newFrame.getId(), frames);
        }
    }

    /**
     * Handles adding of a new field that's shares a frame with other fields, so modifies the existing frame rather
     * than creating an ew frame for these special cases
     *
     * @param list
     * @param frameMap
     * @param existingFrame
     * @param frame
     */
    private void addNewFrameOrAddField(List<TagField> list, HashMap frameMap, AbstractID3v2Frame existingFrame, AbstractID3v2Frame frame)
    {
        /**
         * If the frame is a TextInformation (but not the TXXX) frame then we just add an extra string to the existing frame
         * otherwise we create a new frame
         */
        if (frame.getBody() instanceof AbstractFrameBodyTextInfo && !(frame.getBody() instanceof FrameBodyTXXX))
        {
            AbstractFrameBodyTextInfo frameBody = (AbstractFrameBodyTextInfo) frame.getBody();
            AbstractFrameBodyTextInfo existingFrameBody = (AbstractFrameBodyTextInfo) existingFrame.getBody();
            existingFrameBody.addTextValue(frameBody.getText());
        }
        else if (frame.getBody() instanceof FrameBodyIPLS)
        {
            FrameBodyIPLS frameBody = (FrameBodyIPLS) frame.getBody();
            FrameBodyIPLS existingFrameBody = (FrameBodyIPLS) existingFrame.getBody();
            existingFrameBody.addPair(frameBody.getText());
        }
        else if (frame.getBody() instanceof FrameBodyTIPL)
        {
            FrameBodyTIPL frameBody = (FrameBodyTIPL) frame.getBody();
            FrameBodyTIPL existingFrameBody = (FrameBodyTIPL) existingFrame.getBody();
            existingFrameBody.addPair(frameBody.getText());
        }
        else
        {
            if (list.size() == 0)
            {
                list.add(existingFrame);
                list.add(frame);
                frameMap.put(frame.getId(), list);
            }
            else
            {
                list.add(frame);
            }
        }
    }

    /**
     * @param field
     * @throws FieldDataInvalidException
     */
    public void setField(TagField field) throws FieldDataInvalidException
    {
        if (!(field instanceof AbstractID3v2Frame))
        {
            throw new FieldDataInvalidException("Field " + field + " is not of type AbstractID3v2Frame");
        }

        AbstractID3v2Frame newFrame = (AbstractID3v2Frame) field;

        Object obj = frameMap.get(field.getId());


        //If no frame of this type exist or if multiples are not allowed
        if (obj == null)
        {
            frameMap.put(field.getId(), field);
        }
        //frame of this type already exists
        else if (obj instanceof AbstractID3v2Frame)
        {
            List<AbstractID3v2Frame> frames = new ArrayList<AbstractID3v2Frame>();
            frames.add((AbstractID3v2Frame) obj);
            mergeDuplicateFrames(newFrame, frames);
        }
        //Multiple frames of this type already exist
        else if (obj instanceof List)
        {
            mergeDuplicateFrames(newFrame, (List<AbstractID3v2Frame>) obj);
        }
    }


    /**
     * Add new field
     * <p/>
     * There is a special handling if adding another text field of the same type, in this case the value will
     * be appended to the existing field, separated by the null character.
     *
     * @param field
     * @throws FieldDataInvalidException
     */
    public void addField(TagField field) throws FieldDataInvalidException
    {
        if (field == null)
        {
            return;
        }

        if (!(field instanceof AbstractID3v2Frame))
        {
            throw new FieldDataInvalidException("Field " + field + " is not of type AbstractID3v2Frame");
        }

        AbstractID3v2Frame frame = (AbstractID3v2Frame) field;

        Object o = frameMap.get(field.getId());

        //No frame of this type
        if (o == null)
        {
            frameMap.put(field.getId(), field);
        }
        //There are already frames of this type
        else if (o instanceof List)
        {
            List<TagField> list = (List<TagField>) o;
            addNewFrameOrAddField(list, frameMap, null, frame);
        }
        //One frame exists, we are adding another so may need to convert to list
        else
        {
            AbstractID3v2Frame existingFrame = (AbstractID3v2Frame) o;
            List<TagField> list = new ArrayList<TagField>();
            addNewFrameOrAddField(list, frameMap, existingFrame, frame);
        }
    }


    /**
     * Used for setting multiple frames for a single frame Identifier
     * <p/>
     * Warning if frame(s) already exists for this identifier thay are overwritten
     * <p/>
     * TODO needs to ensure do not add an invalid frame for this tag
     *
     * @param identifier
     * @param multiFrame
     */
    public void setFrame(String identifier, List<AbstractID3v2Frame> multiFrame)
    {
        logger.finest("Adding " + multiFrame.size() + " frames for " + identifier);
        frameMap.put(identifier, multiFrame);
    }

    /**
     * Return the number of frames in this tag of a particular type, multiple frames
     * of the same time will only be counted once
     *
     * @return a count of different frames
     */
    /*
    public int getFrameCount()
    {
        if (frameMap == null)
        {
            return 0;
        }
        else
        {
            return frameMap.size();
        }
    }
    */

    /**
     * Return all frames which start with the identifier, this
     * can be more than one which is useful if trying to retrieve
     * similar frames e.g TIT1,TIT2,TIT3 ... and don't know exactly
     * which ones there are.
     * <p/>
     * Warning the match is only done against the identifier so if a tag contains a frame with an unsupported body
     * but happens to have an identifier that is valid for another version of the tag it will be returned.
     *
     * @param identifier
     * @return an iterator of all the frames starting with a particular identifier
     */
    public Iterator getFrameOfType(String identifier)
    {
        Iterator<String> iterator = frameMap.keySet().iterator();
        HashSet result = new HashSet();
        String key;
        while (iterator.hasNext())
        {
            key = iterator.next();
            if (key.startsWith(identifier))
            {
                result.add(frameMap.get(key));
            }
        }
        return result.iterator();
    }


    /**
     * Delete Tag
     *
     * @param file to delete the tag from
     * @throws IOException if problem accessing the file
     *                     <p/>
     */
    //TODO should clear all data and preferably recover lost space and go upto end of mp3s 
    public void delete(RandomAccessFile file) throws IOException
    {
        // this works by just erasing the "ID3" tag at the beginning
        // of the file
        byte[] buffer = new byte[FIELD_TAGID_LENGTH];
        //Read into Byte Buffer
        final FileChannel fc = file.getChannel();
        fc.position();
        ByteBuffer byteBuffer = ByteBuffer.allocate(TAG_HEADER_LENGTH);
        fc.read(byteBuffer, 0);
        byteBuffer.flip();
        if (seek(byteBuffer))
        {
            file.seek(0L);
            file.write(buffer);
        }
    }

    /**
     * Is this tag equivalent to another
     *
     * @param obj to test for equivalence
     * @return true if they are equivalent
     */
    public boolean equals(Object obj)
    {
        if (!(obj instanceof AbstractID3v2Tag))
        {
            return false;
        }
        AbstractID3v2Tag object = (AbstractID3v2Tag) obj;
        return this.frameMap.equals(object.frameMap) && super.equals(obj);
    }


    /**
     * Return the frames in the order they were added
     *
     * @return and iterator of the frmaes/list of multi value frames
     */
    public Iterator iterator()
    {
        return frameMap.values().iterator();
    }

    /**
     * Remove frame(s) with this identifier from tag
     *
     * @param identifier frameId to look for
     */
    public void removeFrame(String identifier)
    {
        logger.finest("Removing frame with identifier:" + identifier);
        frameMap.remove(identifier);
    }

    /**
     * Remove all frame(s) which have an unsupported body, in other words
     * remove all frames that are not part of the standard frameSet for
     * this tag
     */
    public void removeUnsupportedFrames()
    {
        for (Iterator i = iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (o instanceof AbstractID3v2Frame)
            {
                if (((AbstractID3v2Frame) o).getBody() instanceof FrameBodyUnsupported)
                {
                    logger.finest("Removing frame" + ((AbstractID3v2Frame) o).getIdentifier());
                    i.remove();
                }
            }
        }
    }

    /**
     * Remove any frames starting with this identifier from tag
     *
     * @param identifier start of frameId to look for
     */
    public void removeFrameOfType(String identifier)
    {
        //First fine matching keys
        HashSet<String> result = new HashSet<String>();
        for (Object match : frameMap.keySet())
        {
            String key = (String) match;
            if (key.startsWith(identifier))
            {
                result.add(key);
            }
        }
        //Then deleteField outside of loop to prevent concurrent modificatioon eception if there are two keys
        //with the same id
        for (String match : result)
        {
            logger.finest("Removing frame with identifier:" + match + "because starts with:" + identifier);
            frameMap.remove(match);
        }
    }


    /**
     * Write tag to file.
     *
     * @param file
     * @param audioStartByte
     * @throws IOException TODO should be abstract
     */
    public void write(File file, long audioStartByte) throws IOException
    {
    }

    /**
     * Get file lock for writing too file
     * <p/>
     * TODO:this appears to have little effect on Windows Vista
     *
     * @param fileChannel
     * @param filePath
     * @return lock or null if locking is not supported
     * @throws IOException if unable to get lock because already locked by another program
     * @throws java.nio.channels.OverlappingFileLockException
     *                     if already locked by another thread in the same VM, we dont catch this
     *                     because indicates a programming error
     */
    protected FileLock getFileLockForWriting(FileChannel fileChannel, String filePath) throws IOException
    {
        logger.finest("locking fileChannel for " + filePath);
        FileLock fileLock;
        try
        {
            fileLock = fileChannel.tryLock();
        }
        //Assumes locking is not supported on this platform so just returns null
        catch (IOException exception)
        {
            return null;
        }

        //Couldnt getFields lock because file is already locked by another application
        if (fileLock == null)
        {
            throw new IOException(ErrorMessage.GENERAL_WRITE_FAILED_FILE_LOCKED.getMsg(filePath));
        }
        return fileLock;
    }

    /**
     * Write tag to file.
     *
     * @param file
     * @throws IOException TODO should be abstract
     */
    public void write(RandomAccessFile file) throws IOException
    {
    }

    /**
     * Write tag to channel.
     *
     * @param channel
     * @throws IOException TODO should be abstract
     */
    public void write(WritableByteChannel channel) throws IOException
    {
    }


    /**
     * Checks to see if the file contains an ID3tag and if so return its size as reported in
     * the tag header  and return the size of the tag (including header), if no such tag exists return
     * zero.
     *
     * @param file
     * @return the end of the tag in the file or zero if no tag exists.
     * @throws java.io.IOException
     */
    public static long getV2TagSizeIfExists(File file) throws IOException
    {
        FileInputStream fis = null;
        FileChannel fc = null;
        ByteBuffer bb = null;
        try
        {
            //Files
            fis = new FileInputStream(file);
            fc = fis.getChannel();

            //Read possible Tag header  Byte Buffer
            bb = ByteBuffer.allocate(TAG_HEADER_LENGTH);
            fc.read(bb);
            bb.flip();
            if (bb.limit() < (TAG_HEADER_LENGTH))
            {
                return 0;
            }
        }
        finally
        {
            if (fc != null)
            {
                fc.close();
            }

            if (fis != null)
            {
                fis.close();
            }
        }

        //ID3 identifier
        byte[] tagIdentifier = new byte[FIELD_TAGID_LENGTH];
        bb.get(tagIdentifier, 0, FIELD_TAGID_LENGTH);
        if (!(Arrays.equals(tagIdentifier, TAG_ID)))
        {
            return 0;
        }

        //Is it valid Major Version
        byte majorVersion = bb.get();
        if ((majorVersion != ID3v22Tag.MAJOR_VERSION) && (majorVersion != ID3v23Tag.MAJOR_VERSION) && (majorVersion != ID3v24Tag.MAJOR_VERSION))
        {
            return 0;
        }

        //Skip Minor Version
        bb.get();

        //Skip Flags
        bb.get();

        //Get size as recorded in frame header
        int frameSize = ID3SyncSafeInteger.bufferToValue(bb);

        //addField header size to frame size
        frameSize += TAG_HEADER_LENGTH;
        return frameSize;
    }

    /**
     * Does a tag of the correct version exist in this file.
     *
     * @param byteBuffer to search through
     * @return true if tag exists.
     */
    public boolean seek(ByteBuffer byteBuffer)
    {
        byteBuffer.rewind();
        logger.info("ByteBuffer pos:" + byteBuffer.position() + ":limit" + byteBuffer.limit() + ":cap" + byteBuffer.capacity());


        byte[] tagIdentifier = new byte[FIELD_TAGID_LENGTH];
        byteBuffer.get(tagIdentifier, 0, FIELD_TAGID_LENGTH);
        if (!(Arrays.equals(tagIdentifier, TAG_ID)))
        {
            return false;
        }
        //Major Version
        if (byteBuffer.get() != getMajorVersion())
        {
            return false;
        }
        //Minor Version
        return byteBuffer.get() == getRevision();
    }

    /**
     * This method determines the total tag size taking into account
     * where the audio file starts, the size of the tagging data and
     * user options for defining how tags should shrink or grow.
     *
     * @param tagSize
     * @param audioStart
     * @return
     */
    protected int calculateTagSize(int tagSize, int audioStart)
    {
        /** We can fit in the tag so no adjustments required */
        if (tagSize <= audioStart)
        {
            return audioStart;
        }
        /** There is not enough room as we need to move the audio file we might
         *  as well increase it more than neccessary for future changes
         */
        return tagSize + TAG_SIZE_INCREMENT;
    }

    /**
     * Adjust the length of the  padding at the beginning of the MP3 file, this is only called when there is currently
     * not enough space before the start of the audio to write the tag.
     * <p/>
     * A new file will be created with enough size to fit the <code>ID3v2</code> tag.
     * The old file will be deleted, and the new file renamed.
     *
     * @param paddingSize This is total size required to store tag before audio
     * @param audioStart
     * @param file        The file to adjust the padding length of
     * @throws FileNotFoundException if the file exists but is a directory
     *                               rather than a regular file or cannot be opened for any other
     *                               reason
     * @throws IOException           on any I/O error
     */
    public void adjustPadding(File file, int paddingSize, long audioStart) throws FileNotFoundException, IOException
    {
        logger.finer("Need to move audio file to accomodate tag");
        FileChannel fcIn = null;
        FileChannel fcOut;

        //Create buffer holds the necessary padding
        ByteBuffer paddingBuffer = ByteBuffer.wrap(new byte[paddingSize]);

        //Create Temporary File and write channel, make sure it is locked        
        File paddedFile;

        try
        {
            paddedFile = File.createTempFile(Utils.getMinBaseFilenameAllowedForTempFile(file), ".new", file.getParentFile());
            logger.finest("Created temp file:" + paddedFile.getName() + " for " + file.getName());
        }
        //Vista:Can occur if have Write permission on folder this file would be created in Denied
        catch (IOException ioe)
        {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
            if (ioe.getMessage().equals(FileSystemMessage.ACCESS_IS_DENIED.getMsg()))
            {
                logger.severe(ErrorMessage.GENERAL_WRITE_FAILED_TO_CREATE_TEMPORARY_FILE_IN_FOLDER.getMsg(file.getName(), file.getParentFile().getPath()));
                throw new UnableToCreateFileException(ErrorMessage.GENERAL_WRITE_FAILED_TO_CREATE_TEMPORARY_FILE_IN_FOLDER.getMsg(file.getName(), file.getParentFile().getPath()));
            }
            else
            {
                logger.severe(ErrorMessage.GENERAL_WRITE_FAILED_TO_CREATE_TEMPORARY_FILE_IN_FOLDER.getMsg(file.getName(), file.getParentFile().getPath()));
                throw new UnableToCreateFileException(ErrorMessage.GENERAL_WRITE_FAILED_TO_CREATE_TEMPORARY_FILE_IN_FOLDER.getMsg(file.getName(), file.getParentFile().getPath()));
            }
        }

        try
        {
            fcOut = new FileOutputStream(paddedFile).getChannel();
        }
        //Vista:Can occur if have special permission Create Folder/Append Data denied
        catch (FileNotFoundException ioe)
        {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
            logger.severe(ErrorMessage.GENERAL_WRITE_FAILED_TO_MODIFY_TEMPORARY_FILE_IN_FOLDER.getMsg(file.getName(), file.getParentFile().getPath()));
            throw new UnableToModifyFileException(ErrorMessage.GENERAL_WRITE_FAILED_TO_MODIFY_TEMPORARY_FILE_IN_FOLDER.getMsg(file.getName(), file.getParentFile().getPath()));
        }

        try
        {
            //Create read channel from original file
            //TODO lock so cant be modified by anything else whilst reading from it ?
            fcIn = new FileInputStream(file).getChannel();

            //Write padding to new file (this is where the tag will be written to later)
            long written = fcOut.write(paddingBuffer);

            //Write rest of file starting from audio
            logger.finer("Copying:" + (file.length() - audioStart) + "bytes");

            //If the amount to be copied is very large we split into 10MB lumps to try and avoid
            //out of memory errors
            long audiolength = file.length() - audioStart;
            if (audiolength <= MAXIMUM_WRITABLE_CHUNK_SIZE)
            {
                long written2 = fcIn.transferTo(audioStart, audiolength, fcOut);
                logger.finer("Written padding:" + written + " Data:" + written2);
                if (written2 != audiolength)
                {
                    throw new RuntimeException(ErrorMessage.MP3_UNABLE_TO_ADJUST_PADDING.getMsg(audiolength, written2));
                }
            }
            else
            {
                long noOfChunks = audiolength / MAXIMUM_WRITABLE_CHUNK_SIZE;
                long lastChunkSize = audiolength % MAXIMUM_WRITABLE_CHUNK_SIZE;
                long written2 = 0;
                for (int i = 0; i < noOfChunks; i++)
                {
                    written2 += fcIn.transferTo(audioStart + (i * MAXIMUM_WRITABLE_CHUNK_SIZE), MAXIMUM_WRITABLE_CHUNK_SIZE, fcOut);
                }
                written2 += fcIn.transferTo(audioStart + (noOfChunks * MAXIMUM_WRITABLE_CHUNK_SIZE), lastChunkSize, fcOut);
                logger.finer("Written padding:" + written + " Data:" + written2);
                if (written2 != audiolength)
                {
                    throw new RuntimeException(ErrorMessage.MP3_UNABLE_TO_ADJUST_PADDING.getMsg(audiolength, written2));
                }
            }

            //Store original modification time
            long lastModified = file.lastModified();

            //Close Channels and locks
            if (fcIn != null)
            {
                if (fcIn.isOpen())
                {
                    fcIn.close();
                }
            }

            if (fcOut != null)
            {
                if (fcOut.isOpen())
                {
                    fcOut.close();
                }
            }

            //Replace file with paddedFile
            replaceFile(file, paddedFile);

            //Update modification time
            //TODO is this the right file ?
            paddedFile.setLastModified(lastModified);
        }
        finally
        {
            try
            {
                //Whatever happens ensure all locks and channels are closed/released
                if (fcIn != null)
                {
                    if (fcIn.isOpen())
                    {
                        fcIn.close();
                    }
                }

                if (fcOut != null)
                {
                    if (fcOut.isOpen())
                    {
                        fcOut.close();
                    }
                }
            }
            catch (Exception e)
            {
                logger.log(Level.WARNING, "Problem closing channels and locks:" + e.getMessage(), e);
            }
        }
    }

    /**
     * Write the data from the buffer to the file
     *
     * @param file
     * @param headerBuffer
     * @param bodyByteBuffer
     * @param padding
     * @param sizeIncPadding
     * @param audioStartLocation
     * @throws IOException
     */
    protected void writeBufferToFile(File file, ByteBuffer headerBuffer, byte[] bodyByteBuffer, int padding, int sizeIncPadding, long audioStartLocation) throws IOException
    {
        FileChannel fc = null;
        FileLock fileLock = null;

        //We need to adjust location of audio file if true
        if (sizeIncPadding > audioStartLocation)
        {
            logger.finest("Adjusting Padding");
            adjustPadding(file, sizeIncPadding, audioStartLocation);
        }

        try
        {
            fc = new RandomAccessFile(file, "rws").getChannel();
            fileLock = getFileLockForWriting(fc, file.getPath());
            fc.write(headerBuffer);
            fc.write(ByteBuffer.wrap(bodyByteBuffer));
            fc.write(ByteBuffer.wrap(new byte[padding]));
        }
        catch (FileNotFoundException fe)
        {
            logger.log(Level.SEVERE, getLoggingFilename() + fe.getMessage(), fe);
            if (fe.getMessage().equals(FileSystemMessage.ACCESS_IS_DENIED.getMsg()))
            {
                logger.severe(ErrorMessage.GENERAL_WRITE_FAILED_TO_OPEN_FILE_FOR_EDITING.getMsg(file.getPath()));
                throw new UnableToModifyFileException(ErrorMessage.GENERAL_WRITE_FAILED_TO_OPEN_FILE_FOR_EDITING.getMsg(file.getPath()));
            }
            else
            {
                logger.severe(ErrorMessage.GENERAL_WRITE_FAILED_TO_OPEN_FILE_FOR_EDITING.getMsg(file.getPath()));
                throw new UnableToCreateFileException(ErrorMessage.GENERAL_WRITE_FAILED_TO_OPEN_FILE_FOR_EDITING.getMsg(file.getPath()));
            }
        }
        catch (IOException ioe)
        {
            logger.log(Level.SEVERE, getLoggingFilename() + ioe.getMessage(), ioe);
            if (ioe.getMessage().equals(FileSystemMessage.ACCESS_IS_DENIED.getMsg()))
            {
                logger.severe(ErrorMessage.GENERAL_WRITE_FAILED_TO_OPEN_FILE_FOR_EDITING.getMsg(file.getParentFile().getPath()));
                throw new UnableToModifyFileException(ErrorMessage.GENERAL_WRITE_FAILED_TO_OPEN_FILE_FOR_EDITING.getMsg(file.getParentFile().getPath()));
            }
            else
            {
                logger.severe(ErrorMessage.GENERAL_WRITE_FAILED_TO_OPEN_FILE_FOR_EDITING.getMsg(file.getParentFile().getPath()));
                throw new UnableToCreateFileException(ErrorMessage.GENERAL_WRITE_FAILED_TO_OPEN_FILE_FOR_EDITING.getMsg(file.getParentFile().getPath()));
            }
        }
        finally
        {
            if (fc != null)
            {
                if (fileLock != null)
                {
                    fileLock.release();
                }
                fc.close();
            }
        }
    }

    /**
     * Replace originalFile with the contents of newFile
     * <p/>
     * Both files must exist in the same folder so that there are no problems with fileystem mount points
     *
     * @param newFile
     * @param originalFile
     * @throws IOException
     */
    private void replaceFile(File originalFile, File newFile) throws IOException
    {
        boolean renameOriginalResult;
        //Rename Original File to make a backup in case problem with new file
        File originalFileBackup = new File(originalFile.getAbsoluteFile().getParentFile().getPath(), AudioFile.getBaseFilename(originalFile) + ".old");
        //If already exists modify the suffix
        int count = 1;
        while (originalFileBackup.exists())
        {
            originalFileBackup = new File(originalFile.getAbsoluteFile().getParentFile().getPath(), AudioFile.getBaseFilename(originalFile) + ".old" + count);
            count++;
        }

        renameOriginalResult = originalFile.renameTo(originalFileBackup);
        if (!renameOriginalResult)
        {
            logger.warning(ErrorMessage.GENERAL_WRITE_FAILED_TO_RENAME_ORIGINAL_FILE_TO_BACKUP.getMsg(originalFile.getAbsolutePath(), originalFileBackup.getName()));
            throw new UnableToRenameFileException(ErrorMessage.GENERAL_WRITE_FAILED_TO_RENAME_ORIGINAL_FILE_TO_BACKUP.getMsg(originalFile.getAbsolutePath(), originalFileBackup.getName()));
        }

        //Rename new Temporary file to the final file
        boolean renameResult = newFile.renameTo(originalFile);
        if (!renameResult)
        {
            //Renamed failed so lets do some checks rename the backup back to the original file
            //New File doesnt exist
            if (!newFile.exists())
            {
                logger.warning(ErrorMessage.GENERAL_WRITE_FAILED_NEW_FILE_DOESNT_EXIST.getMsg(newFile.getAbsolutePath()));
            }

            //Rename the backup back to the original
            renameOriginalResult = originalFileBackup.renameTo(originalFile);
            if (!renameOriginalResult)
            {
                //TODO now if this happens we are left with testfile.old instead of testfile.mp3
                logger.warning(ErrorMessage.GENERAL_WRITE_FAILED_TO_RENAME_ORIGINAL_BACKUP_TO_ORIGINAL.getMsg(originalFileBackup.getAbsolutePath(), originalFile.getName()));
            }


            logger.warning(ErrorMessage.GENERAL_WRITE_FAILED_TO_RENAME_TO_ORIGINAL_FILE.getMsg(originalFile.getAbsolutePath(), newFile.getName()));
            throw new UnableToRenameFileException(ErrorMessage.GENERAL_WRITE_FAILED_TO_RENAME_TO_ORIGINAL_FILE.getMsg(originalFile.getAbsolutePath(), newFile.getName()));
        }
        else
        {
            //Rename was okay so we can now deleteField the backup of the original
            boolean deleteResult = originalFileBackup.delete();
            if (!deleteResult)
            {
                //Not a disaster but can't deleteField the backup so make a warning
                logger.warning(ErrorMessage.GENERAL_WRITE_WARNING_UNABLE_TO_DELETE_BACKUP_FILE.getMsg(originalFileBackup.getAbsolutePath()));
            }
        }
    }

    /*
    * Copy framne into map, whilst accounting for multiple frame of same type which can occur even if there were
    * not frames of the same type in the original tag
    */

    protected void copyFrameIntoMap(String id, AbstractID3v2Frame newFrame)
    {

        if (frameMap.containsKey(newFrame.getIdentifier()))
        {
            Object o = frameMap.get(newFrame.getIdentifier());
            if (o instanceof AbstractID3v2Frame)
            {
                List<AbstractID3v2Frame> list = new ArrayList<AbstractID3v2Frame>();
                list.add((AbstractID3v2Frame) o);
                list.add(newFrame);
                frameMap.put(newFrame.getIdentifier(), list);
            }
            else
            {
                List<AbstractID3v2Frame> list = (List) o;
                list.add(newFrame);
            }
        }
        else
        {
            frameMap.put(newFrame.getIdentifier(), newFrame);
        }
    }

    /**
     * Add frame to the frame map
     *
     * @param frameId
     * @param next
     */
    protected void loadFrameIntoMap(String frameId, AbstractID3v2Frame next)
    {
        if (next.getBody() instanceof FrameBodyEncrypted)
        {
            loadFrameIntoSpecifiedMap(encryptedFrameMap, frameId, next);
        }
        else
        {
            loadFrameIntoSpecifiedMap(frameMap, frameId, next);
        }
    }


    /**
     * Decides what to with the frame that has just been read from file.
     * If the frame is an allowable duplicate frame and is a duplicate we add all
     * frames into an ArrayList and add the ArrayList to the HashMap. if not allowed
     * to be duplicate we store the number of bytes in the duplicateBytes variable and discard
     * the frame itself.
     *
     * @param frameId
     * @param next
     */
    protected void loadFrameIntoSpecifiedMap(HashMap map, String frameId, AbstractID3v2Frame next)
    {
        if ((ID3v24Frames.getInstanceOf().isMultipleAllowed(frameId)) || (ID3v23Frames.getInstanceOf().isMultipleAllowed(frameId)) || (ID3v22Frames.getInstanceOf().isMultipleAllowed(frameId)))
        {
            //If a frame already exists of this type
            if (map.containsKey(frameId))
            {
                Object o = map.get(frameId);
                if (o instanceof ArrayList)
                {
                    ArrayList<AbstractID3v2Frame> multiValues = (ArrayList<AbstractID3v2Frame>) o;
                    multiValues.add(next);
                    logger.finer("Adding Multi Frame(1)" + frameId);
                }
                else
                {
                    ArrayList<AbstractID3v2Frame> multiValues = new ArrayList<AbstractID3v2Frame>();
                    multiValues.add((AbstractID3v2Frame) o);
                    multiValues.add(next);
                    map.put(frameId, multiValues);
                    logger.finer("Adding Multi Frame(2)" + frameId);
                }
            }
            else
            {
                logger.finer("Adding Multi FrameList(3)" + frameId);
                map.put(frameId, next);
            }
        }
        //If duplicate frame just stores the name of the frame and the number of bytes the frame contains
        else if (map.containsKey(frameId))
        {
            logger.warning("Ignoring Duplicate Frame" + frameId);
            //If we have multiple duplicate frames in a tag separate them with semicolons
            if (this.duplicateFrameId.length() > 0)
            {
                this.duplicateFrameId += ";";
            }
            this.duplicateFrameId += frameId;
            this.duplicateBytes += ((AbstractID3v2Frame) frameMap.get(frameId)).getSize();
        }
        else
        {
            logger.finer("Adding Frame" + frameId);
            map.put(frameId, next);
        }
    }

    /**
     * Return tag size based upon the sizes of the tags rather than the physical
     * no of bytes between start of ID3Tag and start of Audio Data.Should be extended
     * by subclasses to include header.
     *
     * @return size of the tag
     */
    public int getSize()
    {
        int size = 0;
        Iterator iterator = frameMap.values().iterator();
        AbstractID3v2Frame frame;
        while (iterator.hasNext())
        {
            Object o = iterator.next();
            if (o instanceof AbstractID3v2Frame)
            {
                frame = (AbstractID3v2Frame) o;
                size += frame.getSize();
            }
            else
            {
                ArrayList<AbstractID3v2Frame> multiFrames = (ArrayList<AbstractID3v2Frame>) o;
                for (ListIterator<AbstractID3v2Frame> li = multiFrames.listIterator(); li.hasNext();)
                {
                    frame = li.next();
                    size += frame.getSize();
                }
            }
        }
        return size;
    }

    /**
     * Write all the frames to the byteArrayOutputStream
     * <p/>
     * <p>Currently Write all frames, defaults to the order in which they were loaded, newly
     * created frames will be at end of tag.
     *
     * @return ByteBuffer Contains all the frames written within the tag ready for writing to file
     * @throws IOException
     */
    protected ByteArrayOutputStream writeFramesToBuffer() throws IOException
    {
        ByteArrayOutputStream bodyBuffer = new ByteArrayOutputStream();
        writeFramesToBufferStream(frameMap, bodyBuffer);
        writeFramesToBufferStream(encryptedFrameMap, bodyBuffer);
        return bodyBuffer;
    }

    /**
     * Write frames in map to bodyBuffer
     *
     * @param map
     * @param bodyBuffer
     * @throws IOException
     */
    private void writeFramesToBufferStream(Map map, ByteArrayOutputStream bodyBuffer) throws IOException
    {
        //Sort keys into Preferred Order
        TreeSet<String> sortedWriteOrder = new TreeSet<String>(getPreferredFrameOrderComparator());
        sortedWriteOrder.addAll(map.keySet());

        AbstractID3v2Frame frame;
        for (String id : sortedWriteOrder)
        {
            Object o = map.get(id);
            if (o instanceof AbstractID3v2Frame)
            {
                frame = (AbstractID3v2Frame) o;
                frame.setLoggingFilename(getLoggingFilename());
                frame.write(bodyBuffer);
            }
            else
            {
                List<AbstractID3v2Frame> multiFrames = (List<AbstractID3v2Frame>) o;
                for (AbstractID3v2Frame nextFrame : multiFrames)
                {
                    nextFrame.setLoggingFilename(getLoggingFilename());
                    nextFrame.write(bodyBuffer);
                }
            }
        }
    }

    /**
     * @return comparator used to order frames in preferred order for writing to file
     *         so that most important frames are written first.
     */
    public abstract Comparator getPreferredFrameOrderComparator();

    public void createStructure()
    {
        createStructureHeader();
        createStructureBody();
    }

    public void createStructureHeader()
    {
        MP3File.getStructureFormatter().addElement(TYPE_DUPLICATEBYTES, this.duplicateBytes);
        MP3File.getStructureFormatter().addElement(TYPE_DUPLICATEFRAMEID, this.duplicateFrameId);
        MP3File.getStructureFormatter().addElement(TYPE_EMPTYFRAMEBYTES, this.emptyFrameBytes);
        MP3File.getStructureFormatter().addElement(TYPE_FILEREADSIZE, this.fileReadSize);
        MP3File.getStructureFormatter().addElement(TYPE_INVALIDFRAMES, this.invalidFrames);
    }

    public void createStructureBody()
    {
        MP3File.getStructureFormatter().openHeadingElement(TYPE_BODY, "");

        AbstractID3v2Frame frame;
        for (Object o : frameMap.values())
        {
            if (o instanceof AbstractID3v2Frame)
            {
                frame = (AbstractID3v2Frame) o;
                frame.createStructure();
            }
            else
            {
                ArrayList<AbstractID3v2Frame> multiFrames = (ArrayList<AbstractID3v2Frame>) o;
                for (ListIterator<AbstractID3v2Frame> li = multiFrames.listIterator(); li.hasNext();)
                {
                    frame = li.next();
                    frame.createStructure();
                }
            }
        }
        MP3File.getStructureFormatter().closeHeadingElement(TYPE_BODY);
    }

    /**
     * Retrieve the values that exists for this id3 frame id
     */
    public List<TagField> getFields(String id) throws KeyNotFoundException
    {
        Object o = getFrame(id);
        if (o == null)
        {
            return new ArrayList<TagField>();
        }
        else if (o instanceof List)
        {
            //TODO should return copy
            return (List<TagField>) o;
        }
        else if (o instanceof AbstractID3v2Frame)
        {
            List<TagField> list = new ArrayList<TagField>();
            list.add((TagField) o);
            return list;
        }
        else
        {
            throw new RuntimeException("Found entry in frameMap that was not a frame or a list:" + o);
        }
    }


    /**
     * Create Frame of correct ID3 version with the specified id
     *
     * @param id
     * @return
     */
    public abstract AbstractID3v2Frame createFrame(String id);

    //TODO

    public boolean hasCommonFields()
    {
        return true;
    }

    /**
     * Does this tag contain a field with the specified id
     *
     * @see org.jaudiotagger.tag.Tag#hasField(java.lang.String)
     */
    public boolean hasField(String id)
    {
        return getFields(id).size() != 0;
    }

    /**
     * Is this tag empty
     *
     * @see org.jaudiotagger.tag.Tag#isEmpty()
     */
    public boolean isEmpty()
    {
        return frameMap.size() == 0;
    }

    /**
     * @return iterator of all fields, multiple values for the same Id (e.g multiple TXXX frames) count as separate
     *         fields
     */
    public Iterator<TagField> getFields()
    {
        //Iterator of each different frameId in this tag
        final Iterator<Map.Entry<String, Object>> it = this.frameMap.entrySet().iterator();

        //Iterator used by hasNext() so doesn't effect next()
        final Iterator<Map.Entry<String, Object>> itHasNext = this.frameMap.entrySet().iterator();


        return new Iterator<TagField>()
        {
            Map.Entry<String, Object> latestEntry = null;

            //this iterates through frames through for a particular frameId
            private Iterator<TagField> fieldsIt;

            private void changeIt()
            {
                if (!it.hasNext())
                {
                    return;
                }

                while (it.hasNext())
                {
                    Map.Entry<String, Object> e = it.next();
                    latestEntry = itHasNext.next();
                    if (e.getValue() instanceof List)
                    {
                        List<TagField> l = (List<TagField>) e.getValue();
                        //If list is empty (which it shouldn't be) we skip over this entry
                        if (l.size() == 0)
                        {
                            continue;
                        }
                        else
                        {
                            fieldsIt = l.iterator();
                            break;
                        }
                    }
                    else
                    {
                        //TODO must be a better way
                        List<TagField> l = new ArrayList<TagField>();
                        l.add((TagField) e.getValue());
                        fieldsIt = l.iterator();
                        break;
                    }
                }
            }

            //TODO assumes if have entry its valid, but what if empty list but very different to check this
            //without causing a side effect on next() so leaving for now
            public boolean hasNext()
            {
                //Check Current frameId, does it contain more values
                if (fieldsIt != null)
                {
                    if (fieldsIt.hasNext())
                    {
                        return true;
                    }
                }

                //No remaining entries return false
                if (!itHasNext.hasNext())
                {
                    return false;
                }

                //Issue #236
                //TODO assumes if have entry its valid, but what if empty list but very different to check this
                //without causing a side effect on next() so leaving for now
                return itHasNext.hasNext();
            }

            public TagField next()
            {
                //Hasn't been initialized yet
                if (fieldsIt == null)
                {
                    changeIt();
                }

                if (fieldsIt != null)
                {
                    //Go to the end of the run
                    if (!fieldsIt.hasNext())
                    {
                        changeIt();
                    }
                }

                if (fieldsIt == null)
                {
                    throw new NoSuchElementException();
                }
                return fieldsIt.next();
            }

            public void remove()
            {
                fieldsIt.remove();
            }
        };
    }

    /**
     * Count number of frames/fields in this tag
     *
     * @return
     */
    public int getFieldCount()
    {
        Iterator<TagField> it = getFields();
        int count = 0;

        //Done this way because it.hasNext() incorrectly counts empty list
        //whereas it.next() works correctly
        try
        {
            while (true)
            {
                TagField next = it.next();
                count++;
            }
        }
        catch (NoSuchElementException nse)
        {
            //this is thrown when no more elements
        }
        return count;
    }

    /**
     * Return count of fields, this considers a text frame with two null separated values as two fields, if you want
     * a count of frames @see getFrameCount
     *
     * @return count of fields
     */
    public int getFieldCountIncludingSubValues()
    {
        Iterator<TagField> it = getFields();
        int count = 0;

        //Done this way because it.hasNext() incorrectly counts empty list
        //whereas it.next() works correctly
        try
        {
            while (true)
            {
                TagField next = it.next();
                if (next instanceof AbstractID3v2Frame)
                {
                    AbstractID3v2Frame frame = (AbstractID3v2Frame) next;
                    if ((frame.getBody() instanceof AbstractFrameBodyTextInfo) && !(frame.getBody() instanceof FrameBodyTXXX))
                    {
                        AbstractFrameBodyTextInfo frameBody = (AbstractFrameBodyTextInfo) frame.getBody();
                        count += frameBody.getNumberOfValues();
                        continue;
                    }
                }
                count++;
            }
        }
        catch (NoSuchElementException nse)
        {
            //this is thrown when no more elements
        }
        return count;
    }

    //TODO is this a special field?

    public boolean setEncoding(String enc) throws FieldDataInvalidException
    {
        throw new UnsupportedOperationException("Not Implemented Yet");
    }

    /**
     * Retrieve the first value that exists for this generic key
     *
     * @param genericKey
     * @return
     */
    public String getFirst(FieldKey genericKey) throws KeyNotFoundException
    {
        return getValue(genericKey, 0);
    }

    /**
     * Retrieve the mth value that exists in the nth frame for this generic key
     *
     * @param genericKey
     * @param n          the index of the frame
     * @param m
     * @return
     */
    public String getSubValue(FieldKey genericKey, int n, int m)
    {
        String wholeValue = getValue(genericKey, n);
        List<String> values = TextEncodedStringSizeTerminated.splitByNullSeperator(wholeValue);
        if (values.size() > m)
        {
            return values.get(m);
        }
        return "";
    }

    /**
     * Retrieve the value that exists for this generic key and this index
     * <p/>
     * Have to do some special mapping for certain generic keys because they share frame
     * with another generic key.
     *
     * @param genericKey
     * @return
     */
    public String getValue(FieldKey genericKey, int index) throws KeyNotFoundException
    {
        if (genericKey == null)
        {
            throw new KeyNotFoundException();
        }

        FrameAndSubId frameAndSubId = getFrameAndSubIdFromGenericKey(genericKey);

        List<TagField> fields = getFields(genericKey);
        if (fields != null && fields.size() > index)
        {
            AbstractID3v2Frame frame = (AbstractID3v2Frame) fields.get(index);
            if (frame != null)
            {
                if (genericKey == FieldKey.TRACK)
                {
                    return String.valueOf(((FrameBodyTRCK) frame.getBody()).getTrackNo());
                }
                else if (genericKey == FieldKey.TRACK_TOTAL)
                {
                    return String.valueOf(((FrameBodyTRCK) frame.getBody()).getTrackTotal());
                }
                else if (genericKey == FieldKey.DISC_NO)
                {
                    return String.valueOf(((FrameBodyTPOS) frame.getBody()).getDiscNo());
                }
                else if (genericKey == FieldKey.DISC_TOTAL)
                {
                    return String.valueOf(((FrameBodyTPOS) frame.getBody()).getDiscTotal());
                }
                else if (genericKey == FieldKey.RATING)
                {
                    return String.valueOf(((FrameBodyPOPM) frame.getBody()).getRating());
                }
                else
                {
                    return doGetValueAtIndex(frameAndSubId, index);
                }
            }
            else
            {
                return "";
            }
        }
        return "";
    }

    /**
     * Create a new TagField
     * <p/>
     * Only textual data supported at the moment. The genericKey will be mapped
     * to the correct implementation key and return a TagField.
     *
     * @param genericKey is the generic key
     * @param value      to store
     * @return
     */
    public TagField createField(FieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException
    {
        if (genericKey == null)
        {
            throw new KeyNotFoundException();
        }

        FrameAndSubId formatKey = getFrameAndSubIdFromGenericKey(genericKey);
        if (genericKey == FieldKey.TRACK)
        {
            AbstractID3v2Frame frame = createFrame(formatKey.getFrameId());
            FrameBodyTRCK framebody = (FrameBodyTRCK) frame.getBody();
            framebody.setTrackNo(Integer.parseInt(value));
            return frame;
        }
        else if (genericKey == FieldKey.TRACK_TOTAL)
        {
            AbstractID3v2Frame frame = createFrame(formatKey.getFrameId());
            FrameBodyTRCK framebody = (FrameBodyTRCK) frame.getBody();
            framebody.setTrackTotal(Integer.parseInt(value));
            return frame;
        }
        else if (genericKey == FieldKey.DISC_NO)
        {
            AbstractID3v2Frame frame = createFrame(formatKey.getFrameId());
            FrameBodyTPOS framebody = (FrameBodyTPOS) frame.getBody();
            framebody.setDiscNo(Integer.parseInt(value));
            return frame;
        }
        else if (genericKey == FieldKey.DISC_TOTAL)
        {
            AbstractID3v2Frame frame = createFrame(formatKey.getFrameId());
            FrameBodyTPOS framebody = (FrameBodyTPOS) frame.getBody();
            framebody.setDiscTotal(Integer.parseInt(value));
            return frame;
        }
        else
        {
            return doCreateTagField(formatKey, value);
        }
    }

    /**
     * Create Frame for Id3 Key
     * <p/>
     * Only textual data supported at the moment, should only be used with frames that
     * support a simple string argument.
     *
     * @param formatKey
     * @param value
     * @return
     * @throws KeyNotFoundException
     * @throws FieldDataInvalidException
     */
    protected TagField doCreateTagField(FrameAndSubId formatKey, String value) throws KeyNotFoundException, FieldDataInvalidException
    {
        AbstractID3v2Frame frame = createFrame(formatKey.getFrameId());
        if (frame.getBody() instanceof FrameBodyUFID)
        {
            ((FrameBodyUFID) frame.getBody()).setOwner(formatKey.getSubId());
            try
            {
                ((FrameBodyUFID) frame.getBody()).setUniqueIdentifier(value.getBytes("ISO-8859-1"));
            }
            catch (UnsupportedEncodingException uee)
            {
                //This will never happen because we are using a charset supported on all platforms
                //but just in case
                throw new RuntimeException("When encoding UFID charset ISO-8859-1 was deemed unsupported");
            }
        }
        else if (frame.getBody() instanceof FrameBodyTXXX)
        {
            ((FrameBodyTXXX) frame.getBody()).setDescription(formatKey.getSubId());
            ((FrameBodyTXXX) frame.getBody()).setText(value);
        }
        else if (frame.getBody() instanceof FrameBodyWXXX)
        {
            ((FrameBodyWXXX) frame.getBody()).setDescription(formatKey.getSubId());
            ((FrameBodyWXXX) frame.getBody()).setUrlLink(value);
        }
        else if (frame.getBody() instanceof FrameBodyCOMM)
        {
            //Set description if set
            if (formatKey.getSubId() != null)
            {
                ((FrameBodyCOMM) frame.getBody()).setDescription(formatKey.getSubId());
                //Special Handling for Media Monkey Compatability
                if (((FrameBodyCOMM) frame.getBody()).isMediaMonkeyFrame())
                {
                    ((FrameBodyCOMM) frame.getBody()).setLanguage(Languages.MEDIA_MONKEY_ID);
                }
            }
            ((FrameBodyCOMM) frame.getBody()).setText(value);
        }
        else if (frame.getBody() instanceof FrameBodyUSLT)
        {
            ((FrameBodyUSLT) frame.getBody()).setDescription("");
            ((FrameBodyUSLT) frame.getBody()).setLyric(value);
        }
        else if (frame.getBody() instanceof FrameBodyWOAR)
        {
            ((FrameBodyWOAR) frame.getBody()).setUrlLink(value);
        }
        else if (frame.getBody() instanceof AbstractFrameBodyTextInfo)
        {
            ((AbstractFrameBodyTextInfo) frame.getBody()).setText(value);
        }
        else if (frame.getBody() instanceof FrameBodyPOPM)
        {
            ((FrameBodyPOPM) frame.getBody()).parseString(value);
        }
        else if (frame.getBody() instanceof FrameBodyIPLS)
        {
            PairedTextEncodedStringNullTerminated.ValuePairs pair = new PairedTextEncodedStringNullTerminated.ValuePairs();
            pair.add(formatKey.getSubId(), value);
            frame.getBody().setObjectValue(DataTypes.OBJ_TEXT, pair);
        }
        else if (frame.getBody() instanceof FrameBodyTIPL)
        {
            PairedTextEncodedStringNullTerminated.ValuePairs pair = new PairedTextEncodedStringNullTerminated.ValuePairs();
            pair.add(formatKey.getSubId(), value);
            frame.getBody().setObjectValue(DataTypes.OBJ_TEXT, pair);
        }
        else if ((frame.getBody() instanceof FrameBodyAPIC) || (frame.getBody() instanceof FrameBodyPIC))
        {
            throw new UnsupportedOperationException(ErrorMessage.ARTWORK_CANNOT_BE_CREATED_WITH_THIS_METHOD.getMsg());
        }
        else
        {
            throw new FieldDataInvalidException("Field with key of:" + formatKey.getFrameId() + ":does not accept cannot parse data:" + value);
        }
        return frame;
    }


    /**
     * @param formatKey
     * @param index
     * @return
     * @throws KeyNotFoundException
     */
    protected String doGetValueAtIndex(FrameAndSubId formatKey, int index) throws KeyNotFoundException
    {
        //Simple 1 to 1 mapping
        if (formatKey.getSubId() == null)
        {
            List<TagField> list = getFields(formatKey.getFrameId());
            if (list.size() > index)
            {
                return getTextValueForFrame((AbstractID3v2Frame) list.get(index));
            }
        }
        else
        {
            //Get list of frames that this uses
            List<TagField> list = getFields(formatKey.getFrameId());
            ListIterator<TagField> li = list.listIterator();
            List<String> listOfMatches = new ArrayList<String>();
            while (li.hasNext())
            {
                AbstractTagFrameBody next = ((AbstractID3v2Frame) li.next()).getBody();

                if (next instanceof FrameBodyTXXX)
                {
                    if (((FrameBodyTXXX) next).getDescription().equals(formatKey.getSubId()))
                    {
                        listOfMatches.add(((FrameBodyTXXX) next).getText());
                    }
                }
                else if (next instanceof FrameBodyWXXX)
                {
                    if (((FrameBodyWXXX) next).getDescription().equals(formatKey.getSubId()))
                    {
                        listOfMatches.add(((FrameBodyWXXX) next).getUrlLink());
                    }
                }
                else if (next instanceof FrameBodyCOMM)
                {
                    if (((FrameBodyCOMM) next).getDescription().equals(formatKey.getSubId()))
                    {
                        listOfMatches.add(((FrameBodyCOMM) next).getText());
                    }
                }
                else if (next instanceof FrameBodyUFID)
                {
                    if (Arrays.equals(((FrameBodyUFID) next).getUniqueIdentifier(), formatKey.getSubId().getBytes()))
                    {
                        listOfMatches.add(new String(((FrameBodyUFID) next).getUniqueIdentifier()));
                    }
                }
                else if (next instanceof FrameBodyIPLS)
                {
                    for (Pair entry : ((FrameBodyIPLS) next).getPairing().getMapping())
                    {
                        if (entry.getKey().equals(formatKey.getSubId()))
                        {
                            listOfMatches.add(entry.getValue());
                        }
                    }
                }
                else if (next instanceof FrameBodyTIPL)
                {
                    for (Pair entry : ((FrameBodyTIPL) next).getPairing().getMapping())
                    {
                        if (entry.getKey().equals(formatKey.getSubId()))
                        {
                            listOfMatches.add(entry.getValue());
                        }
                    }
                }
                else
                {
                    throw new RuntimeException("Need to implement getFields(FieldKey genericKey) for:" + next.getClass());
                }
            }
            if (listOfMatches.size() > index)
            {
                return listOfMatches.get(index);
            }
            else
            {
                return "";
            }
        }
        return "";
    }

    /**
     * Create a link to artwork, this is not recommended because the link may be broken if the mp3 or image
     * file is moved
     *
     * @param url specifies the link, it could be a local file or could be a full url
     * @return
     */
    public TagField createLinkedArtworkField(String url)
    {
        AbstractID3v2Frame frame = createFrame(getFrameAndSubIdFromGenericKey(FieldKey.COVER_ART).getFrameId());
        if (frame.getBody() instanceof FrameBodyAPIC)
        {
            FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
            body.setObjectValue(DataTypes.OBJ_PICTURE_DATA, Utils.getDefaultBytes(url, TextEncoding.CHARSET_ISO_8859_1));
            body.setObjectValue(DataTypes.OBJ_PICTURE_TYPE, PictureTypes.DEFAULT_ID);
            body.setObjectValue(DataTypes.OBJ_MIME_TYPE, FrameBodyAPIC.IMAGE_IS_URL);
            body.setObjectValue(DataTypes.OBJ_DESCRIPTION, "");
        }
        else if (frame.getBody() instanceof FrameBodyPIC)
        {
            FrameBodyPIC body = (FrameBodyPIC) frame.getBody();
            body.setObjectValue(DataTypes.OBJ_PICTURE_DATA, Utils.getDefaultBytes(url, TextEncoding.CHARSET_ISO_8859_1));
            body.setObjectValue(DataTypes.OBJ_PICTURE_TYPE, PictureTypes.DEFAULT_ID);
            body.setObjectValue(DataTypes.OBJ_IMAGE_FORMAT, FrameBodyAPIC.IMAGE_IS_URL);
            body.setObjectValue(DataTypes.OBJ_DESCRIPTION, "");
        }
        return frame;
    }


    /**
     * Delete fields with this generic key
     *
     * @param genericKey
     */
    public void deleteField(FieldKey genericKey) throws KeyNotFoundException
    {
        if (genericKey == null)
        {
            throw new KeyNotFoundException();
        }
        FrameAndSubId formatKey = getFrameAndSubIdFromGenericKey(genericKey);
        doDeleteTagField(formatKey);
    }

    /**
     * Internal delete method
     *
     * @param formatKey
     * @throws KeyNotFoundException
     */
    protected void doDeleteTagField(FrameAndSubId formatKey) throws KeyNotFoundException
    {
        //Simple 1 to 1 mapping
        if (formatKey.getSubId() == null)
        {
            removeFrame(formatKey.getFrameId());
        }
        else
        {
            //Get list of frames that this uses
            List<TagField> list = getFields(formatKey.getFrameId());
            ListIterator<TagField> li = list.listIterator();
            while (li.hasNext())
            {
                AbstractTagFrameBody next = ((AbstractID3v2Frame) li.next()).getBody();
                if (next instanceof FrameBodyTXXX)
                {
                    if (((FrameBodyTXXX) next).getDescription().equals(formatKey.getSubId()))
                    {
                        if(list.size()==1)
                        {
                            removeFrame(formatKey.getFrameId());
                        }
                        else
                        {
                            li.remove();
                        }
                    }
                }
                else if (next instanceof FrameBodyWXXX)
                {
                    if (((FrameBodyWXXX) next).getDescription().equals(formatKey.getSubId()))
                    {
                        if(list.size()==1)
                        {
                            removeFrame(formatKey.getFrameId());
                        }
                        else
                        {
                            li.remove();
                        }
                    }
                }
                else if (next instanceof FrameBodyUFID)
                {
                    if (Arrays.equals(((FrameBodyUFID) next).getUniqueIdentifier(), formatKey.getSubId().getBytes()))
                    {
                        if(list.size()==1)
                        {
                            removeFrame(formatKey.getFrameId());
                        }
                        else
                        {
                            li.remove();
                        }
                    }
                }
                //A single TIPL frame is used for multiple fields, so we just delete the matching pair rather than
                //deleting the frame itself unless now empty
                else if (next instanceof FrameBodyTIPL)
                {
                    PairedTextEncodedStringNullTerminated.ValuePairs pairs = ((FrameBodyTIPL) next).getPairing();
                    ListIterator<Pair> pairIterator = pairs.getMapping().listIterator();
                    while(pairIterator.hasNext())
                    {
                        Pair nextPair =  pairIterator.next();
                        if(nextPair.getKey().equals(formatKey.getSubId()))
                        {
                            pairIterator.remove();
                        }
                    }
                    if(pairs.getMapping().size()==0)
                    {
                        removeFrame(formatKey.getFrameId());
                    }
                }
                //A single IPLS frame is used for multiple fields, so we just delete the matching pair rather than
                //deleting the frame itself unless now empty 
                else if (next instanceof FrameBodyIPLS)
                {
                    PairedTextEncodedStringNullTerminated.ValuePairs pairs = ((FrameBodyIPLS) next).getPairing();
                    ListIterator<Pair> pairIterator = pairs.getMapping().listIterator();
                    while(pairIterator.hasNext())
                    {
                        Pair nextPair =  pairIterator.next();
                        if(nextPair.getKey().equals(formatKey.getSubId()))
                        {
                            pairIterator.remove();
                        }
                    }

                    if(pairs.getMapping().size()==0)
                    {
                        removeFrame(formatKey.getFrameId());
                    }
                }
                else
                {
                    throw new RuntimeException("Need to implement getFields(FieldKey genericKey) for:" + next.getClass());
                }
            }
        }
    }

    protected abstract FrameAndSubId getFrameAndSubIdFromGenericKey(FieldKey genericKey);

    /**
     * Get field(s) for this key
     *
     * @param genericKey
     * @return
     * @throws KeyNotFoundException
     */
    public List<TagField> getFields(FieldKey genericKey) throws KeyNotFoundException
    {

        if (genericKey == null)
        {
            throw new KeyNotFoundException();
        }

        FrameAndSubId formatKey = getFrameAndSubIdFromGenericKey(genericKey);

        //Get list of frames that this uses, as we are going to remove entries we don't want take a copy
        List<TagField> list = getFields(formatKey.getFrameId());
        List<TagField> filteredList = new ArrayList<TagField>();
        String subFieldId = formatKey.getSubId();

        //... do we need to refine the list further i.e we only want TXXX frames that relate to the particular
        //key that was passed as a parameter
        if (subFieldId != null)
        {
            for (TagField tagfield : list)
            {
                AbstractTagFrameBody next = ((AbstractID3v2Frame) tagfield).getBody();
                if (next instanceof FrameBodyTXXX)
                {
                    if (((FrameBodyTXXX) next).getDescription().equals(formatKey.getSubId()))
                    {
                        filteredList.add(tagfield);
                    }
                }
                else if (next instanceof FrameBodyWXXX)
                {
                    if (((FrameBodyWXXX) next).getDescription().equals(formatKey.getSubId()))
                    {
                        filteredList.add(tagfield);
                    }
                }
                else if (next instanceof FrameBodyCOMM)
                {
                    if (((FrameBodyCOMM) next).getDescription().equals(formatKey.getSubId()))
                    {
                        filteredList.add(tagfield);
                    }
                }
                else if (next instanceof FrameBodyUFID)
                {
                    if (Arrays.equals(((FrameBodyUFID) next).getUniqueIdentifier(), formatKey.getSubId().getBytes()))
                    {
                        filteredList.add(tagfield);
                    }
                }
                else if (next instanceof FrameBodyIPLS)
                {
                    for (Pair entry : ((FrameBodyIPLS) next).getPairing().getMapping())
                    {
                        if (entry.getKey().equals(formatKey.getSubId()))
                        {
                            filteredList.add(tagfield);
                        }
                    }
                }
                else if (next instanceof FrameBodyTIPL)
                {
                    for (Pair entry : ((FrameBodyTIPL) next).getPairing().getMapping())
                    {

                        if (entry.getKey().equals(formatKey.getSubId()))
                        {
                            filteredList.add(tagfield);
                        }
                    }
                }
                else
                {
                    throw new RuntimeException("Need to implement getFields(FieldKey genericKey) for:" + next.getClass());
                }
            }
            return filteredList;
        }
        else
        {
            return list;
        }
    }

    /**
     * This class had to be created to minimize the duplicate code in concrete subclasses
     * of this class. It is required in some cases when using the fieldKey enums because enums
     * cannot be sub classed. We want to use enums instead of regular classes because they are
     * much easier for end users to  to use.
     */
    class FrameAndSubId
    {
        private String frameId;
        private String subId;

        public FrameAndSubId(String frameId, String subId)
        {
            this.frameId = frameId;
            this.subId = subId;
        }

        public String getFrameId()
        {
            return frameId;
        }

        public String getSubId()
        {
            return subId;
        }
    }

    public Artwork getFirstArtwork()
    {
        List<Artwork> artwork = getArtworkList();
        if (artwork.size() > 0)
        {
            return artwork.get(0);
        }
        return null;
    }

    /**
     * Create field and then set within tag itself
     *
     * @param artwork
     * @throws FieldDataInvalidException
     */
    public void setField(Artwork artwork) throws FieldDataInvalidException
    {
        this.setField(createField(artwork));
    }

    /**
     * Create field and then set within tag itself
     *
     * @param artwork
     * @throws FieldDataInvalidException
     */
    public void addField(Artwork artwork) throws FieldDataInvalidException
    {
        this.addField(createField(artwork));
    }

    /**
     * Delete all instance of artwork Field
     *
     * @throws KeyNotFoundException
     */
    public void deleteArtworkField() throws KeyNotFoundException
    {
        this.deleteField(FieldKey.COVER_ART);
    }
}
