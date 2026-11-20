package com.spas.crss.config;

import com.spas.crss.dao.AcademicSemesterDAO;
import com.spas.crss.model.AcademicSemester;

import java.sql.Date;
import java.util.Scanner;

public class AcademicSemesterAddTest {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        AcademicSemesterDAO dao = new AcademicSemesterDAO();

        System.out.println("===== ADD ACADEMIC SEMESTER =====");

        System.out.print("Academic Year: ");
        String academicYear = scanner.nextLine();

        System.out.print("Semester Name: ");
        String semesterName = scanner.nextLine();

        System.out.print("Start Date (YYYY-MM-DD): ");
        Date startDate = Date.valueOf(scanner.nextLine());

        System.out.print("End Date (YYYY-MM-DD): ");
        Date endDate = Date.valueOf(scanner.nextLine());

        System.out.print("Semester Status: ");
        String semesterStatus = scanner.nextLine();

        AcademicSemester semester = new AcademicSemester(
                0,
                academicYear,
                semesterName,
                startDate,
                endDate,
                semesterStatus
        );

        int semesterId = dao.addSemester(semester);

        if (semesterId != -1) {
            System.out.println("Semester added successfully!");
            System.out.println("Semester ID: " + semesterId);
        } else {
            System.out.println("Failed to add semester.");
        }

        scanner.close();
    }
}