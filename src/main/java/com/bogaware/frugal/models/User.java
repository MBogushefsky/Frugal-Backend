package com.bogaware.frugal.models;

import com.bogaware.frugal.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Column(name = "ID")
    @Id
    private String Id;
    @Column(name = "Username")
    private String username;
    @Column(name = "PasswordHash")
    private String passwordHash;
    @Column(name = "FirstName")
    private String firstName;
    @Column(name = "LastName")
    private String lastName;
    @Column(name = "Email")
    private String email;
    @Column(name = "PhoneNumber")
    private String phoneNumber;
    @Column(name = "ProfileImage")
    private byte[] profileImage;

    public UserDTO getDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(this.getId());
        userDTO.setUsername(this.getUsername());
        userDTO.setPasswordHash(this.getPasswordHash());
        userDTO.setFirstName(this.getFirstName());
        userDTO.setLastName(this.getLastName());
        userDTO.setEmail(this.getEmail());
        userDTO.setPhoneNumber(this.getPhoneNumber());
        StringBuilder profileImageStringBuilder = new StringBuilder();
        profileImageStringBuilder.append("data:image/png;base64,");
        profileImageStringBuilder.append(StringUtils.newStringUtf8(Base64.encodeBase64(this.getProfileImage(), false)));
        userDTO.setProfileImage(profileImageStringBuilder.toString());
        return userDTO;
    }
}
