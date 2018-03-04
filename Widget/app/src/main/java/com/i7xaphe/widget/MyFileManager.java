package com.i7xaphe.widget;

import java.io.File;
import java.util.List;

/**
 * Created by Kamil on 2016-08-14.
 */
public class MyFileManager {
    private List<String> list;
    private File file;

    public MyFileManager(File file) {
        this.file = file;
        list = MyFileClass.LoadFile(file);
    }

    public int getListSize() {
        return list.size();
    }

    public List<String> getList() {
        return list;
    }

    public String getElement(int i) {
        if (i >= list.size()) {
            return "";
        }
        return list.get(i);
    }

    public void addToList(String line) {
        list.add(line);
    }

    public boolean removeLine(String line) {
        return list.remove(line);
    }
    public void removeLine(int position) {
         list.remove(position);
    }

    public void removeLines(String str) {
        while (removeLine(str)) ;

    }

    public boolean swapElement(String oldElement, String newElement) {
        if (list.contains(oldElement)) {
            list.set(list.indexOf(oldElement), newElement);
            return true;
        } else

            return false;
    }

    public void clearList() {
        list.clear();
    }

    public boolean checkIfExist(String line) {
        return list.contains(line);
    }

    public boolean contains(String str) {

        return list.contains(str);

    }
    public void setElement(int position,String line){
        if(position<list.size()){
            list.set(position,line);
        }
    }

    public boolean fillList(String str, int max) {
        if (list.size() > max) return false;

        for (int i = list.size(); i < max; i++) {
            list.add(str);
        }
        return true;
    }
    public void confirmChanges() {
        MyFileClass.SaveFile(file, list);
    }

}
