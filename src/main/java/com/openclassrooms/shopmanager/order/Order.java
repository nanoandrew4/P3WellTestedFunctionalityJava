package com.openclassrooms.shopmanager.order;

import java.time.LocalDate;
import java.util.List;

public class Order {

    private Long id;

    private List<CartLine> lines;

    private LocalDate date = LocalDate.now();

    private String name;

    private String address;

    private String city;

    private String zip;

    private String country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CartLine> getLines() {
        return lines;
    }

    public void setLines(List<CartLine> lines) {
        this.lines = lines;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
