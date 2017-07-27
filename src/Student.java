import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Ethan on 7/24/17.
 */
public class Student {
    public ArrayList<Courses> getRequested() {
        return requested;
    }

    public void setRequested(ArrayList<Courses> requested) {
        this.requested = requested;
    }

    public ArrayList<Courses> getAssigned() {
        return assigned;
    }

    public void setAssigned(ArrayList<Courses> assigned) {
        this.assigned = assigned;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void changePeriod(int index, Courses course){
        assigned.set(index, course);
    }

    ArrayList<Courses> requested = new ArrayList<Courses>();
    ArrayList<Courses> assigned = new ArrayList<Courses>();
    String identifier;
    public Student(ArrayList<Courses> requested, String identifier){
        this.requested = requested;
        this.identifier = identifier;
    }
}
