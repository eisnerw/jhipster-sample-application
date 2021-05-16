package com.eisnerw.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;

/**
 * A Birthday.
 */
@Entity
@Table(name = "birthday")
public class Birthday implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "lname")
    private String lname;

    @Column(name = "fname")
    private String fname;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "is_alive")
    private Boolean isAlive;

    @Column(name = "additional")
    private String additional;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Birthday id(Long id) {
        this.id = id;
        return this;
    }

    public String getLname() {
        return this.lname;
    }

    public Birthday lname(String lname) {
        this.lname = lname;
        return this;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getFname() {
        return this.fname;
    }

    public Birthday fname(String fname) {
        this.fname = fname;
        return this;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public LocalDate getDob() {
        return this.dob;
    }

    public Birthday dob(LocalDate dob) {
        this.dob = dob;
        return this;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Boolean getIsAlive() {
        return this.isAlive;
    }

    public Birthday isAlive(Boolean isAlive) {
        this.isAlive = isAlive;
        return this;
    }

    public void setIsAlive(Boolean isAlive) {
        this.isAlive = isAlive;
    }

    public String getAdditional() {
        return this.additional;
    }

    public Birthday additional(String additional) {
        this.additional = additional;
        return this;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Birthday)) {
            return false;
        }
        return id != null && id.equals(((Birthday) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Birthday{" +
            "id=" + getId() +
            ", lname='" + getLname() + "'" +
            ", fname='" + getFname() + "'" +
            ", dob='" + getDob() + "'" +
            ", isAlive='" + getIsAlive() + "'" +
            ", additional='" + getAdditional() + "'" +
            "}";
    }
}
