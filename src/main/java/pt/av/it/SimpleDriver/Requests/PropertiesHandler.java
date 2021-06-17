package pt.av.it.SimpleDriver.Requests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class PropertiesHandler {

    private final Properties applicationproperties=new Properties();
    private final Properties osmclientfile=new Properties();
    private final OsmClientProperties osmClientProperties=new OsmClientProperties();
    private final String path="src/main/resources/";
    private final String name="osmClient_";
    private int numberofOsmClients;
    private ArrayList <OsmClientProperties> osmClientPropertieslist;

    public PropertiesHandler() {
        this.loadPropertiesFile();
    }

    /**
     * Only to be used in a local scenario
     *
     **/

    public PropertiesHandler(String implementation) {
        assert implementation== "local";
        this.loadPropertiesFile();

         this.numberofOsmClients=getNumberofOsmClients();
         this.osmClientPropertieslist=new ArrayList<>(this.numberofOsmClients);

         for(int i=0;i<numberofOsmClients;i++){

         osmClientPropertieslist.add(readProperties(i));
         }

    }

    private void loadPropertiesFile(){
        InputStream inputStream=null;
        try {
            File file=new File(path+"application.properties");
            if(!file.exists() || !file.canRead()){
                return;
            }

            //System.out.println(file.exists());
            //System.out.println(file.getAbsolutePath());
            //System.out.println(file.canRead());

            inputStream=new FileInputStream(path+"application.properties");

            this.applicationproperties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadPropertiesFile(String path){
        InputStream inputStream=null;
        try {
            inputStream=new FileInputStream(path);
            this.osmclientfile.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private OsmClientProperties readProperties(int clientNumber){
        loadPropertiesFile(path+name+clientNumber+".properties");
        osmClientProperties.uri=osmclientfile.getProperty("uri");
        osmClientProperties.user=osmclientfile.getProperty("user");
        osmClientProperties.password=osmclientfile.getProperty("password");
        osmClientProperties.project=osmclientfile.getProperty("project_id");
        osmClientProperties.vimAccount=osmclientfile.getProperty("vim_account");
        return osmClientProperties;
    }
    
    protected String readUserAgent(){
        return applicationproperties.getProperty("userAgent");
    }
    public int getNumberofOsmClients(){
        return Integer.parseInt(applicationproperties.getProperty("numOsmClients"));
    }

    public OsmClientProperties getOsmClientProperties(int osmclientId) {
        return osmClientPropertieslist.get(osmclientId);
    }

    public String getPath() {
        return path;
    }
}
