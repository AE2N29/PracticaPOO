package es.upm.etsisi.poo.model.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CompanyClients")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class CompanyClient extends Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int numericCompanyId;
    public CompanyClient(String name, String id, String email, Cashier cashier) {
        super(name, id, email, cashier);
    }

    @Override
    protected String getHeader() {
        return "COMPANY"; //
    }
}
