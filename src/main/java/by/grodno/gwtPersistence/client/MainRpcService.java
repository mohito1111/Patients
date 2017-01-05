package by.grodno.gwtPersistence.client;

import by.grodno.gwtPersistence.shared.Patient;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;


@RemoteServiceRelativePath("gwtPersistService")
public interface MainRpcService extends RemoteService {

    List<Patient> getAllPatients();

    void addNewPatient(Patient patient);

    void updatePatient(Patient patient);

    List<Patient> findPatient(String searchValue);

    void deletePatient(Patient patient);
}
