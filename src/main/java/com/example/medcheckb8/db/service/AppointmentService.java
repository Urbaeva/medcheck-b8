package com.example.medcheckb8.db.service;

import com.example.medcheckb8.db.dto.request.appointment.AddAppointmentRequest;
import com.example.medcheckb8.db.dto.response.AppointmentResponse;
import com.example.medcheckb8.db.dto.response.AppointmentResponseId;
import com.example.medcheckb8.db.dto.response.appointment.AddAppointmentResponse;
import com.example.medcheckb8.db.dto.response.appointment.GetAllAppointmentResponse;
import com.example.medcheckb8.db.dto.response.SimpleResponse;

import java.util.List;

public interface AppointmentService {
    AddAppointmentResponse save(AddAppointmentRequest request);

    List<GetAllAppointmentResponse> getAll(String keyWord);

    SimpleResponse canceled(Long id);

    SimpleResponse delete(List<Long> appointments);
    
    List<AppointmentResponse> getUserAppointments();

    AppointmentResponseId getUserAppointmentById(Long id);

}
