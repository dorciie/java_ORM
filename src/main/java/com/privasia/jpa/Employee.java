package com.privasia.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Employee {

    @Id
    private String first_name;
    @Column
    private String last_name;
    @Column
    private int salary;

    public Employee() {
    }

    public Employee(String fname, String lname, int salary) {
        this.first_name = fname;
        this.last_name = lname;
        this.salary = salary;
    }

}
