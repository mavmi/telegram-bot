package mavmi.telegram_bot.lib.database_starter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "certificate", schema = "certificate")
public class CertificateModel {
    @Id
    @Column(name = "userid")
    private Long userid;
    @Column(name = "certificate")
    private String certificate;
    @Column(name = "key")
    private String key;
    @Column(name = "expiry_date")
    private Timestamp expiryDate;
}
