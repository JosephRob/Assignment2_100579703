package client;

import javafx.scene.control.TreeItem;

import java.io.FileNotFoundException;
import java.io.File;

/**
 * Created by Joseph on 25/02/17.
 */
public class getFiles {
    /**
     * gen names takes a file and a tree item of type place, then fills the tree item with the contents of the directory
     *
     * @see place
     * @param folder
     * @param base
     * @throws FileNotFoundException
     */
    public static void getNames(File folder, TreeItem<place> base) throws FileNotFoundException{
        if(folder.isDirectory()) {
            File files[] = folder.listFiles();
            for (int x = 0; x < files.length; x++) {
                TreeItem<place> more = new TreeItem<>(new place(files[x]));
                more.setExpanded(true);
                getNames(files[x], more);
                base.getChildren().add(more);
            }
        }
    }
}
