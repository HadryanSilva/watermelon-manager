package br.com.hadryan.api.transaction.dto;

import br.com.hadryan.api.order.Order;
import br.com.hadryan.api.purchase.Purchase;
import br.com.hadryan.api.transaction.enums.Category;
import br.com.hadryan.api.transaction.enums.Type;

import java.time.LocalDate;

public class TransactionDTOFactory {

    public static TransactionDTO createFromOrder(Order order) {
        return new TransactionDTO(
                order.getField().getName(),
                "Order: " + order.getCustomer().getName(),
                Type.INCOME,
                Category.ORDER_PAYMENT,
                order.getTotal(),
                LocalDate.now()
        );
    }

    public static TransactionDTO createFromPurchase(Purchase purchase) {
        return new TransactionDTO(
                purchase.getName(),
                purchase.getDescription(),
                Type.EXPENSE,
                Category.SUPPLIES,
                purchase.getTotal(),
                LocalDate.now()
        );
    }

}
