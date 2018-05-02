package Chris.Matthews.Walmart.HW;

/**
 * Created by Chris on 5/2/2018.
 */
public class Venue implements  TicketService {
    /*some method stubs to prevent error messages*/
    public int numSeatsAvailable() {return 1;}
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {return new SeatHold();}
    public String reserveSeats(int seatHoldId, String customerEmail) {return new String();}
    /*actual methods*/
}
