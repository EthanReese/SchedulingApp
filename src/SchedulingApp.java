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
        ArrayList<ArrayList<String>> forecastingTable  = readCSV(forecastingFile);
        ArrayList<ArrayList<String>> teacherTable = readCSV(teacherFile);
        ArrayList<ArrayList<String>> courseTable = readCSV(courseFile);

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
            //Make a buffered reader that can read in the csv file
            br = new BufferedReader(new FileReader(filePath));
            while((br.readLine()) != null){
                ArrayList<String> tempList = new ArrayList<String>(Arrays.asList((br.readLine()).split(",")));
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
            if (courseTable.get(i).get(1) == "true") {
                isRequired = true;
            }
            Double credit = Double.parseDouble(courseTable.get(i).get(2));
            Courses course = new Courses(name, isRequired, credit);
            courses.add(course);
        }
    }

    public void teachers(ArrayList<ArrayList<String>> teacherTable) {
        for (int i = 0; i < teacherTable.size(); i++) {
            ArrayList<Courses> qualified = new ArrayList<Courses>();
            for(int j = 1; j < teacherTable.get(i).size(); j++) {
                for (int k = 0; k < courses.size(); k++) {
                    if (courses.get(k).courseCode == teacherTable.get(i).get(j)) {
                        qualified.add(courses.get(i));
                    }
                }
            }
            Teacher teacher = new Teacher(qualified, teacherTable.get(i).toString());
        }
    }

    public void electives(ArrayList<ArrayList<String>> forecastingTable) {
        for (int i = 0; i < forecastingTable.size(); i++) {
            for (int j = 1; j < forecastingTable.get(i).size(); j++) {
                for (int k = 0; k < courses.size(); k++) {
                    if (courses.get(k).courseCode == forecastingTable.get(i).get(j)) {
                        studentsInCourse.add(forecastingTable.get(i).get(0));
                    }
                }
            }
        }
    }

}
