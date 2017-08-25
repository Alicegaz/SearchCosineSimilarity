package sample;

import java.util.LinkedList;
import java.util.List;

/**Data structure that uses the list of document numbers(postings list)
and for each document there is a corresponding list of positions in
 which the word is seen
 *
 */
public class FreqPos {
    double idf;
    LinkedList<Integer> postings_list = new LinkedList<>();
    LinkedList<Double> tfs_list = new LinkedList<>();
    LinkedList<LinkedList<Integer>> positions_lists = new LinkedList<LinkedList<Integer>>();
    int count = 0;

    /**creation of the structure without the initial parameters is not allowed**/
    public FreqPos(int num, int pos)
    {
        idf = 0;
        LinkedList<Integer> positions = new LinkedList<>();
        positions.add(pos);
        positions_lists.add(count, positions);
        postings_list.add(num);
    }

    /** here we assume that we are not scipping the documents order, indexing documents one by one***/
    public void add(int num, int pos)
    {

        if (postings_list.getLast() != num)
        {
                postings_list.add(num);
                positions_lists.add(new LinkedList<Integer>());
                positions_lists.getLast().add(pos);
        }
        else {
            if (positions_lists.size() == 0)
            {
                positions_lists.add(new LinkedList<Integer>());
            }
            positions_lists.getLast().add(pos);
        }

        /** Debug **/
        /*System.out.println("Check the modified list");
        for (int i = 0; i<positions_lists.getLast().size(); i++)
        {
            System.out.print(positions_lists.getLast().get(i)+" ");
        }*/

    }

    public void set_idf()
    {
        if (postings_list.size()!=0)
        {
            idf = Math.log(IndexingFiles.N/postings_list.size());
            //System.out.println(idf);
        }
    }

    public void set_tfs()
    {
        if (postings_list.size()!=0 && positions_lists.size()!=0)
        {
            for (int i = 0; i<postings_list.size(); i++)
            {
                double tf = 1 + Math.log(positions_lists.get(i).size());
                tfs_list.add(tf);
            }
        }
    }

    public void reset_postingsList(LinkedList<Integer> list)
    {
        postings_list = list;
    }
}
