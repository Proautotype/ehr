package com.custard.ehr.vitals.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class UpdateVitalsRequest {

    @Min(value = 50, message = "Systolic must be at least 50")
    @Max(value = 300, message = "Systolic must be at most 300")
    private Integer systolic;

    @Min(value = 30, message = "Diastolic must be at least 30")
    @Max(value = 300, message = "Diastolic must be at most 300")
    private Integer diastolic;

    @DecimalMin(value = "1.0", message = "Weight must be at least 1.0 kg")
    @DecimalMax(value = "500.0", message = "Weight must be at most 500.0 kg")
    private BigDecimal weightKg;

    @DecimalMin(value = "30.0", message = "Height must be at least 30.0 cm")
    @DecimalMax(value = "250.0", message = "Height must be at most 250.0 cm")
    private BigDecimal heightCm;

    @DecimalMin(value = "30.0", message = "Temperature must be at least 30.0°C")
    @DecimalMax(value = "45.0", message = "Temperature must be at most 45.0°C")
    private BigDecimal temperatureCelsius;

    @Min(value = 20, message = "Pulse rate must be at least 20")
    @Max(value = 250, message = "Pulse rate must be at most 250")
    private Integer pulseRate;

    @Min(value = 5, message = "Respiratory rate must be at least 5")
    @Max(value = 80, message = "Respiratory rate must be at most 80")
    private Integer respiratoryRate;

    @Min(value = 50, message = "Oxygen saturation must be at least 50")
    @Max(value = 100, message = "Oxygen saturation must be at most 100")
    private Integer oxygenSaturation;

    private String notes;

    // Standard Getters and Setters
    public Integer getSystolic() { return systolic; }
    public void setSystolic(Integer systolic) { this.systolic = systolic; }

    public Integer getDiastolic() { return diastolic; }
    public void setDiastolic(Integer diastolic) { this.diastolic = diastolic; }

    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }

    public BigDecimal getHeightCm() { return heightCm; }
    public void setHeightCm(BigDecimal heightCm) { this.heightCm = heightCm; }

    public BigDecimal getTemperatureCelsius() { return temperatureCelsius; }
    public void setTemperatureCelsius(BigDecimal temperatureCelsius) { this.temperatureCelsius = temperatureCelsius; }

    public Integer getPulseRate() { return pulseRate; }
    public void setPulseRate(Integer pulseRate) { this.pulseRate = pulseRate; }

    public Integer getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(Integer respiratoryRate) { this.respiratoryRate = respiratoryRate; }

    public Integer getOxygenSaturation() { return oxygenSaturation; }
    public void setOxygenSaturation(Integer oxygenSaturation) { this.oxygenSaturation = oxygenSaturation; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}