package com.sample.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Patient {
    public String id;
    public String name;
    public String surname;
    public String phone;
    public String email;
    private ArrayList<String> medicalConditions;

    public Patient() {}

    public Patient(String id, String name, String surname, String phone, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;

        this.medicalConditions = new ArrayList<>();
//        this.setMedicalConditions(medicalConditions);
    }

    public void setMedicalConditions(String medicalConditions) {
        String[] condtions = medicalConditions.split(",");
        this.medicalConditions.addAll(Arrays.asList(condtions));
    }

    public void setMedicalConditions(Collection<String> medicalConditions) {
        this.medicalConditions.clear();
        this.medicalConditions.addAll(medicalConditions);
    }

    public ArrayList<String> getMedicalConditions() {
        return medicalConditions;
    }

    public void addMedicalConditions(Collection<String> medicalConditions) {
        this.medicalConditions.addAll(medicalConditions);
    }

}
