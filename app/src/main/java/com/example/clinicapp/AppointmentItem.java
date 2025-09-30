package com.example.clinicapp;

import com.example.clinicapp.Model.Appointment;
import com.example.clinicapp.Model.Doctor;

public class AppointmentItem {
    public Appointment appointment;
    public Doctor doctor;

    public AppointmentItem(Appointment appointment, Doctor doctor) {
        this.appointment = appointment;
        this.doctor = doctor;
    }
}
