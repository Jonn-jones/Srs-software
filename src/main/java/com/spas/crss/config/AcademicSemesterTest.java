package com.spas.crss.config;

import com.spas.crss.dao.AcademicSemesterDAO;
import com.spas.crss.model.AcademicSemester;

public class AcademicSemesterTest {

    public static void main(String[] args) {

        AcademicSemesterDAO dao = new AcademicSemesterDAO();

        System.out.println("================================");
        System.out.println("ACADEMIC SEMESTER TEST");
        System.out.println("================================");

        for (AcademicSemester semester : dao.getAllSemester()) {

            System.out.println("Semester ID: " + semester.getSemesterId());
            System.out.println("Academic Year: " + semester.getAcademicYear());
            System.out.println("Semester Name: " + semester.getSemesterName());
            System.out.println("Start Date: " + semester.getStartDate());
            System.out.println("End Date: " + semester.getEndDate());
            System.out.println("Status: " + semester.getSemesterStatus());

            System.out.println("--------------------------------");
        }
    }
}
