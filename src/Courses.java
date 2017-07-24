import java.util.ArrayList;

public class Courses {

    String courseCode;
    Boolean requried;
    Float creditAmount;
    ArrayList<String> studentsInCourse;
    ArrayList<String> teachersTeachingCourse;

    public Courses(String codeInput, Boolean requiredInput, Float creditInput){
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
