import java.util.ArrayList;

public class Sections {
    Courses course;
    int period;

    public Courses getCourse() {
        return course;
    }

    public void setCourse(Courses course) {
        this.course = course;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

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
