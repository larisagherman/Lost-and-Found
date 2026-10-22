package backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "display_name", nullable = false)
    private String displayName;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    //pentru admin
    @ManyToOne
    @JoinColumn(name = "institution_id",nullable = false)
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

}
