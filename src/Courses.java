import java.util.ArrayList;

public class Courses {
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Boolean getRequried() {
        return requried;
    }

    public void setRequried(Boolean requried) {
        this.requried = requried;
    }

    public Double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public ArrayList<String> getStudentsInCourse() {
        return studentsInCourse;
    }

    public void setStudentsInCourse(ArrayList<String> studentsInCourse) {
        this.studentsInCourse = studentsInCourse;
    }

    public ArrayList<String> getTeachersTeachingCourse() {
        return teachersTeachingCourse;
    }

    public void setTeachersTeachingCourse(ArrayList<String> teachersTeachingCourse) {
        this.teachersTeachingCourse = teachersTeachingCourse;
    }

    String courseCode;
    Boolean requried;
    Double creditAmount;
    ArrayList<String> studentsInCourse = new ArrayList<String>();
    ArrayList<String> teachersTeachingCourse = new ArrayList<String>();

    public Courses(String codeInput, Boolean requiredInput, Double creditInput){
        courseCode = codeInput;
        requried = requiredInput;
        creditAmount = creditInput;
    }

    public void addStudent(String studentName){
        studentsInCourse.add(studentName);
    }

    public void addTeacher(String teacherName){
        teachersTeachingCourse.add(teacherName);
    }




}
