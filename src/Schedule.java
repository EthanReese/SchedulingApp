import java.util.ArrayList;

public class Schedule {

    ArrayList<Courses> courses;
    ArrayList<Student> students;

    public ArrayList<Courses> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Courses> courses) {
        this.courses = courses;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public ArrayList<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(ArrayList<Teacher> teachers) {
        this.teachers = teachers;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public ArrayList<Sections> getSections() {
        return sections;
    }

    public void setSections(ArrayList<Sections> sections) {
        this.sections = sections;
    }

    ArrayList<Teacher> teachers;
    double score;
    ArrayList<Sections> sections;
    int newTeachers;

    public int getNewTeachers() {
        return newTeachers;
    }

    public void setNewTeachers(int newTeachers) {
        this.newTeachers = newTeachers;
    }

    public Schedule(ArrayList<Courses> courses, ArrayList<Student> students, ArrayList<Teacher> teachers, double score, ArrayList<Sections> section, int newTeachers){
        this.courses = new ArrayList<>(courses);
        this.students = new ArrayList<>(students);
        this.teachers = new ArrayList<>(teachers);
        this.score  = score;
        this.newTeachers = newTeachers;
        this.sections = new ArrayList<>(section);
    }




}
