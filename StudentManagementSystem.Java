
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class StudentManagementSystem {

    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Student> students = new ArrayList<>();
    private static final String FILE_NAME = "data/students.txt";

    public static void main(String[] args) {

        loadStudents();

        int choice;

        do {
            System.out.println("\n==================================");
            System.out.println("     STUDENT MANAGEMENT SYSTEM");
            System.out.println("==================================");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Exit");
            System.out.println("==================================");

            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    addStudent();
                    break;

                case 2:
                    viewStudents();
                    break;

                case 3:
                    searchStudent();
                    break;

                case 4:
                    updateStudent();
                    break;

                case 5:
                    deleteStudent();
                    break;

                case 6:
                    saveStudents();
                    System.out.println("Thank you for using the system!");
                    break;

                default:
                    System.out.println("Invalid choice! Try again.");
            }

        } while (choice != 6);

        sc.close();
    }

    // Read integer safely
    private static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Read marks safely
    private static double readMarks(String message) {
        while (true) {
            try {
                System.out.print(message);
                double marks = Double.parseDouble(sc.nextLine().trim());

                if (marks >= 0 && marks <= 100) {
                    return marks;
                }

                System.out.println("Marks must be between 0 and 100.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter valid marks.");
            }
        }
    }

    // Find student by ID
    private static Student findStudent(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }

    // Add student
    private static void addStudent() {

        System.out.println("\n--- ADD STUDENT ---");

        int id = readInt("Enter Student ID: ");

        if (findStudent(id) != null) {
            System.out.println("Student ID already exists!");
            return;
        }

        System.out.print("Enter Student Name: ");
        String name = sc.nextLine().trim();

        if (!isValidText(name)) {
            System.out.println("Invalid name!");
            return;
        }

        int age = readInt("Enter Age: ");

        if (age <= 0 || age > 120) {
            System.out.println("Please enter a valid age.");
            return;
        }

        System.out.print("Enter Course: ");
        String course = sc.nextLine().trim();

        if (!isValidText(course)) {
            System.out.println("Invalid course!");
            return;
        }

        double marks = readMarks("Enter Marks (0-100): ");

        Student student = new Student(id, name, age, course, marks);

        students.add(student);
        saveStudents();

        System.out.println("Student added successfully!");
    }

    // View all students
    private static void viewStudents() {

        System.out.println("\n--- ALL STUDENTS ---");

        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }

        for (Student student : students) {
            student.display();
        }

        System.out.println("Total Students: " + students.size());
    }

    // Search student
    private static void searchStudent() {

        System.out.println("\n--- SEARCH STUDENT ---");

        int id = readInt("Enter Student ID: ");

        Student student = findStudent(id);

        if (student != null) {
            student.display();
        } else {
            System.out.println("Student not found!");
        }
    }

    // Update student
    private static void updateStudent() {

        System.out.println("\n--- UPDATE STUDENT ---");

        int id = readInt("Enter Student ID: ");

        Student student = findStudent(id);

        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

        System.out.println("Enter new details:");

        System.out.print("Enter New Name: ");
        String name = sc.nextLine().trim();

        if (!isValidText(name)) {
            System.out.println("Invalid name!");
            return;
        }

        int age = readInt("Enter New Age: ");

        if (age <= 0 || age > 120) {
            System.out.println("Invalid age!");
            return;
        }

        System.out.print("Enter New Course: ");
        String course = sc.nextLine().trim();

        if (!isValidText(course)) {
            System.out.println("Invalid course!");
            return;
        }

        double marks = readMarks("Enter New Marks: ");

        student.setName(name);
        student.setAge(age);
        student.setCourse(course);
        student.setMarks(marks);

        saveStudents();

        System.out.println("Student updated successfully!");
    }

    // Delete student
    private static void deleteStudent() {

        System.out.println("\n--- DELETE STUDENT ---");

        int id = readInt("Enter Student ID: ");

        Student student = findStudent(id);

        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

        student.display();

        System.out.print("Are you sure you want to delete? (yes/no): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes")) {
            students.remove(student);
            saveStudents();
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    // Validate text and file delimiter
    private static boolean isValidText(String value) {
        return value != null && !value.isEmpty()
                && !value.contains("|")
                && !value.contains("\n")
                && !value.contains("\r");
    }

    // Save records into file
    private static void saveStudents() {

        File file = new File(FILE_NAME);
        File parent = file.getParentFile();

        try {
            if (parent != null) {
                parent.mkdirs();
            }

            try (BufferedWriter writer =
                    new BufferedWriter(new FileWriter(file))) {

                for (Student student : students) {
                    writer.write(student.toFileString());
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            System.out.println("Error saving student records: "
                    + e.getMessage());
        }
    }

    // Load records from file
    private static void loadStudents() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                try {
                    String[] data = line.split("\\|", -1);

                    if (data.length != 5) {
                        continue;
                    }

                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    int age = Integer.parseInt(data[2]);
                    String course = data[3];
                    double marks = Double.parseDouble(data[4]);

                    if (id <= 0 || age <= 0 || age > 120
                            || !isValidText(name)
                            || !isValidText(course)
                            || marks < 0 || marks > 100
                            || findStudent(id) != null) {
                        continue;
                    }

                    students.add(
                        new Student(id, name, age, course, marks)
                    );

                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid student record.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading records: "
                    + e.getMessage());
        }
    }
}
