package com.example.online_courier.dto;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String city;
    private String status;

    public UserDTO() {}

    public UserDTO(Long id, String name, String email, String role, String city, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.city = city;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
