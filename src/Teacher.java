import java.util.ArrayList;

/**
 * Created by Ethan on 7/24/17.
 */
public class Teacher {
    public ArrayList<Courses> getQualified() {
        return qualified;
    }

    public void setQualified(ArrayList<Courses> qualified) {
        this.qualified = qualified;
    }

    public ArrayList<Courses> getTeaching() {
        return teaching;
    }

    public void setTeaching(ArrayList<Courses> teaching) {
        this.teaching = teaching;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    ArrayList<Courses> qualified = new ArrayList<Courses>();
    ArrayList<Courses> teaching = new ArrayList<Courses>();
    String identifier;
    public Teacher(ArrayList<Courses> qualified, String identifier){
        this.qualified = qualified;
        this.identifier = identifier;
    }


}
