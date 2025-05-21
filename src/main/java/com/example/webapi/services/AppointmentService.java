package com.example.webapi.services;

import java.util.List;
import java.util.Date;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webapi.models.entities.Appointment;
import com.example.webapi.models.entities.AppointmentServices;
import com.example.webapi.models.entities.ServiceEntity;
import com.example.webapi.models.dto.AddServicesRequest;
import com.example.webapi.models.dto.AppointmentRequest;
import com.example.webapi.models.dto.CancelAppointmentRequest;
import com.example.webapi.repositories.AppointmentRepository;
import com.example.webapi.repositories.ServiceRepository;
import com.example.webapi.repositories.AppointmentServiceRepository;
@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired 
    private PatientService patientService;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private AppointmentServiceRepository appointmentServiceRepository;

    private static final int MAX_DAYS_AHEAD = 30;
    private static final int MINUTES_THRESHOLD = 30; // Minimum minutes between appointments for same patient
    private static final int MINUTES_BEFORE_APPOINTMENT = 30; // Minimum minutes before appointment to cancel
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
    }

    public Appointment createAppointment(AppointmentRequest request) {
        // Validate appointment date
        Date appointmentDate = request.getAppointmentDate();
        Date now = new Date();
        
        // Check if appointment is in the past
        if (appointmentDate.before(now)) {
            throw new RuntimeException("Cannot book appointments in the past");
        }
        
        // Check if appointment is within allowed time range
        long daysBetween = ChronoUnit.DAYS.between(now.toInstant(), appointmentDate.toInstant());
        if (daysBetween > MAX_DAYS_AHEAD) {
            throw new RuntimeException("Appointments can only be booked up to " + MAX_DAYS_AHEAD + " days in advance");
        }

        // Check for existing non-canceled appointment with same doctor at same time
        boolean hasDoctorAppointment = appointmentRepository.existsByStaffIdAndAppointmentTimeAndStatusNot(
            request.getDoctorId(), 
            appointmentDate,
            "canceled"
        );
        if (hasDoctorAppointment) {
            throw new RuntimeException("Doctor already has an appointment at this time");
        }

        // Check for existing non-canceled appointment with same patient at nearby time
        Date startTime = new Date(appointmentDate.getTime() - (MINUTES_THRESHOLD * 60 * 1000));
        Date endTime = new Date(appointmentDate.getTime() + (MINUTES_THRESHOLD * 60 * 1000));
        boolean hasPatientAppointment = appointmentRepository.existsByCustomerIdAndAppointmentTimeBetweenAndStatusNot(
            request.getPatientId(),
            startTime,
            endTime,
            "canceled"
        );
        if (hasPatientAppointment) {
            throw new RuntimeException("Patient already has an appointment within " + MINUTES_THRESHOLD + " minutes of this time");
        }

        // Create new appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patientService.getPatientById(request.getPatientId()));
        appointment.setDoctor(doctorService.findById(request.getDoctorId()));
        appointment.setAppointmentDate(appointmentDate);
        appointment.setReason(request.getReason());
        appointment.setStatus("pending");
        // appointment.setBaseFee(request.getBaseFee());
        appointment.setBaseFee(100000);
        appointment.setTotalFee(100000);
        appointment.setCreatedAt(new Date());
        appointment.setUpdatedAt(new Date());
        
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = getAppointmentById(id);
        
        // Validate appointment date for updates as well if provided
        if (request.getAppointmentDate() != null) {
            Date appointmentDate = request.getAppointmentDate();
            Date now = new Date();
            
            // if (appointmentDate.before(now)) {
            //     throw new RuntimeException("Cannot update appointment to a past time");
            // }
            
            long daysBetween = ChronoUnit.DAYS.between(now.toInstant(), appointmentDate.toInstant());
            if (daysBetween > MAX_DAYS_AHEAD) {
                throw new RuntimeException("Appointments can only be scheduled up to " + MAX_DAYS_AHEAD + " days in advance");
            }

            // Check for existing appointment with same doctor at same time (excluding current appointment)
            boolean hasDoctorAppointment = appointmentRepository.existsByStaffIdAndAppointmentTimeAndIdNot(
                request.getDoctorId(), 
                appointmentDate,
                id
            );
            if (hasDoctorAppointment) {
                throw new RuntimeException("Doctor already has an appointment at this time");
            }

            // Check for existing appointment with same patient at nearby time (excluding current appointment)
            Date startTime = new Date(appointmentDate.getTime() - (MINUTES_THRESHOLD * 60 * 1000));
            Date endTime = new Date(appointmentDate.getTime() + (MINUTES_THRESHOLD * 60 * 1000));
            boolean hasPatientAppointment = appointmentRepository.existsByCustomerIdAndAppointmentTimeBetweenAndIdNot(
                request.getPatientId(),
                startTime,
                endTime,
                id
            );
            if (hasPatientAppointment) {
                throw new RuntimeException("Patient already has an appointment within " + MINUTES_THRESHOLD + " minutes of this time");
            }

            appointment.setAppointmentDate(appointmentDate);
        }

        // Only update fields that are provided in the request
        if (request.getPatientId() != null) {
            appointment.setPatient(patientService.getPatientById(request.getPatientId()));
        }
        if (request.getDoctorId() != null) {
            appointment.setDoctor(doctorService.findById(request.getDoctorId()));
        }
        if (request.getReason() != null) {
            appointment.setReason(request.getReason());
        }
        if (request.getStatus() != null) {
            appointment.setStatus(request.getStatus());
        }
        if (request.getBaseFee() != 0) { // Assuming 0 is not a valid fee
            appointment.setBaseFee(request.getBaseFee());
        }

        // Handle service updates if provided
        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            List<ServiceEntity> requestedServices = serviceRepository.findAllById(request.getServiceIds());
            
            if (requestedServices.isEmpty()) {
                throw new RuntimeException("Không tìm thấy dịch vụ nào từ danh sách ID.");
            }
            
            // Xóa tất cả dịch vụ hiện có
            appointment.getServices().clear();
            
            // Tạo danh sách dịch vụ mới
            List<AppointmentServices> newServices = requestedServices.stream()
                .map(service -> AppointmentServices.builder()
                    .appointment(appointment)
                    .service(service)
                    .build())
                .toList();
            
            // Thêm danh sách dịch vụ mới
            appointment.getServices().addAll(newServices);

            // Calculate total fee (base fee + sum of all service fees)
            double totalServiceFees = appointment.getServices().stream()
                .mapToDouble(as -> as.getService().getPrice())
                .sum();
            appointment.setTotalFee(appointment.getBaseFee() + totalServiceFees);
        }
        else{
            appointment.setTotalFee(appointment.getBaseFee());
        }

        appointment.setUpdatedAt(new Date());
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointmentRepository.delete(appointment);
    }

    public void cancelAppointment(Long id, CancelAppointmentRequest request) {
        Appointment appointment = getAppointmentById(id);
        
        // Kiểm tra trạng thái hiện tại
        String currentStatus = appointment.getStatus();
        if ("completed".equals(currentStatus) || "canceled".equals(currentStatus)) {
            throw new RuntimeException("Cannot cancel an appointment that is already " + currentStatus);
        }

        // Kiểm tra thời gian huỷ
        Date now = new Date();
        Date appointmentTime = appointment.getAppointmentDate();
        long minutesUntilAppointment = ChronoUnit.MINUTES.between(now.toInstant(), appointmentTime.toInstant());
        
        if (minutesUntilAppointment <= MINUTES_BEFORE_APPOINTMENT) {
            throw new RuntimeException("Cannot cancel appointment less than " + MINUTES_BEFORE_APPOINTMENT + " minutes before the scheduled time");
        }

        // Cập nhật trạng thái và lý do huỷ
        appointment.setStatus("canceled");
        appointment.setCancelReason(request.getCancelReason());
        appointment.setCancelBy(request.getCancelBy());
        appointment.setUpdatedAt(now);
        
        appointmentRepository.save(appointment);
    }

    // public void addServicesToAppointment(Long appointmentId, AddServicesRequest request) {
    //     Appointment appointment = getAppointmentById(appointmentId);
        
    //     // Lấy danh sách dịch vụ từ request
    //     List<ServiceEntity> services = serviceRepository.findAllById(request.getServiceIds());
        
    //     // Kiểm tra nếu không tìm thấy dịch vụ
    //     if (services.isEmpty()) {
    //         throw new RuntimeException("No services found with the provided IDs");
    //     }

    //     // Tính tổng tiền dịch vụ
    //     double totalServiceFee = services.stream()
    //             .mapToDouble(ServiceEntity::getPrice)
    //             .sum();

    //     // Cập nhật thông tin lịch khám
    //     appointment.setServices(services);
    //     appointment.setServiceFee(totalServiceFee);
    //     appointment.setTotalFee(appointment.getBaseFee() + totalServiceFee);
    //     appointment.setUpdatedAt(new Date());

    //     // Lưu thông tin lịch khám
    //     appointmentRepository.save(appointment);
    // }

    // public void addServicesToAppointment1(Long appointmentId, AddServicesRequest request) {
    //     Appointment appointment = getAppointmentById(appointmentId);
        
    //     List<ServiceEntity> services = serviceRepository.findAllById(request.getServiceIds());
        
    //     if (services.isEmpty()) {
    //         throw new RuntimeException("Không tìm thấy dịch vụ nào từ danh sách ID.");
    //     }
    // }


    // public void addServicesToAppointment(Long appointmentId, AddServicesRequest request) {
    //     Appointment appointment = getAppointmentById(appointmentId);
        
    //     // Lấy danh sách dịch vụ từ request
    //     List<ServiceEntity> services = serviceRepository.findAllById(request.getServiceIds());
        
    //     // Kiểm tra nếu không tìm thấy dịch vụ
    //     if (services.isEmpty()) {
    //         throw new RuntimeException("Không tìm thấy dịch vụ nào từ danh sách ID.");
    //     }

    //     // Tạo danh sách AppointmentServices
    //     List<AppointmentServices> appointmentServices = services.stream()
    //         .map(service -> {
    //             AppointmentServices as = new AppointmentServices();
    //             as.setAppointment(appointment);
    //             as.setService(service);
    //             return as;
    //         })
    //         .toList();

    //     // Tính tổng tiền dịch vụ
    //     double totalServiceFee = services.stream()
    //         .mapToDouble(ServiceEntity::getPrice)
    //         .sum();

    //     appointmentServiceRepository.saveAll(appointmentServices);
    //     // Cập nhật thông tin lịch khám
    //     appointment.setServices(appointmentServices);
    //     appointment.setUpdatedAt(new Date());

    //     // Lưu thông tin lịch khám
    //     appointmentRepository.save(appointment);
    // }

    public void addServicesToAppointment(Long appointmentId, AddServicesRequest request) {
        Appointment appointment = getAppointmentById(appointmentId);
    
        // Lấy danh sách dịch vụ từ request
        List<ServiceEntity> requestedServices = serviceRepository.findAllById(request.getServiceIds());
    
        if (requestedServices.isEmpty()) {
            throw new RuntimeException("Không tìm thấy dịch vụ nào từ danh sách ID.");
        }
    
        // Danh sách dịch vụ hiện có trong lịch khám
        List<AppointmentServices> currentServices = appointment.getServices();
    
        // Lọc ra các dịch vụ chưa tồn tại để thêm mới
        List<AppointmentServices> newServices = requestedServices.stream()
            .filter(service -> currentServices.stream()
                .noneMatch(as -> as.getService().getId().equals(service.getId())))
            .map(service -> AppointmentServices.builder()
                .appointment(appointment)
                .service(service)
                .build())
            .toList();
    
        // Nếu không có dịch vụ nào mới thì không cần làm gì thêm
        if (newServices.isEmpty()) {
            throw new RuntimeException("Tất cả các dịch vụ đã tồn tại trong lịch khám.");
        }
    
        // Thêm dịch vụ mới vào danh sách hiện tại
        currentServices.addAll(newServices);
    
        // Lưu các bản ghi mới vào bảng appointment_services
        // appointmentServiceRepository.saveAll(newServices);
    
        // Cập nhật thời gian và lưu lại lịch khám
        appointment.setUpdatedAt(new Date());
        appointmentRepository.save(appointment);
    }
    
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
    
    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
}