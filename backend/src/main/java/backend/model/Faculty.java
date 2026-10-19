package backend.model;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String code;
    @ManyToOne(optional = false)
    @JoinColumn(name="institution_id",nullable = false)
    private Institution institution;
}
