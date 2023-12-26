package com.epam.integration.client;

import com.epam.integration.IntegrationApplication;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = IntegrationApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringContextConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(CucumberSpringContextConfiguration.class);

    @Before
    public void setUp() {
        LOG.info("------ Spring Context Initialized For Executing Cucumber Tests --------");
    }

}
