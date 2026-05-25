package personal_finance_manager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Table(name = "transactions")

public class Transaction {

    @Id
    @GeneratedValue(
            strategy =
                    GenerationType.IDENTITY
    )

    private Long id;


    private Double amount;

    private String date;

    private String description;

    private String category;

}