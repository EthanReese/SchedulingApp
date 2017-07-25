import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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


        //Call the functions corresponding to each individual file
        ArrayList<ArrayList<String>> forecastingTable = readCSV(forecastingFile);
        ArrayList<ArrayList<String>> teacherTable = readCSV(teacherFile);
        ArrayList<ArrayList<String>> courseTable = readCSV(courseFile);
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

    public void requestedClasses(ArrayList<ArrayList<String>> forecastTable, ArrayList<Courses> courses) {
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

    public Courses search(ArrayList<Courses> courseList, String code ) {
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).courseCode.equals(code)) {
                return courseList.get(i);
            }
        }
        return null;
    }

}
