package com.custard.ehr.drug.application.mapper;

import com.custard.ehr.drug.application.dto.CreateDrugDto;
import com.custard.ehr.drug.application.dto.DrugResponse;
import com.custard.ehr.drug.domain.Drug;

public class DrugMapper {
    public static Drug toEntity(CreateDrugDto createDrugDto){
        return new Drug(
                createDrugDto.getName(),
                createDrugDto.getStrength(),
                createDrugDto.getForm(),
                createDrugDto.getDescription(),
                createDrugDto.getCode()
        );
    }
}
