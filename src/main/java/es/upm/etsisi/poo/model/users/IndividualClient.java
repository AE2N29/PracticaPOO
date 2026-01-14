package es.upm.etsisi.poo.model.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "IndividualClients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualClient extends Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int numericIndividualId;
    public IndividualClient(String name, String id, String email, Cashier cashier) {
        super(name, id, email, cashier);
    }

    @Override
    protected String getHeader() {
        return "USER"; //
    }
}
