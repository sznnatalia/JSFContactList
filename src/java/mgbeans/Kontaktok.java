/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mgbeans;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import pojos.Contact;
import pojos.Phone;

/**
 *
 * @author Droti
 */
@ManagedBean
@SessionScoped
public class Kontaktok {

    private List<Contact> kontaktok;
    private Contact kivalasztott;
    private Contact szerkesztKontakt;
    private Phone kivalasztottTelszam;
    private List<Phone> telszamok;
    private List<String> tipusok;
    private String tipus;
    private String telszam;
    private String kereso;
    private boolean uj;

    private String datumString;

    public Kontaktok() {
        Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        kontaktok = session.createQuery("FROM Contact").list();

        session.close();

       

        tipusok = new ArrayList<>();
        tipusok.add("mobile");
        tipusok.add("homeOffice");
        tipusok.add("home");

        telszam = "";
        tipus = tipusok.get(0);

        uj = false;
    }

    public String modosit(Phone telszam) {
        kivalasztottTelszam = telszam;
        tipus = kivalasztottTelszam.getType();
        this.telszam = telszam.getNumber();
        return "telszam";
    }

    public void kivalaszt(Contact kontakt) {
        kivalasztott = kontakt;
        telszamok = new ArrayList<>(kontakt.getPhones());
    }

    public void torol(Phone telszam) {
        Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(telszam);
        session.getTransaction().commit();
        session.close();

        telszamok.remove(telszam);
        kivalasztott.getPhones().remove(telszam);

    }

    public String mentes() {
        boolean uj = true;
        if (kivalasztottTelszam != null) {
            kivalasztottTelszam.setNumber(telszam);
            kivalasztottTelszam.setType(tipus);
            uj = false;
        } else {
            kivalasztottTelszam = new Phone();
            kivalasztottTelszam.setContact(kivalasztott);
            kivalasztottTelszam.setNumber(telszam);
            kivalasztottTelszam.setType(tipus);

        }

        Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(kivalasztottTelszam);
        session.getTransaction().commit();
        session.close();

        if (uj) {
            telszamok.add(kivalasztottTelszam);
            kivalasztott.getPhones().add(kivalasztottTelszam);
        }

        return "index";
    }

    public String ujTelszam() {
        kivalasztottTelszam = null;
        return "telszam";
    }

    public void kereses() {
        kontaktok.clear();
        Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        //kontaktok = session.createCriteria(Contact.class).add(Restrictions.like("name", kereso, MatchMode.ANYWHERE)).list();
        kontaktok = session.createQuery("FROM Contact WHERE name LIKE :par1").setString("par1", kereso + "%").list();
        session.close();

    }

    public String ujKontakt() {
        szerkesztKontakt = new Contact();
        uj = true;
        datumString = "éééé-hh-nn";

        return "kontakt";
    }
    
    public void keresesTorles(){
         Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        kontaktok = session.createQuery("FROM Contact").list();
        

        session.close();
        
        kereso ="";
    }

    public String modositKontakt(Contact kontakt) {
        szerkesztKontakt = kontakt;

        datumString = kontakt.getBirthdate().toString();

        return "kontakt";
    }

    public String mentesKontakt() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date datum = null;
        try {
            datum = sdf.parse(datumString);
        } catch (ParseException ex) {
            System.out.println(ex);;
        }

        szerkesztKontakt.setBirthdate(datum);
        Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(szerkesztKontakt);
        session.getTransaction().commit();
        session.close();

        if (uj) {
            kontaktok.add(szerkesztKontakt);
        }

        return "index";
    }

    public void torolKontakt(Contact kontakt) {
        Session session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(kontakt);
        session.getTransaction().commit();
        session.close();

        kontaktok.remove(kontakt);

    }

    public Contact getKivalasztott() {
        return kivalasztott;
    }

    public void setKivalasztott(Contact kivalasztott) {
        this.kivalasztott = kivalasztott;
    }

    public List<Contact> getKontaktok() {
        return kontaktok;
    }

    public void setKontaktok(List<Contact> kontaktok) {
        this.kontaktok = kontaktok;
    }

    public Phone getKivalasztottTelszam() {
        return kivalasztottTelszam;
    }

    public void setKivalasztottTelszam(Phone kivalasztottTelszam) {
        this.kivalasztottTelszam = kivalasztottTelszam;
    }

    public List<Phone> getTelszamok() {
        return telszamok;
    }

    public void setTelszamok(List<Phone> telszamok) {
        this.telszamok = telszamok;
    }

    public List<String> getTipusok() {
        return tipusok;
    }

    public void setTipusok(List<String> tipusok) {
        this.tipusok = tipusok;
    }

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public String getTelszam() {
        return telszam;
    }

    public void setTelszam(String telszam) {
        this.telszam = telszam;
    }

    public String getKereso() {
        return kereso;
    }

    public void setKereso(String kereso) {
        this.kereso = kereso;
    }

    public String getDatumString() {
        return datumString;
    }

    public void setDatumString(String datumString) {
        this.datumString = datumString;
    }

    public Contact getSzerkesztKontakt() {
        return szerkesztKontakt;
    }

    public void setSzerkesztKontakt(Contact szerkesztKontakt) {
        this.szerkesztKontakt = szerkesztKontakt;
    }

}
