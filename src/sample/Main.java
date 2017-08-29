package sample;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Main {

    static Map<String, FreqPos> index = new HashMap<>();
    public static void main(String[] args) throws IOException, InterruptedException {

        /*** creating the list of documents paths to index****/
        String dir = System.getProperty("user.dir");

        dir = dir.substring(0, dir.length()-"sample/".length());
        String docsPath = dir + "/test_data";
        IndexingFiles indexingFiles = new IndexingFiles();
        //System.out.println(args[0]);
        String s = args[0];
        //ProcessBuilder pb = new ProcessBuilder("/Users/alisa/IdeaProjects/inverted_index1/src/sample/run2.sh", "enter the number of relevant documents to retrieve");
        System.out.println("Enter the int K - number of most relevant documents for the query");
        String input = System.console().readLine();
        int K = Integer.parseInt(input);

        System.out.println("Enter the absolute path to the folder with the documents to index in your PC");
        System.out.println("to use my collection type in 'default'");
        input = System.console().readLine();
        if (input.compareTo("default")!=0) {
            docsPath = input;
        }
        LinkedList<String> docs_paths = indexingFiles.listFilesForFolder(docsPath);
        /*splitting the documents content into tokens***/
        /* #TODO normalization and stemming is needed*/

        indexingFiles.ParseFile();

        indexingFiles.SortDictionary();
        indexingFiles.search(s, K);

        //indexingFiles.GapEncode();
        //indexingFiles.VBEncode();



    }
}
