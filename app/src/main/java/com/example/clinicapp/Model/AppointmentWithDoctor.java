package com.example.clinicapp.Model;

public class AppointmentWithDoctor {
    public Appointment appointment;
    public Doctor doctor;

    public AppointmentWithDoctor(Appointment appointment, Doctor doctor) {
        this.appointment = appointment;
        this.doctor = doctor;
    }
}