import java.util.ArrayList;

public class Schedule {

    ArrayList<Courses> courses;
    ArrayList<Student> students;

    public ArrayList<Courses> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Courses> courses) {
        this.courses = (ArrayList<Courses>)courses.clone();
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = (ArrayList<Student>)students.clone();
    }

    public ArrayList<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(ArrayList<Teacher> teachers) {
        this.teachers = (ArrayList<Teacher>)teachers.clone();
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
        this.sections = (ArrayList<Sections>)sections.clone();
    }

    ArrayList<Teacher> teachers;
    double score;
    ArrayList<Sections> sections;
    int newTeachers;

    public ArrayList<Teacher> getAddedTeachers() {
        return addedTeachers;
    }

    public void setAddedTeachers(ArrayList<Teacher> addedTeachers) {
        this.addedTeachers = (ArrayList<Teacher>)addedTeachers.clone();
    }

    ArrayList<Teacher> addedTeachers;

    public int getNewTeachers() {
        return newTeachers;
    }

    public void setNewTeachers(int newTeachers) {
        this.newTeachers = newTeachers;
    }

    public Schedule(){

    }




}
