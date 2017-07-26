import java.util.ArrayList;

public class Sections {
    Courses course;
    int period;
    Teacher teacher;
    ArrayList<Student> students;


    public void setThePeriod(int number) {
        this.period = number;
    }

    public void setTheTeacher(Teacher assigned) {
        this.teacher = assigned;
    }

    
    public Sections(Courses course, int period, Teacher teacher, ArrayList<Student> students) {
        this.course = course;
        this.period = period;
        this.teacher = teacher;
        this.students = students;
    }

}
