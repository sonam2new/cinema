package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        final double Infant_Price = 0.0;
        final double Child_Price = 10.0;
        final double Adult_Price = 20.0;

        int infantCount = 0;
        int childCount = 0;
        int adultCount = 0;
        double totalPrice = 0.0;

        // Calculate the count of ticket and total price
        for (TicketTypeRequest req : ticketTypeRequests) {
            TicketTypeRequest.Type type = req.getTicketType();
            int quantity = req.getNoOfTickets();

            switch (type) {
                case INFANT:
                    infantCount += quantity;
                    break;
                case CHILD:
                    childCount += quantity;
                    totalPrice += Child_Price * quantity;
                    break;
                case ADULT:
                    adultCount += quantity;
                    totalPrice += Adult_Price * quantity;
                    break;
                default:
                    throw new InvalidPurchaseException("Ticket type is invalid: " + type);
            }
        }

    }
    private void validateMaxPurchaseTickets(TicketTypeRequest[] ticketTypeRequests) {
        final int max_tickets_per_purchase = 20;
        if (ticketTypeRequests.length > max_tickets_per_purchase) {
            throw new InvalidPurchaseException("Maximum of 20 tickets can be purchased at a time.");
        }
    }

    private void validateChildAndInfantWithoutAdult(int infantCount,int adultCount){
        if(infantCount > 0 && adultCount == 0) {
            throw new InvalidPurchaseException("Infant tickets cannot be purchased without an Adult ticket.");
        }
    }
    
}
