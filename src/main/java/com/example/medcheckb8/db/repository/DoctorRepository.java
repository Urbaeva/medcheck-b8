package com.example.medcheckb8.db.repository;

import com.example.medcheckb8.db.dto.response.DoctorResponse;
import com.example.medcheckb8.db.dto.response.ExpertResponse;
import com.example.medcheckb8.db.dto.response.OurDoctorsResponse;
import com.example.medcheckb8.db.dto.response.SearchResponse;
import com.example.medcheckb8.db.entities.Department;
import com.example.medcheckb8.db.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("select new com.example.medcheckb8.db.dto.response.DoctorResponse(d.id, d.firstName, d.lastName, d.position," +
            " d.image, d.description, cast(d.department.name as string),d.department.id) " +
            "from Doctor d")
    List<DoctorResponse> getAll();
    @Query("select new com.example.medcheckb8.db.dto.response.ExpertResponse(d.id,d.isActive, d.firstName, d.lastName, d.position," +
            " d.image, cast(d.department.name as string),s.dataOfFinish) " +
            "from Doctor d left join Schedule s on d.id = s.doctor.id where :keyWord = null or d.firstName ilike concat('%',:keyWord,'%') or d.lastName ilike concat('%',:keyWord,'%') or" +
            " cast(d.department.name as STRING ) ilike concat('%',:keyWord,'%') order by d.id desc")
    List<ExpertResponse> getAllWithSearch(String keyWord);

    @Query("select new com.example.medcheckb8.db.dto.response.DoctorResponse(d.id, d.firstName, d.lastName, d.position," +
            " d.image, d.description, cast(d.department.name as string),d.department.id) " +
            "from Doctor d where d.id=?1")
    Optional<DoctorResponse> findByDoctorId(Long id);

    @Query("select d from Doctor d where lower(d.department.name) = lower(?1) and d.schedule != null")
    List<Doctor> findByDepartmentName(String department);

    @Query("select new com.example.medcheckb8.db.dto.response.SearchResponse(d.id, p.id, d.firstName, d.lastName, d.position, lower(p.name), d.image) from Doctor d join d.department p" +
            " where  d.firstName ilike %:word% or d.lastName ilike %:word% or lower(p.name) ilike lower(concat('%', :word, '%'))")
    List<SearchResponse> globalSearch(String word);

    Boolean existsDoctorByDepartmentAndId(Department department, Long id);
    @Query("select new com.example.medcheckb8.db.dto.response.OurDoctorsResponse(d.id,d.firstName,d.lastName,d.position,d.image,cast(d.department.name as string )) " +
            "from Doctor d where cast(d.department.name as STRING) = ?1 ")
    List<OurDoctorsResponse> findDoctorByDepartmentName(String name);
}