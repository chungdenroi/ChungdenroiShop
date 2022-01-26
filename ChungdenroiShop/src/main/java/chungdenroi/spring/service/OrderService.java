package chungdenroi.spring.service;

import chungdenroi.spring.model.Orders;
import chungdenroi.spring.model.Product;
import chungdenroi.spring.repository.OrderProductsRepository;
import chungdenroi.spring.repository.OrdersRepository;
import chungdenroi.spring.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OrderService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    OrderProductsRepository orderProductsRepository;

    private Orders orders;
    private int quantity;
    private List<Product> productList;

    private double totalPrice;
    public void addProduct(Product product) {
        productRepository.save(product);
        productList.add(product);
    }
    public void removeProduct(Product product) {
        productRepository.delete(product);
        productList.remove(product);

    }
    public double calTotalPrice(Product product) {
//        for (Product product : productList) {
//            double productPrice = productRepository.queryById(product.getId());
//            totalPrice += productPrice
//        }
        double productPrice = product.getPrice();
        return (productPrice * quantity);
    }

//    public ProductRepository getProductRepository() {
//        return productRepository;
//    }
//
//    public void setProductRepository(ProductRepository productRepository) {
//        this.productRepository = productRepository;
//    }
//
//    public OrdersRepository getOrdersRepository() {
//        return ordersRepository;
//    }

    public void setOrdersRepository(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public OrderProductsRepository getOrderProductsRepository() {
        return orderProductsRepository;
    }

    public void setOrderProductsRepository(OrderProductsRepository orderProductsRepository) {
        this.orderProductsRepository = orderProductsRepository;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
