package com.dowglasmaia.wallet.service.impl;

import com.dowglasmaia.wallet.entity.AccountEntity;
import com.dowglasmaia.wallet.repository.AccountRepository;
import com.dowglasmaia.wallet.service.FindBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindBalanceServiceImpl implements FindBalanceService {

    private final AccountRepository repository;

    @Autowired
    public FindBalanceServiceImpl(AccountRepository repository){
        this.repository = repository;
    }

    @Override
    public Mono<AccountEntity> findByUserId(String userId){
        return repository.findByUserId(userId);
    }
}
