package com.cs;

import org.junit.Test;

import java.io.*;

public class JavaTest1 {
    public static int count=0;
    @Test
    public void test1() throws IOException {
        File file=new File("C:\\Users\\13973\\IdeaProjects");
        System.out.println(count(file));
    }

    public static int count(File file) throws IOException {
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f : files) {
                count(f);
            }
        }
        if (file.getName().endsWith(".java")){
            BufferedReader reader=new BufferedReader(new FileReader(file));
            String s;
            while ((s=reader.readLine())!=null){
                count++;
            }
        }
        return count;
    }
}
