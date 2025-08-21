package br.edu.ifpb.pweb2.bloomfinance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "authorities", uniqueConstraints = {
        @UniqueConstraint(name = "ix_auth_username", columnNames = {"user_id", "authority"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_authorities_users"))
    private User user;

    @Column(length = 50, nullable = false)
    private String authority; // ex: "ROLE_ADMIN", "ROLE_USER"
}
