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
        if(assigned.size()>=this.assigned.size()) {
            for (int i = this.assigned.size(); i < assigned.size() + 1; i++) {
                this.assigned.add(null);

            }
        }
        for(int x = 0; x < assigned.size(); x++){
            this.assigned.set(x, assigned.get(x));
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void changePeriod(int index, Courses course){
        for (int i = 0; i < this.assigned.size(); i++) {
            if(this.assigned.get(i) == course){
                this.assigned.remove(i);
            }
        }
        if(this.assigned.size() >= index) {
        }
        else{
            for (int i = this.assigned.size(); i < index; i++) {
                this.assigned.add(null);
            }
        }
        this.assigned.set(index, course);
    }

    ArrayList<Courses> requested = new ArrayList<Courses>();
    ArrayList<Courses> assigned = new ArrayList<>();
    String identifier;
    public Student(ArrayList<Courses> requested, String identifier){

        for(int i = 0; i < requested.size(); i++){
            this.requested.add(requested.get(i));
        }
        this.identifier = identifier;


    }
    public void setClass(int period, Courses course){
        this.assigned.set(period, course);
    }
}