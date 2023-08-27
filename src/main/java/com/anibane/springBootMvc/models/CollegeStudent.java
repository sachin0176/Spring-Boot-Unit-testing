package com.anibane.springBootMvc.models;

import jakarta.persistence.*;

@Entity
@Table(name = "student")
public class CollegeStudent implements Student{

    // field
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "firstname")
    private String firstname;
    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email_address")
    private String emailAddress;



    // constructor
    public CollegeStudent(){}

    public CollegeStudent (String firstname, String lastname, String emailAddress) {

        this.firstname = firstname;
        this.lastname = lastname;
        this.emailAddress = emailAddress;
    }
// getter and setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {
        return "CollegeStudent{" +
                "id=" + id +
                ", firstName='" + firstname + '\'' +
                ", lastName='" + lastname + '\'' +
                ", email='" + emailAddress + '\'' +
                '}';
    }

    @Override
    public String studentInformation() {
        return getFullName() +" "+getId();
    }

    @Override
    public String getFullName() {
        return getFirstname() + " "+ getLastname();
    }
}
