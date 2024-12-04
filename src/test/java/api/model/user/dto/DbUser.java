package api.model.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data //Generate constructor and getter
public class DbUser {
    @Id
    private UUID id;
    /*@Column(name = "\"firstName\"")*/
    private String firstName;
    /*@Column(name = "\"lastName\"")*/
    private String lastName;
   /* @Column(name = "\"middleName\"")*/
    private String middleName;
    private String birthday;
    private String phone;
    private String email;
    @JsonSerialize(using = InstantSerializer.class)
    private Instant createdAt;
    @JsonSerialize(using = InstantSerializer.class)
    private Instant updatedAt;

}
