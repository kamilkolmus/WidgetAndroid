package com.i7xaphe.widget;

import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2016-04-12.
 */
////////////////klasa z metodami do zapisu odczytu loaderListCaunter modyfikacji pliku tekstowego//////////////
public class MyFileClass {

    public static void SaveFile(File file, List<String> data)
    {
        FileWriter fileWriter=null;
        try {
            fileWriter = new FileWriter(file);
            for (int i = 0; i<data.size(); i++)
            {
                fileWriter.write(data.get(i));
                fileWriter.write("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileWriter!=null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //=============================================================================================
    public static List<String> LoadFile(File file)
    {
        FileInputStream fis = null;
   //     int numberOfLines=0;
        List<String> fullFileArray;
        String line;
        try
        {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        fullFileArray = new ArrayList<>();
        int i = 0;
        try
        {
            while((line=br.readLine())!=null)
            {
                fullFileArray.add(line);
                i++;
            }
        }
        catch (IOException e) {e.printStackTrace();}
        return fullFileArray;
    }
    //=============================================================================================
    public static void AddNewLine(File file, String data)
    {
        FileWriter fileWriter=null;
        try {
            //true po to by można było dopisywać
            fileWriter = new FileWriter(file,true);
            fileWriter.write(data);
            fileWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileWriter!=null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    //=============================================================================================
    public static void ClearFile(File file)
    {
        FileWriter fileWriter=null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileWriter!=null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //=============================================================================================
}
