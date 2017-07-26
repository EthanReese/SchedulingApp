import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by Ethan Reese, Aletea VanVeldhuesen, and Josh Bromley on 7/24/17.
 */



public class SchedulingApp {

    //Create the swing interface elements
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();
    JButton generateButton = new JButton("Generate Schedules");
    JLabel forecastLabel = new JLabel("Forecasting Database Path");
    JLabel teacherLabel = new JLabel("Teacher Database Path");
    JLabel courseLabel = new JLabel("Course Database Path");
    JTextField forecastInput = new JTextField();
    JTextField teacherInput = new JTextField();
    JTextField courseInput = new JTextField();

    BufferedReader br = null;
    Scanner scanner = new Scanner(System.in);
    ArrayList<Courses> coursesList = new ArrayList<Courses>();
    ArrayList<Teacher> teacherList = new ArrayList<Teacher>();
    ArrayList<Sections> totalSections = new ArrayList<Sections>();
    ArrayList<Courses> courses = new ArrayList<Courses>();
    ArrayList<Teacher> teachers = new ArrayList<Teacher>();
    ArrayList<Student> students = new ArrayList<Student>();
    int MIN = 15;
    int MAX = 40;
    int totalPeriods = 8;
    public SchedulingApp(){
        //Potentially do this as some kind of GUI


        //Prompt the user for input of the files and assign the paths to strings
        System.out.println("Please input the path of the file with the forecasting options.");
        String forecastingFile = scanner.nextLine();
        System.out.println("Please input the path of the file with a teacher list and qualifications.");
        String teacherFile = scanner.nextLine();
        System.out.println("Please input the path of the file with the course list.");
        String courseFile = scanner.nextLine();
        System.out.println("What is the maximum number of students in each class");
        String maximum = scanner.nextLine();
        MIN = Integer.parseInt(maximum);
        System.out.println("What is the minimum number of students in each class");
        String minimum = scanner.nextLine();
        MAX = Integer.parseInt(minimum);
        System.out.println("how many periods does your school offer?");
        String periodNumber = scanner.nextLine();
        totalPeriods = Integer.parseInt(periodNumber);



        //Call the functions corresponding to each individual file
        ArrayList<ArrayList<String>> forecastingTable  = readCSV(forecastingFile);
        ArrayList<ArrayList<String>> teacherTable = readCSV(teacherFile);
        ArrayList<ArrayList<String>> courseTable = readCSV(courseFile);
        classes(courseTable);
        teacherCreation(teacherTable);
        requestedClasses(forecastingTable, courses);
        setClassList(forecastingTable);
        testStudents(students);




    }

    public static void main(String[] args){
        new SchedulingApp();
    }

    public ArrayList<ArrayList<String>> readCSV(String filePath){
        //Make a proper arraylist to return
        ArrayList<ArrayList<String>> returnList= new ArrayList<ArrayList<String>>();
        int counter = 0;
        //Attempt to read in the file
        try{
            String line;
            //Make a buffered reader that can read in the csv file
            br = new BufferedReader(new FileReader(filePath));
            while((line  = br.readLine()) != null){
                ArrayList<String> tempList = new ArrayList<>(Arrays.asList((line).split(",")));
                returnList.add(counter, tempList);
                counter += 1;
            }

        }catch(FileNotFoundException e){
            e.printStackTrace();
            //Todo: Tell the user to input a new forecasting file
        }catch(IOException e){
            e.printStackTrace();
            //idk how the user is supposed to fix that
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnList;
    }

    public void classes(ArrayList<ArrayList<String>> courseTable) {
        for (int i = 0; i < courseTable.size(); i++) {
            String name = courseTable.get(i).get(0);
            boolean isRequired = false;
            if (courseTable.get(i).get(1).equals("true")) {
                isRequired = true;
            }
            Double credit = Double.parseDouble(courseTable.get(i).get(2));
            Courses course = new Courses(name, isRequired, credit);
            courses.add(course);
        }
    }


    //Turns the teacher list into a list of teacher objects
    public void teacherCreation(ArrayList<ArrayList<String>> teacherTable) {
        ArrayList<Courses> qualified = new ArrayList<Courses>();
        for (int i = 0; i < teacherTable.size(); i++) {
            for(int j = 0; j < teacherTable.get(i).size(); j++) {

               qualified.add(search(courses,teacherTable.get(i).get(j)));
               qualified.remove(0);
            }
            teachers.add(new Teacher(qualified,teacherTable.get(i).get(0)));
            qualified.clear();
        }
    }
    //Create student objects
    public void requestedClasses(ArrayList<ArrayList<String>> forecastTable, ArrayList<Courses> courses) {
        //Temporary array list for storing the requested courses
        ArrayList<Courses> request = new ArrayList<Courses>();
        //Store the id
        String id = new String();
        //For each student, get their ID, the search for the classes they want and create the student object
        for (int i = 0; i < forecastTable.size(); i++) {
            id = forecastTable.get(i).get(0);
            for (int j = 0; j < forecastTable.get(i).size(); j++) {
                System.out.println(forecastTable.get(i).get(j));
                request.add(search(courses, forecastTable.get(i).get(j)));
                request.remove(0);
            }
            students.add(new Student(request, id));
            request.clear();
        }


    }

    //get the students in a course and set that
    public void setClassList(ArrayList<ArrayList<String>> forecastingTable) {
        ArrayList<ArrayList<String>> studentCourseList = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < forecastingTable.size(); i++) {
            for (int j = 1; j < forecastingTable.get(i).size(); j++) {
                for (int k = 0; k < courses.size(); k++) {
                    if (courses.get(k).courseCode == forecastingTable.get(i).get(j)) {
                        courses.get(k).addStudent(forecastingTable.get(i).get(0));
                    }
                }
            }
        }
        for (int i = 0; i < studentCourseList.size(); i++) {
            courses.get(i).setStudentsInCourse(studentCourseList.get(i));
        }
    }

    public void teachingClasses (ArrayList<Teacher> teachers, ArrayList<Courses> courses){
        for(int i = 0; i < teachers.size(); i++){
            for(int j = 0; j < teachers.get(i).qualified.size(); j++){
                search(courses,teachers.get(i).qualified.get(j).courseCode).addTeacher(teachers.get(i).identifier);
            }
        }
    }
    //Seach for certain courses
    public Courses search(ArrayList<Courses> courseList, String code ) {
        for (int i = 0; i < courseList.size(); i++) {
            //Go through the list of courses one by one until the inputted code matches a course code
            if (courseList.get(i).courseCode.equals(code)) {
                return courseList.get(i);
            }
        }
        //if there is no match, return nothing
        System.out.println("Null");
        return null;
    }


    public void reassign(ArrayList<Courses> courseList) {
        ArrayList<Courses> nonRequired = new ArrayList<Courses>();
        for (int k = 0; k < courses.size(); k++) {
            if (!(courses.get(k).getRequried())) {
                nonRequired.add(courses.get(k));
            }
        }
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getStudentsInCourse().size() < MIN) {
                ArrayList<String> studentReassigned = new ArrayList<String>(courseList.get(i).getStudentsInCourse());
                courses.remove(courseList.get(i));
                for (int j = 0; j < studentReassigned.size(); j++) {
                    int randCourse = (int) (Math.random() * (nonRequired.size()-1));
                    nonRequired.get(randCourse).addStudent(studentReassigned.get(j));
                }
            }
        }
    }

    //Quicksort method: sort out the courses array
    public void quickSort(ArrayList<Courses> array, int low, int high) {
        //If the array has only one element, then it is already sorted
        if (array == null || array.size() <= 1)
            return;
        //If low is higher than high, then the algorithm cannot work
        if (low >= high)
            return;

        // pick the pivot
        int middle = low + (high - low) / 2;
        int pivot = array.get(middle).getStudentsInCourse().size();

        // make left < pivot and right > pivot
        int i = low, j = high;
        //Sort through the array and swap numbers to the other side of the pivot if necessary.
        while (i <= j) {
            while (array.get(i).getStudentsInCourse().size() < pivot) {
                i++;
            }

            while (array.get(j).getStudentsInCourse().size() > pivot) {
                j--;
            }

            if (i <= j) {
                Courses temp = array.get(i);
                array.set(j,array.get(i));
                array.set(i, temp);
                i++;
                j--;
            }
        }

        // recursively sort two sub parts
        if (low < j){
            quickSort(array, low, j);
        }

        if (high > i){
            quickSort(array, i, high);
        }
    }

    //Sort through the sections and pick periods where they can be
    public ArrayList<Courses> antiMode(){
        //Set an integer to the max value of an integer
        int returnInt = Integer.MAX_VALUE;
        ArrayList<Courses> returnList = new ArrayList<Courses>();
        int[] numOfEach = new int[courses.get(courses.size()).getSections()];
        //Loop through the list of courses and make an additional array that has an element for each number of sections.
        for (int i = 0; i < courses.size(); i++){
            numOfEach[courses.get(i).getSections()+1]++;
        }
        //Loop through the resultant array and find the number that is the lowest and keep track of its index
        for (int i = 0; i < numOfEach.length; i++) {
            if(numOfEach[i] < returnInt){
                returnInt = i;
            }
        }
        //Loop through the courses list and take all of the antimode classes into a new return list.
        for (int i = 0; i < courses.size(); i++) {
            if(courses.get(i).getSections() == returnInt){
                returnList.add(courses.get(i));
            }
        }
        return returnList;
    }

    public void addSections() {
        //for each course, for each section, create a new section for that course
        for (int i = 0; i < coursesList.size(); i++) {
            for (int j = 0; j < coursesList.get(i).getSections(); j++) {
                Sections section = new Sections(coursesList.get(i), 0, null, null);
                totalSections.add(section);
            }
        }
    }

    //for each course
    public void addPeriod() {
        //keep track of max number of courses in a period
        int[] periodTracker = new int[totalPeriods];
        int maxPeriods = (int)((totalSections.size()/totalPeriods)+.5);
        //get antiMode courses
        ArrayList<Courses> List = antiMode();
        //Loop through the list of classes at the antimode that need to be assigned.
        for (int i = 0; i < List.size(); i++) {
            //determine how many sections of this class can be assigned to one period
            int overlap = (int)((List.get(i).getSections()/totalPeriods)+.5);
            int[] assigned = new int[totalPeriods];
            for (int j = 0; j < List.get(i).getSections(); j++) {
                int k = 0;
                //find the sections for this course
                while(totalSections.get(k).course != List.get(i)) {
                    k++;
                }
                //assigne a random period, and add it to the array keeping track of total classes in a period
                int periodAssigned = (int)(Math.random()*(totalPeriods-1)+1);
                periodTracker[periodAssigned]++;
                //make sure there aren't too many of this class in this period, and that is doesn't go over max periods
                while (periodTracker[periodAssigned] == maxPeriods+1 || assigned[periodAssigned] == overlap) {
                    periodAssigned = (int)(Math.random()*(totalPeriods-1)+1);
                }
                assigned[periodAssigned]++;
                //Change the period in the array?
                totalSections.get(k).setThePeriod(periodAssigned);
                //Also link it to the course object
                List.get(i).addSection(totalSections.get(k));

            }
        }
    }


    //assigns teachers to separate sections for a specific course
    public void teacherSections(Courses course) {
        int sections = course.getSections();
        ArrayList<Sections> courseSections = new ArrayList<Sections>();
        //find the course's sections
        for (int i = 0; i < totalSections.size(); i++) {
            if (totalSections.get(i).course == course) {
                courseSections.add(totalSections.get(i));
            }
        }
        //keep track of teachers that can teach this course and their quialifications
        ArrayList<String> teachers = course.getTeachersTeachingCourse();
        ArrayList<Teacher> qualifyList = new ArrayList<Teacher>();
        for (int i = 0; i < teachers.size(); i++) {
            for (int j = 0; j < teacherList.size(); j++) {
                if (teacherList.get(j).identifier == teachers.get(i)) {
                    qualifyList.add(teacherList.get(j));
                }
            }
        }
        //for each section, assign a teacher that is qualified
        //AND FREE (coding this now)
        for (int i = 0; i < sections; i++) {
            /*ArrayList<Teacher> freeList = qualifyList;
            for (int j = 0; j < freeList.size(); j++) {
                for (int k = 0; k < freeList.get(j).getTeaching().size(); k++) {
                    if (freeList.get(j).getTeaching().get(k).period == )
                }
            }*/
            Teacher first = qualifyList.get(i);
            int smallestIndex = i;
            for (int j = i; j < qualifyList.size(); j++) {
                if (qualifyList.get(j).qualified.size() < first.qualified.size()) {
                    first = qualifyList.get(j);
                    smallestIndex = j;
                }
            }
            courseSections.get(i).setTheTeacher(first);
            first.addTeaching(courseSections.get(i));
            qualifyList.remove(smallestIndex);
        }
    }

    public void testStudents (ArrayList<Student> testStudents){
        for(int i = 0; i < 10; i++){
            System.out.println(testStudents.get(i).identifier + ",");
            for(int j = 0; j < testStudents.get(i).requested.size(); j++){
                System.out.println(testStudents.get(i).requested.get(j).courseCode + ",");
            }
        }
    }

    //Assign students to a section semi randomly
    public void assignStudentsToSection(Courses course){


    }
}
