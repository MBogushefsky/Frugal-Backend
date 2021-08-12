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
@Table(name = "user_preference")
public class UserPreference {
    @Column(name = "TypeID")
    @Id
    private String typeId;
    @Column(name = "UserID")
    private String userId;
    @Column(name = "Value")
    private String value;
}
