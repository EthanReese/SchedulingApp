import java.lang.reflect.Array;
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
       for(int i = 0; i < studentsInCourse.size(); i++){
           this.studentsInCourse.set(i,studentsInCourse.get(i));

       }

    }

    public ArrayList<String> getTeachersTeachingCourse() {
        return teachersTeachingCourse;
    }

    public void setTeachersTeachingCourse(ArrayList<String> teachersTeachingCourse) {
        for(int i = 0; i < teachersTeachingCourse.size(); i++){
            this.teachersTeachingCourse.set(i,teachersTeachingCourse.get(i));

        }
    }

    //Getter and setter for the sections
    public ArrayList<Sections> getSectionsOccuring(){
        return sections;
    }
    public void setSectionsOccuring(ArrayList<Sections> sections){
        for (int i = 0; i < sections.size(); i++) {
            this.sections.add(sections.get(i));
        }
    }
    public void addSection(Sections section){
        this.sections.add(section);
    }

    public int getSections() {
        return numSections;
    }

    public void setSections(int sections) {
        this.numSections = sections;
    }

    String courseCode;
    Boolean requried;
    Double creditAmount;
    int numSections;
    ArrayList<String> studentsInCourse = new ArrayList<String>();
    ArrayList<String> teachersTeachingCourse = new ArrayList<String>();
    ArrayList<Sections> sections = new ArrayList<>();

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


    public void removeStudentFromCourse(String studentName){
        for (int i = 0; i < this.studentsInCourse.size(); i++) {
            if(this.studentsInCourse.get(i) == studentName){
                this.studentsInCourse.remove(studentName);
            }
        }
    }





}
