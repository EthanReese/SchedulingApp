
import javax.swing.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Ethan Reese, Aletea VanVeldhuesen, and Josh Bromley on 7/24/17.
 * 7/27/17 13:33
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
        //Convert the files into the proper types of objects
        classes(courseTable);
        teacherCreation(teacherTable);
        requestedClasses(forecastingTable, courses);
        //Run all of our actual functions that do stuff
        setClassList();
        reassign(courses);
        teachingClasses(teachers, courses);
        addSections();
        ArrayList<Courses> antiModeCourses = antiMode();
        addPeriod(antiModeCourses);
        for (int i = 0; i < antiModeCourses.size(); i++) {
            teacherSections(antiModeCourses.get(i));
        }
        for (int i = 0; i < antiModeCourses.size(); i++) {
            assignStudentsToSection(antiModeCourses.get(i));
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
                sectionsOutput+= "Period: " + i + "\n";
                for (int j = 0; j < sectionSchedule.size(); j++) {
                    sectionsOutput += sectionSchedule.get(j).course.courseCode + ", " + sectionSchedule.get(j).teacher.identifier + ", " + sectionSchedule.get(j).students.size() + " students" + "\n";
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
                studentOutput += students.get(i).identifier + ":\n";
                for (int j = 0; j < students.get(i).assigned.size(); j++) {
                    studentOutput +=students.get(i).assigned.get(j).courseCode + ", ";
                }
            }
            ow.write(studentOutput);
            ow.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
                        if (teachers.get(i).getTeaching().get(k).period == (j+1)) {
                            teacherOutput += teachers.get(i).getTeaching().get(k) + ", ";
                            period++;
                        }
                    }
                    if (period == j) {
                        teacherOutput += "Free Period, ";
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
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }


        //find how many classes that were requested by students were actually assigned to them
        int perfected = 0;
        for(int i = 0; i < students.size(); i++) {
            for (int j = 0; j < students.get(i).requested.size(); j++) {
                for (int k = 0; k < students.get(i).assigned.size(); k++) {
                    if (students.get(i).requested.get(j).courseCode == students.get(i).assigned.get(k).courseCode) {
                        perfected++;
                    }
                }
            }
        }
        double percent = (perfected / (forecastingTable.size()*totalPeriods))*100;
        int roundPercent = (int)(percent);
        System.out.println("Requested Courses : Assigned Courses = " + roundPercent);
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
            teachers.get(i).freePeriods= Integer.parseInt(teacherTable.get(i).get(1));
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
            for (int j = 1; j < students.get(i).requested.size(); j++){
                for (int k = 0; k < courses.size(); k++) {
                    if (courses.get(k).courseCode == students.get(i).requested.get(j).courseCode) {
                        courses.get(k).addStudent(students.get(i).identifier);
                    }
                }
            }
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
        int[] numOfEach = new int[courses.get(courses.size() - 1).getSections()+1];
        //Loop through the list of courses and make an additional array that has an element for each number of sections.
        for (int i = 0; i < courses.size(); i++){
            numOfEach[courses.get(i).getSections()]++;
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
        //Loop through until it gets to the first course that is already sorted as part of the antimode
        for (int i = 0; i < courses.size(); i++) {
            if(courses.get(i).getSections() == returnInt){
                //When it hits the middleish point, first go down from there
                for (int j = i; j < 0; j--) {
                    if(courses.get(j).getSections() != returnInt){
                        returnList.add(courses.get(j));
                    }
                }
                //Then after it goes all the way down and adds everything under it into it, then go up from the middleish point
                for (int j = i; j < courses.size(); j++) {
                    if(courses.get(j).getSections() != returnInt){
                        returnList.add(courses.get(j));
                    }
                }
            }
        }
        return returnList;
    }
    //Create section objects for each section of the course
    public void addSections() {
        //for each course, for each section, create a new section for that course
        for (int i = 0; i < courses.size(); i++) {
            int total = (int)(Math.ceil(courses.get(i).getStudentsInCourse().size()/MAX));
            courses.get(i).setSections(total);
            for (int j = 0; j < courses.get(i).getSections(); j++) {
                ArrayList<Student> fakeStudents = new ArrayList<Student>();
                Sections section = new Sections(courses.get(i), 0, null, fakeStudents);
                totalSections.add(section);
                courses.get(i).addSection(section);
                System.out.println("Hello");
            }
        }
    }

    //for each course
    public void addPeriod(ArrayList<Courses> List) {
        //keep track of max number of courses in a period
        int[] periodTracker = new int[totalPeriods];
        int maxPeriods = (int)((totalSections.size()/totalPeriods)+.5);
        //get antiMode courses
        //Loop through the list of classes at the antimode that need to be assigned.
        for (int i = 0; i < List.size(); i++) {
            //determine how many sections of this class can be assigned to one period
            int overlap = (int)((List.get(i).getSections()/totalPeriods)+.5);
            int[] assigned = new int[totalPeriods];
            for (int j = 0; j < List.get(i).getSections(); j++) {
                //assign a random period, and add it to the array keeping track of total classes in a period
                int periodAssigned = (int)(Math.random()*(totalPeriods-1)+1);
                periodTracker[periodAssigned]++;
                //make sure there aren't too many of this class in this period, and that is doesn't go over max periods
                while (periodTracker[periodAssigned] == maxPeriods+1 || assigned[periodAssigned] == overlap) {
                    periodAssigned = (int)(Math.random()*(totalPeriods-1)+1);
                }
                assigned[periodAssigned]++;
                //Change the period in the array?
                List.get(i).getSectionsOccuring().get(j).setThePeriod(periodAssigned);
            }
        }
    }


    //assigns teachers to separate sections for a specific course
    public void teacherSections(Courses course) {
        ArrayList<Sections> courseSections = new ArrayList<Sections>();
        //find the course's sections
        courseSections = course.getSectionsOccuring();
        //keep track of teachers that can teach this course and their quialifications
        ArrayList<String> teacher = course.getTeachersTeachingCourse();
        ArrayList<Teacher> qualifyList = new ArrayList<Teacher>();
        for (int i = 0; i < teachers.size(); i++) {
            for (int j = 0; j < teachers.size(); j++) {
                if (teachers.get(j).identifier == teacher.get(i)) {
                    qualifyList.add(teachers.get(j));
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
            ArrayList<Integer> remover = new ArrayList<Integer>();
            for (int j = 0; j < freeList.size(); j++) {
                //if the teacher has exceeded their class limit, remove them
                if (freeList.get(j).getTeaching().size() >= (totalPeriods-freeList.get(j).freePeriods)) {
                    remover.add(j);
                }
                //if the teacher is already teaching this period, remove them
                else {
                    for (int k = 0; k < freeList.get(j).getTeaching().size(); k++) {
                        if (freeList.get(j).getTeaching().get(k).period == courseSections.get(i).period) {
                            //add to a list of teachers to remove
                            busyTeachers.add(freeList.get(j));
                            remover.add(j);
                        }
                    }
                }
            }
            //remove teachers on list of teachers to remove
            for (int j = 0; j < remover.size(); j++) {
                freeList.remove(remover.get(j));
            }
            if (freeList.size() != 0) {
                Teacher first = freeList.get(i);
                int smallestIndex = i;
                for (int j = i; j < freeList.size(); j++) {
                    if (freeList.get(j).qualified.size() < first.qualified.size()) {
                        first = freeList.get(j);
                        smallestIndex = j;
                    }
                }
                courseSections.get(i).setTheTeacher(first);
                first.addTeaching(courseSections.get(i));
                freeList.remove(smallestIndex);
            }
            else {
                //for every busy teacher, if a different qualified teacher can teach their section, do so and assign busy teacher
                while(courseSections.get(i).getTeacher() == null) {
                    if (i > 1) {
                        for (int j = 0; j < i; j++) {
                            boolean freeToTeach = true;
                            for (int k = 0; k < courseSections.get(j).getTeacher().getTeaching().size(); k++) {
                                if (courseSections.get(j).getTeacher().getTeaching().get(k).getPeriod() == courseSections.get(i).getPeriod()) {
                                    freeToTeach = false;
                                }
                            }
                            if (freeToTeach == true) {
                                for (int k = 0; k < busyTeachers.size(); k++) {

                                }
                            }
                        }
                    }
                    for (int j = 0; j < busyTeachers.size(); j++) {
                        for (int k = 0; k < busyTeachers.get(i).getTeaching().size(); k++) {
                            if (busyTeachers.get(j).getTeaching().get(k).period == courseSections.get(i).period) {
                               Courses currentCourse = busyTeachers.get(j).getTeaching().get(k).getCourse();
                                for (int l = 0; l < teachers.size(); l++) {
                                    for (int m = 0; m < teachers.get(l).getQualified().size(); m++) {
                                        if (teachers.get(l).getQualified().get(m) == currentCourse) {
                                            boolean canTeach = true;
                                            for (int n = 0; n < teachers.get(l).getTeaching().size(); n++) {
                                                if(teachers.get(l).getTeaching().get(n).period == courseSections.get(i).period) {
                                                    canTeach = false;
                                                }
                                            }
                                            if (canTeach == true) {
                                                //set new teacher for busy teacher's course and add to their teaching
                                                //assign busy teacher to section. Remove previous course from busyTeacher's teaching and replace.
                                                busyTeachers.get(j).getTeaching().get(k).setTheTeacher(teachers.get(l));
                                                courseSections.get(i).setTheTeacher(busyTeachers.get(j));
                                                busyTeachers.get(j).teaching.remove(currentCourse);
                                                busyTeachers.get(j).addTeaching(courseSections.get(i));
                                                teachers.get(l).addTeaching(busyTeachers.get(j).getTeaching().get(k));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                ArrayList<Courses> newQualified = new ArrayList<Courses>();
                newQualified.add(courseSections.get(i).course);
                Teacher newTeacher = new Teacher(newQualified, "New Teacher");
                addedTeachers.add(newTeacher);
                teachers.add(newTeacher);
                newTeacher.addTeaching(courseSections.get(i));
                courseSections.get(i).setTheTeacher(newTeacher);
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
    public void assignStudentsToSection(Courses course){
        //Loop through all the students in a course
        OUTER:
        for (int i = 0; i < course.getStudentsInCourse().size(); i++) {
            //Find the arraylist of sections that are available to the student

            ArrayList<Sections> masterSections = course.getSectionsOccuring();
            ArrayList<Sections> sections = new ArrayList<>();
            System.out.println(course.getSectionsOccuring().size());
            for (int j = 0; j < course.getSectionsOccuring().size(); j++) {
                sections.add(course.getSectionsOccuring().get(i));
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
                            if(freePeriods[conflict.getSectionsOccuring().get(k).getPeriod()]){
                                //Change the period to be at one of the new free ones and remove the student from the previous period and add them into the new section
                                conflict.getSectionsOccuring().get(k).removeStudent(student);
                                student.changePeriod(conflict.getSectionsOccuring().get(k).getPeriod(), schedule.get(masterSections.get(j).getPeriod()));
                                conflict.getSectionsOccuring().get(k).addStudent(student);
                                //Change the original period to be back to null
                                student.changePeriod(masterSections.get(j).getPeriod(), masterSections.get(j).getCourse());
                                masterSections.get(j).addStudent(student);
                                freePeriods[conflict.getSectionsOccuring().get(k).getPeriod()] = false;
                                continue OUTER;
                            }
                        }
                    }
                    //If it doesn't work out, then it needs to find a way to add a note into the student's final schedule that there was no possible way to fit both.
                    //TODO: Figure out the messaging system bc a required class can't be changed
                    //Maybe try out changing around an elective in the schedule
                    continue OUTER;
                }
                //However if the course isn't required
                else{
                    //If that doesn't work try to move the student to free sections of the other courses they are taking
                    ArrayList<Courses> schedule = student.getAssigned();
                    for (int j = 0; j < masterSections.size(); j++) {
                        Courses conflict = schedule.get(masterSections.get(j).getPeriod());
                        for (int k = 0; k < masterSections.size(); k++) {
                            //If a student has a
                            if(freePeriods[conflict.getSectionsOccuring().get(k).getPeriod()]){
                                conflict.getSectionsOccuring();
                            }
                        }
                    }

                    //Finally if that doesn't work, the student is going to be reassigned to a different course.
                }
                continue OUTER;
            }
            else{
                System.out.println(sections.size());
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
            ArrayList<Courses> studentSched = new ArrayList<>(student.getAssigned());
            if(studentSched.size() <= sections.get(indexOfBestSection).getPeriod()){
                studentSched.add(sections.get(indexOfBestSection).getPeriod(), sections.get(indexOfBestSection).getCourse());
            }
            else{
                studentSched.set(sections.get(indexOfBestSection).getPeriod(), sections.get(indexOfBestSection).getCourse());
            }
            student.setAssigned(studentSched);

            //Add a student to the section's list of studentsB
            sections.get(indexOfBestSection).addStudent(student);
        }

    }

}
