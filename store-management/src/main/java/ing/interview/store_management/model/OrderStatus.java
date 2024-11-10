package ing.interview.store_management.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum OrderStatus {

    CREATED("created"),
    PLACED("placed"),
    COMPLETED("completed"),
    CANCELED("canceled"),
    ANULATED("anulated");

    private final String status;

    // Optional: To convert a string to an enum value
    public static OrderStatus fromString(String status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.status.equalsIgnoreCase(status)) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}