package client;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.scene.layout.GridPane;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.Socket;

import java.util.List;

import java.io.*;
import java.util.regex.Pattern;

/**
 * @author Joseph Robertson
 * @see javafx.application.Application
 */
public class Main extends Application {
    static String IP;
    static String location;
    static File base;

    /**
     * the start method creates the UI and the treads that run it
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("client");

        SplitPane both=new SplitPane();
        TreeItem<place> up=new TreeItem<>();
        getFiles.getNames(base,up);
        up.setExpanded(true);
        final TreeView<place> downloads=new TreeView<place>(up);
        final ListView<String> uploads=new ListView<>();
        both.getItems().addAll(downloads,uploads);

        GridPane buttons=new GridPane();
        Button exit=new Button("exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        Button changeFolder=new Button("change folder");
        changeFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser a=new DirectoryChooser();
                base=a.showDialog(new Stage());
            }
        });
        final Button upload = new Button("Upload");
        upload.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    TreeItem<place> selected=downloads.getSelectionModel().getSelectedItem();
                    try {
                        File location = selected.getValue().getFile();

                        if (location.isFile() && location.exists() && !(location.isDirectory())) {
                            String uploadTarget = location.getName();
                            if (uploadTarget.equals(null)!=true) {

                                Socket socket = new Socket(IP, 1234);
                                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                                BufferedReader br = new BufferedReader(new FileReader(location));
                                String line;
                                out.writeBytes("UPLOAD " + uploadTarget + "\n");
                                out.flush();
                                while ((line = br.readLine()) != null) {
                                    out.writeBytes(line + "\n");
                                }
                                out.flush();
                                socket.close();
                            }
                        }
                    }
                    catch (java.lang.NullPointerException n){System.out.println("please select a file");}

                }
                catch (Exception ex){
                    System.err.println(ex);
                }
            }
        });
        final ObservableList<String> uploadNames=FXCollections.observableArrayList();
        uploads.setItems(uploadNames);
        final Button download = new Button("Download");
        download.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    String location =uploads.getSelectionModel().getSelectedItems()+"";
                    location = "."+File.separator+location.substring(1, location.length() - 1);


                    String fileLocation="."+File.separator+"Downloads"+File.separator+location;

                    if (fileLocation.equals("."+File.separator+"Downloads"+File.separator+"."+File.separator)!=true) {
                        if (new File(fileLocation).exists() != true) {
                            (new File(fileLocation)).mkdirs();
                        }

                        Socket socket = new Socket(IP, 1234);
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        DataInputStream in = new DataInputStream(socket.getInputStream());

                        File need=new File("."+File.separator+"Downloads" + location.substring(location.lastIndexOf(File.separator),location.length()));
                        if(need.exists()!=true)
                            need.getParentFile().mkdirs();
                        FileWriter writer = new FileWriter(need);

                        //System.out.println("."+File.separator+"Downloads"+File.separator + location.split(File.separator)[location.split(File.separator).length-1]);
                        out.writeBytes("DOWNLOAD " + location + "\n");
                        out.flush();

                        String line;
                        while ((line = in.readLine()) != null) {
                            writer.write(line + "\n");
                        }                        writer.close();
                        socket.close();
                    }
                }
                catch (Exception ex){
                    System.err.println(ex);
                }

            }
        });
        buttons.add(download,0,0);
        buttons.add(upload,1,0);
        buttons.add(changeFolder,2,0);
        buttons.add(exit,3,0);

        BorderPane back =new BorderPane();
        back.setTop(buttons);
        back.setCenter(both);
        back.setPadding(new Insets(10,10,10,10));

        primaryStage.setScene(new Scene(back,600,600));
        primaryStage.show();
        Runnable names =new Runnable(){
            public void run(){
                while(true){
                    Socket socket=new Socket();
                    try {
                        socket=new Socket(IP,1234);
                        PrintWriter out=new PrintWriter(socket.getOutputStream());
                        out.println("DIR");
                        out.flush();
                        DataInputStream in=new DataInputStream(socket.getInputStream());
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    uploadNames.clear();
                                }
                            });
                        String str="";
                        final List<String> check=FXCollections.observableArrayList();
                        while ((str=in.readLine())!=null){

                            final String Name=str;
                            Platform.runLater(new Runnable() {
                                  @Override
                                  public void run() {
                                      check.add(Name);
                                      uploadNames.add(Name);
                                  }
                            });
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    TreeItem<place> up=new TreeItem<>();
                                    getFiles.getNames(base,up);
                                    up.setExpanded(true);
                                    downloads.setRoot(up);

                                }
                                catch (java.io.FileNotFoundException e){}
				catch(Exception e){}
                            }
                        });
                        Thread.sleep(7500);
                    }
                    catch (java.net.ConnectException e){System.out.println(e);try{socket.close();}catch (java.io.IOException E){}System.exit(1);}
                    catch(Exception e){}
                }
            }
        };
        Thread updates=new Thread(names);
        updates.start();
    }

    /**
     * this is the main method
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length>0){
            IP=args[0];
            location=args[1];
        }
        else {
            IP="localHost";
            location=".";
        }
        base=new File(location);
        launch(args);
    }
}
