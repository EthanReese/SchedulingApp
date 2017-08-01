
import javax.swing.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
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
    ArrayList<Sections> totalSections = new ArrayList<Sections>();
    ArrayList<Courses> courses = new ArrayList<Courses>();
    ArrayList<Teacher> teachers = new ArrayList<Teacher>();
    ArrayList<Teacher> addedTeachers = new ArrayList<Teacher>();
    ArrayList<Student> students = new ArrayList<Student>();
    int MIN = 15;
    int MAX = 40;
    int totalPeriods = 8;
    Random random = new Random();
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
        MAX = Integer.parseInt(maximum);
        System.out.println("What is the minimum number of students in each class");
        String minimum = scanner.nextLine();
        MIN = Integer.parseInt(minimum);
        System.out.println("How many periods does your school offer?");
        String periodNumber = scanner.nextLine();
        totalPeriods = Integer.parseInt(periodNumber);



        //Call the functions corresponding to each individual file
        ArrayList<ArrayList<String>> forecastingTable  = readCSV(forecastingFile);
        ArrayList<ArrayList<String>> teacherTable = readCSV(teacherFile);
        ArrayList<ArrayList<String>> courseTable = readCSV(courseFile);
        //Convert the files into the proper types of objects
        classes(courseTable);
        teacherCreation(teacherTable);
        requestedClasses(forecastingTable, courses);
        //Run all of our actual functions that do stuff
        setClassList();
        reassign(courses);
        teachingClasses(teachers, courses);
        addSections();
        courses  = BubbleSort(courses);


        addPeriod(courses);
        for (int i = 0; i < courses.size(); i++) {
            teacherSections(courses.get(i));
        }
        for (int i = 0; i < courses.size(); i++) {
            assignStudentsToSection(courses.get(i));
        }

        PrintWriter pw;
        try {
            pw = new PrintWriter(new FileWriter(new File("sectionsOutput.txt")));
            //create the output string
            String sectionsOutput = "";
            for (int i = 0; i < totalPeriods; i++) {
                ArrayList<Sections> sectionSchedule = new ArrayList<Sections>();
                for (int j = 0; j < totalSections.size(); j++) {
                    if (totalSections.get(j).period == i) {
                        sectionSchedule.add(totalSections.get(j));
                    }
                }
                sectionsOutput+= "Period: " + (i+1) + "\n";
                //Loop through all of the sections that have been scheduled and find what period they are occurring.
                for (int j = 0; j < sectionSchedule.size(); j++) {
                    try {
                        sectionsOutput += sectionSchedule.get(j).getCourse().getCourseCode() + ", " + sectionSchedule.get(j).getTeacher().getIdentifier() + ", " + sectionSchedule.get(j).getStudents().size() + " students" + "\n";
                    } catch (NullPointerException n) {

                    }
                }
            }
            pw.write(sectionsOutput);
            pw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
        PrintWriter ow;
        try {
            ow = new PrintWriter(new FileWriter(new File("studentOutput.txt")));
            //create the output string
            //IS STUDENT ASSIGNMENT IN ORDER???? That's what this assumes.
            String studentOutput = "";
            for (int i = 0; i < students.size(); i++) {
                studentOutput += students.get(i).getIdentifier() + ":\n";
                for (int j = 1; j < totalPeriods+1; j++) {
                    try {
                        studentOutput += students.get(i).getAssigned().get(j).getCourseCode() + ", \n";
                    }catch(NullPointerException e){
                        studentOutput += "Student was unable to be assigned to a course, \n";
                    }
                }
            }
            ow.write(studentOutput);
            ow.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        PrintWriter ww;
        try {
            ww = new PrintWriter(new FileWriter(new File("teacherOutput.txt")));
            //create the output string
            String teacherOutput = "";
            for (int i = 0; i < teachers.size(); i++) {
                teacherOutput += teachers.get(i).identifier + ": ";
                for (int j = 0; j < totalPeriods; j++) {
                    int period = j;
                    for (int k = 0; k < teachers.get(i).getTeaching().size(); k++) {
                        if (teachers.get(i).getTeaching().get(k).getPeriod() == (j)) {
                            teacherOutput += teachers.get(i).getTeaching().get(k).getCourse().getCourseCode() + ", ";
                            period++;
                        }
                    }
                    if (period == j) {
                        teacherOutput += "Free, ";
                    }
                }
                teacherOutput += "\n";
            }
            //track how many New Teachers are added
            int totalNewTeachers = 0;
            for (int i = 0; i < teachers.size(); i++) {
                if (teachers.get(i).identifier.equals("New Teacher")) {
                    totalNewTeachers++;
                }
            }
            teacherOutput += "\nTotal New Teachers: " + totalNewTeachers;
            ww.write(teacherOutput);
            ww.close();
            System.out.println(score(students, totalNewTeachers));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
        PrintWriter xw;
        try{
            xw = new PrintWriter(new FileWriter(new File("superSectionOutput.txt")));
            String superSectionOutput = "";
            for(int i = 0; i < teachers.size(); i++){
                for(int j = 0; j < teachers.get(i).getTeaching().size(); j++){
                    superSectionOutput += teachers.get(i).getTeaching().get(j).getCourse().getCourseCode() + "," + (teachers.get(i).getTeaching().get(j).period+1) + "," + teachers.get(i).getIdentifier() + ",";
                    for(int k = 0; k < teachers.get(i).getTeaching().get(j).getStudents().size(); k++){
                        superSectionOutput += teachers.get(i).getTeaching().get(j).getStudents().get(k).getIdentifier() +",";
                    }
                    superSectionOutput += "\n";
                }
            }
            xw.write(superSectionOutput);
            xw.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(0);
        }
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
            for(int j = 1; j < teacherTable.get(i).size(); j++) {
                qualified.add(search(courses,teacherTable.get(i).get(j)));

            }
            teachers.add(new Teacher(qualified,teacherTable.get(i).get(0)));
            ArrayList<Sections> teaching = new ArrayList<Sections>();
            teachers.get(i).setTeaching(teaching);
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
                request.add(search(courses, forecastTable.get(i).get(j)));
            }
            request.remove(0);
            students.add(new Student(request, id));
            request.clear();
        }


    }

    //get the students in a course and set that
    public void setClassList() {
        for (int i = 0; i < students.size(); i++) {
            for (int j = 0; j < students.get(i).getRequested().size(); j++){
                for (int k = 0; k < courses.size(); k++) {
                    if (courses.get(k).getCourseCode() == students.get(i).getRequested().get(j).getCourseCode()) {
                        courses.get(k).addStudent(students.get(i).getIdentifier());
                    }
                }
            }
        }

    }

    public void teachingClasses (ArrayList<Teacher> teachers, ArrayList<Courses> courses){
        for(int i = 0; i < teachers.size(); i++){
            for(int j = 1; j < teachers.get(i).getQualified().size(); j++) {
                search(courses, teachers.get(i).getQualified().get(j).getCourseCode()).addTeacher(teachers.get(i).getIdentifier());
            }
        }
    }
    //Seach for certain courses
    public Courses search(ArrayList<Courses> courseList, String code ) {
        for (int i = 0; i < courseList.size(); i++) {
            //Go through the list of courses one by one until the inputted code matches a course code
            if (courseList.get(i).getCourseCode().equals(code)) {
                return courseList.get(i);
            }
        }
        //if there is no match, return nothing

        return null;
    }

    //take any non-required courses that don't meet the minimum
    //delete them
    //reassign the students to a random non-required course
    public void reassign(ArrayList<Courses> courseList) {
        ArrayList<Courses> nonRequired = new ArrayList<Courses>();
        for (int k = 0; k < courses.size(); k++) {
            if (!(courses.get(k).getRequried())) {
                nonRequired.add(courses.get(k));
            }
        }
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getStudentsInCourse().size() < MIN && !courses.get(i).getRequried()) {
                nonRequired.remove(courses.get(i));
            }
        }
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getStudentsInCourse().size() < MIN && !courses.get(i).getRequried()) {
                ArrayList<String> studentReassigned = new ArrayList<String>(courseList.get(i).getStudentsInCourse());
                for (int j = 0; j < studentReassigned.size(); j++) {
                    int randCourse = random.nextInt(nonRequired.size());
                    nonRequired.get(randCourse).addStudent(studentReassigned.get(j));
                }
            }
        }
    }

    //Maybe try out bubblesort
    //Create a method to run the bubble sort method against the data set
    public ArrayList<Courses> BubbleSort(ArrayList<Courses> dataSet) {

        Courses temp;
        //Compare each pair of numbers and swap if they are out of order
        for (int j = 0; j < dataSet.size(); j++) {
            for (int i = 0; i < dataSet.size() - 1; i++) {
                if (dataSet.get(i).getSections() > dataSet.get(i+1).getSections()) {
                    //Swap the two numbers
                    temp = dataSet.get(i);
                    dataSet.set(i, dataSet.get(i+1));
                    dataSet.set(i+1, temp);
                }
            }
        }
        return dataSet;
    }

    //Create section objects for each section of the course
    public void addSections() {
        //for each course, for each section, create a new section for that course
        for (int i = 0; i < courses.size(); i++) {
            int total = (int)(Math.ceil((double)courses.get(i).getStudentsInCourse().size()/(double)MAX));
            courses.get(i).setSections(total);
            for (int j = 0; j < total; j++) {
                ArrayList<Student> fakeStudents = new ArrayList<Student>();
                Sections section = new Sections(courses.get(i), 0, null, fakeStudents);
                totalSections.add(section);
                courses.get(i).addSection(section);
            }
        }
    }

    //for each course
    public void addPeriod(ArrayList<Courses> List) {
        //keep track of max number of courses in a period
        int[] periodTracker = new int[totalPeriods + 1];
        int maxPeriods = (int)(Math.ceil((totalSections.size()/totalPeriods)+.5));
        //get antiMode courses
        //Loop through the list of classes at the antimode that need to be assigned.
        for (int i = 0; i < List.size(); i++) {
            //determine how many sections of this class can be assigned to one period
            int overlap = (int)(Math.ceil((List.get(i).getSections()/totalPeriods)+.5));
            int[] assigned = new int[totalPeriods + 1];
            for (int j = 0; j < List.get(i).getSections(); j++) {
                //assign a random period, and add it to the array keeping track of total classes in a period
                int periodAssigned = (int)(Math.random()*(totalPeriods));
                periodTracker[periodAssigned]++;
                //make sure there aren't too many of this class in this period, and that is doesn't go over max periods
                while (periodTracker[periodAssigned] == maxPeriods+1 || assigned[periodAssigned] == overlap) {
                    periodAssigned = random.nextInt(totalPeriods);
                }
                assigned[periodAssigned]++;
                //Change the period in the array and change the period for the section
                for (int k = 0; k < totalSections.size(); k++) {
                    if (totalSections.get(k) == List.get(i).getSectionsOccuring().get(j)) {
                        totalSections.get(k).setThePeriod(periodAssigned);
                    }
                }
                List.get(i).getSectionsOccuring().get(j).setThePeriod(periodAssigned);
            }
        }
    }


    //assigns teachers to separate sections for a specific course
    public void teacherSections(Courses course) {
        ArrayList<Sections> courseSections = new ArrayList<Sections>();
        //find the course's sections
        for (int i = 0; i < course.getSectionsOccuring().size(); i++) {
            courseSections.add(course.getSectionsOccuring().get(i));
        }
        //keep track of teachers that can teach this course and their qualifications
        ArrayList<String> teacher = course.getTeachersTeachingCourse();
        ArrayList<Teacher> qualifyList = new ArrayList<Teacher>();
        for (int i = 0; i < teachers.size(); i++) {
            for (int j = 0; j < teachers.get(i).getQualified().size(); j++) {
                if (teachers.get(i).getQualified().get(j).courseCode == course.courseCode) {
                    qualifyList.add(teachers.get(i));
                }
            }
        }
        //for each section, assign a teacher that is qualified and free
        for (int i = 0; i < courseSections.size(); i++) {
            //close qualifylist, and get rid of non-free teachers for this section
            ArrayList<Teacher> freeList = new ArrayList<Teacher>();
            ArrayList<Teacher> busyTeachers = new ArrayList<Teacher>();
                for (int j = 0; j < qualifyList.size(); j++) {
                    freeList.add(qualifyList.get(j));
                }
                ArrayList<Teacher> remover = new ArrayList<Teacher>();
                for (int j = 0; j < freeList.size(); j++) {
                    if (freeList.get(j).getTeaching().size() >= totalPeriods) {
                        remover.add(freeList.get(j));
                    }
                    //if the teacher has exceeded their class limit, remove them
                    if (freeList.get(j).getTeaching().size() >= (totalPeriods - freeList.get(j).freePeriods)) {
                        remover.add(freeList.get(j));
                    }
                    //if the teacher is already teaching this period, remove them
                    else {
                        for (int k = 0; k < freeList.get(j).getTeaching().size(); k++) {
                            if (freeList.get(j).getTeaching().get(k).getPeriod() == courseSections.get(i).getPeriod()) {
                                //add to a list of teachers to remove
                                busyTeachers.add(freeList.get(j));
                                remover.add(freeList.get(j));
                            }
                        }
                    }
                }
                //remove teachers on list of teachers to remove
            for (int j = 0; j < remover.size(); j++) {
                for (int k = freeList.size()-1; k >= 0; k--) {
                    if (remover.get(j).getIdentifier().equals(freeList.get(k).getIdentifier())) {
                        freeList.remove(k);
                    }
                }
            }
            freeList.trimToSize();
            if (freeList.size() != 0) {
                Teacher first = freeList.get(0);
                int smallestIndex = 0;
                for (int j = 1; j < freeList.size(); j++) {
                    if (freeList.get(j).qualified.size() < first.qualified.size() && !freeList.get(j).getIdentifier().equals("New Teacher")) {
                        first = freeList.get(j);
                        smallestIndex = j;
                    }
                }
                courseSections.get(i).setTheTeacher(first);
                for (int j = 0; j < totalSections.size(); j++) {
                    if (totalSections.get(j) == courseSections.get(i)) {
                        totalSections.get(j).setTheTeacher(first);
                    }
                }
                first.addTeaching(courseSections.get(i));
                freeList.remove(smallestIndex);
            }
            else {
                //for every busy teacher, if a different qualified teacher can teach their section, do so and assign busy teacher
                while(courseSections.get(i).getTeacher() == null) {
                    if (i > 0) {
                        for (int j = 0; j < i; j++) {
                            boolean freeToTeach = true;
                            for (int k = 0; k < courseSections.get(j).getTeacher().getTeaching().size(); k++) {
                                if (courseSections.get(j).getTeacher().getTeaching().get(k).getPeriod() == courseSections.get(i).getPeriod()) {
                                    freeToTeach = false;
                                }
                            }
                            if (freeToTeach == true) {
                                for (int k = 0; k < busyTeachers.size(); k++) {
                                    boolean freeToSwap = true;
                                    for (int l = 0; l < busyTeachers.get(k).getTeaching().size(); l++) {
                                        if (busyTeachers.get(k).getTeaching().get(l).getPeriod() == courseSections.get(j).getPeriod()) {
                                            freeToSwap = false;
                                        }
                                    }
                                    if (freeToSwap == true) {
                                        //set new teacher for busy teacher's course and add to their teaching
                                        //assign busy teacher to section. Remove previous course from busyTeacher's teaching and replace.
                                        Teacher swapTeacher = courseSections.get(j).getTeacher();
                                        for (int n = 0; n < totalSections.size(); n++) {
                                            if (totalSections.get(n) == courseSections.get(i)) {
                                                totalSections.get(n).setTheTeacher(courseSections.get(j).getTeacher());
                                            }
                                            if(totalSections.get(n) == courseSections.get(j)) {
                                                totalSections.get(n).setTheTeacher(busyTeachers.get(k));
                                            }
                                        }
                                        courseSections.get(i).setTheTeacher(swapTeacher);
                                        swapTeacher.addTeaching(courseSections.get(i));
                                        swapTeacher.getTeaching().remove(courseSections.get(j));
                                        busyTeachers.get(k).addTeaching(courseSections.get(j));
                                        courseSections.get(j).setTheTeacher(busyTeachers.get(k));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    for (int j = 0; j < busyTeachers.size(); j++) {
                        for (int k = 0; k < busyTeachers.get(j).getTeaching().size(); k++) {
                            if (busyTeachers.get(j).getTeaching().get(k).getPeriod() == courseSections.get(i).getPeriod()) {
                               Courses currentCourse = busyTeachers.get(j).getTeaching().get(k).getCourse();
                                for (int l = 0; l < teachers.size(); l++) {
                                    for (int m = 0; m < teachers.get(l).getQualified().size(); m++) {
                                        if (teachers.get(l).getQualified().get(m) == currentCourse) {
                                            boolean canTeach = true;
                                            for (int n = 0; n < teachers.get(l).getTeaching().size(); n++) {
                                                if(teachers.get(l).getTeaching().get(n).getPeriod() == courseSections.get(i).getPeriod()) {
                                                    canTeach = false;
                                                }
                                            }
                                            if (canTeach == true) {
                                                //set new teacher for busy teacher's course and add to their teaching
                                                //assign busy teacher to section. Remove previous course from busyTeacher's teaching and replace.
                                                Sections teacherSection = busyTeachers.get(j).getTeaching().get(k);
                                                for (int n = 0; n < totalSections.size(); n++) {
                                                    if (totalSections.get(n) ==  busyTeachers.get(j).getTeaching().get(k)) {
                                                        totalSections.get(n).setTheTeacher(teachers.get(l));
                                                    }
                                                    if(totalSections.get(n) == courseSections.get(i)) {
                                                        totalSections.get(n).setTheTeacher(busyTeachers.get(j));
                                                    }
                                                }
                                                teacherSection.setTheTeacher(teachers.get(l));
                                                courseSections.get(i).setTheTeacher(busyTeachers.get(j));
                                                busyTeachers.get(j).getTeaching().remove(teacherSection);
                                                busyTeachers.get(j).addTeaching(courseSections.get(i));
                                                teachers.get(l).addTeaching(teacherSection);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                if (courseSections.get(i).getTeacher() == null) {
                    ArrayList<Courses> newQualified = new ArrayList<Courses>();
                    newQualified.add(course);
                    Teacher newTeacher = new Teacher(newQualified, "New Teacher");
                    qualifyList.add(newTeacher);
                    addedTeachers.add(newTeacher);
                    teachers.add(newTeacher);
                    newTeacher.addTeaching(courseSections.get(i));
                    courseSections.get(i).setTheTeacher(newTeacher);
                }
            }
        }
    }


    //Function to search for a student given their identifier
    public Student searchStudent(String identifier){
        //Loop through all the students and find if a student of the identifier exists
        for (int i = 0; i < students.size(); i++) {
            if(students.get(i).getIdentifier().equals(identifier)){
                return students.get(i);
            }
        }
        //If the student doesn't exist then its an error so this is a debug comment
        System.out.println("A search was made for a student that doesn't exist.");
        return null;
    }


    //Assign students to a section semi randomly
    @SuppressWarnings("Duplicates")
    public void assignStudentsToSection(Courses course){
        //Loop through all the students in a course
        OUTER:
        for (int i = 0; i < course.getStudentsInCourse().size(); i++) {
            //Find the arraylist of sections that are available to the student

            ArrayList<Sections> masterSections = course.getSectionsOccuring();
            ArrayList<Sections> sections = new ArrayList<>();
            //System.out.println(course.getSectionsOccuring().size());
            for (int j = 0; j < course.getSectionsOccuring().size() ; j++) {
                sections.add(course.getSectionsOccuring().get(j));
            }
            //Make an array of periods that the student has free
            boolean[] freePeriods = new boolean[totalPeriods];
            Student student = searchStudent(course.getStudentsInCourse().get(i));
            for (int j = 0; j < totalPeriods; j++) {
                //Make sure that I don't get an index out of bounds exception, but check if something has already been put into the arraylist
                Boolean test = true;
                try{
                    if(student.getAssigned().get(j).equals(null)){
                        test = false;
                    }
                }catch(IndexOutOfBoundsException e){
                    test = false;
                }catch(NullPointerException e){
                    test = false;
                }
                //If the student doesn't already have a class assigned during that period, then it will be true
                if(!test){
                    freePeriods[j] = true;
                }
                //Otherwise they are busy so they aren't free
                else{
                    freePeriods[j] = false;
                }
            }
            //Go through and knock out any of the periods that the student isn't available
            //System.out.println(sections.size());
            for (int j = 0; j < sections.size(); j++) {
                //Check whether or not the student is free during that period, and if they aren't knock it off of the list
                if(!freePeriods[sections.get(j).getPeriod()]){
                    sections.remove(j);
                }
            }
            //First check to make sure the student is free for some sections and if they aren't try to reassign another class
            if(sections.size() == 0){
                //If the course is required, first try to assign it to a different period
                if(course.getRequried()){
                    //If there aren't any other similar periods free, then try to move a class that is in one of those periods to a different section
                    ArrayList<Courses> schedule = student.getAssigned();
                    for (int j = 0; j < masterSections.size(); j++) {
                        Courses conflict = schedule.get(masterSections.get(j).getPeriod());
                        for (int k = 0; k < conflict.getSectionsOccuring().size(); k++) {
                            //noinspection Duplicates
                            if(freePeriods[conflict.getSectionsOccuring().get(k).getPeriod()]){
                                //Change the period to be at one of the new free ones and remove the student from the previous period and add them into the new section
                                student.getAssigned().get(masterSections.get(j).getPeriod()).getSectionsOccuring().get(k).removeStudent(student);
                                student.changePeriod(conflict.getSectionsOccuring().get(k).getPeriod(), schedule.get(masterSections.get(j).getPeriod()));
                                student.getAssigned().get(masterSections.get(j).getPeriod() ).getSectionsOccuring().get(k).addStudent(student);
                                //Change the original period to be back to null
                                student.changePeriod(masterSections.get(j).getPeriod() , masterSections.get(j).getCourse());
                                course.getSectionsOccuring().get(j).addStudent(student);
                                freePeriods[conflict.getSectionsOccuring().get(k).getPeriod() ] = false;
                                continue OUTER;
                            }
                        }
                    }
                    //If it doesn't work out, then it needs to find a way to add a note into the student's final schedule that there was no possible way to fit both.
                    //Maybe try out changing around an elective in the schedule
                    for (int j = 0; j < schedule.size(); j++) {
                        //If they have an elective in their schedule
                        if(!schedule.get(j).getRequried()){
                            //Loop through the possible sections of the course that isn't assigned
                            for (int k = 0; k < masterSections.size(); k++) {
                                    //Find a course that is available in one of the same periods as the students
                                    for (int l = 0; l < courses.size(); l++) {
                                        //As long as the course isn't required it can be assigned
                                        if(!courses.get(l).getRequried()){
                                            for (int m = 0; m < courses.get(l).getSectionsOccuring().size(); m++) {
                                                //If the student is free in that period, add them to that class and remove them from their previous class
                                                if(freePeriods[courses.get(l).getSectionsOccuring().get(m).getPeriod()] && courses.get(l).getSectionsOccuring().get(m).getPeriod() != masterSections.get(k).getPeriod()){
                                                    Courses oldElective = schedule.get(j);
                                                    Sections oldSection = masterSections.get(k);
                                                    //Loop through to figure out what section the student was originally in
                                                    CENTER:
                                                    for (int n = 0; n < schedule.get(j).getSectionsOccuring().size(); n++) {
                                                        for (int o = 0; o < schedule.get(j).getSectionsOccuring().get(n).getStudents().size(); o++) {
                                                            if (schedule.get(j).getSectionsOccuring().get(n).getStudents().get(o) == student) {
                                                                oldSection = oldElective.getSectionsOccuring().get(n);
                                                                break CENTER;
                                                            }
                                                        }
                                                    }
                                                    Courses newElective = courses.get(l);
                                                    Sections newSection = newElective.getSectionsOccuring().get(m);
                                                    Courses required = course;
                                                    Sections reqSection = masterSections.get(k);
                                                    //Remove the student from the original sections
                                                    oldSection.removeStudent(student);
                                                    //Remove the previous unrequired from the student's schedule
                                                    student.removePeriod(oldSection.getPeriod() );
                                                    //Remove the student from the previous unrequired course
                                                    oldElective.removeStudentFromCourse(student.getIdentifier());

                                                    //Add the student to the new course
                                                    newElective.addStudent(student.getIdentifier());
                                                    //Add the required course to the student's schedule
                                                    student.setClass(reqSection.getPeriod(), required);
                                                    //Add the student to the new required section
                                                    reqSection.addStudent(student);
                                                    //Add the new unrequired to the student's schedule
                                                    student.setClass(newSection.getPeriod(), newElective);
                                                    //Add the student to the new unrequired section
                                                    newSection.addStudent(student);


                                                    continue OUTER;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    continue OUTER;
                }
                //However if the course isn't required
                else{
                    //If that doesn't work try to move the student to free sections of the other courses they are taking
                    ArrayList<Courses> schedule = student.getAssigned();
                    for (int a = 0; a < totalPeriods; a++) {
                        try {
                            schedule.get(a);
                        } catch(IndexOutOfBoundsException e) {
                            schedule.add(null);
                        }
                    }
                    for (int j = 0; j < masterSections.size(); j++) {
                        Courses conflict = schedule.get(masterSections.get(j).getPeriod() );
                        System.out.println(conflict);
                            for (int k = 0; k < conflict.getSectionsOccuring().size(); k++) {
                                //If a student has a scheduling conflict
                                //noinspection duplicates
                                if (freePeriods[conflict.getSectionsOccuring().get(k).getPeriod()]) {
                                    //get the assigned classes, get the period of the j item of masterSections
                                    student.getAssigned().get(masterSections.get(j).getPeriod()).getSectionsOccuring().get(k).removeStudent(student);
                                    student.changePeriod(conflict.getSectionsOccuring().get(k).getPeriod(), schedule.get(masterSections.get(j).getPeriod()));
                                    student.getAssigned().get(conflict.getSectionsOccuring().get(k).getPeriod()).getSectionsOccuring().get(k).addStudent(student);
                                    //Change the original period to be back to null
                                    student.changePeriod(masterSections.get(j).getPeriod(), masterSections.get(j).getCourse());
                                    course.getSectionsOccuring().get(j).addStudent(student);
                                    freePeriods[conflict.getSectionsOccuring().get(k).getPeriod()] = false;
                                    continue OUTER;
                                }
                            }

                    }
                    //Finally if that doesn't work, the student is going to be reassigned to a different course for that elective
                        //If the student is free for that period find the course that is farthest away from needing an additional section
                        int mini = Integer.MAX_VALUE;
                        Courses a = null;
                        Sections b = null;
                        for (int k = 0; k < courses.size(); k++) {
                            //First check to make sure the student isn't already taking the other course
                            if(student.getAssigned().contains(courses.get(k))) {
                                //If the course is the farthest away from needing another section so far then it needs to save it
                                if ((courses.get(k).getStudentsInCourse().size() % MAX) < mini) {
                                    //Then I need to check to make sure that the student actually has a free period that coincides with when the class is offered
                                    for (int l = 0; l < courses.get(k).getSectionsOccuring().size(); l++) {
                                        //If the student is free in a period when the course is offered then save that section as the best section to add the student to
                                        if (freePeriods[courses.get(k).getSectionsOccuring().get(l).getPeriod()]) {
                                            mini = courses.get(k).getSections();
                                            a = courses.get(k);
                                            b = courses.get(k).getSectionsOccuring().get(l);
                                        }
                                    }

                                }
                            }
                        }
                        if(a == null){
                            continue OUTER;
                        }
                        //Add the student to a section and remove them from the previous course
                        course.removeStudentFromCourse(student.identifier);
                        a.addStudent(student.identifier);
                        b.addStudent(student);
                        student.changePeriod(b.getPeriod() , b.getCourse());
                        continue OUTER;
                    }
                }

            //Now the student is free for all the sections in the list, so it puts them in the section with the fewest people
            int minCourseCount = Integer.MAX_VALUE;
            int indexOfBestSection = 0;
            //Loop through all of the sections and find the number of students in them
            for (int j = 0; j < sections.size(); j++) {
                if(sections.get(j).getStudents().size() < minCourseCount){
                    minCourseCount = sections.get(j).getStudents().size();
                    indexOfBestSection = j;
                }
            }
            //Add the course to the student's schedule
            ArrayList<Courses> studentSched = new ArrayList<Courses>(student.getAssigned());
            for (int j = 0; j < totalPeriods; j++) {
                try{
                    studentSched.get(j);
                }catch (IndexOutOfBoundsException e ){
                    studentSched.add(j, null);
                }
            }
            if(studentSched.size() <= sections.get(indexOfBestSection).getPeriod() ){
                for (int j = studentSched.size(); j < indexOfBestSection; j++) {
                    studentSched.add(j, null);
                }
                studentSched.set(sections.get(indexOfBestSection).getPeriod() , sections.get(indexOfBestSection).getCourse());
            }
            else{
                studentSched.set(sections.get(indexOfBestSection).getPeriod() , sections.get(indexOfBestSection).getCourse());
            }
            student.setAssigned(studentSched);

            //Add a student to the section's list of studentsB
            sections.get(indexOfBestSection).addStudent(student);
        }

    }
    //Score the result on a variety of factors
    public Double score(ArrayList<Student> testStudents, int newTeachers){
        Double score = 0.0;
        //Score the algorithm on how many students got the classes they wanted
        ArrayList<Double> averageGranted = new ArrayList<Double>();
        for (int i = 0; i < testStudents.size(); i++) {
            Double number = 0.0;
            for (int j = 0; j < testStudents.get(i).getRequested().size(); j++) {
                for (int k = 0; k < testStudents.get(i).getAssigned().size(); k++) {
                    if(testStudents.get(i).getAssigned().get(k) == testStudents.get(i).getRequested().get(j)){
                        ++number;
                    }
                }
            }
            Double percentage = number/totalPeriods;
            averageGranted.add(percentage);
        }
        //Percentage of students that are happy
        Double a = averageGranted.stream().mapToDouble(i -> i).average().getAsDouble();

        //Score the algorithm on how many new teachers are hired as a percent of the teachers that exist
        Double b = 1.0 - (double)newTeachers/(teachers.size()+newTeachers);

        score = (a + b)/2;

        return score;
    }

}
