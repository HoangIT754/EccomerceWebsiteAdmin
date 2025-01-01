package com.UserProviderWithKeycloak.UserProvider.provider;

import com.UserProviderWithKeycloak.UserProvider.model.Customer;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class CustomerAdapter extends AbstractUserAdapterFederatedStorage {

    private final String id;
    private final Customer customer;

    public CustomerAdapter(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel, Customer customer) {
        super(session, realm, storageProviderModel);
        this.customer = customer;
        this.id = StorageId.keycloakId(storageProviderModel, customer.getId().toString());
    }

    @Override
    public String getUsername() {
        return customer.getUserName();
    }

    @Override
    public void setUsername(String s) {
        customer.setUserName(s);
    }

    @Override
    public String getId(){
        return id;
    }

    @Override
    public String getEmail(){
        return customer.getEmail();
    }

    @Override
    public void setEmail(String email){
        customer.setEmail(email);
    }

    public String getPassword() {
        return customer.getPassword(); // Thêm getter cho mật khẩu
    }

    public boolean isEnabled() {
        return customer.getActive() == 1; // Trả về trạng thái active
    }
}