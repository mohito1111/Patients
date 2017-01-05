package test.hibernate;

import by.grodno.gwtPersistence.server.ContactDatabase;
import by.grodno.gwtPersistence.shared.Patient;

import java.util.List;

public class TestContactDatabase {
    public static void main(String[] args) {
        List<Patient> patientsList;// = ContactDatabase.getPatientsList();
        patientsList = ContactDatabase.findPatientByFirsName("А");
        print(patientsList);
    }

    public static void print(List<Patient> patientsList) {
        for (Patient o : patientsList) {
            System.out.println(o);
        }
    }

    public static void add(Patient p) {
        ContactDatabase.addNewPatient(p);
    }
}
