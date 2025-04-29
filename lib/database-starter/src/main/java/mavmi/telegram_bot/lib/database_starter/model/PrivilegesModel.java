package mavmi.telegram_bot.lib.database_starter.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;

import java.util.List;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "privileges", schema = "privilege")
public class PrivilegesModel {
    @Id
    @Column(name = "id")
    private Long id;
    @Basic
    @Column(name = "privileges", columnDefinition = "text[]")
    @Enumerated(EnumType.STRING)
    private List<PRIVILEGE> privileges;
}
