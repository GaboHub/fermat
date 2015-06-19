package com.bitdubai.fermat_cry_plugin.layer.crypto_vault.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.ProtocolStatus;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Transaction;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.TransactionStatus;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoStatus;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantInsertRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantUpdateRecord;

import java.util.List;
import java.util.UUID;

/**
 * Created by rodrigo on 2015.06.17..
 */
public class CryptoVaultDatabaseActions {
    Database database;

    public CryptoVaultDatabaseActions(Database database){
        this.database = database;
    }

    public void saveIncomingTransaction(String txHash){
        DatabaseTable cryptoTxTable;
        cryptoTxTable = database.getTable(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_NAME);
        DatabaseTableRecord incomingTxRecord =  cryptoTxTable.getEmptyRecord();
        incomingTxRecord.setStringValue(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_TRX_ID_COLUMN_NAME, UUID.randomUUID().toString());
        incomingTxRecord.setStringValue(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_TRX_HASH_COLUMN_NAME, txHash);
        incomingTxRecord.setStringValue(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_PROTOCOL_STS_COLUMN_NAME, ProtocolStatus.TO_BE_NOTIFIED.toString());
        incomingTxRecord.setStringValue(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_TRANSACTION_STS_COLUMN_NAME, CryptoStatus.IDENTIFIED.toString());
        try {
            cryptoTxTable.insertRecord(incomingTxRecord);
        } catch (CantInsertRecord cantInsertRecord) {
            //todo see how I will handle this
            cantInsertRecord.printStackTrace();
        }
    }

    /**
     * Validates if the transaction ID passed is new or not. This helps to decide If I need to apply the transactions or not
     * @param txId the ID of the transaction
     * @return
     */
    public boolean isNewFermatTransaction(UUID txId){
        DatabaseTable fermatTxTable;
        fermatTxTable = database.getTable(CryptoVaultDatabaseConstants.FERMAT_TRANSACTIONS_TABLE_NAME);
        fermatTxTable.setStringFilter(CryptoVaultDatabaseConstants.FERMAT_TRANSACTIONS_TABLE_TRX_ID_COLUMN_NAME, txId.toString(), DatabaseFilterType.EQUAL);
        /**
         * If I couldnt find any record with this transaction id, then it is a new transactions.
         */
        if (fermatTxTable.getRecords().isEmpty())
            return true;
        else
            return false;
    }


    /**
     * I will persist a new crypto transaction generated by our wallet.
     */
    public UUID persistNewTransaction(String txHash){
        UUID txId = UUID.randomUUID();

        DatabaseTable cryptoTxTable;
        cryptoTxTable = database.getTable(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_NAME);
        DatabaseTableRecord incomingTxRecord =  cryptoTxTable.getEmptyRecord();
        incomingTxRecord.setStringValue(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_TRX_ID_COLUMN_NAME, txId.toString());
        incomingTxRecord.setStringValue(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_TRX_HASH_COLUMN_NAME, txHash);
        /**
         * since the wallet generated this transaction, we dont need to inform it.
         */
        incomingTxRecord.setStringValue(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_PROTOCOL_STS_COLUMN_NAME, ProtocolStatus.NO_ACTION_REQUIRED.toString());
        incomingTxRecord.setStringValue(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_TRANSACTION_STS_COLUMN_NAME, CryptoStatus.IDENTIFIED.toString());
        try {
            cryptoTxTable.insertRecord(incomingTxRecord);
        } catch (CantInsertRecord cantInsertRecord) {
            //todo see how I will handle this
            cantInsertRecord.printStackTrace();
        }
        return txId;
    }

    /**
     * Will retrieve all the transactions that are in status pending ProtocolStatus = TO_BE_NOTIFIED
     * @return
     */
    public List<Transaction>  getPendingTransactions(){
        //todo complete this

        return null;
    }

    /**
     * will update the transaction to the new state
     * @param txHash
     * @param newState
     */
    public void updateCryptoTransactionStatus(String txHash, CryptoStatus newState){
        DatabaseTable cryptoTxTable;
        cryptoTxTable = database.getTable(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_NAME);
        cryptoTxTable.setStringFilter(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_TRX_HASH_COLUMN_NAME, txHash, DatabaseFilterType.EQUAL);
        DatabaseTableRecord toUpdate = cryptoTxTable.getRecords().get(0); //todo add validation that only one record exists

        /**
         * I set the Protocol status to the new value
         */
        toUpdate.setStringValue(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_TRANSACTION_STS_COLUMN_NAME, newState.toString());
        try {
            cryptoTxTable.updateRecord(toUpdate);
        } catch (CantUpdateRecord cantUpdateRecord) {
            //todo deals with this error
            cantUpdateRecord.printStackTrace();
        }

    }

    /**
     * Will update the protocol status of the passed transaction.
     * @param txId
     * @param newStatus
     */
    public void updateTransactionProtocolStatus(UUID  txId, ProtocolStatus newStatus){
        DatabaseTable cryptoTxTable;
        cryptoTxTable = database.getTable(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_NAME);
        cryptoTxTable.setStringFilter(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_TRX_ID_COLUMN_NAME, txId.toString(), DatabaseFilterType.EQUAL);
        DatabaseTableRecord toUpdate = cryptoTxTable.getRecords().get(0); //todo add validation that only one record exists

        /**
         * I set the Protocol status to the new value
         */
        toUpdate.setStringValue(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_PROTOCOL_STS_COLUMN_NAME, newStatus.toString());
        try {
            cryptoTxTable.updateRecord(toUpdate);
        } catch (CantUpdateRecord cantUpdateRecord) {
            //todo deals with this error
            cantUpdateRecord.printStackTrace();
        }

    }

    /**
     * returns the current protocol status of this transaction
     * @param txId
     * @return
     */
    public ProtocolStatus getCurrentTransactionProtocolStatus(UUID txId){
        DatabaseTable cryptoTxTable;
        cryptoTxTable = database.getTable(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_NAME);
        cryptoTxTable.setStringFilter(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_TRX_ID_COLUMN_NAME, txId.toString(), DatabaseFilterType.EQUAL);
        DatabaseTableRecord currentStatus = cryptoTxTable.getRecords().get(0); //todo add validation that only one record exists

        return ProtocolStatus.valueOf(currentStatus.getStringValue(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_PROTOCOL_STS_COLUMN_NAME));
    }

    /**
     * will return true if there are transactions in NO_BE_NOTIFIED status
     * @return
     */
    public boolean isPendingTransactions(){
        DatabaseTable cryptoTxTable;
        cryptoTxTable = database.getTable(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_NAME);
        cryptoTxTable.setStringFilter(CryptoVaultDatabaseConstants.CRYPTO_TRANSACTIONS_TABLE_PROTOCOL_STS_COLUMN_NAME,ProtocolStatus.TO_BE_NOTIFIED.toString() ,DatabaseFilterType.EQUAL);

        if (!cryptoTxTable.getRecords().isEmpty())
            return false;
        else
            return true;

    }

}
