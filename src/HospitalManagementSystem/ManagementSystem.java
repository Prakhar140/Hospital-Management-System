package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class ManagementSystem {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/hospital";
    private static final String username = "root";
    private static final String password = "123456";


    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);


        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println();
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println();
                System.out.print("Enter Your Choice : ");
                int choice = scanner.nextInt();
                System.out.println();


                switch (choice) {
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctor();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5:
                        exit();
                        return;

                    default:
                        System.out.println("Enter valid choice!!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient ID : ");
        int patientid = scanner.nextInt();
        System.out.print("Enter Doctor ID : ");
        int doctorid = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD) : ");
        String appDate = scanner.next();

        if(patient.getPatientById(patientid) && doctor.getDoctorById(doctorid)){
            if(checkDoctorAvlb(doctorid, appDate, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                try{

                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientid);
                    preparedStatement.setInt(2, doctorid);
                    preparedStatement.setString(3, appDate);
                    int rowAff = preparedStatement.executeUpdate();

                    if(rowAff > 0){
                        System.out.println("Appointment Booked !!");
                    } else {
                        System.out.println("Failed to book appointment !!");
                    }

                } catch (SQLException e){
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date!!!");
            }
        } else {
            System.out.println("Either doctor or patient not exists!!!");
        }
    }


    public static boolean checkDoctorAvlb(int doctorid, String appDate, Connection connection){

        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorid);
            preparedStatement.setString(2, appDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count == 0){
                   return true;
                } else {
                    return false;
                }

            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }


    public static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5;
        while(i != 0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("THANK YOU !! for using Hospital Management System");
    }


}
