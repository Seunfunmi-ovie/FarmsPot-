package ng.farmsPot.data.models;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "Accounts")
@Entity
@Data
public class FarmersAccount {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @OneToOne
    @JoinColumn(name = "farmer", nullable = false)
    private Farmer farmer;

   private double balance;





}
