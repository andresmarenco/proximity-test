package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.MachineConfig;
import com.xyz.vendingmachine.machine.dto.CashDTO;
import com.xyz.vendingmachine.machine.exception.InvalidSaleException;
import com.xyz.vendingmachine.machine.model.Cash;
import com.xyz.vendingmachine.machine.model.CashType;
import com.xyz.vendingmachine.machine.repository.CashRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link CashService}
 * @author amarenco
 */
@Service("cashService")
public class CashServiceImpl implements CashService {
    /** Multiplier for the denominations of the cash */
    private static final BigDecimal MULTIPLIER = BigDecimal.valueOf(100);

    @Autowired
    private CashRepository cashRepository;

    @Autowired
    private MachineConfig machineConfig;


    @Override
    public List<CashDTO> getAll() {
        return cashRepository.findAll(Sort.by(Sort.Direction.DESC, "denomination")).stream()
                .map(cash -> {
                    CashDTO dto = new CashDTO();

                    if (cash.getType() == CashType.BILL) {
                        dto.setDenomination(BigDecimal.valueOf(cash.getDenomination())
                                .divide(MULTIPLIER, machineConfig.getDefaultMathContext()).intValue());
                    } else {
                        dto.setDenomination(cash.getDenomination());
                    }

                    dto.setQuantity(cash.getQuantity());
                    dto.setType(cash.getType());

                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public void clearCash() {
        cashRepository.clearCash();
    }


    @Override
    public BigDecimal countCash() {
        BigDecimal total = cashRepository.findAll().stream()
                .map(cash -> BigDecimal.valueOf(cash.getDenomination())
                        .divide(MULTIPLIER, machineConfig.getDefaultMathContext())
                        .multiply(BigDecimal.valueOf(cash.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total != null ? total : BigDecimal.ZERO;
    }


    @Override
    public void addCash(int denomination, CashType type, int quantity) throws IllegalArgumentException {
        if (isValidCash(denomination, type)) {
            int convertedDenomination = (type == CashType.BILL) ?
                    BigDecimal.valueOf(denomination).multiply(MULTIPLIER).intValue() : denomination;

            Cash cash = cashRepository.findById(convertedDenomination).orElseGet(() -> Cash.builder().denomination(convertedDenomination).type(type).build());
            cash.setQuantity(cash.getQuantity() + quantity);

            cashRepository.save(cash);
        } else {
            throw new IllegalArgumentException("Invalid cash denomination/type");
        }
    }


    @Override
    public boolean isValidCash(int denomination, CashType type) {
        switch (type) {
            case BILL:
                return machineConfig.getValidBills().contains(denomination);

            case COIN:
                return machineConfig.getValidCoins().contains(denomination);

            default:
                return false;
        }
    }


    @Override
    public BigDecimal getCashValue(int denomination, CashType type) {
        if (type == CashType.COIN) {
            return BigDecimal.valueOf(denomination).divide(MULTIPLIER, machineConfig.getDefaultMathContext());
        } else {
            return BigDecimal.valueOf(denomination);
        }
    }


    @Override
    public void setCoins(int denomination, int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("The quantity must be greater than 0");
        }

        if (isValidCash(denomination, CashType.COIN)) {
            Cash cash = cashRepository.findById(denomination).orElseGet(() -> Cash.builder().denomination(denomination).type(CashType.COIN).build());
            cash.setQuantity(quantity);

            cashRepository.save(cash);
        } else {
            throw new IllegalArgumentException("Invalid coin denomination");
        }
    }


    @Override
    public Map<Cash, AtomicInteger> changeCoins(BigDecimal total, BigDecimal cash) throws InvalidSaleException {
        if (cash.compareTo(total) < 0) {
            throw new InvalidSaleException("The cash must be more than the total");
        } else if (cash.compareTo(total) == 0) {
            return Collections.emptyMap();
        } else  {
            BigDecimal remaining = cash.subtract(total);
            Map<Cash, AtomicInteger> selectedMap = new HashMap<>();

            List<Cash> coins = cashRepository.findByType(CashType.COIN);
            if (!CollectionUtils.isEmpty(coins)) {
                coins.sort((c1, c2) -> Integer.compare(c2.getDenomination(), c1.getDenomination()));

                while (remaining.compareTo(BigDecimal.ZERO) > 0) {
                    Cash selectedCoin = coins.get(0);
                    AtomicInteger taken = selectedMap.computeIfAbsent(selectedCoin, key -> new AtomicInteger(0));
                    BigDecimal coinValue = getCashValue(selectedCoin.getDenomination(), CashType.COIN);

                    if (taken.get() >= selectedCoin.getQuantity() || coinValue.compareTo(remaining) > 0) {
                        coins.remove(0);
                    } else {
                        taken.incrementAndGet();
                        remaining = remaining.subtract(coinValue);
                    }

                    if (coins.isEmpty()) {
                        break;
                    }
                }
            }

            if (remaining.compareTo(BigDecimal.ZERO) == 0) {
                selectedMap.forEach((coin, quantity) -> {
                    coin.setQuantity(coin.getQuantity() - quantity.get());
                    cashRepository.save(coin);
                });

                return selectedMap;
            } else {
                throw new InvalidSaleException("There are not enough coins to change the money");
            }
        }
    }
}
