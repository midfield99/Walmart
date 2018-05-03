package Chris.Matthews.Walmart.HW;

import java.util.ArrayList;
import java.util.List;

/**
 * The SeatHold class contains data about a customer's held or reserved seats.
 *
 * A timer would be useful, so that user's ability to hold seats without purchasing them is limited.
 */
public class SeatHold {
    public final int Id;
    public final String venue;
    public final List<String> heldSeats;
    public String confirmationCode;
    public String email;
    private boolean confirmed;

    public SeatHold(int uniqueId, String venueName)
    {
        Id = uniqueId;
        venue = venueName;
        heldSeats = new ArrayList<>();
        confirmationCode = null;
        confirmed = false;
    }

    /**
     * @return boolean. Has the SeatHold been confirmed?
     * Technically not used yet, but probably would be useful.
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
