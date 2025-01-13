package mavmi.telegram_bot.common.database.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;

import java.util.List;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "privileges", schema = "privilege")
@ToString
public class PrivilegesModel {
    @Id
    @Column(name = "id")
    private Long id;
    @Basic
    @Column(name = "privileges", columnDefinition = "text[]")
    @Enumerated(EnumType.STRING)
    private List<PRIVILEGE> privileges;
}
