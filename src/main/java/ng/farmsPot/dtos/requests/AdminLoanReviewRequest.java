package ng.farmsPot.dtos.requests;

import lombok.Data;

@Data
public class AdminLoanReviewRequest {
    /** "APPROVE" (override a rejection) or "REJECT" (turn down a loan awaiting release). */
    private String decision;
    /** Required: why the admin made this decision. */
    private String note;
}
