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
        for(int y = 0; y < requested.size(); y++){
            this.requested.set(y,requested.get(y));

        }
    }

    public ArrayList<Courses> getAssigned() {
        return assigned;
    }

    public void setAssigned(ArrayList<Courses> assigned) {
        for(int x = 0; x < assigned.size(); x++){
            this.assigned.set(x,assigned.get(x));

        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    ArrayList<Courses> requested = new ArrayList<Courses>();
    ArrayList<Courses> assigned = new ArrayList<Courses>();
    String identifier;
    public Student(ArrayList<Courses> requested, String identifier){

        for(int i = 0; i < requested.size(); i++){
            this.requested.add(requested.get(i));
        }
        this.identifier = identifier;

    }
}
