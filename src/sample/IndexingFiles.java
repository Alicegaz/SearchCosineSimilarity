package sample;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.regex.Pattern;
import java.io.File;
import java.util.regex.Matcher;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.stream.Collectors;
import java.lang.Object;

public class IndexingFiles {

    static int N = 0;
    int[] y;
    static int K_PARAM = 50;
    static LinkedList<String> docs_paths = new LinkedList<>();
    static Map<String, FreqPos> index = new HashMap<>();
    double beta = 0.6;
    String normal = "[\\p{Punct}+]";
    String stop = "\\b(a|about|above|across|after|afterwards|again|against|all|almost|alone|along|already|also|although|always|am|among|amongst|amoungst|amount|an|and|another|any|anyhow|anyone|anything|anyway|anywhere|are|around|as|at|back|be|became|because|become|becomes|becoming|been|before|beforehand|behind|being|below|beside|besides|between|beyond|bill|both|bottom|but|by|call|can|cannot|cant|co|computer|con|could|couldnt|cry|de|describe|detail|do|done|down|due|during|each|eg|eight|either|eleven|else|elsewhere|empty|enough|etc|even|ever|every|everyone|everything|everywhere|except|few|fifteen|fify|fill|find|fire|first|five|for|former|formerly|forty|found|four|from|front|full|further|get|give|go|had|has|hasnt|have|he|hence|her|here|hereafter|hereby|herein|hereupon|hers|herse|hi|himse|his|how|however|hundred|i|ie|if|in|inc|indeed|interest|into|is|it|its||tse|keep|last|latter|latterly|least|less|ltd|made|many|may|me|meanwhile|might|mill|mine|more|moreover|most|mostly|move|much|must|my|myse|name|namely|neither|never|nevertheless|next|nine|no|nobody|none|noone|nor|not|nothing|now|nowhere|of|off|often|on|once|one|only|onto|or|other|others|otherwise|our|ours|ourselves|out|over|own|part|per|perhaps|please|put|rather|re|same|see|seem|seemed|seeming|seems|serious|several|she|should|show|side|since|sincere|six|sixty|so|some|somehow|someone|something|sometime|sometimes|somewhere|still|such|system|take|ten|than|that|the|their|them|themselves|then|thence|there|thereafter|thereby|therefore|therein|thereupon|these|they|thick|thin|third|this|those|though|three|through|throughout|thru|thus|to|together|too|top|toward|towards|twelve|twenty|two|un|under|until|up|upon|us|very|via|was|we|well|were|what|whatever|when|whence|whenever|where|whereafter|whereas|whereby|wherein|whereupon|wherever|whether|which|while|whither|who|whoever|whole|whom|whose|why|will|with|within|without|would|yet|you|your|yours|yourself|yourselves)\\b";
    public IndexingFiles(int[] test)
    {
        y = test;
    }
    public LinkedList<String> listFilesForFolder(final String folder)
    {

        try(Stream<Path> pathStream = Files.walk(Paths.get(String.valueOf(folder))))
        {
            pathStream.forEach(filePath ->{
                if (Files.isRegularFile(filePath))
                {
                    docs_paths.add(String.valueOf(filePath));
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return docs_paths;
    }

    //#TODO to lower case, normalize, Soundex, Lemmatization, sample.Stemming

    public void ParseFile() throws IOException {
        PrintWriter out = new PrintWriter("Dictionary.txt");

        //initializing the initial parameters for the document number and the position of the word
        int num = 0;
        int pos = 0;

        N = docs_paths.size();
        //iterating through the documents
        for (int i = 0; i < docs_paths.size(); i++)
        {
            num = i;
            //getting the document
            BufferedReader reader = new BufferedReader(new FileReader(docs_paths.get(i)));

            //going through the document lines
            for (String ln = reader.readLine(); ln != null; ln = reader.readLine())
            {
                ln.replaceAll("^ +| +$|(?= )", "");
                for (String _word: ln.split(" "))//splitting the line of the document into tokens
                {
                    String word = _word.toLowerCase(); //case folding to lower case
                    pos++;
                    word = word.replaceAll(normal, ""); // normalizing tokens, removing punctuation, spaces and '_'
                    word = word.replaceAll("\\s+", "");
                    Stemmer stemmed = new Stemmer(word); //using the Potter's stemmer to stem tokens
                    stemmed.stem();
                    word = stemmed.toString();
                    Pattern pt = Pattern.compile(stop); //if the normalized token is a stop word remove it
                    Matcher m = pt.matcher(word);
                    word = m.replaceAll("");
                    if (word.compareTo("")!=0)
                    {
                        FreqPos idx = index.get(word);

                        out.write(word + " -> " + docs_paths.get(i) + "\n");
                        if (idx == null)
                        {
                            idx = new FreqPos(num, pos);
                            index.put(word, idx);
                        }
                        else
                        {
                            idx.add(num, pos);
                        }
                    }
                }
                //System.out.println("adding " + docs_paths.get(i) + " " + pos + " words");
            }
            pos = 0;
        }

        out.close();

    }

    public String Normalize(String wd)
    {
        wd = wd.replaceAll(normal, ""); // normalizing tokens, removing punctuation, spaces and '_'
        wd = wd.replaceAll("\\s+", "");
        Stemmer stemmed = new Stemmer(wd); //using the Potter's stemmer to stem tokens
        stemmed.stem();
        wd = stemmed.toString();
        Pattern pt = Pattern.compile(stop); //if the normalized token is a stop word remove it
        Matcher m = pt.matcher(wd);
        wd = m.replaceAll("");
        return wd;
    }


    class MyComparator implements Comparator<Object> {

        Map<String, FreqPos> map;

        public MyComparator(Map<String, FreqPos> map) {
            this.map = map;
        }

        public int compare(Object o1, Object o2) {

            int comp = ((String)o1).compareTo((String)o2);
            if (comp == 0)
                return -1;
            else
                return comp;

        }
    }

    class MyComparator2 implements Comparator<Object> {

        Map<Integer, Double> map;

        public MyComparator2(Map<Integer, Double> map) {
            this.map = map;
        }

        public int compare(Object o1, Object o2) {

            int comp = ((Double)map.get(o1)).compareTo((Double)map.get(o2));
            if (comp == 0)
                return -1;
            else
                return comp;

        }
    }

    public void SortDictionary() throws IOException
    {
        PrintWriter out1 = new PrintWriter("SortedDictionary.txt");

        //sorting the dictionary in the alphabetical order
        MyComparator comparator = new MyComparator(index);
        Map<String, FreqPos> tm = new TreeMap<String, FreqPos>(comparator);
        tm.putAll(index);
        //index = tm;
        //iterating through the objects of the map

        //Writing the index to the file
        for (Map.Entry<String, FreqPos> entry : tm.entrySet())
        {
            String entry_word = entry.getKey();
            //entry.getKey().replaceAll("\\s+", "");
            LinkedList<Integer> entry_list = index.get(entry_word).postings_list;
            //double idf = tf_estimate()
            index.get(entry_word).set_idf();
            index.get(entry_word).set_tfs();
            out1.write(entry_word + " [idf: " + index.get(entry_word).idf + " ] " + " -> ");

            for (int i = 0; i<index.get(entry_word).postings_list.size(); i++)
            {
                //out1.write(docs_paths.get(entry_list.get(i)) + " | ");
                out1.write(entry_list.get(i)+ " (tf: " + index.get(entry_word).tfs_list.get(i) + " ) " + " | ");
            }
            out1.write("\n");
        }
        out1.write("\n");
        out1.flush();
    }

    //<doc> - <pos of w1> <pos of w2>
    public LinkedList<Pair<Integer, Pair<Integer, Integer>>> PositionalIntersect(FreqPos p1, FreqPos p2, int k)
    {
        LinkedList<Pair<Integer, Pair<Integer, Integer>>> answer = new LinkedList<Pair<Integer, Pair<Integer, Integer>>>();

        LinkedList<Integer> docs1 = p1.postings_list;
        LinkedList<Integer> docs2 = p2.postings_list;

        int i = 0;
        int j = 0;
        while(i<docs1.size() && j < docs2.size())
        {
            if (docs1.get(i) == docs2.get(j))
            {
                LinkedList<Integer> l = new LinkedList<Integer>();
                LinkedList<Integer> pp1 = p1.positions_lists.get(i);
                LinkedList<Integer> pp2 = p2.positions_lists.get(j);

                int pp1_index = 0;
                int pp2_index = 0;
                while (pp1_index<pp1.size())
                {
                    while (pp2_index<pp2.size())
                    {
                        if (Math.abs(pp1.get(pp1_index) - pp2.get(pp2_index)) <= k)
                        {
                            l.add(pp2.get(pp2_index));
                        }
                        else if (pp2.get(pp2_index) > pp1.get(pp1_index))
                            break;
                        pp2_index++;
                    }
                    while (l.size()!=0 && Math.abs(l.get(0) - pp1.get(pp1_index)) > k)
                    {
                        l.remove(0);
                    }
                    for (int m = 0; i<l.size(); i++)
                    {
                        answer.add(new Pair<Integer, Pair<Integer, Integer>>(docs1.get(i), new Pair<Integer, Integer>(pp1.get(pp1_index), l.get(m))));
                    }
                    pp1_index++;
                }
                i++;
                j++;
            }
            else if (docs1.get(i) < docs2.get(j))
            {
                i++;
            }
            else
                j++;
        }
        return answer;
    }

    public LinkedList<String> QueryPreProcess(String query)
    {
        query.replaceAll("^ +| +$|(?= )", "");
        String[] s = query.split(" ");
        for (int i = 0; i<s.length; i++)
        {
            s[i] = Normalize(s[i]);
        }
        return new LinkedList<String>(Arrays.asList(s));

    }

    public void metric(int[] predicted)
    {
        int t_p = 0;
        int t_n = 0;
        int f_p = 0;
        int f_n = 0;
        for (int i = 0; i<predicted.length; i++)
        {
            if (predicted[i] == y[i])
            {
                if(predicted[i] == 1) {
                    t_p++;
                }
                else t_n++;
            }
            else
            {
                if(predicted[i] == 0)
                    f_n++;
                else
                    f_p++;
            }
        }
        System.out.println("true positive "+t_p+" true negative "+t_n+" false positive "+f_p+" false negative "+f_n);
        double precision = (double)t_p/(double)(t_p+f_p);

        double recall = (double)t_p/(double)(t_p+f_n);
        double f_1 = (1+beta*beta)*(precision*recall/((beta*beta*precision)+recall));
        System.out.println("precision "+precision+" recall "+recall+" f_1 "+f_1);
    }

    //retrieve K most relevant documents
    public void search(String query, int K) throws InterruptedException
    {
        LinkedList<String> words = QueryPreProcess(query);

        LinkedList<Integer> docs_list = new LinkedList<Integer>();

        LinkedList<Integer> docs_list1 = new LinkedList<Integer>();
        int count = 0;
        int[] predicted = new int[N];
        Arrays.fill(predicted, 0);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < words.size(); i++)
        {
            if (index.get(words.get(i))!=null) {
                LinkedList<Integer> t_list = index.get(words.get(i)).postings_list;
                for (int b = 0; b < t_list.size(); b++) {
                    if (!docs_list.contains(t_list.get(b))) {
                        docs_list.add(t_list.get(b));

                        //predicted[t_list.get(b)] = 1;
                    }
                }
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("time to find docs "+estimatedTime + " ms");
        long  startTime2 = System.currentTimeMillis();
        if (docs_list.size()==0) {System.out.println("Nothing was found !"); return ;}
        HashMap<String, Double> frequencymap = new HashMap<String, Double>();
        for (String w: words
             ) {
            if (frequencymap.containsKey(w)) {
                frequencymap.put(w, frequencymap.get(w) + 1);
            }
            else{
                frequencymap.put(w, (double)1);
            }
        }
        double weight = 0;
        for (HashMap.Entry<String, Double> entry: frequencymap.entrySet())
        {
            double tf = (double) (1+Math.log(entry.getValue()/frequencymap.size()));
            frequencymap.put(entry.getKey(), tf);
            weight += tf*tf;
        }
        weight = Math.sqrt(weight);

        Map<Integer, Pair<Double, Double>> score_for_doc = new HashMap<Integer, Pair<Double, Double>>();

        double[] Scores = new double[docs_list.size()];
        List<Double>[] new_scores = new List[docs_list.size()];
        for (int i = 0; i<new_scores.length; i++
             ) {
            new_scores[i] = new LinkedList<Double>();

        }
        double[] Lengths = new double[docs_list.size()];
        Arrays.fill(Scores, 0);
        for (HashMap.Entry<String, Double> entry:frequencymap.entrySet())
        {
            double word_score = entry.getValue();
            FreqPos postings_list = index.get(entry.getKey());
            for (int doc: postings_list.postings_list
                 ) {
                //add weighting
                int doc_index = postings_list.postings_list.indexOf(doc);
                double tf_doc = postings_list.tfs_list.get(doc_index);
                if(score_for_doc.containsKey(doc))
                {
                    score_for_doc.put(doc, new Pair<Double, Double>(score_for_doc.get(doc).getKey()+word_score*tf_doc, score_for_doc.get(doc).getValue()+tf_doc*tf_doc));
                }
                else score_for_doc.put(doc, new Pair<Double, Double>(word_score*tf_doc, tf_doc*tf_doc));
            }
        }
        Map<Integer, Double> new_map = new HashMap<Integer, Double>();
        for (Map.Entry<Integer, Pair<Double, Double>> entry: score_for_doc.entrySet()
             ) {
            double score = entry.getValue().getKey()/entry.getValue().getValue();

            new_map.put(entry.getKey(), score);
            //System.out.println("score "+score);
            if(score>-0.09)
            {
                predicted[entry.getKey()] = 1;
            }

        }


        Map<Integer, Double> result2 = new LinkedHashMap<>();
        new_map.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .forEachOrdered(x -> result2.put(x.getKey(), x.getValue()));


        long estimatedTime2 = System.currentTimeMillis() - startTime2 + estimatedTime;
        System.out.println("Time to compute scores, build vectors, compute cosine score "+estimatedTime2+" ms");
        System.out.println("K most relevant documents for the query '"+query+"'");
        for (Map.Entry<Integer, Double> entry : result2.entrySet()) {
            if (result2.size() < K) {
                K = Scores.length;
            }
            if (count < K) {
                int doc = entry.getKey();
                System.out.println(docs_paths.get(doc)+" "+entry.getValue());
                count++;
            } else break;

        }
        for (int i = 0; i<predicted.length; i++)
        {
            System.out.print(predicted[i]+" ");
        }
        metric(predicted);
    }

}
