package ng.farmsPot.b2b.dtos.requests;

import lombok.Data;

/** Optional reason, used when a farmer rejects an order. */
@Data
public class NoteRequest {
    private String note;
}
