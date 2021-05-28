package com.rubicon.Rubicon.Water.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class JacksonConfiguration {

    @Autowired
    public JacksonConfiguration(ObjectMapper objectMapper){
        objectMapper.setTimeZone(TimeZone.getDefault());
    }
}
