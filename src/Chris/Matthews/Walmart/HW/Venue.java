package Chris.Matthews.Walmart.HW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Venue implements  TicketService {
    /*It looks to me like a section of seats in the same location would be useful to work with.
    Groups would probably want to sit together.
    one undivided row, seat seat seat, would be one section.
    one divided row, seat seat aisle seat, would be two sections.
    */
    private class SeatSection {
        public final int qualityRank;
        public final int sectionId;
        private int available;
        private Integer[] seatHoldIds; //a seat (array cell) should be null if it is not held.

        public SeatSection(int size, int rank, int id)
        {
            available = size;
            seatHoldIds = new Integer[size];
            qualityRank = rank;
            sectionId = id;
        }

        /*

        */
        public String getSeatId(int seat){
            return Integer.toString(sectionId) + "_" + Integer.toString(seat);
        }


        public  int reserveSeats(int numSeats, SeatHold buyer)
        {
            //preconditions: numSeats > 0, buyer is not null
            if (numSeats <= 0) {
                throw new RuntimeException("numSeats needs to be a positive number");
            }
            if (buyer == null) {
                throw new RuntimeException("SeatHold object can't be null");
            }

            int taken = 0;
            for (int i = 0; i < seatHoldIds.length; i++) {
                if (seatHoldIds[i] == null && taken < numSeats) {
                    seatHoldIds[i] = buyer.Id;
                    buyer.heldSeats.add(getSeatId(i));
                    taken += 1;
                }
            }

            available -= taken;
            free -= taken;
            /*postConditions: seatHold still valid, SeatSection.available and Venue.free is accurate.
            buyer.Ids have been added, seat ids have been added to buyer.heldSeats. Everything else is unchanged.
            Assumption is that a show will be handled by only one Venue object instance. */
            return  taken;
        }

        public int removeSeatHold(SeatHold buyer)
        {
            //precondition: valid/not null SeatHold
            if (buyer == null) {
                throw new RuntimeException("SeatHold object is not valid");
            }

            int found = 0;
            for (int i = 0; i < seatHoldIds.length; i++) {
                if (seatHoldIds[i] == buyer.Id) {
                    seatHoldIds[i] = null;
                    found += 1;
                }
            }

            free += found;
            available += found;
            /*postconditions: seatHold still valid, SeatSection.available and Venue.free is accurate.
            buyer.Ids have been removed. Everything else is unchanged. Assumption is that a show will be handled by
            only one Venue object instance.
            */
            return found;
        }
    }

    public final String VenueName;

    private List<SeatSection> sections;
    private int free;
    private HashMap<Integer, SeatHold> seatHoldMap;
    public Venue (int size, String name) {
        free = size;
        sections = new ArrayList<>();
        seatHoldMap = new HashMap<>();
        VenueName = name;
        //Should add more initialization code later. I can't really test this if I can't control SeatSection creation.
    }

    public  int reserveSeats(int numSeats, SeatHold buyer)
    {
        //preconditions: 0 < numSeats, buyer is not null
        if (numSeats <= 0) {
            throw new RuntimeException("numSeats needs to be a positive number");
        }
        if (buyer == null) {
            throw new RuntimeException("SeatHold object can't be null");
        }


        return -1;
    }

    public int removeSeatHold(SeatHold buyer) {
        //precondition: valid/not null SeatHold
        if (buyer == null) {
            throw new RuntimeException("SeatHold object is not valid");
        }

        int found = 0;
        for (SeatSection s : sections) {
            found += s.removeSeatHold(buyer);
        }

        buyer.heldSeats.clear();
        /*postconditions: buyer still valid, Venue.free/SeatSection.available is accurate. buyer.Ids have been removed.
        I'm assuming the number of seat holds removed would be useful to know.*/
        return found;
    }

    public int numSeatsAvailable() {return free;}

    /* My assumption is that seats close to the front will be better than seats further away. My second assumption is
    that SeatSections next to each other have similar levels of quality. I'm also assuming that a good ranking of
    seat sections would be a list of each section sorted by row number.

    These assumptions could definitely be wrong. For instance, venues with multiple levels might be more complex or
    strange venue design decisions might make ranking seating more complex. My suggestion would be to turn Venue into
    an abstract class, abstract or override methods dealing with section quality or ranking and pull venue-specific
    ranking logic into subclasses. But I'm only working with simple one-level venues right now so I'm keeping Venue as
    a concrete class.
     */
    private List<SeatSection> getRankedSeatSections() {
        return getRankedSeatSections(0);
    }

    private List<SeatSection> getRankedSeatSections(int minAvailable) {
        if (minAvailable < 0) {
            String msg = "Invalid minAvailable. A SeatSection shouldn't have a negative number of sets available.";
            throw new RuntimeException(msg);
        }

        List<SeatSection> rankedSections =  new ArrayList<>();
        for (SeatSection group: sections) {
            if (group.available >= minAvailable) {
                rankedSections.add(group);
            }
        }
        Collections.sort(rankedSections, Comparator.comparingInt(SeatSection -> SeatSection.qualityRank));
        /* Postconditions. returns a sorted list by qualityRank, low to high. Every SeatSection will have at least
        minAvailable seats available.
         */
        return rankedSections;
    }

    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        /*preconditions: numSeats is valid. Assume customerEmail is valid and that a show will be handled by only
        one Venue object instance. Otherwise id generation should be changed to enforce uniqueness.
         */
        List<SeatSection> rankedSeatSections;
        int id = seatHoldMap.size();
        SeatHold buyer = new SeatHold(id ,VenueName);

        //Check if there are sections with room for the entire group.
        rankedSeatSections = getRankedSeatSections(numSeats);
        //Yes, place group in one section.
        if (!rankedSeatSections.isEmpty()) {
            rankedSeatSections.get(0).reserveSeats(numSeats, buyer);
        }
        else {
            /*No, separate group. This method is kind of naive, especially near the end. It doesn't minimize the number
            of different sections the group is in. In fact, group splintering may be a little worse than needed,
            especially for groups near the end.
            */
            int held = 0;
            rankedSeatSections = getRankedSeatSections(numSeats);
            for (SeatSection s: rankedSeatSections) {
                held += s.reserveSeats(numSeats - held, buyer);
                if (held >= numSeats){
                    break;
                }
            }
        }

        seatHoldMap.put(id, buyer);
        /*postconditions: buyer is valid. numSeats have been reserved. seatHoldMap is up to date.
        Seat reservations have been made with the quality of seats in mind. And nothing else has changed.
         */
        return buyer;
    }


    public String reserveSeats(int seatHoldId, String customerEmail) {
        SeatHold buyer = seatHoldMap.get(seatHoldId);
        //precondition: not null SeatHold
        if (buyer == null) {
            throw new RuntimeException("SeatHold object not found");
        }

        buyer.setEmail(customerEmail);
        buyer.confirm();

        return buyer.getConfirmationCode();
    }
}
