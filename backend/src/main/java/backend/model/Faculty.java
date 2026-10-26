package backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_faculty_institution_name",
                columnNames = {"institution_id", "name"}
        )
})
@Getter
@Setter
@NoArgsConstructor
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String code;
    @ManyToOne(optional = false)
    @JoinColumn(name="institution_id",nullable = false)
    private Institution institution;
}
