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
public class PrivilegesModel {
    @Id
    @Column(name = "id")
    private Long id;
    @ElementCollection
    @Column(name = "privileges")
    private List<PRIVILEGE> privileges;
}
