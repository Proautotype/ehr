package com.custard.ehr.vitals.application.mapper;

import com.custard.ehr.shared.exception.BusinessException;
import com.custard.ehr.vitals.application.dto.RecordVitalsRequest;
import com.custard.ehr.vitals.application.dto.UpdateVitalsRequest;
import com.custard.ehr.vitals.domain.Vitals;

public class VitalsMapper {

    public static Vitals toEntity(RecordVitalsRequest request){
        return new Vitals(
                request.encounterId(),
                request.systolic(),
                request.diastolic(),
                request.weightKg(),
                request.heightCm(),
                request.temperatureCelsius(),
                request.pulseRate(),
                request.respiratoryRate(),
                request.oxygenSaturation(),
                request.notes()
        );
    }

    public static void toUpdate(Vitals vitals, UpdateVitalsRequest request){
        if (vitals == null)
            throw new BusinessException("vitals is null");

        vitals.setSystolic(request.getSystolic());
        vitals.setDiastolic(request.getDiastolic());
        vitals.setWeightKg(request.getWeightKg());
        vitals.setHeightCm(request.getHeightCm());
        vitals.setTemperatureCelsius(request.getTemperatureCelsius());
        vitals.setPulseRate(request.getPulseRate());
        vitals.setRespiratoryRate(request.getRespiratoryRate());
        vitals.setOxygenSaturation(request.getOxygenSaturation());
        vitals.setNotes(request.getNotes());
    }

}
