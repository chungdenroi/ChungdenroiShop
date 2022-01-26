package chungdenroi.spring.controller.admin;

import chungdenroi.spring.controller.Service;
import chungdenroi.spring.model.*;
import chungdenroi.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/admin/order/")
public class OrderController {
    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderProductsRepository orderProductsRepository;


    @Autowired
    ProductDetailRepository productDetailRepository;

    @Autowired
    DashboardController dashboardController;

//    OrderService orderService;


    @RequestMapping("")
    public String getAllOrder(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                              Model model, HttpSession session) {
        if(dashboardController.checkLoginAdmin(session)) {
            Pageable pageable = PageRequest.of(page,7);
            Page<Orders> pageTotal = ordersRepository.findOrdersBy(pageable);
            int paging = pageTotal.getTotalPages();
            model.addAttribute("orders", pageTotal); //send list order
            model.addAttribute("pageTotal", paging); //send total page

            List<OrderDetail> orderDetails = orderDetailRepository.findAll();
            model.addAttribute("orders", pageTotal);
            model.addAttribute("orderDetails", orderDetails);
            System.out.println("Send data success");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String curentDate = LocalDate.now().format(formatter);
            model.addAttribute("curentDate", curentDate);

            return "admin/order/orderList";
        } else return "redirect:/login";
    }

    @RequestMapping("/{id}")
    public String getOrderByID(Model model,
                                 @PathVariable(value = "id") Long id) {
        _viewOrderDetail(model,id);
        return "admin/order/orderDetail";
    }

    @RequestMapping("/status/{status}")
    public String getOrderByStatus(Model model,
                               @PathVariable(value = "status") String status) {
        try{
            List<Orders> orders = ordersRepository.findAllByStatus(status);
            model.addAttribute("orders", orders);
            return "admin/order/orderList";
        } catch (Exception e) {
            return "redirect:/admin/order/";
        }
    }



    @RequestMapping("/customer/{customer_id}")
    public String getOrderByCustomerID(Model model,
                               @PathVariable(value = "customer_id") Long id) {
        List<Orders> orders = ordersRepository.findAllByCustomer_Id(id);
        model.addAttribute("orders", orders);
        return "admin/order/orderList";
    }


    //update Order +  orderProducts
    @RequestMapping("/update/{id}")
    public String updateOrder(
            @PathVariable (value = "id") Long id, Model model)  {
        _updateOrder(id, model);
        return "admin/order/orderUpdate";
    }

    //update orderDetail
    @RequestMapping("/updateDetail/{id}")
    public String updateOrderDetail(
            @PathVariable (value = "id") Long id, Model model)  {
        _updateOrderDt(id,model);
        return "admin/order/orderDetailUpdate";
    }

    //save update
    @RequestMapping("/saveUpdate")
    public String saveOrderUpdate (
            @Valid Orders order,
            @RequestParam(value = "orderStatus", required = false) String orderStatus,
            @RequestParam(value = "orderProductID") List<Long> orderProductIDs,
            @RequestParam(value = "ProductID") List<Long> ProductID,
            @RequestParam(value = "product_quantity") List<Integer> product_quantity,
            BindingResult result) {
        System.out.println("Call save update method");
        if(result.hasErrors()) {
            return "admin/order/orderUpdate";
        }
        List<OrderProducts> orderProductsList = orderProductsRepository.findAllByOrders_Id(order.getId());
        int size = orderProductsList.size();
        for (int i = 0; i< size; i++) {
            System.out.println("i = " +i);
            if(setQuantityProduct(productDetailRepository.findProductDetailByProduct_Id(ProductID.get(i)), product_quantity.get(i), orderProductsList.get(i).getOrderProductQuantity())) {
                orderProductsList.set(i, orderProductsRepository.getById(orderProductIDs.get(i)));
                orderProductsList.get(i).setOrderProductQuantity(product_quantity.get(i));
            } else {
                return "admin/order/orderUpdate";
            }
        }
        order.setStatus(orderStatus);
        ordersRepository.save(order);
        System.out.println("Save success");
        return "redirect:/admin/order/";
    }


//    int i = 1;
    //removew product in orderproduct
    @RequestMapping("/update/removeProduct/{orderProduct_id}")
        public String removeProduct(
                @Valid @PathVariable Long orderProduct_id) {
        try {
            if(orderProduct_id.equals(null)) {

                return "redirec:/admin/order/";
            } else {
                OrderProducts orderProduct = orderProductsRepository.getById(orderProduct_id);
                Orders orders = ordersRepository.getById(orderProduct.getOrders().getId());
                orderProductsRepository.delete(orderProduct);
//                System.out.println("send removeItem session " + i);
//                i++;
                return "redirect:/admin/order/update/"+orders.getId().toString();
            }
        } catch (NullPointerException e) {
            System.out.println("orderProductid is null");
            return "redirec:/admin/order/";
        }
    }


    //save update order Detail
    @RequestMapping("/saveUpdateDetail")
    public String saveOrderDetailUpdate (
            @Valid OrderDetail orderDetail,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "order", required = false) Long order_id,
            BindingResult result) {
        if(_saveOrderDetail(order_id,orderDetail)) {
            return "redirect:/admin/order/"+order_id;
        } else return "redirect:/admin/order/";

    }


    @RequestMapping("/delete/{id}")
    public String deleteOrder(
            @PathVariable(value = "id") Long id) {
        Orders order = ordersRepository.getById(id);
        OrderDetail orderDetail = orderDetailRepository.findOrderDetailByOrder_Id(id);
        List<OrderProducts> orderProducts = orderProductsRepository.findAllByOrders_Id(id);
        for (OrderProducts orderProduct : orderProducts) {
            System.out.println("Order: " + orderProduct.getId());
            orderProductsRepository.delete(orderProduct);
        }
        orderDetailRepository.delete(orderDetail);
        ordersRepository.delete(order);

        return "redirect:/admin/order/";
    }

    @RequestMapping("/search")
    public String searchOrder(
            Model model,
            @RequestParam(value = "name") String name) {
        System.out.println("call search medthod");
        List<Orders> orders  = ordersRepository.findOrdersByNameIsLike("%"+name+"%");
        model.addAttribute("orders", orders);
        System.out.println("Name search: " +  name);
        return "admin/order/orderList";
    }
    @RequestMapping("/asc")
    public String sortByAsc(Model model) {
        List<Orders> orders = ordersRepository.findAll(Sort.by(Sort.Direction.ASC,"name"));
        model.addAttribute("orders", orders);
        return "admin/order/orderList";
    }
    @RequestMapping("/desc")
    public String sortByDESC(Model model) {
        List<Orders> orders = ordersRepository.findAll(Sort.by(Sort.Direction.DESC,"name"));
        model.addAttribute("orders", orders);
        return "admin/order/orderList";
    }

    @RequestMapping("/date")
    public String viewByDate(
            Model model,
            @RequestParam(value = "date") String date) {


        model.addAttribute("curentDate", date);

        List<Orders> orders  = ordersRepository.findOrdersByOrderDateLike("%"+date+"%");
        model.addAttribute("orders", orders);
        return "admin/order/orderList";
    }

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
    public boolean setQuantityProduct(ProductDetail productDetail, int quntityBuyNew, int quantityOld) {
        int curentQuantity = productDetail.getQuantity();
        if(curentQuantity < quntityBuyNew) {
            return false;
        } else {
            productDetail.setQuantity((curentQuantity - quntityBuyNew)+quantityOld);
            productDetailRepository.save(productDetail);
            return true;
        }
    }

}
