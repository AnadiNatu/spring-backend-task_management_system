package com.newSystem.TaskManagementSystemImplemented.entity;

import com.newSystem.TaskManagementSystemImplemented.enums.UserRoles;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;





@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Users implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String password;
    private int age;
    private String department;
    private boolean taskAssignment;
    private int completeTask;
    private String resetToken;

    @Enumerated(EnumType.STRING)
    private UserRoles userRoles;

    @OneToMany(mappedBy = "assignedTo" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private List<Task> task = new ArrayList<>();

    @Lob
    @Column(name = "profile_image")
    private byte[] profileImage;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRoles.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", department='" + department + '\'' +
                ", taskAssignment=" + taskAssignment +
                ", completeTask=" + completeTask +
                ", resetToken='" + resetToken + '\'' +
                ", userRoles=" + userRoles +
//                ", task=" + task +
                '}';
    }
}

