package com.nanodegree.abhinav.titanfilms.tfutils;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.application.TitanFilmsApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Singleton Design Pattern implemented.
 * Reads configurations defined in appconfig file.
 *
 * Created by Abhinav Puri
 */
public final class AppConfig {

    /**
     * Singleton Instance variable
     */
    public static AppConfig appConfig= null;

    /**
     * Config file data is saved here
     * Mutable StringBuilder Class is used.
     */
    private static StringBuilder readData = null;

    /**
     * Surpassing default constructor :
     */
    private AppConfig() {}

    /**
     * Making a Singleton Thread Safe Instance Initiation
     */
    public synchronized static AppConfig Instance() throws IOException{

        if(appConfig == null) {

            // Load config file :
            loadFile();

            // Instantiation of singleton :
            appConfig = new AppConfig();
        }

        return appConfig;
    }

    /**
     * Loads the config file and saves them.
     *
     * @throws IOException while reading from file.
     */
    private static void loadFile() throws IOException{

        // Reading File :
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(TitanFilmsApplication.getResource().openRawResource(R.raw.appconfig)));

        // Instantiating String Builder :
        readData = new StringBuilder();

        String line = null;

        // Looping through every line of config file :
        while (!(line = bufferedReader.readLine()).equals("##EOF##") ){

            line = line.trim();
            if(line.length() >= 2 && line.substring(0,2).equals("##")){
                // Skip these, they are comments :
                continue;
            }
            if(line.length()>0) {
                readData.append(line);
            }
        }
    }

    /**
     * This function finds map defined by the id specified as input.
     *
     * @param id
     * @return Hash Map
     */
    public static HashMap<String,String> findMap(String id){
        HashMap<String,String> map = null;

        String value = valueIfExist(id.trim());

        // Value should not be empty :
        if(!(value == null) && !(value.equals(""))){
            map = fillMap(map,value);
        }

        return map;
    }

    /**
     * This function confirms whether value exist or not,
     * if exist returns in string value of id,
     * else empty String is returned.
     *
     * @param id
     * @return value in String
     */
    private static String valueIfExist(String id) {

        String data = readData.toString();
        String value = "";

        // If direct match found or wild card is found, only then proceed :
        if(!(data.contains(id)) && !(data.contains("*." + id.substring(id.indexOf('.') + 1)))){
            return value;
        }

        try {

            // Find first '=' :
            int startIndex = data.indexOf("=",data.indexOf(id.substring(id.indexOf('.') + 1)))+1;

            // Find ';' for last index :
            int endIndex = data.indexOf(";",startIndex);

            value = data.substring(startIndex,endIndex).trim();

        } catch(IndexOutOfBoundsException e){
            // Do nothing, let the returning string be empty.
        }

        return value;
    }

    /**
     * This function inflated map,
     * Called from findMap function.
     *
     * @param map
     * @param value
     * @return map
     */
    private static HashMap<String,String> fillMap(HashMap<String,String> map, String value){

        // It should start with "[" and end with "]"
        if((!value.startsWith("[")) && (!value.endsWith("]"))){
            map = null;
            return map;
        }

        // Initialize hash map :
        map =  new HashMap<>();

        // Continue till all values are read :
        while(value.indexOf("{",0)!=-1){

            String entry = value.substring(value.indexOf("{")+1,value.indexOf("}")).trim();
            entry = entry.substring(1,entry.length()-1);

            String entryKey = entry.substring(0,entry.indexOf(":")).trim();
            entryKey = entryKey.substring(0,entryKey.length()-1);
            String entryValue = entry.substring(entry.indexOf(":")+1).trim();
            entryValue = entryValue.substring(1);

            map.put(entryKey,entryValue);

            value = value.substring(value.indexOf("}")+1);
        }
        return map;
    }

}

