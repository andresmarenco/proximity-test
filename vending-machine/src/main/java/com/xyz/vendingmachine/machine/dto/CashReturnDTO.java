package com.xyz.vendingmachine.machine.dto;

import com.xyz.vendingmachine.machine.model.Cash;
import com.xyz.vendingmachine.machine.service.CashService;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DTO for the coins returned after a cash payment
 * @author amarenco
 */
@Data
public class CashReturnDTO {
    private BigDecimal total;
    private List<CoinDTO> change;

    /**
     * @param applicationContext the Spring application context
     * @param returnedChange the returned change
     */
    public CashReturnDTO(ApplicationContext applicationContext, Map<Cash, AtomicInteger> returnedChange) {
        total = BigDecimal.ZERO;
        change = new ArrayList<>();

        if (!CollectionUtils.isEmpty(returnedChange)) {
            CashService cashService = applicationContext.getBean(CashService.class);

            for (Map.Entry<Cash, AtomicInteger> coin : returnedChange.entrySet()) {
                int quantity = coin.getValue().get();

                if (quantity > 0) {
                    CoinDTO dto = new CoinDTO();
                    dto.setDenomination(coin.getKey().getDenomination());
                    dto.setQuantity(quantity);

                    change.add(dto);
                    total = total.add(cashService.getCashValue(dto.getDenomination(), coin.getKey().getType()).multiply(BigDecimal.valueOf(quantity)));
                }
            }
        }
    }


    @Data
    public static final class CoinDTO {
        private int denomination;
        private int quantity;
    }
}
