
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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



public class SchedulingApp implements ActionListener{

    //Create the swing interface elements
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();
    JButton generateButton = new JButton("Generate Schedules");
    JLabel forecastLabel = new JLabel("Forecasting Database Path");
    JLabel teacherLabel = new JLabel("Teacher Database Path");
    JLabel courseLabel = new JLabel("Course Database Path");
    JLabel periodLabel = new JLabel("Number of periods");
    JLabel maxStudentsInCourse = new JLabel("Maximum Students in Course");
    JLabel minStudentsInCourse = new JLabel("Minimum Students in the Course");
    JLabel freePeriodLabel = new JLabel("Minimum free periods per teacher");
    JTextField forecastInput = new JTextField();
    JTextField teacherInput = new JTextField();
    JTextField courseInput = new JTextField();
    JTextField periodInput = new JTextField();
    JTextField maxStudentsInput = new JTextField();
    JTextField minStudentsInput = new JTextField();
    JTextField freePeriodInput = new JTextField();
    JButton selectForecast = new JButton("Open File");
    JButton selectTeacher = new JButton("Open File");
    JButton selectCourse = new JButton("Open File");
    final JFileChooser fc = new JFileChooser();

    private Container north = new Container();
    private Container south = new Container();


    BufferedReader br = null;
    Scanner scanner = new Scanner(System.in);
    ArrayList<Courses> coursesList = new ArrayList<Courses>();
    ArrayList<Sections> totalSections = new ArrayList<Sections>();
    ArrayList<Courses> courses = new ArrayList<Courses>();
    ArrayList<Teacher> teachers = new ArrayList<Teacher>();
    ArrayList<Teacher> addedTeachers = new ArrayList<Teacher>();
    ArrayList<Student> students = new ArrayList<Student>();
    ArrayList<ArrayList<Sections>> twodschedule = new ArrayList<>();
    ArrayList<Schedule> schedules = new ArrayList<>();
    int MIN;
    int MAX;
    int totalPeriods;
    int counter = 0;
    Random random = new Random();
    public static int setFinalPeriods = 8;
    public static int finalFreePeriods = 0;
    int totalNewTeachers = 0;
    boolean failureToAssign = false;
    File forecastingFile;
    File teacherFile;
    File courseFile;
    ArrayList<ArrayList<String>> forecastingTable;
    ArrayList<ArrayList<String>> teacherTable;
    ArrayList<ArrayList<String>> courseTable;

    public SchedulingApp() {
        frame.setSize(1000, 400);
        frame.setLayout(new BorderLayout());
        north.setLayout(new GridLayout(3,3));
        north.add(forecastLabel);
        north.add(forecastInput);
        north.add(selectForecast);
        north.add(teacherLabel);
        north.add(teacherInput);
        north.add(selectTeacher);
        north.add(courseLabel);
        north.add(courseInput);
        north.add(selectCourse);
        selectForecast.addActionListener(this);
        selectTeacher.addActionListener(this);
        selectCourse.addActionListener(this);

        generateButton.addActionListener(this);


        south.setLayout(new GridLayout(2,4));
        south.add(periodLabel);
        south.add(maxStudentsInCourse);
        south.add(minStudentsInCourse);
        south.add(freePeriodLabel);
        south.add(periodInput);
        south.add(maxStudentsInput);
        south.add(minStudentsInput);
        south.add(freePeriodInput);


        frame.add(generateButton, BorderLayout.CENTER);
        frame.add(south, BorderLayout.SOUTH);
        frame.add(north, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);


        //Potentially do this as some kind of GUI



    }

    public static void main(String[] args) {
        new SchedulingApp();
    }

    public ArrayList<ArrayList<String>> readCSV(File filePath) {
        //Make a proper arraylist to return
        ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();
        int counter = 0;
        //Attempt to read in the file
        try {
            String line;
            //Make a buffered reader that can read in the csv file
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                ArrayList<String> tempList = new ArrayList<>(Arrays.asList((line).split(",")));
                returnList.add(counter, tempList);
                ++counter;
            }

        } catch (FileNotFoundException e) {
            System.out.println("There is no file located at the path " + filePath);
                if(String.valueOf(forecastingFile) == String.valueOf(filePath)){
                    JOptionPane.showMessageDialog(null, "Please enter a new forecasting file and restart the program");
                }
                else if(teacherFile == filePath) {
                    JOptionPane.showMessageDialog(null, "Please enter a new teacher file.");
                }
                else if(courseFile == filePath) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid courses file.");
                }
                return null;

        } catch (IOException e) {
            e.printStackTrace();
            //idk how the user is supposed to fix that
        } finally {
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
            Courses course = new Courses(name, isRequired);
            courses.add(course);
        }
    }


    //Turns the teacher list into a list of teacher objects
    public void teacherCreation(ArrayList<ArrayList<String>> teacherTable) {
        ArrayList<Courses> qualified = new ArrayList<Courses>();
        for (int i = 0; i < teacherTable.size(); i++) {
            for (int j = 1; j < teacherTable.get(i).size(); j++) {
                qualified.add(search(courses, teacherTable.get(i).get(j)));
            }
            teachers.add(new Teacher(qualified, teacherTable.get(i).get(0)));
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
            Student student = new Student(request, id, totalPeriods);
            students.add(student);
            student.setTotalPeriods(totalPeriods);
            request.clear();
        }


    }

    //get the students in a course and set that
    public void setClassList() {
        for (int i = 0; i < students.size(); i++) {
            for (int j = 0; j < students.get(i).getRequested().size(); j++) {
                for (int k = 0; k < courses.size(); k++) {
                    try {
                        if (courses.get(k).getCourseCode() == students.get(i).getRequested().get(j).getCourseCode()) {
                            courses.get(k).addStudent(students.get(i).getIdentifier());
                        }
                    }catch(NullPointerException e){
                        //System.out.println("Null");
                    }
                }
            }
        }

    }

    public void teachingClasses(ArrayList<Teacher> teachers, ArrayList<Courses> courses) {
        for (int i = 0; i < teachers.size(); i++) {
            for (int j = 1; j < teachers.get(i).getQualified().size(); j++) {
                try {
                    search(courses, teachers.get(i).getQualified().get(j).getCourseCode()).addTeacher(teachers.get(i).getIdentifier());
                }catch(NullPointerException e){

                }
            }
        }
    }

    //Seach for certain courses
    public Courses search(ArrayList<Courses> courseList, String code) {
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
                    boolean duplicate = false;
                    boolean still = false;
                    while (duplicate == false) {
                        for (int k = 0; k < students.size(); k++) {
                            if (students.get(k).getIdentifier() == studentReassigned.get(j)) {
                                for (int l = 0; l < students.get(k).getRequested().size(); l++) {
                                    if (students.get(k).getRequested().get(l) == nonRequired.get(randCourse)) {
                                        randCourse = random.nextInt(nonRequired.size());
                                        still = true;
                                    }
                                }
                            }
                        }
                        if (still == false) {
                            duplicate = true;
                        }
                        still = false;
                    }
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
                if (dataSet.get(i).getSections() > dataSet.get(i + 1).getSections()) {
                    //Swap the two numbers
                    temp = dataSet.get(i);
                    dataSet.set(i, dataSet.get(i + 1));
                    dataSet.set(i + 1, temp);
                }
            }
        }
        return dataSet;
    }

    //Create section objects for each section of the course
    public void addSections() {
        //for each course, for each section, create a new section for that course
        for (int i = 0; i < courses.size(); i++) {
            int total = (int) (Math.ceil((double) courses.get(i).getStudentsInCourse().size() / (double) (MAX)));
            courses.get(i).setSections(total);
            for (int j = 0; j < total; j++) {
                ArrayList<Student> fakeStudents = new ArrayList<>();
                Sections section = new Sections(courses.get(i), 0, null, fakeStudents);
                totalSections.add(section);
                courses.get(i).addSection(section);
            }
        }
    }

    //for each course
    public void addPeriod(ArrayList<Courses> List) {
        ArrayList<Courses> List2 = new ArrayList<Courses>();
        for (int i = List.size() - 1; i >= 0; i--) {
            List2.add(List.get(i));
        }
        //keep track of max number of courses in a period
        int[] periodTracker = new int[totalPeriods];
        int maxPeriods = (int) (Math.ceil(((double) totalSections.size() / (double) totalPeriods)));
        //get antiMode courses
        //Loop through the list of classes at the antimode that need to be assigned.
        for (int i = 0; i < List2.size(); i++) {
            //determine how many sections of this class can be assigned to one period
            int overlap = (int) (Math.ceil(((double) List2.get(i).getSections() / (double) totalPeriods)));
            int[] assigned = new int[totalPeriods];
            for (int j = 0; j < List2.get(i).getSections(); j++) {
                //assign a random period, and add it to the array keeping track of total classes in a period
                int periodAssigned = random.nextInt(totalPeriods);
                boolean thereIsFree = false;
                for (int k = 0; k < totalPeriods; k++) {
                    if (assigned[k] == 0 && k != periodAssigned && periodTracker[k] < maxPeriods) {
                        thereIsFree = true;
                    }
                }
                int takeoff = 0;
                for (int k = 0; k < periodTracker.length; k++) {
                    if (periodTracker[k] == maxPeriods) {
                        takeoff++;
                    }
                    overlap = (int) (Math.ceil(((double) List2.get(i).getSections() / (double) (totalPeriods - takeoff))));
                }
                //make sure there aren't too many of this class in this period, and that is doesn't go over max periods
                while (periodTracker[periodAssigned] == maxPeriods || assigned[periodAssigned] == overlap || (assigned[periodAssigned] != 0 && thereIsFree == true)) {
                    periodAssigned = random.nextInt(totalPeriods);
                }
                periodTracker[periodAssigned]++;
                assigned[periodAssigned]++;
                //Change the period in the array and change the period for the section
                for (int k = 0; k < totalSections.size(); k++) {
                    if (totalSections.get(k) == List2.get(i).getSectionsOccuring().get(j)) {
                        totalSections.get(k).setThePeriod(periodAssigned);
                    }
                }
                List2.get(i).getSectionsOccuring().get(j).setThePeriod(periodAssigned);
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
        //for each section, assign a teacher that is qualified and free
        ArrayList<Teacher> qualifyList = new ArrayList<Teacher>();
        for (int x = 0; x < teachers.size(); x++) {
            for (int j = 0; j < teachers.get(x).getQualified().size(); j++) {
                if (teachers.get(x).getQualified().get(j).getCourseCode() == course.getCourseCode()) {
                    qualifyList.add(teachers.get(x));
                }
            }
        }
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
                if (freeList.get(j).getTeaching().size() >= (totalPeriods - freeList.get(j).getFreePeriods())) {
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
                for (int k = freeList.size() - 1; k >= 0; k--) {
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
                    if (freeList.get(j).qualified.size() == first.qualified.size()) {
                        if(freeList.get(j).getTeaching().size() > first.getTeaching().size()) {
                            first = freeList.get(j);
                            smallestIndex = j;
                        }
                    }
                    else if (freeList.get(j).qualified.size() < first.qualified.size() && !freeList.get(j).getIdentifier().equals("New Teacher")) {
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
            } else {
                //for every busy teacher, if a different qualified teacher can teach their section, do so and assign busy teacher
                while (courseSections.get(i).getTeacher() == null) {
                    int amountBusy = busyTeachers.size();
                    if (i > 0) {
                        Sections mainSection = courseSections.get(i);
                        for (int j = 0; j < i; j++) {
                            Sections nextSection = courseSections.get(j);
                            boolean freeToTeach = true;
                            for (int k = 0; k < courseSections.get(j).getTeacher().getTeaching().size(); k++) {
                                if (courseSections.get(j).getTeacher().getTeaching().get(k).getPeriod() == mainSection.getPeriod()) {
                                    freeToTeach = false;
                                }
                            }
                            if (freeToTeach == true) {
                                for (int k = 0; k < amountBusy; k++) {
                                    boolean freeToSwap = true;
                                    int teachingSize = busyTeachers.get(k).getTeaching().size();
                                    for (int l = 0; l < teachingSize; l++) {
                                        if (busyTeachers.get(k).getTeaching().get(l).getPeriod() == nextSection.getPeriod()) {
                                            freeToSwap = false;
                                        }
                                    }
                                    if (freeToSwap == true && busyTeachers.get(k).getTeaching().size() < (totalPeriods - busyTeachers.get(k).getFreePeriods())) {
                                        //set new teacher for busy teacher's course and add to their teaching
                                        //assign busy teacher to section. Remove previous course from busyTeacher's teaching and replace.
                                        Teacher swapTeacher = nextSection.getTeacher();
                                        for (int n = 0; n < totalSections.size(); n++) {
                                            if (totalSections.get(n) == mainSection) {
                                                totalSections.get(n).setTheTeacher(courseSections.get(j).getTeacher());
                                            }
                                            if (totalSections.get(n) == mainSection) {
                                                totalSections.get(n).setTheTeacher(busyTeachers.get(k));
                                            }
                                        }
                                        mainSection.setTheTeacher(swapTeacher);
                                        swapTeacher.addTeaching(mainSection);
                                        swapTeacher.getTeaching().remove(nextSection);
                                        busyTeachers.get(k).addTeaching(nextSection);
                                        nextSection.setTheTeacher(busyTeachers.get(k));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    for (int j = 0; j < amountBusy; j++) {
                        for (int k = 0; k < busyTeachers.get(j).getTeaching().size(); k++) {
                            if (busyTeachers.get(j).getTeaching().get(k).getPeriod() == courseSections.get(i).getPeriod()) {
                                Courses currentCourse = busyTeachers.get(j).getTeaching().get(k).getCourse();
                                for (int l = 0; l < teachers.size(); l++) {
                                    if (teachers.get(l) != busyTeachers.get(j)) {
                                        for (int m = 0; m < teachers.get(l).getQualified().size(); m++) {
                                            if (teachers.get(l).getQualified().get(m) == currentCourse) {
                                                boolean canTeach = true;
                                                for (int n = 0; n < teachers.get(l).getTeaching().size(); n++) {
                                                    if (teachers.get(l).getTeaching().get(n).getPeriod() == courseSections.get(i).getPeriod()) {
                                                        canTeach = false;
                                                    }
                                                }
                                                if (canTeach == true && teachers.get(l).getTeaching().size() < (totalPeriods - teachers.get(l).getFreePeriods())) {
                                                    //set new teacher for busy teacher's course and add to their teaching
                                                    //assign busy teacher to section. Remove previous course from busyTeacher's teaching and replace.
                                                    Sections teacherSection = busyTeachers.get(j).getTeaching().get(k);
                                                    for (int n = 0; n < totalSections.size(); n++) {
                                                        if (totalSections.get(n) == busyTeachers.get(j).getTeaching().get(k)) {
                                                            totalSections.get(n).setTheTeacher(teachers.get(l));
                                                        }
                                                        if (totalSections.get(n) == courseSections.get(i)) {
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
                    }
                    break;
                }
                // The Dancing Queen
                //     ________
                //    / ______ \
                //   / | o  o | \
                //   \ |  __  | /
                //   / |______| \
                //   \ ___||___ /
                //    //|    |\\  //
                //   // |    | \\//
                //   \\ |____|
                //    \\/ /\ \
                //     / /  \ \
                //    / /    \ \

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
    public Student searchStudent(String identifier) {
        //Loop through all the students and find if a student of the identifier exists
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getIdentifier().equals(identifier)) {
                return students.get(i);
            }
        }
        //If the student doesn't exist then its an error so this is a debug comment
        //System.out.println("A search was made for a student that doesn't exist.");
        return null;
    }


    //Assign students to a section semi randomly
    @SuppressWarnings("Duplicates")
    public void assignStudentsToSection(Courses course) {
        //Loop through all the students in a course
        OUTER:
        //       Buttons
        //
        //      |||||||||
        //     / *  _  * \
        //     \_________/
        //      ___| |____
        //     // |  . | \\
        //    ||  |  . |  ||
        //    ||  |  . |  ||
        //        / /\ \
        //       / /  \ \
        //      / /    \ \

        for (int i = 0; i < course.getStudentsInCourse().size(); i++) {
            //Find the arraylist of sections that are available to the student

            ArrayList<Sections> masterSections = course.getSectionsOccuring();
            ArrayList<Sections> sections = new ArrayList<>();
            //System.out.println(course.getSectionsOccuring().size());
            for (int j = 0; j < course.getSectionsOccuring().size(); j++) {
                sections.add(course.getSectionsOccuring().get(j));
            }
            //Make an array of periods that the student has free
            boolean[] freePeriods = new boolean[totalPeriods];
            Student student = searchStudent(course.getStudentsInCourse().get(i));
            for (int j = 0; j < totalPeriods; j++) {
                //Make sure that I don't get an index out of bounds exception, but check if something has already been put into the arraylist
                Boolean test = true;
                try {
                    if (student.getAssigned()[j].equals(null)) {
                        test = false;
                    }
                } catch (IndexOutOfBoundsException e) {
                    test = false;
                } catch (NullPointerException e) {
                    test = false;
                }
                //If the student doesn't already have a class assigned during that period, then it will be false
                if (!test) {
                    freePeriods[j] = true;
                }
                //Otherwise they are busy so they aren't free
                else {
                    freePeriods[j] = false;
                }
            }
            //Go through and knock out any of the periods that the student isn't available
            //System.out.println(sections.size());
            for (int j = sections.size() - 1; j >= 0; j--) {
                //Check whether or not the student is free during that period, and if they aren't knock it off of the list
                if (!freePeriods[sections.get(j).getPeriod()]) {
                    sections.remove(j);
                }
            }
            //  Squidward

            //    ----
            //   --|-|-
            //    ----
            //      |
            //   ---|----
            //      |
            //     / \
            //    /   \

            //First check to make sure the student is free for some sections and if they aren't try to reassign another class
            if (sections.size() == 0) {
                //If the course is required, first try to assign it to a different period
                if (course.getRequried()) {
                    //If there aren't any other similar periods free, then try to move a class that is in one of those periods to a different section
                    Sections[] schedule = student.getAssigned();
                    for (int j = 0; j < masterSections.size(); j++) {
                        Sections conflict = schedule[masterSections.get(j).getPeriod()];
                        if (masterSections.get(j).getStudents().size() < MAX) {
                            for (int k = 0; k < conflict.getCourse().getSectionsOccuring().size(); k++) {
                                //noinspection Duplicates
                                if (freePeriods[conflict.getCourse().getSectionsOccuring().get(k).getPeriod()]
                                        && conflict.getCourse().getSectionsOccuring().get(k).getStudents().size() < MAX) {
                                    //Change the period to be at one of the new free ones and remove the student from the previous period and add them into the new section
                                    conflict.removeStudent(student);
                                    student.changePeriod(conflict.getCourse().getSectionsOccuring().get(k).getPeriod(), conflict.getCourse().getSectionsOccuring().get(k));
                                    conflict.getCourse().getSectionsOccuring().get(k).addStudent(student);
                                    //Change the original period to be back to null
                                    student.changePeriod(masterSections.get(j).getPeriod(), masterSections.get(j));
                                    masterSections.get(j).addStudent(student);
                                    freePeriods[conflict.getCourse().getSectionsOccuring().get(k).getPeriod()] = false;
                                    continue OUTER;
                                }
                            }
                        }
                    }
                    for (int j = 0; j < masterSections.size(); j++) {
                        Sections conflict = schedule[masterSections.get(j).getPeriod()];
                        if (masterSections.get(j).getStudents().size() < MAX && !conflict.getCourse().getRequried()) {
                            //Change the period to be at one of the new free ones and remove the student from the previous period and add them into the new section
                            conflict.removeStudent(student);
                            //Change the original period to be back to null
                            student.changePeriod(masterSections.get(j).getPeriod(), masterSections.get(j));
                            masterSections.get(j).addStudent(student);
                            studentReassign(student);
                            for (int x = 0; x < totalPeriods; x++) {
                                //Make sure that I don't get an index out of bounds exception, but check if something has already been put into the arraylist
                                Boolean test = true;
                                try {
                                    if (student.getAssigned()[x].equals(null)) {
                                        test = false;
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    test = false;
                                } catch (NullPointerException e) {
                                    test = false;
                                }
                                //If the student doesn't already have a class assigned during that period, then it will be false
                                if(!test){
                                    freePeriods[x] = true;
                                }
                                //Otherwise they are busy so they aren't free
                                else {
                                    freePeriods[x] = false;
                                }
                            }
                            continue OUTER;
                        }
                    }

                    // Dancing Ethan
                    //     _____
                    //    / O O \
                    //    \     /
                    //     ----- //
                    //       || //
                    //   \\  ||//
                    //   \\//||
                    //      //\\
                    //     //  \\
                    //    //    \\
                    //If it doesn't work out, then it needs to find a way to add a note into the student's final schedule that there was no possible way to fit both.
                    //Maybe try out changing around an elective in the schedule
                    for (int j = 0; j < schedule.length; j++) {
                        //If they have an elective in their schedule
                        try {
                            if (!schedule[j].getCourse().getRequried()) {
                                //Loop through the possible sections of the course that isn't assigned
                                for (int k = 0; k < masterSections.size(); k++) {
                                    //Find a course that is available in one of the same periods as the students
                                    for (int l = 0; l < courses.size(); l++) {
                                        //As long as the course isn't required it can be assigned
                                        if (!courses.get(l).getRequried()) {
                                            for (int m = 0; m < courses.get(l).getSectionsOccuring().size(); m++) {
                                                //If the student is free in that period, add them to that class and remove them from their previous class
                                                if (freePeriods[courses.get(l).getSectionsOccuring().get(m).getPeriod()] && courses.get(l).getSectionsOccuring().get(m).getPeriod() != masterSections.get(k).getPeriod()
                                                        && courses.get(l).getSectionsOccuring().get(m).getStudents().size() < MAX) {
                                                    Courses oldElective = schedule[j].getCourse();
                                                    Sections oldSection = masterSections.get(k);
                                                    //Loop through to figure out what section the student was originally in
                                                    CENTER:
                                                    for (int n = 0; n < schedule[j].getCourse().getSectionsOccuring().size(); n++) {
                                                        for (int o = 0; o < schedule[j].getCourse().getSectionsOccuring().get(n).getStudents().size(); o++) {
                                                            if (schedule[j].getCourse().getSectionsOccuring().get(n).getStudents().get(o) == student) {
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
                                                    student.removePeriod(oldSection.getPeriod());
                                                    //Remove the student from the previous unrequired course
                                                    oldElective.removeStudentFromCourse(student.getIdentifier());

                                                    //Add the student to the new course
                                                    newElective.addStudent(student.getIdentifier());
                                                    //Add the required course to the student's schedule
                                                    student.setClass(reqSection.getPeriod(), reqSection);
                                                    //Add the student to the new required section
                                                    reqSection.addStudent(student);
                                                    //Add the new unrequired to the student's schedule
                                                    student.setClass(newSection.getPeriod(), newSection);
                                                    //Add the student to the new unrequired section
                                                    newSection.addStudent(student);

                                                    continue OUTER;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (NullPointerException n) {
                            //System.out.println("NULL");
                        }
                    }
                    continue OUTER;
                }
                //  Skater Dude
                //
                //    \\\\\\\
                //    | -  - |
                //    |   o  |
                //     __||__
                //   //|    |\\
                //  // |    | ||
                // //  |____| ||
                //     //  \\
                //    //    \\
                //   //      \\

                //However if the course isn't required
                else {
                    //If that doesn't work try to move the student to free sections of the other courses they are taking
                    Sections[] schedule = student.getAssigned();
                    for (int j = 0; j < masterSections.size(); j++) {
                        Sections conflict = schedule[masterSections.get(j).getPeriod()];
                        for (int k = 0; k < conflict.getCourse().getSectionsOccuring().size(); k++) {
                            //If a student has a scheduling conflict
                            //noinspection duplicates
                            if (freePeriods[conflict.getCourse().getSectionsOccuring().get(k).getPeriod()] && conflict.getCourse().getSectionsOccuring().get(k).getStudents().size() < MAX) {
                                //get the assigned classes, get the period of the j item of masterSections
                                student.getAssigned()[masterSections.get(j).getPeriod()].getCourse().getSectionsOccuring().get(k).removeStudent(student);
                                student.changePeriod(conflict.getCourse().getSectionsOccuring().get(k).getPeriod(), schedule[masterSections.get(j).getPeriod()]);
                                student.getAssigned()[conflict.getCourse().getSectionsOccuring().get(k).getPeriod()].getCourse().getSectionsOccuring().get(k).addStudent(student);
                                //Change the original period to be back to null
                                student.changePeriod(masterSections.get(j).getPeriod(), masterSections.get(j));
                                course.getSectionsOccuring().get(j).addStudent(student);
                                freePeriods[conflict.getCourse().getSectionsOccuring().get(k).getPeriod()] = false;

                                continue OUTER;
                            }
                        }

                    }
                    //Finally if that doesn't work, the student is going to be reassigned to a different course for that elective
                    //If the student is free for that period find the course that is farthest away from needing an additional section
                    int mini = Integer.MAX_VALUE;
                    Courses a = null;
                    Sections b = null;
                    for (int k = 0; k < totalSections.size(); k++) {
                        //First check to make sure the student isn't already taking the other course
                        for (int j = 0; j < totalPeriods; j++) {
                            if (student.getAssigned()[j] != null) {
                                if (student.getAssigned()[j].getCourse() == totalSections.get(k).getCourse()) {
                                    //If the course is the farthest away from needing another section so far then it needs to save it
                                    if ((totalSections.get(k).getCourse().getStudentsInCourse().size() % MAX) < mini) {
                                        //Then I need to check to make sure that the student actually has a free period that coincides with when the class is offered
                                        for (int l = 0; l < totalSections.get(k).getCourse().getSectionsOccuring().size(); l++) {
                                            //If the student is free in a period when the course is offered then save that section as the best section to add the student to
                                            if (freePeriods[totalSections.get(k).getCourse().getSectionsOccuring().get(l).getPeriod()]) {
                                                mini = totalSections.get(k).getCourse().getSections();
                                                a = totalSections.get(k).getCourse();
                                                b = totalSections.get(k);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                    if (a == null) {
                        continue OUTER;
                    }
                    //Add the student to a section and remove them from the previous course
                    course.removeStudentFromCourse(student.identifier);
                    a.addStudent(student.identifier);
                    b.addStudent(student);
                    student.changePeriod(b.getPeriod(), b);
                    continue OUTER;
                }
            }

            //Now the student is free for all the sections in the list, so it puts them in the section with the fewest people
            int minCourseCount = MAX;
            int indexOfBestSection = 0;
            //Loop through all of the sections and find the number of students in them
            for (int j = 0; j < sections.size(); j++) {
                if(sections.get(j).getStudents().size() < minCourseCount){
                    minCourseCount = sections.get(j).getStudents().size();
                    indexOfBestSection = j;
                }
            }
            //     Box Head
            //
            //      -----
            //     | .  . |
            //     |  ~   |
            //       -----
            //        |
            //        |/\
            //      \/|
            //       /\
            //      /  \
            //
            //ADD MORE OF WHAT ETHAN HAD HERE???
            //Add the course to the student's schedule
            student.addAssigned(sections.get(indexOfBestSection).getPeriod(), sections.get(indexOfBestSection));
            freePeriods[sections.get(indexOfBestSection).getPeriod()] = false;
            //Add a student to the section's list of studentsB
            sections.get(indexOfBestSection).addStudent(student);
        }

    }

    //Score the result on a variety of factors
    public Double score(ArrayList<Student> testStudents, int newTeachers) {
        Double score;
        //Score the algorithm on how many students got the classes they wanted
        ArrayList<Double> averageGranted = new ArrayList<Double>();
        for (int i = 0; i < testStudents.size(); i++) {
            Double number = 0.0;
            for (int j = 0; j < testStudents.get(i).getRequested().size(); j++) {
                for (int k = 0; k < testStudents.get(i).getAssigned().length; k++) {
                    if (testStudents.get(i).getAssigned()[k] == null) {

                    } else {
                        if (testStudents.get(i).getAssigned()[k].getCourse() == testStudents.get(i).getRequested().get(j)) {
                            ++number;
                        }
                    }
                }
            }
            Double percentage = number / totalPeriods;
            averageGranted.add(percentage);
        }
        //Percentage of students that are happy
        Double a = averageGranted.stream().mapToDouble(i -> i).average().getAsDouble();

        //Score the algorithm on how many new teachers are hired as a percent of the teachers that exist
        Double b = 1.0 - (double) newTeachers / (teachers.size() + newTeachers);

        score = (a + b) / 2;

        //Double check to make sure that students have been assigned to all classes
        if (failureToAssign == true) {
            return 0.0;
        }

        return score;
    }

    //this reassigns a student to a random non-required course
    public void studentReassign(Student student) {
        ArrayList<Courses> freeCourses = new ArrayList<Courses>();
        ArrayList<Sections> freeSections = new ArrayList<Sections>();
        //makes sure the student is not already taking the course
        if(student.getRequested().size() > totalPeriods) {
            for (int i = totalPeriods; i < student.getRequested().size(); i++) {
                freeCourses.add(student.getRequested().get(i));
            }
            for(int i = 0; i < freeCourses.size(); i++){
                for(int j = 0; j < freeCourses.get(i).getSectionsOccuring().size(); j++){
                    if(student.getAssigned()[freeCourses.get(i).getSectionsOccuring().get(j).getPeriod()] == null){
                        student.getAssigned()[freeCourses.get(i).getSectionsOccuring().get(j).getPeriod()] = freeCourses.get(i).getSectionsOccuring().get(j);
                    }
                }
            }
        }
        for (int i = 0; i < totalSections.size(); i++) {
            boolean isAssigned = false;
            for (int j = 0; j < student.getAssigned().length; j++) {
                if (student.getAssigned()[j] != null) {
                    if (student.getAssigned()[j].getCourse() == totalSections.get(i).getCourse()) {
                        isAssigned = true;
                    }
                }
            }
            if (totalSections.get(i).getCourse().getRequried() == false
                    && totalSections.get(i).getStudents().size() >= MIN-1
                    && student.getAssigned()[totalSections.get(i).getPeriod()] == null && !isAssigned) {
                freeSections.add(totalSections.get(i));
            }
        }
        if (freeSections.size() != 0) {
            int newSections = random.nextInt(freeSections.size());
            if (student.getAssigned()[freeSections.get(newSections).getPeriod()] != null) {
                student.getAssigned()[freeSections.get(newSections).getPeriod()].removeStudent(student);
            }
            student.getAssigned()[freeSections.get(newSections).getPeriod()] = freeSections.get(newSections);
            freeSections.get(newSections).addStudent(student);
            return;
        }

    }

    //   Disco Daz
    //
    //      ###
    //   ########
    //  ###O##O###
    //  ####__####
    //   #######  ##
    //      ##  ##
    //    ######
    //  ##  ##
    //   ## ##
    //     ####
    //    ##  ##
    //   ##    ##
    //using an array of students, this method reassigns them to a different section of their required course
    public void reassignRequired(ArrayList<Student> students, Courses course, Sections section) {
        for (int i = 0; i < students.size(); i++) {
            boolean assigned = false;
            //first, try to put it where an elective course is, and then reassign the elective
            for (int j = 0; j < course.getSectionsOccuring().size(); j++) {
                if(course.getSectionsOccuring().get(j) != section) {
                    if (course.getSectionsOccuring().get(j).getStudents().size() < MAX) {
                        if (students.get(i).getAssigned()[course.getSectionsOccuring().get(j).getPeriod()] != null) {
                            if (!students.get(i).getAssigned()[course.getSectionsOccuring().get(j).getPeriod()].getCourse().getRequried() && students.get(i).getAssigned()[course.getSectionsOccuring().get(j).getPeriod()].getStudents().size() < MAX) {
                                Courses reassign = students.get(i).getAssigned()[course.getSectionsOccuring().get(j).getPeriod()].getCourse();
                                students.get(i).getAssigned()[course.getSectionsOccuring().get(j).getPeriod()].removeStudent(students.get(i));
                                reassignElective(students.get(i), reassign);
                                students.get(i).getAssigned()[course.getSectionsOccuring().get(j).getPeriod()] = course.getSectionsOccuring().get(j);
                                course.getSectionsOccuring().get(j).addStudent(students.get(i));
                                assigned = true;
                                break;
                            }
                        }
                    }
                }
            }
            boolean breaker = false;
            //if impossible, try to reassign a different required course, and then place it there
            for (int j = 0; j < students.get(i).getAssigned().length; j++) {
                for (int n = 0; n < course.getSectionsOccuring().size(); n++) {
                    if(students.get(i).getAssigned()[j] != null) {
                        if (j == course.getSectionsOccuring().get(n).getPeriod() &&
                                course.getSectionsOccuring().get(n).getStudents().size() < MAX &&
                                students.get(i).getAssigned()[j].getCourse().getRequried()) {
                            if (reassignSecondRequired(students.get(i), students.get(i).getAssigned()[j].getCourse(), students.get(i).getAssigned()[j].getPeriod()) == true) {
                                if (students.get(i).getAssigned()[j] != null) {
                                    Courses elective = students.get(i).getAssigned()[j].getCourse();
                                    students.get(i).getAssigned()[j].removeStudent(students.get(i));
                                    students.get(i).getAssigned()[j] = course.getSectionsOccuring().get(n);
                                    course.getSectionsOccuring().get(n).addStudent(students.get(i));
                                    reassignElective(students.get(i), elective);
                                    breaker = true;
                                    assigned = true;
                                    break;
                                }
                                students.get(i).getAssigned()[j] = course.getSectionsOccuring().get(n);
                                course.getSectionsOccuring().get(n).addStudent(students.get(i));
                                assigned = true;
                                breaker = true;
                                break;
                            }
                        }
                    }
                }
                if (breaker == true) {
                    break;
                }
            }
            if (assigned == false) {
                section.addStudent(students.get(i));
                if(students.get(i).getAssigned()[section.getPeriod()] != null) {
                    failureToAssign = true;
                    System.out.println("Error");
                }
                students.get(i).getAssigned()[section.getPeriod()] = section;
                counter++;
            }
        }
    }

    //this method will reassign a required course for a specific student with an elective course if it can
    //if it can't, it will return false
    public boolean reassignSecondRequired(Student student, Courses course, int not) {
        for (int j = 0; j < course.getSectionsOccuring().size(); j++) {
            if(student.getAssigned()[course.getSectionsOccuring().get(j).getPeriod()] == null) {
                //this is where this second required course was
            }
            else if (!student.getAssigned()[course.getSectionsOccuring().get(j).getPeriod()].getCourse().getRequried()
                    && course.getSectionsOccuring().get(j).getPeriod() != not && course.getSectionsOccuring().get(j).getStudents().size() < MAX) {
                Courses change = student.getAssigned()[course.getSectionsOccuring().get(j).getPeriod()].getCourse();
                student.getAssigned()[course.getSectionsOccuring().get(j).getPeriod()].removeStudent(student);
                reassignElective(student, change);
                student.getAssigned()[course.getSectionsOccuring().get(j).getPeriod()] = course.getSectionsOccuring().get(j);
                course.getSectionsOccuring().get(j).addStudent(student);
                return true;
            }
        }
        return false;
    }

    //this course will reassign an elective course into a student's free slot
    //if it can't, it will call a method to reassign it to a random elective
    public void reassignElective(Student student, Courses course) {
        for (int i = 0; i < course.getSectionsOccuring().size(); i++) {
            if (student.getAssigned()[course.getSectionsOccuring().get(i).getPeriod()] == null) {
                student.getAssigned()[course.getSectionsOccuring().get(i).getPeriod()] = course.getSectionsOccuring().get(i);
                course.getSectionsOccuring().get(i).addStudent(student);
                return;
            }
        }
        studentReassign(student);
    }

    public void makeSchedule(ArrayList<Sections> sectionList) {
        int period;
        for (int i = 0; i < sectionList.size(); i++) {
            twodschedule.add(new ArrayList<Sections>());
        }
        for (int i = 0; i < sectionList.size(); i++) {
            period = sectionList.get(i).getPeriod();
            twodschedule.get(period).add(sectionList.get(i));
        }
    }
    //     Bubbles
    //
    //       OOO
    //      O+O+O
    //     OOOOOOO     |
    //      O___O      |
    //       OOO       |
    //        O        |
    //       OOO O O O 0
    //      O O
    //     O OOO
    //    O O   O
    //     o     O
    //    O       O
    //after assigning students, findReassign finds every sections exceeding the maximum amount of students,
    //and sends them to another method to reassign them
    public void findReassign() {
        for (int i = 0; i < totalSections.size(); i++) {
            if (totalSections.get(i).getStudents().size() > MAX) {
                ArrayList<Student> reassigned = new ArrayList<Student>();
                for (int j = 0; j < totalSections.get(i).getStudents().size()-(MAX+1); j++) {
                    reassigned.add(totalSections.get(i).getStudents().get(j));
                }
                for (int j = reassigned.size()-1; j >= 0; j--) {
                    totalSections.get(i).getStudents().get(j).getAssigned()[totalSections.get(i).getPeriod()] = null;
                    totalSections.get(i).removeStudent(totalSections.get(i).getStudents().get(j));
                }
                reassignRequired(reassigned, totalSections.get(i).getCourse(), totalSections.get(i));
                reassigned.clear();
                int count = counter;
                int times = 0;
                while (count > 5 && times < 100) {
                    reassigned.clear();
                    counter = 0;
                    for (int j = 0; j < count; j++) {
                        //System.out.println(totalSections.get(i));
                        try {
                            reassigned.add(totalSections.get(i).getStudents().get(j));
                        } catch(Exception e) {

                        }
                    }
                    for (int j = reassigned.size()-1; j >= 0; j--) {
                        totalSections.get(i).getStudents().get(j).getAssigned()[totalSections.get(i).getPeriod()] = null;
                    }
                    for (int j = reassigned.size()-1; j >= 0 ; j--) {
                        totalSections.get(i).removeStudent(totalSections.get(i).getStudents().get(j));
                    }
                    reassignRequired(reassigned, totalSections.get(i).getCourse(), totalSections.get(i));
                    count = counter;
                    times++;
                }
                counter = 0;
                reassigned.clear();
            }
        }
    }


    //this program will reassign all the students in a required class with less than the minimum number of students
    //hopefully, we will be able to place each student into this required class elsewhere
    //otherwise, they get a random elective (which has rarely happened)
    //then, this section will be deleted
    public void reassignReqBelowMin(ArrayList<Student> studentList, Sections section) {
            //get rid of this course, and put everyone in the other sections!
            //fairly certain... this actually won't happen considering how sections are made
            for (int i = 0; i < studentList.size(); i++) {
                studentList.get(i).getAssigned()[section.getPeriod()] = null;
                section.getStudents().remove(studentList.get(i));
            }
            reassignRequired(studentList, section.getCourse(), section);
            if(section.getStudents().size() > 0) {
                for (int i = 0; i < section.getStudents().size() ; i++) {
                    boolean breaker = false;
                    Student student = section.getStudents().get(i);
                    student.getAssigned()[section.getPeriod()] = null;
                    for (int j = 0; j < section.getCourse().getSectionsOccuring().size(); j++) {
                        Sections newSection = section.getCourse().getSectionsOccuring().get(j);
                        if(newSection != section) {
                            if(student.getAssigned()[newSection.getPeriod()] == null) {
                                student.getAssigned()[newSection.getPeriod()] = newSection;
                                newSection.addStudent(student);
                                break;
                            }
                            else if (!student.getAssigned()[newSection.getPeriod()].getCourse().getRequried()) {
                                reassignElective(student, student.getAssigned()[newSection.getPeriod()].getCourse());
                                student.getAssigned()[newSection.getPeriod()] = newSection;
                                newSection.addStudent(student);
                                break;
                            }
                        }
                    }
                    if(student.getAssigned()[section.getPeriod()] == null) {
                        for (int j = 0; j < section.getCourse().getSectionsOccuring().size(); j++) {
                            if (section.getCourse().getSectionsOccuring().get(j) != section) {
                                if (student.getAssigned()[section.getCourse().getSectionsOccuring().get(j).getPeriod()].getCourse().getRequried()) {
                                    if (reassignSecondRequired(student,
                                            student.getAssigned()[section.getCourse().getSectionsOccuring().get(j).getPeriod()].getCourse(),
                                            section.getPeriod()) == true) {
                                        if (student.getAssigned()[section.getCourse().getSectionsOccuring().get(j).getPeriod()] != null) {
                                            Courses elective = student.getAssigned()[section.getCourse().getSectionsOccuring().get(j).getPeriod()].getCourse();
                                            student.getAssigned()[section.getCourse().getSectionsOccuring().get(j).getPeriod()].removeStudent(section.getStudents().get(i));
                                            student.getAssigned()[section.getCourse().getSectionsOccuring().get(j).getPeriod()] = section.getCourse().getSectionsOccuring().get(j);
                                            section.getCourse().getSectionsOccuring().get(j).addStudent(student);
                                            reassignElective(student, elective);
                                            break;
                                        }
                                        student.getAssigned()[section.getCourse().getSectionsOccuring().get(j).getPeriod()] = section.getCourse().getSectionsOccuring().get(j);
                                        section.getCourse().getSectionsOccuring().get(j).addStudent(student);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if(student.getAssigned()[section.getPeriod()] == null) {
                        for (int j = 0; j < section.getCourse().getSectionsOccuring().size(); j++) {
                            Sections otherFirstSection = section.getCourse().getSectionsOccuring().get(j);
                            breaker = false;
                            if (otherFirstSection != section) {
                                if (student.getAssigned()[otherFirstSection.getPeriod()].getCourse().getRequried()) {
                                    Courses requiredCourseOne = student.getAssigned()[otherFirstSection.getPeriod()].getCourse();
                                    for (int k = 0; k < requiredCourseOne.getSections(); k++) {
                                        Sections requiredCourseSection = requiredCourseOne.getSectionsOccuring().get(k);
                                        if(student.getAssigned()[requiredCourseSection.getPeriod()] == null) {
                                            student.getAssigned()[requiredCourseSection.getPeriod()] = requiredCourseSection;
                                            requiredCourseSection.addStudent(student);
                                            student.getAssigned()[otherFirstSection.getPeriod()].removeStudent(student);
                                            student.getAssigned()[otherFirstSection.getPeriod()] = otherFirstSection;
                                            otherFirstSection.addStudent(student);
                                            breaker = true;
                                            break;
                                        }
                                        else if(student.getAssigned()[requiredCourseSection.getPeriod()].getCourse().getRequried() &&
                                                requiredCourseSection.getPeriod() != otherFirstSection.getPeriod()) {
                                            if (reassignSecondRequired(student, student.getAssigned()[requiredCourseSection.getPeriod()].getCourse(),
                                                    requiredCourseSection.getPeriod()) == true) {
                                                student.getAssigned()[requiredCourseSection.getPeriod()] = requiredCourseSection;
                                                requiredCourseSection.addStudent(student);
                                                student.getAssigned()[otherFirstSection.getPeriod()].removeStudent(student);
                                                student.getAssigned()[otherFirstSection.getPeriod()] = otherFirstSection;
                                                otherFirstSection.addStudent(student);
                                                breaker = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (breaker == true) {
                                break;
                            }
                        }
                    }


                    //this means none of the sections are in elective slots, all in required slots
                    //and, they cannot reassign any other required courses
                    //maybe, try to reassign required courses blocking other required courses? Will that work at all?
                    if(student.getAssigned()[section.getPeriod()] == null) {
                        studentReassign(student);
                        System.out.println("failure to assign");
                        failureToAssign = true;
                    }
                    section.getStudents().remove(student);
                }
            }
            section.getTeacher().getTeaching().remove(section);
            for (int i = 0; i < totalSections.size(); i++) {
                if(totalSections.get(i) == section) {
                    totalSections.get(i).getTeacher().getTeaching().remove(totalSections.get(i));
                    totalSections.remove(i);
                }
            }
    }


    //this method will take sections above the maximum number of students, and will split it into two sections
    public void splitMax(Sections section, ArrayList<Student> students) {
        ArrayList<Student> newStudents = new ArrayList<Student>();
        Sections newSection = new Sections(section.getCourse(), section.getPeriod(), null, newStudents);
        for (int i = 0; i < students.size(); i++) {
            newSection.addStudent(students.get(i));
            students.get(i).getAssigned()[section.getPeriod()] = section;
        }
        for (int i = 0; i < teachers.size(); i++) {
            boolean free = false;
            for (int j = 0; j < teachers.get(i).getQualified().size(); j++) {
                if(teachers.get(i).getQualified().get(j) == newSection.getCourse()) {
                    free = true;
                }
            }
            if(teachers.get(i).getTeaching().size() == totalPeriods-teachers.get(i).getFreePeriods()) {
                free = false;
            }
            for (int j = 0; j < teachers.get(i).getTeaching().size(); j++) {
                if(teachers.get(i).getTeaching().get(j).getPeriod() == newSection.getPeriod()) {
                    free = false;
                }
            }
            if(free == true) {
                teachers.get(i).addTeaching(newSection);
                newSection.setTheTeacher(teachers.get(i));
                totalSections.add(newSection);
                break;
            }
        }
        if(newSection.getTeacher() == null) {
            ArrayList<Courses> newQualified = new ArrayList<Courses>();
            newQualified.add(newSection.getCourse());
            Teacher newTeacher = new Teacher(newQualified, "New Teacher");
            addedTeachers.add(newTeacher);
            teachers.add(newTeacher);
            newTeacher.addTeaching(newSection);
            newSection.setTheTeacher(newTeacher);
            totalSections.add(newSection);
        }
    }

    //this method will try and cut down the number of teachers
    //if two teacher's schedules can be combined into one, do it, and remove one of the teachers

    public void reassignTeachers() {
        for (int i = 0; i < teachers.size(); i++) {
            for (int j = 0; j < teachers.size(); j++) {
                if (teachers.get(i).getIdentifier() == "New Teacher" && teachers.get(j).getIdentifier() != "New Teacher") {
                    if (teachers.get(j).getTeaching().size() != 0 && teachers.get(i).getTeaching().size() != 0) {
                        boolean takeAll = true;
                        for (int t = 0; t < teachers.get(i).getTeaching().size(); t++) {
                            if (!teachers.get(j).isQualified(teachers.get(i).getTeaching().get(t).getCourse())) {
                                takeAll = false;
                            }
                            for (int k = 0; k < teachers.get(j).getTeaching().size(); k++) {
                                if (teachers.get(i).getTeaching().get(t).getPeriod() == teachers.get(j).getTeaching().get(k).getPeriod()) {
                                    takeAll = false;
                                }
                            }
                        }
                        if (takeAll == true && (teachers.get(j).getTeaching().size() + teachers.get(i).getTeaching().size()) <= (totalPeriods - teachers.get(j).getFreePeriods())) {
                            for (int k = 0; k < teachers.get(i).getTeaching().size(); k++) {
                                Sections section = teachers.get(i).getTeaching().get(k);
                                teachers.get(j).getTeaching().add(section);
                                section.setTheTeacher(teachers.get(j));
                            }
                            teachers.get(i).getTeaching().clear();
                        }
                    }
                }
                else {
                    if (teachers.get(j).getTeaching().size() != 0 && teachers.get(i).getTeaching().size() != 0) {
                        boolean takeAll = true;
                        for (int t = 0; t < teachers.get(j).getTeaching().size(); t++) {
                            if (!teachers.get(i).isQualified(teachers.get(j).getTeaching().get(t).getCourse())) {
                                takeAll = false;
                            }
                            for (int k = 0; k < teachers.get(i).getTeaching().size(); k++) {
                                if (teachers.get(j).getTeaching().get(t).getPeriod() == teachers.get(i).getTeaching().get(k).getPeriod()) {
                                    takeAll = false;
                                }
                            }
                        }
                        if (takeAll == true && (teachers.get(i).getTeaching().size() + teachers.get(j).getTeaching().size()) <= (totalPeriods - teachers.get(i).getFreePeriods())) {
                            for (int k = 0; k < teachers.get(j).getTeaching().size(); k++) {
                                Sections section = teachers.get(j).getTeaching().get(k);
                                teachers.get(i).getTeaching().add(section);
                                section.setTheTeacher(teachers.get(i));
                            }
                            teachers.get(j).getTeaching().clear();
                        }
                    }
                }
            }
        }
    }

    public void combineSections(Sections one, Sections two) {
        for (int i = 0; i < two.getStudents().size(); i++) {
            one.addStudent(two.getStudents().get(i));
            two.getStudents().get(i).getAssigned()[two.getPeriod()] = one;
        }
        two.getStudents().clear();
        two.getTeacher().getTeaching().remove(two);
        two.setTheTeacher(null);
    }


    @Override
    public void actionPerformed(ActionEvent ex) {
        //If the user presses the button to get a forcasting file
        if(ex.getSource() == selectForecast){
            int returnVal = fc.showOpenDialog(null);
            //Then it needs to pop up the picker and once a value is picked it needs to remember that value and write it into the jtextentry
            if(returnVal == JFileChooser.APPROVE_OPTION){
                forecastingFile = fc.getSelectedFile();
                forecastInput.setText(forecastingFile.getAbsolutePath());
            }
        }
        else if(ex.getSource() == selectTeacher){
            int returnVal = fc.showOpenDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION){
                teacherFile = fc.getSelectedFile();
                teacherInput.setText(teacherFile.getAbsolutePath());
            }
        }
        else if(ex.getSource() == selectCourse){
            int returnVal = fc.showOpenDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION){
                courseFile = fc.getSelectedFile();
                courseInput.setText(courseFile.getAbsolutePath());
            }
        }
        //Otherwise, we're going to need to run the whole program to generate.
        else if(ex.getSource() == generateButton){
            //Validate file inputs, if it isn't already defined, check the input
            if(forecastingFile == null){
                String k = forecastInput.getText();
                forecastingFile = new File(k);
            }
            if(teacherFile == null){
                String k = teacherInput.getText();
                teacherFile = new File(k);
            }
            if(courseFile == null){
                String k = courseInput.getText();
                courseFile = new File(k);
            }
            //Take in the ancillary inputs, and validate them to make sure they work right
            //Period input
            try {
                totalPeriods = Integer.parseInt(periodInput.getText());
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please enter a valid Integer for your number of periods.");
                return;
            }
            //If the period input isn't a number that we want it to be than make them enter a new one
            if(totalPeriods>10 || totalPeriods<5){
                JOptionPane.showMessageDialog(null, "Please enter a number between 5 and 10 for the total periods.");
                return;
            }
            setFinalPeriods = totalPeriods;
            //Maximum students in the class
            try{
                MAX = Integer.parseInt(maxStudentsInput.getText());
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please enter a valid Integer for your maximum students in course.");
                return;
            }
            //Make sure the max is above 10
            if(MAX<10){
                JOptionPane.showMessageDialog(null, "Please enter a number above 10 for your max students in section.");
                return;
            }

            //Minimum students in a section
            try {
                MIN = Integer.parseInt(minStudentsInput.getText());
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please enter a valid integer for your minimum number of students in section");
                return;
            }
            //Make sure the minimum is also less than half of the minimum
            if(MIN<5 || MIN>((MAX/2)+1)){
                JOptionPane.showMessageDialog(null, "Please enter a number that is above 5 and less than half of the maximum students for your minimum students");
                return;
            }

            //Check on the free periods input
            try{
                finalFreePeriods = Integer.parseInt(freePeriodInput.getText());
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please enter a valid integer for your minimum teacher free periods");
            }
            //Make sure the input for teacher free periods is reasonable
            if(finalFreePeriods>totalPeriods-1){
                JOptionPane.showMessageDialog(null, "Please enter a number for minimum teacher free periods that is at least 2 less than the maximum periods");
            }



            //Call the functions corresponding to each individual file
            forecastingTable = readCSV(forecastingFile);
            teacherTable = readCSV(teacherFile);
            courseTable = readCSV(courseFile);
            if(forecastingTable == null || teacherTable == null || courseTable == null){
                return;
            }
            //Run the following part a few times
            for (int c = 0; c < 3; c++) {
                //Convert the files into the proper types of objects
                classes(courseTable);
                teacherCreation(teacherTable);
                requestedClasses(forecastingTable, courses);
                //Run all of our actual functions that do stuff
                setClassList();
                reassign(courses);
                teachingClasses(teachers, courses);
                addSections();
                courses = BubbleSort(courses);


                addPeriod(courses);
                for (int i = 0; i < courses.size(); i++) {
                    teacherSections(courses.get(i));
                }
                for (int i = 0; i < courses.size(); i++) {
                    assignStudentsToSection(courses.get(i));
                }
                //run through the total sections, and if two can combine, combine them
                ArrayList<Sections> emptySections = new ArrayList<Sections>();
                for (int i = 0; i < totalSections.size(); i++) {
                    for (int j = 0; j < totalSections.size(); j++) {
                        if(totalSections.get(i) != totalSections.get(j) &&
                                totalSections.get(i).getPeriod() == totalSections.get(j).getPeriod() &&
                                totalSections.get(i).getCourse() == totalSections.get(j).getCourse() &&
                                totalSections.get(i).getStudents().size() <= MAX/2 &&
                                totalSections.get(j).getStudents().size() <= MAX/2 &&
                                totalSections.get(j).getStudents().size() != 0 &&
                                totalSections.get(i).getStudents().size() != 0) {
                            emptySections.add(totalSections.get(j));
                            combineSections(totalSections.get(i), totalSections.get(j));
                        }
                    }
                }
                for (int i = 0; i < emptySections.size(); i++) {
                    totalSections.remove(emptySections.get(i));
                }findReassign();
                for (int i = 0; i < students.size(); i++) {
                    for (int j = 0; j < students.get(i).getAssigned().length; j++) {
                        if(students.get(i).getAssigned()[j] == null) {
                            studentReassign(students.get(i));
                        }
                    }
                }
                for (int i = totalSections.size()-1; i >= 0; i--) {
                    if (totalSections.get(i).getStudents().size() < MIN) {
                        if(totalSections.get(i).getStudents().size() == 0) {
                            totalSections.get(i).getTeacher().getTeaching().remove(totalSections.get(i));
                            totalSections.remove(i);
                        }
                        else if(totalSections.get(i).getCourse().getRequried() == false) {
                            for (int j = 0; j < totalSections.get(i).getStudents().size(); j++) {
                                Student student = totalSections.get(i).getStudents().get(j);
                                student.getAssigned()[totalSections.get(i).getPeriod()] = null;
                                studentReassign(student);
                            }
                            totalSections.get(i).getTeacher().getTeaching().remove(totalSections.get(i));
                            totalSections.remove(i);

                        }else {
                            ArrayList<Student> reassignReq = new ArrayList<Student>();
                            for (int j = 0; j < totalSections.get(i).getStudents().size(); j++) {
                                reassignReq.add(totalSections.get(i).getStudents().get(j));
                            }
                            reassignReqBelowMin(reassignReq, totalSections.get(i));
                        }
                    }
                }
                for (int i = totalSections.size()-1; i >= 0; i--) {
                    if(totalSections.get(i).getStudents().size() > MAX) {
                        //System.out.println(totalSections.get(i).getCourse().getCourseCode() + ", " + totalSections.get(i).getPeriod());
                        ArrayList<Student> swapStudents = new ArrayList<Student>();
                        int half = (int)(totalSections.get(i).getStudents().size() / 2);
                        for (int j = totalSections.get(i).getStudents().size()-1; j >= half; j--) {
                            swapStudents.add(totalSections.get(i).getStudents().get(j));
                            totalSections.get(i).getStudents().get(j).getAssigned()[totalSections.get(i).getPeriod()] = null;
                        }
                        for (int j = 0; j < swapStudents.size(); j++) {    totalSections.get(i).removeStudent(swapStudents.get(j));
                        }
                        splitMax(totalSections.get(i), swapStudents);
                    }
                }
                reassignTeachers();
                makeSchedule(totalSections);
                for (int i = 0; i < teachers.size(); i++) {
                    if (teachers.get(i).getIdentifier().equals("New Teacher") && teachers.get(i).getTeaching().size() > 0) {
                        totalNewTeachers++;
                    }
                }
                Schedule schedule = new Schedule();
                schedule.setTeachers(teachers);
                schedule.setAddedTeachers(addedTeachers);
                schedule.setSections(totalSections);
                schedule.setCourses(courses);
                schedule.setStudents(students);
                schedule.setScore(score(students, totalNewTeachers));
                schedule.setNewTeachers(totalNewTeachers);
                schedules.add(schedule);
                teachers.clear();
                addedTeachers.clear();
                totalSections.clear();
                courses.clear();
                students.clear();
                totalNewTeachers = 0;
                failureToAssign = false;
            }
            //Loop through all of the schedules that are created and find the one with the highest score
            Double maxScore = Double.MIN_VALUE;
            Schedule bestSchedule = new Schedule();
            for (int i = 0; i < schedules.size(); i++) {
                if(schedules.get(i).getScore()>maxScore){
                    maxScore = schedules.get(i).getScore();
                    bestSchedule = schedules.get(i);
                }
            }
            teachers = bestSchedule.getTeachers();
            addedTeachers = bestSchedule.getAddedTeachers();
            totalSections = bestSchedule.getSections();
            courses = bestSchedule.getCourses();
            students = bestSchedule.getStudents();
            totalNewTeachers = bestSchedule.getNewTeachers();
            if(bestSchedule.getTeachers() == null || bestSchedule.getAddedTeachers() == null ||
                    bestSchedule.getSections() == null) {
                JOptionPane.showMessageDialog(null, "There was an internal error. Try running the program again with the same inputs.");
                System.exit(0);
            }
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            makeSchedule(bestSchedule.getSections());
            PrintWriter pw;
            try {
                fc.setSelectedFile(new File("Sections.txt"));
                int returnVal = fc.showSaveDialog(null);
                File dir;
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    dir = fc.getSelectedFile();
                }else{
                    JOptionPane.showMessageDialog(null, "The directory you selected is invalid.");
                    return;
                }
                String path = dir.getAbsolutePath();
                pw = new PrintWriter(new FileWriter(new File(path)));
                //create the output string
                String sectionsOutput = "Course, Teacher, # of Students\n";
                for (int i = 0; i < totalPeriods; i++) {
                    ArrayList<Sections> sectionSchedule = new ArrayList<Sections>();
                    for (int j = 0; j < totalSections.size(); j++) {
                        if (totalSections.get(j).getPeriod() == i) {
                            sectionSchedule.add(totalSections.get(j));
                        }
                    }
                    sectionsOutput += "Period: " + (i + 1) + "\n";
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
                e.printStackTrace();
                System.exit(0);
            }
            PrintWriter ow;
            try {
                fc.setSelectedFile(new File("Students.txt"));
                int returnVal = fc.showSaveDialog(null);
                File dir;
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    dir = fc.getSelectedFile();
                }else{
                    JOptionPane.showMessageDialog(null, "The directory you selected is invalid.");
                    return;
                }
                String path = dir.getAbsolutePath();
                ow = new PrintWriter(new FileWriter(new File(path)));
                //create the output string
                //IS STUDENT ASSIGNMENT IN ORDER???? That's what this assumes.
                String studentOutput = "Student, Per. 1(teacher), Per. 2(teacher), ...\n";
                for (int i = 0; i < students.size(); i++) {
                    studentOutput += students.get(i).getIdentifier() + ":\n";
                    for (int j = 0; j < totalPeriods; j++) {
                        try {
                            studentOutput += students.get(i).getAssigned()[j].getCourse().getCourseCode() + "(" + students.get(i).getAssigned()[j].getTeacher().getIdentifier()+ ")";
                        } catch (NullPointerException e) {
                            studentOutput += "Free,";
                        }
                    }
                    studentOutput += "\n";
                }
                int perfect = 0;
                for (int i = 0; i < students.size(); i++) {
                    for (int j = 0; j < students.get(i).getRequested().size(); j++) {
                        for (int k = 0; k < students.get(i).getAssigned().length; k++) {
                            try {
                                if (students.get(i).getAssigned()[k] == null) {
                                    //System.out.println("?");
                                } else if (students.get(i).getAssigned()[k].getCourse().getCourseCode() == students.get(i).getRequested().get(j).getCourseCode()) {
                                    perfect++;
                                    break;
                                }
                            }catch(NullPointerException e){
                            }
                        }
                    }
                }
                double totalPerfect = ((double)perfect / ((double)students.size()*totalPeriods))*100.0;
                studentOutput += "Student Satisfaction: " + totalPerfect + "%";
                ow.write(studentOutput);
                ow.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            PrintWriter ww;
            try {
                fc.setSelectedFile(new File("Teachers.txt"));
                int returnVal = fc.showSaveDialog(null);
                File dir;
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    dir = fc.getSelectedFile();
                }else{
                    JOptionPane.showMessageDialog(null, "The directory you selected is invalid.");
                    return;
                }
                String path = dir.getAbsolutePath();
                ww = new PrintWriter(new FileWriter(new File(path)));
                //create the output string
                String teacherOutput = "Teacher,Per. 1, Per. 2, ...\n";
                ArrayList<Teacher> fired = new ArrayList<Teacher>();
                for (int i = 0; i < teachers.size(); i++) {
                    if (teachers.get(i).getTeaching().size() != 0) {
                        if(teachers.get(i).identifier == "New Teacher") {
                            teacherOutput += "New " + teachers.get(i).getQualified().get(0).getCourseCode() + ": ";
                        }
                        else {
                            teacherOutput += teachers.get(i).identifier + ": ";
                        }
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
                    } else {
                        fired.add(teachers.get(i));
                    }
                }
                teacherOutput += "\nTeachers to be Fired: \n";
                for (int i = 0; i < fired.size(); i++) {
                    if(fired.get(i).getIdentifier() != "New Teacher") {
                        teacherOutput += fired.get(i).getIdentifier() + "\n";
                    }
                }

                //track how many New Teachers are added

                teacherOutput += "\nTotal New Teachers: " + totalNewTeachers;
                ww.write(teacherOutput);
                ww.close();
                System.out.println("Score: " + maxScore);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
            PrintWriter xw;
            try {
                fc.setSelectedFile(new File("Big Sections.txt"));
                int returnVal = fc.showSaveDialog(null);
                File dir;
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    dir = fc.getSelectedFile();
                }else{
                    JOptionPane.showMessageDialog(null, "The directory you selected is invalid.");
                    return;
                }
                String path = dir.getAbsolutePath();
                xw = new PrintWriter(new FileWriter(new File(path)));
                String superSectionOutput = "Course, Teacher, Period, Student 1, Student 2, Student 3,...\n";
                for (int i = 0; i < totalPeriods; i++) {
                    for (int j = 0; j < twodschedule.get(i).size(); j++) {
                        superSectionOutput += twodschedule.get(i).get(j).getCourse().getCourseCode() + "," + (twodschedule.get(i).get(j).getTeacher().getIdentifier()) + "," + (twodschedule.get(i).get(j).getPeriod() + 1);
                        for (int k = 0; k < twodschedule.get(i).get(j).getStudents().size(); k++) {
                            superSectionOutput += "," + twodschedule.get(i).get(j).getStudents().get(k).getIdentifier();
                        }
                        superSectionOutput += "\n";
                    }
                    superSectionOutput += "\n";
                }
                xw.write(superSectionOutput);
                xw.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
