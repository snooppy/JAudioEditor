/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaudioeditor;

import java.io.File;

/**
 *
 * @author dimon
 */
class AudioFileFilter extends javax.swing.filechooser.FileFilter {

    String[] extensions;
    String description;

    AudioFileFilter(String[] ext, String descr) {
        this.extensions = ext;
        this.description = descr;
    }

    AudioFileFilter(String ext, String descr) {
        String[] tmpExt = {ext};
        this.extensions = tmpExt;
        this.description = descr;
    }

    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return true;
        }
        String name = pathname.getName().toLowerCase();
        for (String ext : extensions) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}