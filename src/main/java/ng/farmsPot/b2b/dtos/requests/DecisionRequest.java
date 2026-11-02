package ng.farmsPot.b2b.dtos.requests;

import lombok.Data;

/** An admin decision (VERIFY / APPROVE / REJECT) with an optional or required reason. */
@Data
public class DecisionRequest {
    private String decision;
    private String note;
}
