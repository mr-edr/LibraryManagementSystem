package com.mredr.Libraray_management.model;

import com.mredr.Libraray_management.model.enums.Availability;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "Books")
public class Books {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private Availability availability;

    @Column
    private Long stock;

    @Column(nullable = false)
    private String category;


    // =========================
    // CONSTRUCTORS
    // =========================

    public Books() {
    }

    public Books(
            Long id,
            String name,
            String description,
            String category,
            Availability availability,
            Long stock
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.availability = availability;
        this.stock = stock;
    }


    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    // =========================
    // EQUALS & HASHCODE
    // =========================

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Books)) {
            return false;
        }

        Books other = (Books) o;

        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    // =========================
    // TO STRING
    // =========================

    @Override
    public String toString() {
        return "Books{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", availability=" + availability +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                '}';
    }
}