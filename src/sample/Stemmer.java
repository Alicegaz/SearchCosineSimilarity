package sample;

public class Stemmer {
    private char[] token;
    private int i,
            end,
            j, last;

    public Stemmer(String w) {
        i = 0;
        //last = 0;
        end = 0;
        token = w.toCharArray();
    }

    public void stem() {
        last = token.length - 1; //the index of the last element of the token
        if (last > 1) {
            step_1();
            step_2();
            step_3();
            step_4();
            step_5();
            step_6();
        }
        end = last + 1;
        i = 0;
    }

    public String toString()
    {
        return new String(token, 0, last+1);
    }

    private final boolean cons(int i) {
        switch (token[i]) {
            case 'a':
            case 'e':
            case 'o':
            case 'i':
            case 'u':
                return false;
            case 'y':
                return (i == 0) ? true : cons(i - 1);
            default:
                return true;

        }
    }

    private final boolean double_cons(int j) {
        if (j < 1) return false;
        if (token[j] != token[j - 1]) return false;
        return cons(j);
    }

    //vcvcvc

    private final int vc() {
        int n = 0;
        int i = 0;
        while (true) {
            if (i > j) return n;
            if (!cons(i)) break;
            i++;
        }
        i++;
        while (true) {
            while (true) {
                if (i > j) return n;
                if (cons(i)) break;
                i++;
            }
            i++;
            n++;
            while (true) {
                if (i > j) return n;
                if (!cons(i)) break;
                i++;
            }
            i++;
        }
    }

    //if token contains a vowel true
    private final boolean vowelinstem() {
        int i;
        for (i = 0; i <= j; i++)
            if (!cons(i)) return true;
        return false;
    }

    //restore 'e' at the end of the short word
    //cav(e), hop(e). If the form is consonant-vowel-consonant, second consonant not w, x, y
    private final boolean cvc(int i) {
        if ((i < 2 || !cons(i) || cons(i - 1) || !cons(i - 2)) || token[i] == 'w' || token[i] == 'x' || token[i] == 'y')
            return false;

        return true;
    }

    private final boolean rule(String s) {
        //int l = last - s.length();
        int l = s.length();
        int o = last - l + 1;
        if (o < 0) return false;
        for (int i = 0; i < l; i++) {
            //System.out.println("token "+token+" last "+last+ " o " + o);
            if (token[o + i] != s.charAt(i)) return false;
        }
        j = last - l; // the index
        return true;
    }

    private final void setto(String s) {
        int l = s.length();
        int o = j+1;
        if (token.length < o+l)
        {
            char[] copyTo = new char[o+l];
            System.arraycopy(token, 0, copyTo, 0, token.length);
            token = copyTo;
        }
        for (int i = 0; i < l; i++)
            token[o + i] = s.charAt(i);
        last = j + l; // the index
    }

    //make plurals singular, reduce -ed, -ing
    private final void step_1() {
        if (token[last] == 's') {
            if (rule("sses"))
                last -= 2;
            else if (rule("ies"))
                //setto("i"); else
                last -= 2;
            else if (token[last - 1] != 's')
                last--;
        }
        if (rule("eed") && vc() > 0) {
            last--;
        } else {
            if ((rule("ed") || rule("ing")) && vowelinstem()) {
                last = j;
                if (rule("at")) setto("ate");
                else if (rule("bl")) setto("ble");
                else if (rule("iz")) setto("ize");
                else if (double_cons(last) && !(token[last] == 'l' || token[last] == 's' || token[last] == 'z')) {
                    last--;
                } else if (vc() == 1 && cvc(last)) setto("e");
            }
        }
    }

    //'y' => 'i' in the end if there is a vowel in the tocken
    private final void step_2() {
        if (rule("y") && vowelinstem())
            token[last] = 'i';
    }

    //rules for suffices
    private final void step_3() {
        if (last == 0)
            return;
        if (vc() > 0) {
            switch (token[last - 1]) {
                case 'a':
                    if (rule("ational")) {
                        setto("ate");
                        break;
                    }
                    if (rule("tional")) {
                        setto("tion");
                        break;
                    }
                    break;
                case 'c':
                    if (rule("enci")) {
                        setto("ence");
                        break;
                    }
                    if (rule("anci")) {
                        setto("ance");
                        break;
                    }
                    break;
                case 'e':
                    if (rule("izer")) {
                        setto("ize");
                        break;
                    }
                    break;
                case 'l':
                    if (rule("bli")) {
                        setto("ble");
                        break;
                    }
                    if (rule("alli")) {
                        setto("al");
                        break;
                    }
                    if (rule("entli")) {
                        setto("ent");
                        break;
                    }
                    if (rule("eli")) {
                        setto("e");
                        break;
                    }
                    if (rule("ousli")) {
                        setto("ous");
                        break;
                    }
                    break;
                case 'o':
                    if (rule("ization")) {
                        setto("ize");
                        break;
                    }
                    if (rule("ation")) {
                        setto("ate");
                        break;
                    }
                    if (rule("ator")) {
                        setto("ate");
                        break;
                    }
                    break;
                case 's':
                    if (rule("alism")) {
                        setto("al");
                        break;
                    }
                    if (rule("iveness")) {
                        setto("ive");
                        break;
                    }
                    if (rule("fulness")) {
                        setto("ful");
                        break;
                    }
                    if (rule("ousness")) {
                        setto("ous");
                        break;
                    }
                    break;
                case 't':
                    if (rule("aliti")) {
                        setto("al");
                        break;
                    }
                    if (rule("iviti")) {
                        setto("ive");
                        break;
                    }
                    if (rule("biliti")) {
                        setto("ble");
                        break;
                    }
                    break;
                case 'g':
                    if (rule("logi")) {
                        setto("log");
                        break;
                    }
            }
        }


    }

    //remove -ful, -ness, -active, change -icate => "ic", "alize" => "al", "iciti" => "ic", "ical" => "ic"
    private final void step_4()
    {
        if (vc()>0) {
            switch (token[last]) {
                case 'e':
                    if (rule("icate")) {
                        setto("ic");
                        break;
                    }
                    if (rule("ative")) {
                        setto("");
                        break;
                    }
                    if (rule("alize")) {
                        setto("al");
                        break;
                    }
                    break;
                case 'i':
                    if (rule("iciti")) {
                        setto("ic");
                        break;
                    }
                    break;
                case 'l':
                    if (rule("ical")) {
                        setto("ic");
                        break;
                    }
                    if (rule("ful")) {
                        setto("");
                        break;
                    }
                    break;
                case 's':
                    if (rule("ness")) {
                        setto("");
                        break;
                    }
                    break;
            }
        }
    }

    private final void step_5()
    {
        if (last == 0)
            return;
        switch(token[last-1])
        {
            case 'a': if (rule("al")) break; return;
            case 'c': if (rule("ance")) break;
                if (rule("ence")) break; return;
            case 'e': if (rule("er")) break; return;
            case 'i': if (rule("ic")) break; return;
            case 'l': if (rule("able")) break;
                if (rule("ible")) break; return;
            case 'n': if (rule("ant")) break;
                if (rule("ement")) break;
                if (rule("ment")) break;
                    /* element etc. not stripped before the m */
                if (rule("ent")) break; return;
            case 'o': if (rule("ion") && j >= 0 && (token[j] == 's' || token[j] == 't')) break;
                                    /* j >= 0 fixes Bug 2 */
                if (rule("ou")) break; return;
                    /* takes care of -ous */
            case 's': if (rule("ism")) break; return;
            case 't': if (rule("ate")) break;
                if (rule("iti")) break; return;
            case 'u': if (rule("ous")) break; return;
            case 'v': if (rule("ive")) break; return;
            case 'z': if (rule("ize")) break; return;
            default: return;
        }
        if (vc()>=2)
            last = j;
    }

    //remove last -e if there is a pattern like <vcvc>
    private final void step_6()
    {
        j = last;
        if (token[last] == 'e')
        {
            if (vc() > 1 || vc() == 1 && !cvc(last-1)) last--;
        }
        if (token[last] == 'l' && double_cons(last) && vc() > 1) last--;

    }
}
