package rental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarCompanyLoader {

    public static CarRentalCompany loadRental(String name, String datafile) {
        try {
            List<Car> cars = loadData(datafile);
            CarRentalCompany company = new CarRentalCompany(name, cars);
            return company;
        } catch (NumberFormatException ex) {
            Logger.getLogger(CarCompanyLoader.class.getName()).log(Level.SEVERE, "bad file", ex);
        } catch (IOException ex) {
            Logger.getLogger(CarCompanyLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static List<Car> loadData(String datafile)
            throws NumberFormatException, IOException {

        List<Car> cars = new LinkedList<Car>();

        int nextuid = 0;

        //open file from jar
        BufferedReader in = new BufferedReader(new InputStreamReader(CarCompanyLoader.class.getClassLoader().getResourceAsStream(datafile)));
        //while next line exists
        while (in.ready()) {
            //read line
            String line = in.readLine();
            //if comment: skip
            if (line.startsWith("#")) {
                continue;
            }
            //tokenize on ,
            StringTokenizer csvReader = new StringTokenizer(line, ",");
            //create new car type from first 5 fields
            CarType type = new CarType(csvReader.nextToken(),
                    Integer.parseInt(csvReader.nextToken()),
                    Float.parseFloat(csvReader.nextToken()),
                    Double.parseDouble(csvReader.nextToken()),
                    Boolean.parseBoolean(csvReader.nextToken()));
            //create N new cars with given type, where N is the 5th field
            for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                cars.add(new Car(nextuid++, type));
            }
        }

        return cars;
    }
}