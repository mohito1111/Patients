package by.grodno.gwtPersistence.client;

import by.grodno.gwtPersistence.shared.Patient;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface MainRpcServiceAsync {
    void getAllPatients(AsyncCallback<List<Patient>> async);

    void findPatient(String searchValue, AsyncCallback<List<Patient>> async);

    void deletePatient(Patient patient, AsyncCallback<Void> async);

    void updatePatient(Patient patient, AsyncCallback<Void> async);

    void addNewPatient(Patient patient, AsyncCallback<Void> async);
}
