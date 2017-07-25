import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Float.parseFloat;

/**
 * Created by Ethan Reese, Aletea VanVeldhuesen, and Josh Bromley on 7/24/17.
 */



public class SchedulingApp {



    BufferedReader br = null;
    Scanner scanner = new Scanner(System.in);
    ArrayList<Courses> courses = new ArrayList<Courses>();
    ArrayList<Teacher> teachers = new ArrayList<Teacher>();
    ArrayList<Student> students = new ArrayList<Student>();
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


        //Call the functions corresponding to each individual file
        ArrayList<ArrayList<String>> forecastingTable = readCSV(forecastingFile);
        ArrayList<ArrayList<String>> teacherTable = readCSV(teacherFile);
        ArrayList<ArrayList<String>> courseTable = readCSV(courseFile);
        classes(courseTable);
        teacherCreation(teacherTable);
        requestedClasses(forecastingTable);
        electives(forecastingTable);


    }

    public static void main(String[] args) {
        new SchedulingApp();
    }

    public ArrayList<ArrayList<String>> readCSV(String filePath) {
        //Make a proper arraylist to return
        ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();
        int counter = 0;
        //Attempt to read in the file
        try{
            String line;
            //Make a buffered reader that can read in the csv file
            br = new BufferedReader(new FileReader(filePath));
            while((line = br.readLine()) != null){
                System.out.println(line);
                ArrayList<String> tempList = new ArrayList<String>(Arrays.asList((line).split(",")));
                returnList.add(counter, tempList);
                counter += 1;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //Todo: Tell the user to input a new forecasting file
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
            if (courseTable.get(i).get(1) == "true") {
                isRequired = true;
            }
            Double credit = Double.parseDouble(courseTable.get(i).get(2));
            Courses course = new Courses(name, isRequired, credit);
            courses.add(course);
        }
    }

    public void teacherCreation(ArrayList<ArrayList<String>> teacherTable) {
        for (int i = 0; i < teacherTable.size(); i++) {
            ArrayList<Courses> qualified = new ArrayList<Courses>();
            for(int j = 1; j < teacherTable.get(i).size(); j++) {
               qualified.add(j,search(courses,teacherTable.get(i).get(j)));
            }
            teachers.add(new Teacher(qualified,teacherTable.get(i).get(0)));
        }
    }

    public void requestedClasses(ArrayList<ArrayList<String>> forecastTable) {
        ArrayList<Courses> request = new ArrayList<Courses>();
        String id = new String();
        for (int i = 0; i < forecastTable.size(); i++) {
            id = forecastTable.get(i).get(0);
            for (int j = 1; j < forecastTable.get(i).size(); j++) {
                request.add(j, search(courses, forecastTable.get(i).get(j)));
            }
        }
        students.add(new Student(request, id));

    }


    public void electives(ArrayList<ArrayList<String>> forecastingTable) {
        for (int i = 0; i < forecastingTable.size(); i++) {
            for (int j = 1; j < forecastingTable.get(i).size(); j++) {
                for (int k = 0; k < courses.size(); k++) {
                    if (courses.get(k).courseCode == forecastingTable.get(i).get(j)) {
                        //studentsInCourse.add(forecastingTable.get(i).get(0));
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

    public Courses search(ArrayList<Courses> courseList, String code ) {
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).courseCode.equals(code)) {
                return courseList.get(i);
            }
        }
        return null;
    }

    //Quicksort method: I chose to store the array outside the function and return nothing because it seemed easier than trying to worry about the recursive returns.
    public static void quickSort(ArrayList<Courses> array, int low, int high) {
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

}
