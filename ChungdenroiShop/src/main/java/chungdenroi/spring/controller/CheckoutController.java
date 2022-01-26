package chungdenroi.spring.controller;

import chungdenroi.spring.controller.admin.CustomerController;
import chungdenroi.spring.controller.admin.OrderController;
import chungdenroi.spring.model.*;
import chungdenroi.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    OrderProductsRepository orderProductsRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerController customerController;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    AccountController accountController;
    @Autowired
    LoginController loginController;

    @Autowired
    OrderController orderController;

    //add product to cart
    @RequestMapping("/cart")
    public String addToCard(@RequestParam(name = "product_id") Long product_id,
                            @RequestParam(name = "product_quantity", defaultValue = "0") int product_quantity,
                            ModelMap model,
                            HttpSession session) {
        try {
            OrderProducts orderProducts = new OrderProducts();
            if(product_quantity == 0 || product_id.equals(null)) {
                System.out.println("Request data false");
            } else {

                System.out.println(product_id);
                System.out.println(product_quantity);

                Product product = productRepository.getById(product_id);
                System.out.println("Create new orderproduct ok");
                double totalePriceProduct = product.getPrice() * product_quantity;
                orderProducts = new OrderProducts(product, product_quantity, totalePriceProduct);
            }
            session.setAttribute("orderProducts",orderProducts);
            session.setAttribute("cartTotal", 1);

            return "redirect:/cart/view";

        } catch (NullPointerException e) {
            System.out.println("product is empty cart");
            return "redirect:/shop";
        }
    }

    //add product to list product cart
    @RequestMapping("/cart/view")
    public String cartProcess(HttpSession session, ModelMap model) {
        try{
            //if cart is empty - > add product to new cart
            if(session.getAttribute("orderProducts") ==  null) {
                return "redirect:/shop";
            } else {
                if(session.getAttribute("cart") == null) {
                    List<OrderProducts> cart = new ArrayList<OrderProducts>();
                    cart.add((OrderProducts) session.getAttribute("orderProducts"));
                    System.out.println("Add to cart success");
                    session.setAttribute("cart", cart);
                } else {
                    List<OrderProducts> cart = (List<OrderProducts>) session.getAttribute("cart");

                    OrderProducts newOrder = (OrderProducts) session.getAttribute("orderProducts");

                    //neu 2 sp giong nhua thi se cong so luong
                    int index = isExistProduct(newOrder.getProduct().getId(), cart);
                    if(index == -1) {
                        cart.add(newOrder);
                    } else {
                        int totalItemCurrent = cart.get(index).getOrderProductQuantity();
                        int quantityNewOrderItem = newOrder.getOrderProductQuantity();
                        //check tong so luong sp va tog sl orderiproduct
                        ProductDetail productDetail =  productDetailRepository.findProductDetailByProduct_Id(newOrder.getProduct().getId());
                        totalItemCurrent += quantityNewOrderItem;

                        //check quantity
                        if(totalItemCurrent > productDetail.getQuantity()) {
//                            session.setAttribute("messageS","Qúa số lượng sản phẩm");
                            return "redirect:/"+productDetail.getUrl();
                        } else cart.get(index).setOrderProductQuantity(totalItemCurrent);

                    }
                    System.out.println("Add more to cart success");
                    //create session cart & cart size
                    session.setAttribute("cart", cart);
                    session.setAttribute("cartTotal",cart.size());
                }
                session.removeAttribute("orderProducts");
                System.out.println("Remove session success");
                return "redirect:/shop";
//                return "cart/viewCart";
            }

        } catch (NullPointerException e) {
            System.out.println("Cart null");
            return "redirect:/shop";
        }
    }
    //view cart
    @RequestMapping("/view-cart")
    public String viewCart(HttpSession session){

        return "cart/viewCart";
    }

    //delete product cart
    @RequestMapping("/cart/delete/{index}")
    public String removeItemCart(@Valid @PathVariable int index, HttpSession session) {
        List<OrderProducts> cart = (List<OrderProducts>) session.getAttribute("cart");
        cart.remove(index);
        session.setAttribute("cartTotal", cart.size());
        return "cart/viewCart";
    }
    //checkout cart
    @RequestMapping("/cart/checkout")
    public String cartCheckout(HttpSession session, Model model) {
        try {
            // get info customer -> send to checkout
            Customer customer = new Customer();
            if(session.getAttribute("username") != null) {
                String username = (String) session.getAttribute("username");
                System.out.println("sesion username checkout: " + username);
                customer = loginController.getCustomerByUsernameSession(username);
            } else {
                System.out.println("Create new customer");
                customer = new Customer();
            }
            if(session.getAttribute("cart") != null) {
                List<OrderProducts> orderProductsList = (List<OrderProducts>) session.getAttribute("cart");
                List<Product> products = new ArrayList<Product>();
                List<ProductDetail> productDetails = new ArrayList<ProductDetail>();

                for (OrderProducts orderProduct : orderProductsList) {
                    Product product = orderProduct.getProduct();
                    products.add(product);
                    ProductDetail productDetail = productDetailRepository.findProductDetailByProduct_Id(product.getId());
                    productDetails.add(productDetail);
                }
                Orders order = new Orders();
                OrderDetail orderDetail = new OrderDetail();
                model.addAttribute("order",order);
                model.addAttribute("orderProducts",orderProductsList);
                model.addAttribute("orderDetail",orderDetail);
                model.addAttribute("customer",customer);
                System.out.println("send data success");
                return "cart/checkout";
            } else return "redirect:/cart/view";

        } catch (NullPointerException e) {
            System.out.println("Session null: "  + e.getMessage());
            return "404";
        }

    }
    //checkout quick buy
    @RequestMapping("/checkout")
    public String checkout(@Valid @RequestParam(name = "product_id") Long product_id,
                           @Valid @RequestParam(name = "product_quantity") int product_quantity,
                           Customer customer,
                           Model model,
                           HttpSession session) {

        String username = (String) session.getAttribute("username");
        System.out.println("sesion checkout: " + username);
        try {
            if(username.length() != 0) {
                customer = loginController.getCustomerByUsernameSession(username);
            } else {
                System.out.println("Create new customer");
                customer = new Customer();
            }
        } catch (NullPointerException e) {
            System.out.println("Session null: "  + e.getMessage());
        }
        if(productRepository.countProductById(product_id) > 0) {
            Product product = productRepository.getById(product_id);
            System.out.println("Product name: " + product.getName());
            ProductDetail productDetail = productDetailRepository.findProductDetailByProduct_Id(product_id);
            Orders order = new Orders();
            List<OrderProducts> orderProducts = new ArrayList<OrderProducts>();
            orderProducts.add(new OrderProducts(product,product_quantity, (product_quantity*product.getPrice())));
            OrderDetail orderDetail = new OrderDetail();

            model.addAttribute("order",order);
            model.addAttribute("orderProducts",orderProducts);
            model.addAttribute("orderDetail",orderDetail);
            model.addAttribute("customer",customer);
            System.out.println("send data success");

            session.setAttribute("cart", orderProducts);
            return "checkout";
        } else return "404";
    }

    //order process -> ca cai controller nay e lam ngao vl =:v
    @RequestMapping("/creatingOrder")
    public String createOrder(
            @Valid Customer customer,
            @RequestParam(name = "doCreateAccount", required = false, defaultValue = "no") String doCreateAccount,
            @RequestParam(name = "message", required = false, defaultValue = "") String message,
            @RequestParam(name = "payment", required = false,defaultValue = "no") String payment,
            Model model, HttpSession session) {
        try
        {
            System.out.println("------------------------------");
            System.out.println("Customer name: " + customer.getFullname());
            System.out.println("Payment method : " + payment);
            System.out.println("------------------------------");

            if(session.getAttribute("cart") != null ) {
                List<OrderProducts> orderProductsList = (List<OrderProducts>) session.getAttribute("cart");

                //cal total payment
                double totalPayment = 0;
                for (OrderProducts orderProduct : orderProductsList) {
                    totalPayment += orderProduct.getTotalPriceProduct();
                }

                //save customer
                if (!saveCustomer(customer)) {
                    return "checkout";
                } else {
                    System.out.println("Full name cus: " + customer.getFullname());
                    //get now day
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String cuDate = LocalDateTime.now().format(formatter);
                    System.out.println(cuDate);

                    //create order
                    Orders order = new Orders();
                    System.out.println("ID cus: " + customer.getId());
                    //check save order
                    Long orderID = saveOrder(order, customer, cuDate, totalPayment, payment);
                    if (orderID != -1L) {
                        System.out.println("Full name: " + customer.getFullname());
                        int o = 0;
                        for (OrderProducts orderProduct : orderProductsList) {
                            ProductDetail productDetail = productDetailRepository.findProductDetailByProduct_Id(orderProduct.getProduct().getId());
                            //check quantity of product
                            if(!orderController.setQuantityProduct(productDetail, orderProduct.getOrderProductQuantity(), 0)) {
                                System.out.println("Quantity product = 0");
                                o = 1;
                            } else {
                                saveOrderProduct(orderProduct, order);
                            }
                        }
                        if(o==1) {
                            System.out.println("Delete order falied success");
                            ordersRepository.deleteById(orderID);
                            return "redirect:/shop";
                        }
                        OrderDetail orderDetail = new OrderDetail();
                        //check save order detail
                        if (saveOrderDetail(order, customer, orderDetail, message)) {

                            if (doCreateAccount.equals("yes")) {
                                UserAccount userAccount = new UserAccount();
                                //check create account
                                if (createUserAccount(customer, userAccount)) {
                                    System.out.println(userAccount.getUsername() + " | " + userAccount.getPassword());
                                    model.addAttribute("username", userAccount.getUsername());
                                    model.addAttribute("password", userAccount.getPassword());
                                    model.addAttribute("order_id", order.getId());

                                    System.out.println("-order success with user");

                                    /*clear data*/
                                    session.removeAttribute("cart");
                                    session.setAttribute("cartTotal", 0);
                                    return "ordersuccess";
                                }
                            } else System.out.println("Not say yes ");

                            model.addAttribute("order_id", order.getId());
                            /*clear data*/
                            session.removeAttribute("cart");
                            System.out.println("-order success with non-user");
                            session.setAttribute("cartTotal", 0);
                            return "ordersuccess";

                        }else return "checkout";

                    } else System.out.println("orderproduct null");
                }
            } return "redirect:/";

        } catch (NullPointerException e) {
            System.out.println("Cart " + e.getMessage());
            System.out.println("back to home page");
            return "redirect:/";
        }
    }
    public boolean saveCustomer(Customer customer) {
        try{
            if(customerRepository.existsCustomerByMobilenumber(customer.getMobilenumber())) {
                System.out.println("Customer has been already");
                return true;
            } else {
                customerRepository.save(customer);
                System.out.println("save customer success");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Save customer faild: " + e.getMessage());
            return false;
        }
    }
    public Long saveOrder(Orders order, Customer customer, String date, double totalPrice, String paymentMethod) {
        try {
            System.out.println("Full name sav : " +  customer.getFullname());
            if(paymentMethod.equals("pay_on")) {
                order.setStatus("Paying");
            } else {order.setStatus("Pending");}
            Customer cus = customerRepository.findCustomerByMobilenumber(customer.getMobilenumber());
            order.setName("Order#"+cus.getFullname()+"#"+cus.getId());
            System.out.println("ID CUSTOMER: " + cus.getId());
            order.setTotalPrice(totalPrice);
            order.setCustomer(cus);
            order.setOrderDate(date);
            ordersRepository.save(order);
            System.out.println("Save order success");
            return order.getId();
        } catch (Exception e){
            System.out.println("Can not save order: " + e.getMessage());
            return -1L;
        }
    }
    public void saveOrderProduct(OrderProducts orderProduct, Orders order) {
        try
        {
            orderProduct.setOrders(order);
            orderProductsRepository.save(orderProduct);
            System.out.println("Save orderproduct success");
        } catch (Exception e) {
            System.out.println("Can not save order product : " + e.getMessage());
        }
    }
    public boolean saveOrderDetail(Orders orders, Customer customer, OrderDetail orderDetail, String message) {
        try {
            orderDetail.setOrder(orders);
            orderDetail.setDeliveryAddress(customer.getAddress());
            orderDetail.setEmail(customer.getEmail());
            orderDetail.setMobilenumber(customer.getMobilenumber());
            orderDetail.setMessage(message);
            orderDetailRepository.save(orderDetail);
            System.out.println("Save orderdetail success");
            return true;
        } catch (Exception e) {
            System.out.println("Can not save order detail");
            return false;
        }
    }
    public boolean createUserAccount(Customer customer, UserAccount userAccount) {
        try {
            userAccount.setPermission("user");
            userAccount.setUsername("user"+customer.getMobilenumber());
            userAccount.setPassword("user"+customer.getMobilenumber().substring(4,9));
            if(userAccountRepository.existsByUsername(userAccount.getUsername())) {
                System.out.println("Has been already account wih this information");
                return false;
            } else {
                userAccount.setEmail(customer.getEmail());
                userAccount.setMobilenumber(customer.getMobilenumber());
                userAccount.setAddress(customer.getAddress());
                userAccountRepository.save(userAccount);
                System.out.println("create account success");
                return true;
            }

        } catch (Exception e) {
            System.out.println("can not create user acc : " + e.getMessage());
            return false;
        }
    }


    public int isExistProduct(Long id, List<OrderProducts> cart) {
        for (int i = 0; i< cart.size() ; i++) {
            if(cart.get(i).getProduct().getId() == id) {
                return i;
            }
        }
        return -1;
    }
}
