package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        final double Infant_Price = 0.0;
        final double Child_Price = 10.0;
        final double Adult_Price = 20.0;
        final int max_tickets_per_purchase = 20;

        // Initialize counters and total price
        int infantCount =0;
        int childCount = 0;
        int adultCount = 0;
        double totalPrice = 0.0;

        // Calculate the count of ticket and total price
        for (TicketTypeRequest req : ticketTypeRequests) {
            TicketTypeRequest.Type type = req.getTicketType();
            int quantity = req.getNoOfTickets();

            if (quantity < 0) {
                throw new InvalidPurchaseException("Error: Invalid ticket quantity.");
            }

            switch (type) {
                case INFANT:
                    infantCount += quantity;
                    totalPrice += Infant_Price * quantity;
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

        // Verify if the number of tickets purchased exceeds the limit
        if (adultCount + childCount + infantCount > max_tickets_per_purchase) {
            throw new InvalidPurchaseException("Error: Maximum of 20 tickets allowed for purchase.");
        }

        // Verify if infant tickets are purchased without an adult ticket
        if ((childCount > 0 || infantCount > 0) && adultCount == 0) {
            throw new InvalidPurchaseException("Error: Child and Infant tickets cannot be purchased without an Adult ticket.");
        }

        // Verify if infant ticket count is more than an adult ticket
        if (infantCount > adultCount) {
            throw new InvalidPurchaseException("Error: The number of infants cannot be greater than the number of adults.");
        }

        // Verify if there is at least one Adult ticket selected for purchase
        if (adultCount + childCount + infantCount == 0) {
            throw new InvalidPurchaseException("Error: Please select at least one Adult ticket for purchase.");
        }

        // Reserve seats for adults and child only
        int total = adultCount + childCount;
        seatReservationService.reserveSeat(accountId, total);

        // Make payment for the tickets purchased
        ticketPaymentService.makePayment(accountId, (int) totalPrice);

    }

}
