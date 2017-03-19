package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.activity.domain.Activity;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider;

import pt.ulisboa.tecnico.softeng.bank.domain.Account;
import pt.ulisboa.tecnico.softeng.bank.domain.Bank;
import pt.ulisboa.tecnico.softeng.bank.domain.Client;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class AdventureProcessMethodTest {
    private final LocalDate begin = new LocalDate(2016, 12, 19);
    private final LocalDate end = new LocalDate(2016, 12, 21);
    private Broker broker;
    private String IBAN;

    @Before
    public void setUp() {
        this.broker = new Broker("BR01", "eXtremeADVENTURE");

        Bank bank = new Bank("Money", "BK01");
        Client client = new Client(bank, "António");
        Account account = new Account(bank, client);
        this.IBAN = account.getIBAN();
        account.deposit(1000);

        Hotel hotel = new Hotel("XPTO123", "Paris");
        new Room(hotel, "01", Type.SINGLE);

        ActivityProvider provider = new ActivityProvider("XtremX", "ExtremeAdventure");
        Activity activity = new Activity(provider, "Bush Walking", 20, 80, 3);
        new ActivityOffer(activity, this.begin, this.end);
    }

    @Test
    public void success() {
        Adventure adventure = new Adventure(this.broker, this.begin, this.end, 20, this.IBAN, 300);

        adventure.process();

        Assert.assertNotNull(adventure.getBankPayment());
        Assert.assertNotNull(adventure.getRoomBooking());
        Assert.assertNotNull(adventure.getActivityBooking());
    }

    @Test
    public void amountGreaterThanAvailable() {
        Adventure adventure = new Adventure(this.broker, this.begin, this.end, 20, this.IBAN, 1300);

        try {
            adventure.process();
            Assert.fail();
        } catch (BankException be) {
            Assert.assertNull(adventure.getBankPayment());
            Assert.assertNull(adventure.getRoomBooking());
            Assert.assertNull(adventure.getActivityBooking());
        }
    }

    @Test
    public void accountIBANNotInBank() {
        Adventure adventure = new Adventure(this.broker, this.begin, this.end, 20, "NULL01", 300);

        try {
            adventure.process();
            Assert.fail();
        } catch (BankException be) {
            Assert.assertNull(adventure.getBankPayment());
            Assert.assertNull(adventure.getRoomBooking());
            Assert.assertNull(adventure.getActivityBooking());
        }
    }

    @Test
    public void noHotelRoomAvailable() {
        Adventure adventure = new Adventure(this.broker, this.begin, this.end, 20, this.IBAN, 300);
        Hotel.reserveHotel(Type.SINGLE, this.begin, this.end); // Simulate unavailability through conflict

        try {
            adventure.process();
            Assert.fail();
        } catch (HotelException he) {
            Assert.assertNotNull(adventure.getBankPayment());
            Assert.assertNull(adventure.getRoomBooking());
            Assert.assertNull(adventure.getActivityBooking());
        }
    }

    @Test
    public void ageUnderRequiredMinimum() {
        Adventure adventure = new Adventure(this.broker, this.begin, this.end, 19, this.IBAN, 300);

        adventure.process();

        Assert.assertNotNull(adventure.getBankPayment());
        Assert.assertNotNull(adventure.getRoomBooking());
        Assert.assertNull(adventure.getActivityBooking());
    }

    @Test
    public void ageOverRequiredMaximum() {
        Adventure adventure = new Adventure(this.broker, this.begin, this.end, 81, this.IBAN, 300);

        adventure.process();

        Assert.assertNotNull(adventure.getBankPayment());
        Assert.assertNotNull(adventure.getRoomBooking());
        Assert.assertNull(adventure.getActivityBooking());
    }

    @After
    public void tearDown() {
        Bank.banks.clear();
        Hotel.hotels.clear();
        ActivityProvider.providers.clear();
        Broker.brokers.clear();
    }
}
