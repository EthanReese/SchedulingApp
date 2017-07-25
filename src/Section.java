import java.util.ArrayList;

/**
 * Acts as a data type to store a single section of a given course
 * Created by Ethan on 7/25/17.
 */
public class Section {
    ArrayList<String> students = new ArrayList<String>();
    String course;
    int period;
    String teacher;
    //Constructor
    public Section(String course, int period, String teacher){
        this.course = course;
        this.period = period;
    }


}
