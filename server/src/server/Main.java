package server;

import javafx.collections.ObservableList;

import java.io.*;

import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;

import java.util.Enumeration;

/**
 * @author Joseph Robertson
 */
public class Main  {
    /**
     * Main method, creates and runs threads required for server
     * @param args
     * @see getFiles
     */
    public static void main(String[] args) {
        String adressName="";
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    System.out.println(i.getHostAddress());
                    if ((i.getHostAddress() + "").charAt(0) != '0') {
                        adressName = ((InetAddress) ee.nextElement()).getHostAddress() + "";
                        break;
                    }
                }
                if (adressName != "") break;
                System.out.println(adressName);
            }
        }
        catch (java.lang.RuntimeException r){adressName="localHost";}
        catch (java.net.SocketException s){}
        System.out.println(adressName);

        Runnable all=new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        Thread.sleep(100);
                        ServerSocket serverSocket=new ServerSocket(1234);
                        while (true){
                            Socket socket=serverSocket.accept();
                            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            String command=br.readLine();
                            if (command.equals("DIR")){
                                DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                                if((new File(".."+File.separator+"share")).exists()) {
                                    ObservableList<String> list = getFiles.getNames(".."+File.separator+"share");

                                    for (String x : list) {
                                        out.writeBytes(x + "\n");
                                    }
                                }
                            }
                            else if (command.split(" ")[0].equals("UPLOAD")){
                                String str=command.split(" ")[1];

                                DataInputStream in = new DataInputStream(socket.getInputStream());

                                String fileLocation="."+File.separator+".."+File.separator+"share"+File.separator+str;
                                
                                (new File(fileLocation)).getParentFile().mkdirs();
                                (new File(fileLocation)).createNewFile();

                                FileWriter out=new FileWriter(fileLocation);
                                System.out.println("reciving: "+str);

                                while((str = in.readLine())!=null){

                                    out.write(str+"\n");
                                }
                                out.close();
                            }
                            else if (command.split(" ")[0].equals("DOWNLOAD")){
                                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                                String location=command.split(" ")[1];

                                if (location.equals(null)!=true)
                                    System.out.println("sending: "+location);

                                if (new File(location).exists()) {
                                    BufferedReader brF = new BufferedReader(new FileReader(location));
                                    String line;
                                    if ((new File(location)).exists()) {
                                        while ((line = brF.readLine()) != null) {
                                            out.writeBytes(line + "\n");
                                        }
                                    }
                                    socket.close();
                                }

                                socket.close();
                            }

                            socket.close();
                        }

                    }
                    catch (Exception e){System.out.println(e);}
                }
            }
        };
        Thread input=new Thread(all);
        input.start();
    }
}
