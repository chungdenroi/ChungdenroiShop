package chungdenroi.spring.controller;

import chungdenroi.spring.controller.admin.OrderController;
import chungdenroi.spring.model.*;
import chungdenroi.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class AccountController {
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    OrderProductsRepository orderProductsRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    OrderController orderController;


    @RequestMapping("/account")
    public String account(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        try{
            if(username == null) {
                return "redirect:/login";
            } else {
                if(userAccountRepository.existsByUsername(username)) {
                    UserAccount userAccount = userAccountRepository.findUserAccountByUsername(username);
                    System.out.println(userAccount.getMobilenumber());
                    Customer customer = customerRepository.findCustomerByMobilenumber(userAccount.getMobilenumber());
                    model.addAttribute("username", username);
                    model.addAttribute("userAccount", userAccount);
                    model.addAttribute("customer", customer);
                    return "account/accountInfo";
                } else return "redirect:/login";
            }
        } catch (NullPointerException e) {
            System.out.println("User name is null - > back to login " + e.getMessage());
            return "redirect:/login";
        }
    }


    @RequestMapping("/account/{username}")
    public String updateAccount(@PathVariable String username, Model model) {
        if(username.equals(null)) {
            return "redirect:/404";
        } else {
            if(userAccountRepository.existsByUsername(username)) {
                UserAccount userAccount = userAccountRepository.findUserAccountByUsername(username);
                Customer customer = customerRepository.findCustomerByMobilenumber(userAccount.getMobilenumber());
                model.addAttribute("customer",customer);
                return "account/updateAccount";
            } else return "redirect:/404";
        }
    }

    @RequestMapping("/account/save")
    public String saveUpdateAccount(@Valid Customer customer, Model model, HttpSession session) {
        if(!customer.equals(null)) {
            String username = (String) session.getAttribute("username");
            System.out.println("Username account update: " + username);
            UserAccount userAccount = userAccountRepository.findUserAccountByUsername(username);
            userAccount.setMobilenumber(customer.getMobilenumber());
            userAccount.setAddress(customer.getAddress());
            userAccount.setEmail(customer.getEmail());
            customerRepository.save(customer);
            userAccountRepository.save(userAccount);
            return "redirect:/account";
        } else return "redirect:/404";
    }
    @RequestMapping("/account/change")
    public ModelAndView changeUserAccount(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        UserAccount userAccount = userAccountRepository.findUserAccountByUsername(username);
        model.addAttribute("userAccount", userAccount);
        return new ModelAndView("account/updateUserAccount");
    }
    @RequestMapping("/account/saveUser")
    public String saveUserAccount(
            @Valid @RequestParam(name = "id") Long id,
            @Valid @RequestParam(name = "username", defaultValue = "unknow", required = false) String usernameChange,
            @Valid @RequestParam(name = "new_pass", defaultValue = "unknow", required = false) String newPass,
            @Valid @RequestParam(name = "repeat_new_pass", defaultValue = "unknow", required = false) String repeatPass,
            HttpSession session,
            Model model) {
        String username = (String) session.getAttribute("username");
        if(usernameChange.equals("unknow") || newPass.equals("unknow") || repeatPass.equals("unknow") || id.equals(null)) {
            return "404";
        } else {
            if(userAccountRepository.existsByUsername(usernameChange) && !usernameChange.equals(username)) {
                System.out.println("Username has been already");
                model.addAttribute("message","Tên người dùng đã tồn tại. Vui lòng chọn tên người dùng khác!");
                return "redirect:/account/change";
            }
            if(newPass.equals(repeatPass)) {
                UserAccount userAccount = userAccountRepository.getById(id);
                userAccount.setPassword(newPass);
                if(username.equals(usernameChange)) {
                    System.out.println("username is not change");
                } else {
                    userAccount.setUsername(usernameChange);
                }
                userAccountRepository.save(userAccount);
                System.out.println("Change successs");
                session.removeAttribute("username");
                return "redirect:/login";
            } else return  "404";
        }
    }
    @RequestMapping("/account/order-history")
    public String viewOrders(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                             Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if(!username.equals(null)) {
            Pageable pageable = PageRequest.of(page,6);
            UserAccount userAccount = userAccountRepository.findUserAccountByUsername(username);
            Customer customer = customerRepository.findCustomerByMobilenumber(userAccount.getMobilenumber());
            Page<Orders> pageTotal = ordersRepository.findAllByCustomer_Id(pageable, customer.getId());

            int paging = pageTotal.getTotalPages();
            model.addAttribute("pageTotal", paging);

            model.addAttribute("ordersList",pageTotal);
            return "account/accountOrders";
        } else return "redirect:/account";
    }
    @RequestMapping("/account/order-history/cancel/{orderID}")
    public String cancelOrder(@Valid @PathVariable Long orderID) {
        try{
            if(orderID != null || ordersRepository.existsById(orderID)) {
                Orders orders = ordersRepository.getById(orderID);
                orders.setStatus("Canceled");
                ordersRepository.save(orders);
                return "redirect:/account/order-history";
            } else {
                return "404";
            }
        } catch (NullPointerException e){
            System.out.println("Null " + e.getMessage());
            return "404";
        }
    }

    @RequestMapping("/account/order-detail/{orderID}")
    public String viewOrder(Model model, @PathVariable Long orderID) {
        try{
            if(orderDetailRepository.existsById(orderID)) {
                orderController._viewOrderDetail(model,orderID);
//                OrderDetail orderDetail = orderDetailRepository.getById(orderID);
//                model.addAttribute("orderDetail", orderDetail);
                return "account/viewOrderDetail";

            } else {
                return "redirect:/account/order-history/";
            }
        } catch (NumberFormatException e) {
            return "redirect:/account/order-history/";
        }
    }

    @RequestMapping("/account/order-detail/update/{orderID}")
    public String updateOrderDetail (@PathVariable (value = "orderID") Long id, Model model) {
        try{
            if(ordersRepository.existsById(id)) {
                orderController._updateOrder(id, model);
                return "account/updateOrder";

            } else return "redirect:/account/order-history/";

        } catch (Exception e) {
            return "redirect:/account/order-history/";
        }
    }

    @RequestMapping("/account/order-detail/removeProduct/{orderProductID}")
    public String removeOrderProduct(@PathVariable(name = "orderProductID") Long id, Model model) {
        System.out.println("call remove product in orderproduct success");
        try {
            if(id.equals(null)) {
                return "redirect:/account/order-history/";
            } else {
                OrderProducts orderProduct = orderProductsRepository.getById(id);
                Orders orders = ordersRepository.getById(orderProduct.getOrders().getId());
                orderProductsRepository.delete(orderProduct);
                return "redirect:/account/order-detail/update/" + orders.getId().toString();
            }
        } catch (NullPointerException e) {
            System.out.println("orderProductid is null");
        } return "redirect:/account/order-history/";
    }

    @RequestMapping("/account/order-detail/update/saveUpdateDetail")
    public String saveOrderUpdate (
            @Valid OrderDetail orderDetail,
            @RequestParam(name = "orderID", required = false) Long orderID,
            @RequestParam(name = "acc-orderTotalPrice") double orderTotalPrice,
            @RequestParam(name = "orderProductID") List<Long> orderProductIDs,
            @RequestParam(name = "ProductID") List<Long> ProductID,
            @RequestParam(name = "product_quantity") List<Integer> product_quantity,
            BindingResult result) {
        System.out.println("Call save update method");
        try {
            if (ordersRepository.existsById(orderID)) {
                Orders orders = ordersRepository.getById(orderID);

                List<OrderProducts> orderProductsList = orderProductsRepository.findAllByOrders_Id(orderID);
                int size = orderProductsList.size();
                for (int i = 0; i < size; i++) {
                    System.out.println("i = " + i);
                    //check so luong san pham
                    if (orderController.setQuantityProduct(productDetailRepository.findProductDetailByProduct_Id(ProductID.get(i)), product_quantity.get(i), orderProductsList.get(i).getOrderProductQuantity())) {
                        orderProductsList.set(i, orderProductsRepository.getById(orderProductIDs.get(i)));
                        orderProductsList.get(i).setOrderProductQuantity(product_quantity.get(i));
                    } else {
                        return "redirect:/account/order-detail/update/" + orders.getId().toString();
                    }

                }
                orders.setTotalPrice(orderTotalPrice);
                if(!orders.getStatus().equals("Successed") || !orders.getStatus().equals("Canceled")) {
                    ordersRepository.save(orders);
                    System.out.println("Save success");
                    return "redirect:/account/order-detail/" + orderDetail.getId();
                } else return "redirect:/account/order-history/";
            } else System.out.println("Not fount id");
            return "redirect:/account/order-history/";
        } catch (Exception e) {
            return "redirect:/account/order-history/";
        }
    }
}

