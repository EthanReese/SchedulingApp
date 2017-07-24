import java.util.ArrayList;

/**
 * Created by Ethan on 7/24/17.
 */
public class Teacher {
    ArrayList<Courses> qualified = new ArrayList<Courses>();
    ArrayList<Courses> teaching = new ArrayList<Courses>();
    String identifier;
    public Teacher(ArrayList<Courses> qualified, String identifier){
        this.qualified = qualified;
        this.identifier = identifier;
    }
}
