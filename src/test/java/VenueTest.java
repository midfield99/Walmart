import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;


public class VenueTest {

    private List<Pair<Integer, Integer>> getSeatSections() {
        List li = new ArrayList();
        li.add(new ImmutablePair(5,7));
        li.add(new ImmutablePair(4,2));
        li.add(new ImmutablePair(3,0));
        li.add(new ImmutablePair(2,9));
        li.add(new ImmutablePair(1,1));
        return li; //size = 15
    }

    /*
    SeatHold can be identified by it's unique id. This is just a method for testing.
     */
    private boolean checkSeatHold(SeatHold sh, int id, String venue, String[] heldSeats,
                                  String confirmationCode, String email, boolean confirmed)
    {
        if (sh.Id == id && sh.venue.equals(venue) && Arrays.equals(sh.heldSeats.toArray(), heldSeats) &&
                sh.getConfirmationCode().equals(confirmationCode) && sh.isConfirmed() == confirmed)
        {
            if ((sh.email == null && email == null) ||
                    sh.email.equals(email)) {
                return true;
            }
        }

        return false;
    }



    //Test preconditions for findAndHoldSeats
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void removeSeatHold()
    {
        List<Pair<Integer, Integer>> seat_sections = getSeatSections();
        Venue kennedy = new Venue(seat_sections, "Kennedy_Center");

        assertTrue(kennedy.numSeatsAvailable() == 15);
        SeatHold removed = kennedy.findAndHoldSeats(5, "removed_buyer");
        assertTrue(kennedy.numSeatsAvailable() == 10);
        assertFalse(removed.heldSeats.isEmpty());

        kennedy.removeSeatHold(removed);
        assertTrue(removed.heldSeats.isEmpty());
        assertTrue(kennedy.numSeatsAvailable() == 15);
    }

    @Test
    public void findAndHoldSeatsMinSeatsNotMet() {
        List<Pair<Integer, Integer>> seat_sections = getSeatSections();
        Venue kennedy = new Venue(seat_sections, "Kennedy_Center");

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Invalid numSeats. At least one seat must be reserved.");

        kennedy.findAndHoldSeats(0, "bad_buyer");
    }

    @Test
    public void findAndHoldSeatsNotEnoughSeats() {
        List<Pair<Integer, Integer>> seat_sections = getSeatSections();
        Venue kennedy = new Venue(seat_sections, "Kennedy_Center");

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Not enough seats are available to hold.");

        kennedy.findAndHoldSeats(16, "bad_buyer");
    }

    @Test
    public void findAndHoldSeatsNullEmail() {
        List<Pair<Integer, Integer>> seat_sections = getSeatSections();
        Venue kennedy = new Venue(seat_sections, "Kennedy_Center");

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("customerEmail can't be null.");

        kennedy.findAndHoldSeats(1, null);
    }


    //Test functionality.
    @Test
    public void findAndHoldSeats() throws Exception {
        List<Pair<Integer, Integer>> seat_sections = getSeatSections();
        Venue kennedy = new Venue(seat_sections, "Kennedy_Center");

        //Case 1: Group. Buyer keeps group together.
        assertTrue(kennedy.numSeatsAvailable() == 15);
        SeatHold buyer1 = kennedy.findAndHoldSeats(5, "buyer1");
        String[] heldSeats1 = {"0_0", "0_1", "0_2", "0_3", "0_4"};
        assertTrue(checkSeatHold(buyer1, 0, "Kennedy_Center", heldSeats1, "Kennedy_Center0", null, false));
        assertTrue(kennedy.numSeatsAvailable() == 10);

        //Case 2: Single purchaser
        SeatHold buyer2 = kennedy.findAndHoldSeats(1, "buyer2");
        String[] heldSeats2 = {"2_0"};
        assertTrue(checkSeatHold(buyer2, 1, "Kennedy_Center", heldSeats2, "Kennedy_Center1", null, false));
        assertTrue(kennedy.numSeatsAvailable() == 9);

        //Case 3: Group is split.
        SeatHold buyer3 = kennedy.findAndHoldSeats(6, "buyer3");
        String[] heldSeats3 = {"2_1", "2_2", "4_0", "1_0", "1_1", "1_2"};
        assertTrue(checkSeatHold(buyer3, 2, "Kennedy_Center", heldSeats3, "Kennedy_Center2", null, false));
        assertTrue(kennedy.numSeatsAvailable() == 3);
    }
}