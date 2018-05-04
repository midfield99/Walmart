import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The SeatHold class contains data about a customer's held or reserved seats.
 */
public class SeatHold {
    public final int Id;
    public final String venue;
    public final List<String> heldSeats;
    public final LocalTime creationTime;
    public String email;
    private String confirmationCode;
    private boolean confirmed;

    public SeatHold(int uniqueId, String venueName)
    {
        Id = uniqueId;
        venue = venueName;
        heldSeats = new ArrayList<>();
        confirmationCode = null;
        confirmed = false;
        creationTime = LocalTime.now();
    }

    /**
     * @return boolean. Has the SeatHold been confirmed?
     */
    public boolean isConfirmed() {return confirmed;}

    /**
     * Confirms the SeatHold.
     */
    public void confirm() {confirmed = true;}

    /**
     * Sets or replaces the email for the SeatHold.
     * @param customerEmail the current email address.
     */
    public void setEmail(String customerEmail) {
        email = customerEmail;
    }

    /**
     * Gets the confirmation
     * @return a reservation confirmation code
     */
    public String getConfirmationCode()
    {
        if (confirmationCode == null) {
            confirmationCode = venue + Integer.toString(Id);
        }

        return confirmationCode;
    }
}
