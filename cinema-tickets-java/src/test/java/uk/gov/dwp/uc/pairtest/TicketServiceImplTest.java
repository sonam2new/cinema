package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    @Mock
    private TicketPaymentService ticketPaymentService;

    @Mock
    private SeatReservationService seatReservationService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    // Utility method to calculate total of requested tickets
    private int getTotalTicketCount(TicketTypeRequest[] requests) {
        int total = 0;
        for (TicketTypeRequest request : requests) {
            total += request.getNoOfTickets();
        }
        return total;
    }

    // Utility method to log booking details
    private void logBookingDetails(TicketTypeRequest[] requests) {
        System.out.println("Total number of requested tickets : " +getTotalTicketCount(requests));
        for (TicketTypeRequest request : requests) {
            System.out.println(request.getTicketType() + " " + request.getNoOfTickets());
        }
    }

    // Utility method to log total amount paid
    private void logTotalAmount(double totalPrice) {
        System.out.println("Total Amount paid: £" + totalPrice);
    }

    // Utility method to log total number of seats reserved
    private void logTotalSeatsReserved(TicketTypeRequest[] requests) {
        int totalSeatsReserved = 0;
        for (TicketTypeRequest request : requests) {
            if (request.getTicketType() == TicketTypeRequest.Type.ADULT || request.getTicketType() == TicketTypeRequest.Type.CHILD) {
                totalSeatsReserved += request.getNoOfTickets();
            }
        }
        System.out.println("Total number of seats reserved : " + totalSeatsReserved);
    }

    // Test cases as mentioned in excel sheet uploaded in GITHUB
    @Test
    public void test1_TwoAdultTicketsNoChildOrInfant() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2)
        };

        // Act
        ticketService.purchaseTickets(1L, requests);

        // Assert
        verify(ticketPaymentService, times(1)).makePayment(1L, 40);
        verify(seatReservationService, times(1)).reserveSeat(1L, 2);

        // Log booking details and total amount
        logBookingDetails(requests);
        double totalPrice = 20.0 * requests[0].getNoOfTickets();
        logTotalAmount(totalPrice);
        logTotalSeatsReserved(requests);
    }

    @Test
    public void test2_OneAdultTwoChildTickets() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2)
        };

        ticketService.purchaseTickets(1L, requests);

        // Assert
        verify(ticketPaymentService, times(1)).makePayment(1L, 40);
        verify(seatReservationService, times(1)).reserveSeat(1L, 3);

        // Log booking details and total amount
        logBookingDetails(requests);
        double totalPrice = 20.0 * requests[0].getNoOfTickets() + 10.0 * requests[1].getNoOfTickets();
        logTotalAmount(totalPrice);
        logTotalSeatsReserved(requests);
    }

    @Test
    public void test3_TwoAdultOneInfantTicket() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        };

        ticketService.purchaseTickets(1L, requests);

        // Assert
        verify(ticketPaymentService, times(1)).makePayment(1L, 40);
        verify(seatReservationService, times(1)).reserveSeat(1L, 2);

        // Log booking details and total amount
        logBookingDetails(requests);
        double totalPrice = 20.0 * requests[0].getNoOfTickets() + 0.0 * requests[1].getNoOfTickets();
        logTotalAmount(totalPrice);
        logTotalSeatsReserved(requests);
    }
    @Test
    public void test4_ThreeAdultOneChildOneInfantTicket() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        };

        ticketService.purchaseTickets(1L, requests);

        //Assert
        verify(ticketPaymentService, times(1)).makePayment(1L, 70);
        verify(seatReservationService, times(1)).reserveSeat(1L, 4);

        // Log booking details and total amount
        logBookingDetails(requests);
        double totalPrice = 20.0 * requests[0].getNoOfTickets() + 10.0 * requests[1].getNoOfTickets() + 0.0 * requests[2].getNoOfTickets();
        logTotalAmount(totalPrice);
        logTotalSeatsReserved(requests);
    }

    @Test
    public void test5_FourAdultTwoChildTwoInfantTicket() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 4),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)
        };

        ticketService.purchaseTickets(1L, requests);

        // Assert
        verify(ticketPaymentService, times(1)).makePayment(1L, 100);
        verify(seatReservationService, times(1)).reserveSeat(1L, 6);

        // Log booking details and total amount
        logBookingDetails(requests);
        double totalPrice = 20.0 * requests[0].getNoOfTickets() + 10.0 * requests[1].getNoOfTickets() + 0.0 * requests[2].getNoOfTickets();
        logTotalAmount(totalPrice);
        logTotalSeatsReserved(requests);
    }

    @Test
    public void test6_TenAdultFiveChildFiveInfantTicket() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5)
        };

        ticketService.purchaseTickets(1L, requests);

        // Assert
        verify(ticketPaymentService, times(1)).makePayment(1L, 250);
        verify(seatReservationService, times(1)).reserveSeat(1L, 15);

        // Log booking details and total amount
        logBookingDetails(requests);
        double totalPrice = 20.0 * requests[0].getNoOfTickets() + 10.0 * requests[1].getNoOfTickets() + 0.0 * requests[2].getNoOfTickets();
        logTotalAmount(totalPrice);
        logTotalSeatsReserved(requests);
    }
    @Test(expected = InvalidPurchaseException.class)
    public void test7_TwentyOneAdultTickets() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 21)
        };

        try {
            ticketService.purchaseTickets(1L, requests);
        } catch (InvalidPurchaseException e) {
            assertEquals("Error: Maximum of 20 tickets allowed for purchase.", e.getMessage());
            throw e;
        }
    }

    @Test(expected = InvalidPurchaseException.class)
    public void test8_TwoChilOneInfantTicketsWithoutdAdult() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        };

        try {
            ticketService.purchaseTickets(1L, requests);
        } catch (InvalidPurchaseException e) {
            assertEquals("Error: Child and Infant tickets cannot be purchased without an Adult ticket.", e.getMessage());
            throw e;
        }
    }
    @Test(expected = InvalidPurchaseException.class)
    public void test9_ThreeInfantTwoAdultTickets() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3)
        };

        try {
            ticketService.purchaseTickets(1L, requests);
        } catch (InvalidPurchaseException e) {
            assertEquals("Error: The number of infants cannot be greater than the number of adults.", e.getMessage());
            throw e;
        }
    }
    @Test(expected = InvalidPurchaseException.class)
    public void test10_InvalidTicketQuantity() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1)
        };

        try {
            ticketService.purchaseTickets(1L, requests);
        } catch (InvalidPurchaseException e) {
            assertEquals("Error: Invalid ticket quantity.", e.getMessage());
            throw e;
        }
    }
    @Test(expected = InvalidPurchaseException.class)
    public void test11_MaximumTicketLimit() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 15),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5)
        };

        try {
            ticketService.purchaseTickets(1L, requests);
        } catch (InvalidPurchaseException e) {
            assertEquals("Error: Maximum of 20 tickets allowed for purchase.", e.getMessage());
            throw e;
        }
    }
    @Test(expected = InvalidPurchaseException.class)
    public void test12_ZeroTicket() throws InvalidPurchaseException {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0)
        };

        try {
            ticketService.purchaseTickets(1L, requests);
        } catch (InvalidPurchaseException e) {
            assertEquals("Error: Please select at least one Adult ticket for purchase.", e.getMessage());
            throw e;
        }
    }

}
