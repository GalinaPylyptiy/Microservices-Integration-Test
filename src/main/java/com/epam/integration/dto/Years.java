package com.epam.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Years {

    @JsonProperty(value = "year")
    private Integer year;

    @JsonProperty(value = "months")
    private List<Months> months = new ArrayList<>();

    public Years() {
    }

    public Years(Integer year, List<Months> months) {
        this.year = year;
        this.months = months;
    }

    public List<Months> getMonths() {
        return months;
    }

    public void setMonths(List<Months> months) {
        this.months = months;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Years years = (Years) o;
        return Objects.equals(year, years.year) &&
                Objects.equals(months, years.months);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, months);
    }

    @Override
    public String toString() {
        return "Years{" +
                "year=" + year +
                ", months=" + months +
                '}';
    }
}
