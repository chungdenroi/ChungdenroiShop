package chungdenroi.spring.controller;

import chungdenroi.spring.controller.admin.OrderController;
import chungdenroi.spring.model.OrderDetail;
import chungdenroi.spring.model.OrderProducts;
import chungdenroi.spring.model.Orders;
import chungdenroi.spring.model.ProductDetail;
import chungdenroi.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;

import java.util.List;

//@ComponentScan
@Configuration
//@Component
//@Controller
public class Service {
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    OrderController orderController;
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    OrderProductsRepository orderProductsRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductDetailRepository productDetailRepository;

    //update order orderProduct
    public void _updateOrder(Long id, Model model) {
        Orders order = ordersRepository.getById(id);
        OrderDetail orderDetail = orderDetailRepository.getById(order.getId());
        List<OrderProducts> orderProductsList = orderProductsRepository.findAllByOrders_Id(order.getId());
        OrderProducts orderProduct = new OrderProducts();
        model.addAttribute("order", order);
        model.addAttribute("orderDetail", orderDetail);
        model.addAttribute("orderProductsList", orderProductsList);
        model.addAttribute("orderProducts", orderProduct);
    }

    //send to orderDetail
    public void _updateOrderDt(Long id, Model model) {
        OrderDetail orderDetail = orderDetailRepository.getById(id);
        model.addAttribute("orderDetail", orderDetail);
    }

    //save to orderDetail
    public boolean _saveOrderDetail(Long orderDetailID, OrderDetail orderDetail) {
        try {
            if(!orderDetailRepository.existsById(orderDetailID)) {
                return  false;
            } else {
                System.out.println("Call save update detail method");
                orderDetail.setId(orderDetailID);
                orderDetailRepository.save(orderDetail);
                System.out.println("Save success");
                return true;
            }
        } catch (Exception e) {
            return  false;
        }
    }

    //view total order
    public void _viewOrderDetail(Model model, Long id) {
        Orders order = ordersRepository.getById(id);
        List<OrderProducts> orderProducts = orderProductsRepository.findAllByOrders_Id(id);
        OrderDetail orderDetail = orderDetailRepository.findOrderDetailByOrder_Id(order.getId());
        model.addAttribute("order", order);
        model.addAttribute("orderDetail", orderDetail);
        model.addAttribute("orderProducts", orderProducts);
    }
    //set quantity product when edit/add order
    public boolean setQuantityProduct(ProductDetail productDetail, int quntityBuy) {
        int curentQuantity = productDetail.getQuantity();
        if(curentQuantity <= quntityBuy) {
            return false;
        } else {
            productDetail.setQuantity(curentQuantity - quntityBuy);
            productDetailRepository.save(productDetail);
            return true;
        }
    }
}
