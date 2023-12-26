package com.epam.integration.config;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/get_training_report.feature",
        glue = "com/epam/integration/client")
public class RunGetTrainingReportFeature {

}
