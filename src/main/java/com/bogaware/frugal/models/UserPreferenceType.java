package com.bogaware.frugal.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_preference_type")
public class UserPreferenceType {
    @Column(name = "ID")
    @Id
    private String Id;
    @Column(name = "DataType")
    private String dataType;
    @Column(name = "Category")
    private String category;
    @Column(name = "Name")
    private String name;
}
