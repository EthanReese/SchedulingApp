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

    public Sections[] getAssigned() {
        return assigned;
    }

    public void setAssigned(Sections[] assigned) {
        for(int x = 0; x < assigned.length; x++){
            this.assigned[x] = assigned[x];
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void changePeriod(int index, Sections course){
        for (int i = 0; i < this.assigned.length; i++) {
            if(this.assigned[i] == course){
                this.assigned[i] = null;
            }
        }
        this.assigned[index] = course;
    }

    public void addAssigned(int index, Sections newSections) {
        this.assigned[index] = newSections;
    }

    public void setTotalPeriods(int period) {
        periods = period;
    }

    int periods = SchedulingApp.setFinalPeriods;
    ArrayList<Courses> requested = new ArrayList<Courses>();
    Sections[] assigned = new Sections[periods];
    String identifier;
    public Student(ArrayList<Courses> requested, String identifier){

        for(int i = 0; i < requested.size(); i++){
            this.requested.add(requested.get(i));
        }
        this.identifier = identifier;


    }
    public void setClass(int period, Sections course){
        this.assigned[period] = course;
    }


    public void removePeriod(int period){
        this.assigned[period] =  null;
    }
}