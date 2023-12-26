package com.epam.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrainingReportResponse {

    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "firstName")
    private String firstName;

    @JsonProperty(value = "lastName")
    private String lastName;

    @JsonProperty(value = "active")
    private Boolean active;

    @JsonProperty(value = "years")
    private List<Years> years = new ArrayList<>();

    public TrainingReportResponse() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Years> getYears() {
        return years;
    }

    public void setYears(List<Years> years) {
        this.years = years;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingReportResponse that = (TrainingReportResponse) o;
        return  Objects.equals(username, that.username) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(active, that.active) &&
                Objects.equals(years, that.years);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, firstName, lastName, active, years);
    }

    @Override
    public String toString() {
        return "TrainingReportResponse{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", active=" + active +
                ", years=" + years +
                '}';
    }
}
