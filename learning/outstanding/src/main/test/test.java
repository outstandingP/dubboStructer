import java.util.HashSet;
import java.util.Set;

/**
 * Created by songll on 2017/4/14.
 */
public class test {
    private static test ourInstance = new test();

    public static test getInstance() {
        return ourInstance;
    }

    private test() {
        Set set = new HashSet();
        for (int i = 0; i < 3; i++) {
            System.out.println(set.add(1));
        }
    }


    public static void main(String[] args) {

    }
}
