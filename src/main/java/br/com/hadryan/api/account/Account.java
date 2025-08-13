package br.com.hadryan.api.account;

import br.com.hadryan.api.customer.Customer;
import br.com.hadryan.api.field.Field;
import br.com.hadryan.api.order.Order;
import br.com.hadryan.api.purchase.Purchase;
import br.com.hadryan.api.transaction.Transaction;
import br.com.hadryan.api.user.User;
import br.com.hadryan.api.vendor.Vendor;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "Account.withUsers",
                attributeNodes = @NamedAttributeNode("users")
        ),
        @NamedEntityGraph(
                name = "Account.withTransactions",
                attributeNodes = @NamedAttributeNode("transactions")
        ),
        @NamedEntityGraph(
                name = "Account.summary",
                attributeNodes = {
                        @NamedAttributeNode("users"),
                        @NamedAttributeNode("customers"),
                        @NamedAttributeNode("fields")
                }
        )
})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private BigDecimal incomes;

    @Column(nullable = false)
    private BigDecimal expenses;

    @OneToMany(mappedBy = "account")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Customer> customers = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Field> fields = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Vendor> vendors = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Purchase> purchases = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Order> orders = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getIncomes() {
        return incomes;
    }

    public void setIncomes(BigDecimal incomes) {
        this.incomes = incomes;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Vendor> getVendors() { return vendors; }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
