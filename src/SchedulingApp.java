import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Created by Ethan Reese, Aletea VanVeldhuesen, and Josh Bromley on 7/24/17.
 */



public class SchedulingApp {

    BufferedReader br = null;
    public SchedulingApp(){




    }

    public static void main(String[] args){
        new SchedulingApp();
    }

    public void readForecasting(String filePath){
        //Attempt to read in the file
        try{
            br = new BufferedReader(new FileReader(filePath));
        }catch(FileNotFoundException e){
            e.printStackTrace();
            //Todo: Tell the user to input a new forecasting file
        }
    }

}
