package com.custard.ehr.vitals.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import com.custard.ehr.shared.exception.BusinessException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "vitals",
        indexes = {
                @Index(name = "idx_vitals_encounter", columnList = "encounterId"),
                @Index(name = "idx_vitals_patient", columnList = "patientId"),
                @Index(name = "idx_vitals_recorded_at", columnList = "recordedAt")
        }
)
public class Vitals extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private UUID encounterId;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private UUID recordedBy;

    @Column(nullable = false)
    private Instant recordedAt;

    private Integer systolic;

    private Integer diastolic;

    private BigDecimal weightKg;

    private BigDecimal heightCm;

    private BigDecimal bmi;

    private BigDecimal temperatureCelsius;

    private Integer pulseRate;

    private Integer respiratoryRate;

    private Integer oxygenSaturation;

    @Column(length = 1000)
    private String notes;

    protected Vitals() {
    }

    public Vitals(
            UUID encounterId,
            UUID patientId,
            UUID recordedBy,
            Integer systolic,
            Integer diastolic,
            BigDecimal weightKg,
            BigDecimal heightCm,
            BigDecimal temperatureCelsius,
            Integer pulseRate,
            Integer respiratoryRate,
            Integer oxygenSaturation,
            String notes
    ) {
        validateBloodPressure(systolic, diastolic);
        validateWeightAndHeight(weightKg, heightCm);
        validateOptionalRange("Temperature", temperatureCelsius, BigDecimal.valueOf(30), BigDecimal.valueOf(45));
        validateOptionalRange("Pulse rate", pulseRate, 20, 250);
        validateOptionalRange("Respiratory rate", respiratoryRate, 5, 80);
        validateOptionalRange("Oxygen saturation", oxygenSaturation, 50, 100);

        this.encounterId = encounterId;
        this.patientId = patientId;
        this.recordedBy = recordedBy;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.bmi = calculateBmi(weightKg, heightCm);
        this.temperatureCelsius = temperatureCelsius;
        this.pulseRate = pulseRate;
        this.respiratoryRate = respiratoryRate;
        this.oxygenSaturation = oxygenSaturation;
        this.notes = notes;
        this.recordedAt = Instant.now();
    }

    public Vitals(
            UUID encounterId,
            Integer systolic,
            Integer diastolic,
            BigDecimal weightKg,
            BigDecimal heightCm,
            BigDecimal temperatureCelsius,
            Integer pulseRate,
            Integer respiratoryRate,
            Integer oxygenSaturation,
            String notes
    ) {
        validateBloodPressure(systolic, diastolic);
        validateWeightAndHeight(weightKg, heightCm);
        validateOptionalRange("Temperature", temperatureCelsius, BigDecimal.valueOf(30), BigDecimal.valueOf(45));
        validateOptionalRange("Pulse rate", pulseRate, 20, 250);
        validateOptionalRange("Respiratory rate", respiratoryRate, 5, 80);
        validateOptionalRange("Oxygen saturation", oxygenSaturation, 50, 100);

        this.encounterId = encounterId;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.bmi = calculateBmi(weightKg, heightCm);
        this.temperatureCelsius = temperatureCelsius;
        this.pulseRate = pulseRate;
        this.respiratoryRate = respiratoryRate;
        this.oxygenSaturation = oxygenSaturation;
        this.notes = notes;
        this.recordedAt = Instant.now();
    }

    private void validateBloodPressure(Integer systolic, Integer diastolic) {
        if (systolic == null && diastolic == null) {
            return;
        }

        if (systolic == null || diastolic == null) {
            throw new BusinessException("Both systolic and diastolic values are required for blood pressure");
        }

        validateOptionalRange("Systolic pressure", systolic, 50, 300);
        validateOptionalRange("Diastolic pressure", diastolic, 30, 200);

        if (diastolic >= systolic) {
            throw new BusinessException("Diastolic pressure cannot be greater than or equal to systolic pressure");
        }
    }

    private void validateWeightAndHeight(BigDecimal weightKg, BigDecimal heightCm) {
        validateOptionalRange("Weight", weightKg, BigDecimal.valueOf(1), BigDecimal.valueOf(500));
        validateOptionalRange("Height", heightCm, BigDecimal.valueOf(30), BigDecimal.valueOf(250));
    }

    private void validateOptionalRange(String field, Integer value, int min, int max) {
        if (value == null) {
            return;
        }

        if (value < min || value > max) {
            throw new BusinessException(field + " must be between " + min + " and " + max);
        }
    }

    private void validateOptionalRange(String field, BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value == null) {
            return;
        }

        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw new BusinessException(field + " must be between " + min + " and " + max);
        }
    }

    private BigDecimal calculateBmi(BigDecimal weightKg, BigDecimal heightCm) {
        if (weightKg == null || heightCm == null) {
            return null;
        }

        BigDecimal heightMeters = heightCm.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        return weightKg
                .divide(heightMeters.multiply(heightMeters), 2, RoundingMode.HALF_UP);
    }

    public UUID getId() {
        return id;
    }

    public UUID getEncounterId() {
        return encounterId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getRecordedBy() {
        return recordedBy;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public Integer getSystolic() {
        return systolic;
    }

    public Integer getDiastolic() {
        return diastolic;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public BigDecimal getBmi() {
        return bmi;
    }

    public BigDecimal getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public Integer getPulseRate() {
        return pulseRate;
    }

    public Integer getRespiratoryRate() {
        return respiratoryRate;
    }

    public Integer getOxygenSaturation() {
        return oxygenSaturation;
    }

    public String getNotes() {
        return notes;
    }

    public void setRecordedBy(UUID recordedBy) {
        this.recordedBy = recordedBy;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }

    public void setSystolic(Integer systolic) {
        this.systolic = systolic;
    }

    public void setDiastolic(Integer diastolic) {
        this.diastolic = diastolic;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public void setBmi(BigDecimal bmi) {
        this.bmi = bmi;
    }

    public void setTemperatureCelsius(BigDecimal temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    public void setPulseRate(Integer pulseRate) {
        this.pulseRate = pulseRate;
    }

    public void setRespiratoryRate(Integer respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public void setOxygenSaturation(Integer oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}