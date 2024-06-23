package com.dowglasmaia.wallet.service;

import com.dowglasmaia.wallet.entity.TransactionEntity;

public interface AuditService {

    void sendMessage(TransactionEntity transaction);
}
