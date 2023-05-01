package com.example.medcheckb8.db.service.impl;

import com.example.medcheckb8.db.dto.request.DoctorSaveRequest;
import com.example.medcheckb8.db.dto.request.DoctorUpdateRequest;
import com.example.medcheckb8.db.dto.response.DoctorResponse;
import com.example.medcheckb8.db.dto.response.ScheduleDateAndTimeResponse;
import com.example.medcheckb8.db.dto.response.ScheduleResponse;
import com.example.medcheckb8.db.dto.response.SimpleResponse;
import com.example.medcheckb8.db.entities.Department;
import com.example.medcheckb8.db.entities.Doctor;
import com.example.medcheckb8.db.entities.ScheduleDateAndTime;
import com.example.medcheckb8.db.exceptions.NotFountException;
import com.example.medcheckb8.db.repository.DepartmentRepository;
import com.example.medcheckb8.db.repository.DoctorRepository;
import com.example.medcheckb8.db.repository.ScheduleDateAndTimeRepository;
import com.example.medcheckb8.db.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final ScheduleDateAndTimeRepository scheduleDateAndTimeRepository;

    @Override
    public SimpleResponse save(DoctorSaveRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new NotFountException(
                        String.format("Department with id: %d not found", request.departmentId())
                ));

        Doctor doctor = Doctor.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .position(request.position())
                .image(request.image())
                .department(department)
                .description(request.description())
                .isActive(false)
                .build();

        department.addDoctor(doctor);
        doctorRepository.save(doctor);

        return SimpleResponse.builder()
                .status(HttpStatus.OK)
                .message(String.format("Doctor with full name: %s %s Successfully saved",
                        doctor.getFirstName(), doctor.getLastName()))
                .build();
    }

    @Override
    public DoctorResponse findById(Long id) {
        return doctorRepository.findByDoctorId(id).orElseThrow(() -> new NotFountException(
                String.format("Doctor with id: %d doesn't exist", id)
        ));
    }

    @Override
    public List<DoctorResponse> getAll() {
        return doctorRepository.getAll();
    }

    @Override
    public SimpleResponse update(DoctorUpdateRequest request) {
        Doctor doctor = doctorRepository.findById(request.doctorId()).orElseThrow(() -> new NotFountException(
                String.format("Doctor with id: %d not found.", request.doctorId())
        ));

        doctor.setFirstName(request.firstName());
        doctor.setLastName(request.lastName());
        doctor.setImage(request.image());
        doctor.setDescription(request.description());

        doctorRepository.save(doctor);
        return SimpleResponse.builder()
                .status(HttpStatus.OK)
                .message(String.format("Doctor with id: %d Successfully updated.", doctor.getId()))
                .build();
    }

    @Override
    public SimpleResponse delete(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new NotFountException(String.format("Doctor with id: %d doesnt exist.", id));
        }
        doctorRepository.deleteById(id);
        return SimpleResponse.builder()
                .status(HttpStatus.OK)
                .message(String.format("Doctor with id: %d Successfully deleted.", id))
                .build();
    }

    @Override
    public SimpleResponse activateAndDeactivateDoctor(Boolean isActive, Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new NotFountException(
                String.format("Doctor with id: %d not found.", doctorId)
        ));

        doctor.setIsActive(isActive);
        doctorRepository.save(doctor);

        if (!isActive) {
            return SimpleResponse.builder()
                    .status(HttpStatus.OK)
                    .message(String.format("Doctor with id: %s is deactivated!", doctor.getId()))
                    .build();
        }
        return SimpleResponse.builder()
                .status(HttpStatus.OK)
                .message(String.format("Doctor with id: %s is activated!", doctor.getId()))
                .build();
    }

    @Override
    public List<ScheduleResponse> findDoctorByDate(String department, ZonedDateTime zonedDateTime) {
        LocalDateTime now = ZonedDateTime.of(zonedDateTime.toLocalDate(), zonedDateTime.toLocalTime(), zonedDateTime.getZone()).toLocalDateTime();
        List<ScheduleResponse> responses = new ArrayList<>();
        List<Doctor> doctors = doctorRepository.findByDepartmentName(department);
        for (Doctor doctor : doctors) {
            List<ScheduleDateAndTime> availableDates =
                    scheduleDateAndTimeRepository.findAvailableDatesByDepartmentAndDate(doctor.getId());
            if (!availableDates.isEmpty()) {
                List<ScheduleDateAndTimeResponse> scheduleDateAndTimeResponses = new ArrayList<>();
                LocalDateTime closestDate = LocalDateTime.of(availableDates.get(availableDates.size() - 1).getDate(), now.toLocalTime());
                for (ScheduleDateAndTime date : availableDates) {
                    if (Duration.between(now, date.getDate().atTime(date.getTimeFrom())).toDays() <= Duration.between(now, closestDate).toDays()) {
                        if (now.toLocalTime().getHour() < date.getTimeFrom().getHour()) {
                            closestDate = LocalDateTime.of(date.getDate(), now.toLocalTime());
                            ScheduleDateAndTimeResponse timeResponse = ScheduleDateAndTimeResponse.builder()
                                    .id(date.getId())
                                    .date(date.getDate())
                                    .timeFrom(date.getTimeFrom())
                                    .timeTo(date.getTimeTo())
                                    .isBusy(date.getIsBusy())
                                    .build();
                            scheduleDateAndTimeResponses.add(timeResponse);
                        }
                    }else {
                        closestDate = LocalDateTime.of(availableDates.get(availableDates.size() - 1).getDate(), now.toLocalTime());
                    }
                }
                ScheduleResponse build = ScheduleResponse.builder()
                        .id(doctor.getId())
                        .fullName(doctor.getLastName() + " " + doctor.getFirstName())
                        .image(doctor.getImage())
                        .position(doctor.getPosition())
                        .localDateTimes(scheduleDateAndTimeResponses)
                        .build();
                responses.add(build);
            }
        }
        if (responses.isEmpty()) {
            throw new NotFountException("There are no free doctors in this department!");
        }
        return responses;
    }
}
