package com.spas.crss.config;

import com.spas.crss.dao.LecturerSpecializationDAO;
import com.spas.crss.model.TeachingSpecialization;

public class TeachingSpecializationTest {

    public static void main(String[] args) {

        LecturerSpecializationDAO dao =
                new LecturerSpecializationDAO();

        TeachingSpecialization specialization =
                new TeachingSpecialization(
                        0,
                        "Data Science",
                        "Data analysis, machine learning and statistical modelling"
                );

        int id = dao.addSpecialization(specialization);

        if (id != -1) {
            System.out.println("Specialization added successfully!");
            System.out.println("Specialization ID: " + id);
        } else {
            System.out.println("Failed to add specialization.");
        }

        System.out.println("\nAVAILABLE SPECIALIZATIONS");

        for (TeachingSpecialization item :
                dao.getAllSpecializations()) {

            System.out.println(item);
        }
    }
}
