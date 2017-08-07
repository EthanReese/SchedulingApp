import java.util.ArrayList;

/**
 * Created by Ethan on 7/24/17.
 */
public class Teacher {
    public ArrayList<Courses> getQualified() {
        return qualified;
    }

    public void setQualified(ArrayList<Courses> qualified) {
        for(int i = 0; i < qualified.size(); i++){
            this.qualified.set(i,qualified.get(i));
        }
    }

    public boolean isQualified(Courses course) {
        for (int i = 0; i < this.qualified.size(); i++) {
            if (this.qualified.get(i) == course) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Sections> getTeaching() {
        return teaching;
    }

    public void setTeaching(ArrayList<Sections> teaching) {
        for(int i = 0; i < teaching.size(); i++){
            this.teaching.set(i,teaching.get(i));
        }
    }

    public void addTeaching(Sections section) {
        this.teaching.add(section);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setFreePeriods(int Free) {
        this.freePeriods = Free;
    }

    public int getFreePeriods() {
        return freePeriods;
    }

    int freePeriods = 1;
    ArrayList<Courses> qualified = new ArrayList<Courses>();
    ArrayList<Sections> teaching = new ArrayList<Sections>();
    String identifier;
    public Teacher(ArrayList<Courses> qualified, String identifier){
        for(int i = 0; i < qualified.size(); i++){
            this.qualified.add(qualified.get(i));
        }
        this.identifier = identifier;
    }


}