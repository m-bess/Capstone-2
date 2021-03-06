package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreInvocationAttribute;

import java.security.Principal;
import java.util.List;

public interface UserDao {

    public User sendMoney(Double amount, Integer sendingToID, Principal principal);

    List<User> findAllUsers(Principal principal);

    List<User> findAllOtherUsers(Principal principal);

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);
}
