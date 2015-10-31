/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;

/**
 *
 * @author Pablo
 */
public class Session {
    
    @PersistenceContext
    protected EntityManager em;
    
    protected CarRentalCompany getCompany(String companyName) {
        return (CarRentalCompany) em.createQuery("SELECT c FROM CarRentalCompany c WHERE c.name LIKE :name")
                .setParameter("name", companyName)
                .getResultList().get(0);
    }
    
}
