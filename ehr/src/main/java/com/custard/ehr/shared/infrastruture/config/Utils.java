package com.custard.ehr.shared.infrastruture.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    private static ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper mapper (){
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//        com.fasterxml.jackson.datatype:jackson-datatype-jsr310
        return mapper;
    }

}
