package chungdenroi.spring.model;

import net.bytebuddy.implementation.bind.annotation.Default;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user_account")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "Username can be not empty!")
    @Column(unique = true, nullable = false)
    private String username;

    @NotEmpty(message = "Enter password!")
    @Size(min = 5, max = 10)
    @Column(length = 10, nullable = false)
    private String password;

    @NotEmpty(message = "Enter mobile number")
    @Pattern(regexp = "[\\d]{10}")
    @Column(nullable = false, unique = true)
    private String mobilenumber;

    @Column(length = 100)
    private String email;

    @ColumnDefault("'user'")
    private String permission;

    @NotEmpty(message = "Address can be not empty!")
    @Size(max=255)
    @Column(length = 255, nullable = false)
    private String address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserAccount() {
    }

    public UserAccount(String username, String password, String mobilenumber, String email, String permission, String address) {
        this.username = username;
        this.password = password;
        this.mobilenumber = mobilenumber;
        this.email = email;
        this.permission = permission;
        this.address = address;
    }
}
