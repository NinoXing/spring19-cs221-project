package edu.uci.ics.cs221.index.ranking;

import edu.uci.ics.cs221.analysis.ComposableAnalyzer;
import edu.uci.ics.cs221.analysis.PorterStemmer;
import edu.uci.ics.cs221.analysis.PunctuationTokenizer;
import edu.uci.ics.cs221.index.inverted.DeltaVarLenCompressor;
import edu.uci.ics.cs221.index.inverted.InvertedIndexManager;
import edu.uci.ics.cs221.storage.Document;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Team8TfIdfTest {
    private  static InvertedIndexManager indexmanger;
    private static String path = "./index/Team8TfIdfTest";
    private  static Document[] documents;
    @BeforeClass
    public static void init(){
        indexmanger = InvertedIndexManager.createOrOpenPositional(path,
                new ComposableAnalyzer(new PunctuationTokenizer(), new PorterStemmer()),
                new DeltaVarLenCompressor());

        documents = new Document[] {
                new Document("An apple a day keeps a doctor away"),
                new Document("One rotten apple spoils the whole barrel"),
                new Document("Fortune knocks once at everyone's rotten door"),
                new Document("Throw away the apple because of the core")

        };
    }

    /**
     Test if searchTfIdf function works well
     **/
    @Test
    public void test1(){
        //add documents
        for(int i=0;i<documents.length;i++){
            indexmanger.addDocument(documents[i]);
            indexmanger.flush();
        }
        List<String> keywords = Arrays.asList("apple", "apple", "rotten");
        Iterator<Document> res = indexmanger.searchTfIdf(keywords,4);
        List<Document> resDoc = Arrays.asList(documents[1],documents[2],documents[0],documents[4]);
        int counter = 0;
        while(res.hasNext()){
            assertEquals(res.next(),resDoc.get(counter++));
        }
    }

    @Test
    public void test2(){
        for(int i=0;i<documents.length;i++){
            indexmanger.addDocument(documents[i]);
        }
        indexmanger.flush();
        //test one non-exist word in our documents
        assertEquals(0,indexmanger.getDocumentFrequency(0,"cs221"));
    }

    @After
    public void clean() {

        try {
            File folder = new File(path);
            String[] entries = folder.list();
            for(String s: entries) {
                File currentFile = new File(folder.getPath(),s);
                currentFile.delete();
            }

            if (folder.delete()) {
                System.out.println("Folder deleted successfully");
            } else {
                System.out.println("Failed to delete the folder");
            }
        } catch (Exception e) {
            System.out.println("Something went wrong when deleting file");
        }
    }
}