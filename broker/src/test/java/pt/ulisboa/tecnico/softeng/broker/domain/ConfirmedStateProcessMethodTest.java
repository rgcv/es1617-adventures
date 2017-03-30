package pt.ulisboa.tecnico.softeng.broker.domain;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.StrictExpectations;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.joda.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ulisboa.tecnico.softeng.activity.dataobjects.ActivityReservationData;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.bank.dataobjects.BankOperationData;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

@RunWith(JMockit.class)
public class ConfirmedStateProcessMethodTest {
    private static final int AGE = 20;
    private static final String IBAN = "BK01987654321";
    private static final int AMOUNT = 300;
    private final LocalDate begin = new LocalDate(2016, 12, 19);
    private final LocalDate end = new LocalDate(2016, 12, 21);

    private static final String PAYMENT_CONFIRMATION = "PaymentConfirmation";
    private static final String ACTIVITY_CONFIRMATION = "ActivityConfirmation";
    private static final String ROOM_CONFIRMATION = "RoomConfirmation";

    private Adventure adventure;

    @Injectable
    private Broker broker;

    @Before
    public void setUp() {
        this.adventure = new Adventure(this.broker, this.begin, this.end, AGE, IBAN, AMOUNT);
        this.adventure.setState(Adventure.State.CONFIRMED);
    }

    @Test
    public void didNotBookRoom(@Mocked final BankInterface bankInterface,
            @Mocked final HotelInterface hotelInterface,
            @Mocked final ActivityInterface activityInterface) {
        this.adventure.process();

        Assert.assertEquals(State.CONFIRMED, this.adventure.getState());

        new Verifications() {{
            BankInterface.getOperationData(this.anyString);

            ActivityInterface.getActivityReservationData(this.anyString);

            HotelInterface.getRoomBookingData(this.anyString);
            this.times = 0;
        }};
    }

    @Test
    public void confirmedPaymentBankException(
            @Mocked final BankInterface bankInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);

        new StrictExpectations() {{
           BankInterface.getOperationData(PAYMENT_CONFIRMATION);
           this.result = new BankException();
        }};

        this.adventure.process();

        Assert.assertEquals(State.CONFIRMED, this.adventure.getState());
    }
    
    @Test
    public void confirmedPaymentBankErrors(
            @Mocked final BankInterface bankInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);

        new StrictExpectations() {{
           BankInterface.getOperationData(PAYMENT_CONFIRMATION);
           this.times = ConfirmedState.MAX_ERRORS;
           this.result = new BankException();
        }};

        for (int i = 0; i < ConfirmedState.MAX_ERRORS; ++i)
            this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState());
    }

    @Test
    public void confirmedPaymentRemoteAccessException(
            @Mocked final BankInterface bankInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);

        new StrictExpectations() {{
           BankInterface.getOperationData(PAYMENT_CONFIRMATION);
           this.result = new RemoteAccessException();
        }};

        this.adventure.process();

        Assert.assertEquals(State.CONFIRMED, this.adventure.getState());
    }

    @Test
    public void confirmedPaymentRemoteAccessErrors(
            @Mocked final BankInterface bankInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);

        new StrictExpectations() {{
           BankInterface.getOperationData(PAYMENT_CONFIRMATION);
           this.times = ConfirmedState.MAX_REMOTE_ERRORS;
           this.result = new RemoteAccessException();
        }};

        for (int i = 0; i < ConfirmedState.MAX_REMOTE_ERRORS; ++i)
            this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState());
    }

    @Test
    public void confirmedPayment(@Mocked final BankInterface bankInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);

        new StrictExpectations() {{
            BankInterface.getOperationData(this.anyString);
        }};

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState());
    }

    @Test
    public void confirmedReservationActivityException(
            @Mocked final BankInterface bankInterface,
            @Mocked final ActivityInterface activityInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);

        new StrictExpectations() {{
            BankInterface.getOperationData(PAYMENT_CONFIRMATION);
            this.result = new BankOperationData();

            ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
            this.result = new ActivityException();
        }};

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState());
    }

    @Test
    public void confirmedReservationRemoteAccessException(
            @Mocked final BankInterface bankInterface,
            @Mocked final ActivityInterface activityInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);

        new StrictExpectations() {{
            BankInterface.getOperationData(PAYMENT_CONFIRMATION);
            this.result = new BankOperationData();

            ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
            this.result = new RemoteAccessException();
        }};

        this.adventure.process();

        Assert.assertEquals(State.CONFIRMED, this.adventure.getState());
    }

    @Test
    public void confirmedReservationRemoteAccessErrors(
            @Mocked final BankInterface bankInterface,
            @Mocked final ActivityInterface activityInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);

        new Expectations() {{
            BankInterface.getOperationData(PAYMENT_CONFIRMATION);
            this.times = ConfirmedState.MAX_REMOTE_ERRORS;
            this.result = new BankOperationData();

            ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
            this.times = ConfirmedState.MAX_REMOTE_ERRORS;
            this.result = new RemoteAccessException();
        }};

        for (int i = 0; i < ConfirmedState.MAX_REMOTE_ERRORS; ++i)
            this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState());
    }

    @Test
    public void confirmedReservation(
            @Mocked final BankInterface bankInterface,
            @Mocked final ActivityInterface activityInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);

        new StrictExpectations() {{
            BankInterface.getOperationData(PAYMENT_CONFIRMATION);

            ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
        }};

        this.adventure.process();

        Assert.assertEquals(State.CONFIRMED, this.adventure.getState());
    }

    @Test
    public void confirmedBookingHotelException(
            @Mocked final BankInterface bankInterface,
            @Mocked final ActivityInterface activityInterface,
            @Mocked final HotelInterface hotelInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);

        new StrictExpectations() {{
            BankInterface.getOperationData(PAYMENT_CONFIRMATION);

            ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

            HotelInterface.getRoomBookingData(ROOM_CONFIRMATION);
            this.result = new HotelException();
        }};

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState());
    }

    @Test
    public void confirmedBookingRemoteAccessException(
            @Mocked final BankInterface bankInterface,
            @Mocked final ActivityInterface activityInterface,
            @Mocked final HotelInterface hotelInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);

        new Expectations() {{
            BankInterface.getOperationData(PAYMENT_CONFIRMATION);
            this.result = new BankOperationData();

            ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
            this.result = new ActivityReservationData();

            HotelInterface.getRoomBookingData(ROOM_CONFIRMATION);
            this.result = new RemoteAccessException();
        }};

        this.adventure.process();

        Assert.assertEquals(State.CONFIRMED, this.adventure.getState());
    }

    @Test
    public void confirmedBookingRemoteAccessErrors(
            @Mocked final BankInterface bankInterface,
            @Mocked final ActivityInterface activityInterface,
            @Mocked final HotelInterface hotelInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);

        new Expectations() {{
            BankInterface.getOperationData(PAYMENT_CONFIRMATION);
            this.times = ConfirmedState.MAX_REMOTE_ERRORS;
            this.result = new BankOperationData();

            ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);
            this.times = ConfirmedState.MAX_REMOTE_ERRORS;
            this.result = new ActivityReservationData();

            HotelInterface.getRoomBookingData(ROOM_CONFIRMATION);
            this.times = ConfirmedState.MAX_REMOTE_ERRORS;
            this.result = new RemoteAccessException();
        }};

        for (int i = 0; i < ConfirmedState.MAX_REMOTE_ERRORS; ++i)
            this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState());
    }

    @Test
    public void confirmedReservation(
            @Mocked final BankInterface bankInterface,
            @Mocked final ActivityInterface activityInterface,
            @Mocked final HotelInterface hotelInterface) {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);

        new StrictExpectations() {{
            BankInterface.getOperationData(PAYMENT_CONFIRMATION);

            ActivityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION);

            HotelInterface.getRoomBookingData(ROOM_CONFIRMATION);
        }};

        this.adventure.process();

        Assert.assertEquals(State.CONFIRMED, this.adventure.getState());
    }

}
