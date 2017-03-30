package client;

import java.io.File;

/**
 * Created by Joseph on 28/03/17.
 */
public class place {
    private File file;

    /**
     * the default constructor of place
     *
     * @param file
     */
    public place(File file){
        this.file=file;
    }
    /**
     * returns file
     *
     * @return File
     */
    public File getFile(){
        return file;
    }
    /**
     * returns name of file as a String
     *
     * @return String
     */
    public String toString(){
        return file.getName();
    }
}
