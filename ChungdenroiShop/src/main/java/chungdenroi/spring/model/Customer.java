package chungdenroi.spring.model;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "Name can be not empty!")
    @Size(min = 2, max = 60)
    @Column(nullable = false, length = 60)
    private String fullname;

    @NotNull(message = "Mobile number can be not empty!")
    @Pattern(regexp = "[\\d]{10}")
    @Column(length = 10, nullable = false, unique = true)
    private String mobilenumber;

    @Column(length = 100)
    private String email;

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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Customer() {
    }
    public Customer(String fullname, String mobilenumber, String email, String address) {
        this.fullname = fullname;
        this.mobilenumber = mobilenumber;
        this.email = email;
        this.address = address;
    }
}
