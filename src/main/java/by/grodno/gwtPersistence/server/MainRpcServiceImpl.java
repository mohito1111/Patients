package by.grodno.gwtPersistence.server;

import by.grodno.gwtPersistence.client.MainRpcService;
import by.grodno.gwtPersistence.shared.Patient;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.List;

public class MainRpcServiceImpl extends RemoteServiceServlet implements MainRpcService {

    public MainRpcServiceImpl() {
    }

    @Override
    public List<Patient> getAllPatients() {
        return ContactDatabase.getPatientsList();
    }

    @Override
    public void addNewPatient(Patient patient) {
        ContactDatabase.addNewPatient(patient);
    }

    @Override
    public void updatePatient(Patient patient) {
        ContactDatabase.updatePatient(patient);
    }

    @Override
    public List<Patient> findPatient(String searchValue) {
        return ContactDatabase.findPatientByFirsName(searchValue);
    }

    @Override
    public void deletePatient(Patient patient) {
        ContactDatabase.deletePatient(patient);
    }

}
