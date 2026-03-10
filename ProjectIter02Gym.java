import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Main class for the Gym Athlete Fitness Tracker program.
 * It reads athlete data from files, stores them in Gym objects,
 * displays summaries, and saves formatted reports to output files.
 */
public class ProjectIter02Gym {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Welcome banner
        System.out.println("**********************************************************");
        System.out.println("*       Welcome to the Gym Athlete Fitness Tracker      *");
        System.out.println("**********************************************************");

        // Prompt the user for number of athletes to track (Gym capacity)
        int MAX_GYM_MEMBERS = getPositiveInt(input, "Enter number of athletes: ");

        // First gym: Elite Fitness (reads from gym1.txt)
        Gym gym1 = new Gym("Elite Fitness", MAX_GYM_MEMBERS);
        try {
            readGymAthletesInfoFromFile("gym1.txt", gym1);
            gym1.displayAthleteSummaries();    // Output summary to console
            gym1.saveReportToFile();           // Save report to file
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
        }

        // Second gym: Work in Progress (reads from gym2.txt)
        Gym gym2 = new Gym("Work in Progress", MAX_GYM_MEMBERS);
        try {
            readGymAthletesInfoFromFile("gym2.txt", gym2);
            gym2.displayAthleteSummaries();
            gym2.saveReportToFile();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
        }

        input.close();
    }

    /**
     * Prompts the user for a positive integer value (validates input).
     */
    public static int getPositiveInt(Scanner input, String prompt) {
        int value;
        do {
            System.out.print(prompt);
            while (!input.hasNextInt()) {
                System.out.print("Invalid input. " + prompt);
                input.next(); // Clear invalid token
            }
            value = input.nextInt();
            if (value <= 0) {
                System.out.println("Value must be greater than 0.");
            }
        } while (value <= 0);
        return value;
    }

    /**
     * Reads athlete data from a file and adds them to the specified Gym object.
     * Each athlete has: name, weight, height, age, and 7 daily calorie values.
     */
    public static void readGymAthletesInfoFromFile(String filename, Gym currentGym) throws FileNotFoundException {
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(new File(filename));
            while (fileScanner.hasNext()) {
                String firstName = fileScanner.next();
                String lastName = fileScanner.next();
                double weight = fileScanner.nextDouble();
                double height = fileScanner.nextDouble();
                int age = fileScanner.nextInt();
                double[] calories = new double[7];
                for (int i = 0; i < 7; i++) {
                    calories[i] = fileScanner.nextDouble();
                }
                Athlete athlete = new Athlete(firstName, lastName, weight, height, age, calories);
                currentGym.addAthlete(athlete); // Adds to gym array if space is available
            }
        } finally 
        {
            if (fileScanner != null) {
                fileScanner.close();
            }
        }
    }
}

/**
 * Athlete class: Represents data and fitness statistics for a single athlete.
 */
class Athlete {
    private String firstName;
    private String lastName;
    private double weight;
    private double height;
    private int age;
    private double[] caloriesPerDay; // Array to store calories burned per day for a week

    // Constructor
    public Athlete(String firstName, String lastName, double weight, double height, int age, double[] caloriesPerDay) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.caloriesPerDay = caloriesPerDay;
    }

    // Returns full name of the athlete
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Calculates max heart rate using standard formula
    public  int getMaxHeartRate() {
        return 220 - age;
    }

    // Calculates average daily calories burned over the 7 days
    public double calculateAverageCaloriesBurned() {
        double total = 0;
        for (int i = 0; i < caloriesPerDay.length; i++) {
            total += caloriesPerDay[i];
        }
        return total / caloriesPerDay.length;
    }

    // Calculates BMI using standard BMI formula for imperial units
    public double calculateBMI() {
        return (703 * weight) / (height * height);
    }

    // Determines BMI category based on value
    public String getBMICategory() {
        double bmi = calculateBMI();
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25) return "Normal";
        else if (bmi < 30) return "Overweight";
        else return "Obese";
    }
}

/**
 * Gym class: Stores and manages a list of athletes, their statistics,
 * and allows generating summaries and reports.
 */
class Gym {
    private String name;
    private Athlete[] athletes;
    private int count; // Number of athletes currently added

    // Constructor initializes gym with a name and maximum number of athletes
    public Gym(String name, int maxAthletes) {
        this.name = name;
        this.athletes = new Athlete[maxAthletes];
        this.count = 0;
    }

    /**
     * Adds an athlete to the gym if there's capacity.
     */
    public void addAthlete(Athlete a) {
        if (count < athletes.length) {
            athletes[count] = a;
            count++;
        } else {
            System.out.println("Gym is full. Can't add " + a.getFullName());
        }
    }
    
    /**
     * Displays all athletes' health summaries, top performer, and underweight list.
     */
    public void displayAthleteSummaries() {
        System.out.println("\n****** Gym Fitness Report ******");
        System.out.println("Gym: " + name);
        for (int i = 0; i < count; i++) {
            Athlete a = athletes[i];
            System.out.println("Athlete: " + a.getFullName());
            System.out.println("\tMax Heart Rate: " + a.getMaxHeartRate() + " bpm");
            System.out.printf("\tAverage Daily Calories Burned: %.1f\n", a.calculateAverageCaloriesBurned());
            double bmi = a.calculateBMI();
            System.out.printf("\tBMI: %.1f \tCategory: %s\n", bmi, a.getBMICategory());
        }

        displayTopAthlete();
        displayUnderweightAthletes();
    }

    /**
     * Identifies and prints the athlete with the highest average calorie burn.
     */
    public void displayTopAthlete() {
        double maxCalories = -1;
        int topIndex = -1;
        for (int i = 0; i < count; i++) {
            double avg = athletes[i].calculateAverageCaloriesBurned();
            if (avg > maxCalories) {
                maxCalories = avg;
                topIndex = i;
            }
        }
        if (topIndex >= 0) {
            System.out.println();
            System.out.println("Top Athlete (Most Calories Burned): " + athletes[topIndex].getFullName());
        }
    }

    /**
     * Displays the list of athletes whose BMI category is "Underweight"
     */
    public void displayUnderweightAthletes() {
        boolean found = false;
        System.out.println("Underweight Athletes:");
        for (int i = 0; i < count; i++) {
            if (athletes[i].getBMICategory().equals("Underweight")) {
                System.out.println(athletes[i].getFullName());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No underweight athletes.");
        }
    }

    /**
     * Saves the fitness report of all athletes in this gym to a text file.
     */
    public void saveReportToFile() throws FileNotFoundException {
        String filename = name.replace(" ", "_") + "_Report.txt";
        PrintWriter writer = new PrintWriter(filename);
        writer.println("****** Gym Fitness Report ******");
        for (int i = 0; i < count; i++) {
            Athlete a = athletes[i];
            writer.println("Athlete: " + a.getFullName());
            writer.println("\tMax Heart Rate: " + a.getMaxHeartRate() + " bpm");
            writer.printf("\tAverage Daily Calories Burned: %.1f\n", a.calculateAverageCaloriesBurned());
            double bmi = a.calculateBMI();
            writer.printf("\tBMI: %.1f \tCategory: %s\n", bmi, a.getBMICategory());
        }
        writer.close();
        System.out.println("Report saved to: " + filename);
    }
}