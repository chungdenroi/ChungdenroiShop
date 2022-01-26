package chungdenroi.spring.model;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "orders_product")
public class OrderProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(cascade =CascadeType.DETACH )
    private Orders orders;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Product product;

    @NotNull
    @Column(nullable = false)
    @ColumnDefault("1")
    private int orderProductQuantity;

    @Column(nullable = false)
    @ColumnDefault("100000")
    private double totalPriceProduct;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public int getOrderProductQuantity() {
        return orderProductQuantity;
    }

    public void setOrderProductQuantity(int orderProductQuantity) {
        this.orderProductQuantity = orderProductQuantity;
    }

//    public Product getProduct() {
//        return product;
//    }
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
//
//    public double getTotalPriceProduct() {
//        return totalPriceProduct;
//    }
//
    public void setTotalPriceProduct(double totalPriceProduct) {
        this.totalPriceProduct = totalPriceProduct;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getTotalPriceProduct() {
        return totalPriceProduct;
    }

    public OrderProducts() {
    }

    public OrderProducts(Product product, int orderProductQuantity, double totalPriceProduct) {
        this.product = product;
        this.orderProductQuantity = orderProductQuantity;
        this.totalPriceProduct = totalPriceProduct;
    }

    public OrderProducts(Product product, int orderProductQuantity) {
        this.product = product;
        this.orderProductQuantity = orderProductQuantity;
    }
}
