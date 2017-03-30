package server;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.io.FileNotFoundException;
import java.io.File;

/**
 * Created by Joseph on 25/02/17.
 */
public class getFiles {
    /**
     * getNames returns a observable list of all the files contained within the directory "share"
     *
     * @param folder
     * @return list
     * @throws FileNotFoundException
     */
    public static ObservableList<String> getNames(String folder) throws FileNotFoundException{
        ObservableList<String> list= FXCollections.observableArrayList();

        File[] listOfFiles = (new File(System.getProperty("user.dir")+File.separator+folder)).listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                list.add(folder+File.separator+listOfFiles[i].getName());
            }
            else if (listOfFiles[i].isDirectory()) {
                list.addAll(getNames(folder+File.separator+listOfFiles[i].getName()));
            }
        }
        return list;
    }
}
