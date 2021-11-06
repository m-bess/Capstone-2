package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO {

    private final JdbcTemplate jdbcTemplate;
    private final JdbcAccountDAO accountDAO;


    public JdbcTransferDAO(DataSource dataSource, JdbcAccountDAO accountDAO) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.accountDAO = accountDAO;
    }

    @Override
    public void createTransfer(Transfer transfer, Principal principal) {

        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?)";

         jdbcTemplate.update(sql,2, transfer.getTransferStatusID(), transfer.getAccountFromID(), transfer.getAccountToID(), transfer.getAmount());
        // transfer.setTransferID(transferID);
        // We set jdbctemp to transferID, returning transferID in sql statement
        accountDAO.sendMoney(transfer.getAmount(), transfer.getAccountToID());
        accountDAO.removeMoney(transfer.getAmount(), principal);
    }

//    @Override
//    public Transfer getTransferByID(int id){
//        Transfer transfer = null;
//        String sql = "Select transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
//                "From transfers where transfer_id = ?";
//        try{
//            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,id);
//            if(results.next()){
//                transfer = mapRowToTransfer(results);
//            }
//        }catch(NullPointerException npe){
//
//        }return transfer;
//    }




    @Override
    public void requestMoneyFromUser( Account senderAccount, int amount) {

    }


    @Override
    public List<Transfer> listUserTransfers(Principal principal) {
        List<Transfer> userTransfers = new ArrayList<>();

        String sentTransfersSql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers " +
                "WHERE account_from = " +
                "(SELECT account_id FROM accounts a " +
                "INNER JOIN users u " +
                "ON u.user_id = a.user_id " +
                "WHERE username = ?);";
        SqlRowSet sentResults = jdbcTemplate.queryForRowSet(sentTransfersSql, principal.getName());
        while(sentResults.next()) {
            Transfer transfer = mapRowToTransfer(sentResults);
            userTransfers.add(transfer);
        }

        String receivedTransfersSql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers " +
                "WHERE account_to = " +
                "(SELECT account_id FROM accounts a " +
                "INNER JOIN users u " +
                "ON u.user_id = a.user_id " +
                "WHERE username = ?);";
        SqlRowSet receivedResults = jdbcTemplate.queryForRowSet(receivedTransfersSql, principal.getName());
        while(receivedResults.next()) {
            Transfer transfer = mapRowToTransfer(receivedResults);
            userTransfers.add(transfer);
        }

        return userTransfers;
    }

    @Override
    public Transfer getTransferDetailsByID(int transferID, Principal principal) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, " +
                "transfer_type_id, " +
                "transfer_status_id, " +
                "account_from, " +
                "account_to, " +
                "amount " +
                "FROM transfers WHERE transfer_id = ? ;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferID);
        if(result.next()){
           transfer = mapRowToTransfer(result);
        }


        return transfer;
    }
    @Override
    public List<Transfer> listAllTransfers(){
        List<Transfer> transferList = new ArrayList<>();
        String sql = "SELECT transfer_id, " +
                "transfer_type_id, " +
                "transfer_status_id, " +
                "account_from, " +
                "account_to, " +
                "amount " +
                "FROM transfers;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()){
            Transfer transfer = mapRowToTransfer(results);
            transferList.add(transfer);
        }return transferList;
    }


    private Transfer mapRowToTransfer(SqlRowSet rowset) {
        Transfer transfer = new Transfer();
        transfer.setTransferID(rowset.getInt("transfer_id"));
        transfer.setTransferTypeID(rowset.getInt("transfer_type_id"));
        transfer.setTransferStatusID(rowset.getInt("transfer_status_id"));
        transfer.setAccountFromID(rowset.getInt("account_from"));
        transfer.setAccountToID(rowset.getInt("account_to"));
        transfer.setAmount(rowset.getDouble("amount"));
        return transfer;
    }
}
