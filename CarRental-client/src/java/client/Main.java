package client;

import java.util.Date;
import java.util.List;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractTestManagement<CarRentalSessionRemote, ManagerSessionRemote> {

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        
        Main main = new Main("trips");
        
        // TODO: use updated manager interface to load cars into companies
        ManagerSessionRemote manager = main.getNewManagerSession("Dit doet", " er niet toe.");
        manager.loadData("Dockx");
        manager.loadData("Hertz");
        
        System.out.println("Car types for Dockx: ");
        for(CarType ct : manager.getCarTypes("Dockx")) {
            System.out.println("\t" + ct.toString());
        }
        
        System.out.println("Car types for Hertz: ");
        for(CarType ct : manager.getCarTypes("Hertz")) {
            System.out.println("\t" + ct.toString());
        }
        
        main.run();
    }
    
    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        CarRentalSessionRemote out = (CarRentalSessionRemote) new InitialContext().lookup(CarRentalSessionRemote.class.getName());
        out.setRenterName(name);
        return out;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        ManagerSessionRemote out = (ManagerSessionRemote) new InitialContext().lookup(ManagerSessionRemote.class.getName());
        return out;
    }
    
    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        System.out.println("Available car types between "+start+" and "+end+":");
        for(CarType ct : session.getAvailableCarTypes(start, end))
            System.out.println("\t"+ct.toString());
        System.out.println();
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String carRentalName) throws Exception {
        session.createQuote(carRentalName, new ReservationConstraints(start, end, carType));
    }

    @Override
    protected List<Reservation> confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        return session.confirmQuotes();
    }
    
    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String renterName) throws Exception {
        return ms.getNumberOfReservationsBy(renterName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String name, String carType) throws Exception {
        return ms.getNumberOfReservations(name, carType);
    }

    @Override
    protected String getCheapestCarType(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        return session.getCheapestCarType(start, end).getName();
    }

    @Override
    protected String getMostPopularCarRentalCompany(ManagerSessionRemote ms) throws Exception {
        return ms.getMostPopularCarRentalCompany();
    }
}