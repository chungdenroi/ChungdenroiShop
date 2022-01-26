package chungdenroi.spring.controller;

import chungdenroi.spring.model.Customer;
import chungdenroi.spring.model.UserAccount;
import chungdenroi.spring.repository.CustomerRepository;
import chungdenroi.spring.repository.UserAccountRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class LoginController {
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    CustomerRepository customerRepository;

    @RequestMapping("/login")
    public String _login(Model model) {
        UserAccount userAccount = new UserAccount();
        model.addAttribute("userAccount",userAccount);
        return "login";
    }

//    @RequestMapping("/checkLogin")
//    public RedirectView Login(@Valid @RequestParam(name = "username", required = true) String username,
//                                   @Valid @RequestParam(name = "password", required = true) String password, HttpSession session) {
//        if(checkLogin(username,password)) {
//            System.out.println("Login success");
//            UserAccount userAccount = userAccountRepository.findUserAccountByUsernameAndPassword(username,password);
//            String permission = userAccount.getPermission();
//            if(permission.equals("user")) {
//                System.out.println("ok");
//                session.setAttribute("username", username);
//                return new RedirectView("account");
//            } else if(permission.equals("admin")) {
//                session.setAttribute("username", username);
//                session.setAttribute("password", password);
//                System.out.println("Admin account");
//                return new RedirectView("admin");
//            } else {
//                userAccount.setPermission("user");
//                userAccountRepository.save(userAccount);
//                System.out.println("set permission success");
//            }
//        }
//        else {
//            return new RedirectView("login");
//        }
//        return new RedirectView("account");
//    }

    @RequestMapping("/checkLogin")
    public String Login(@Valid @RequestParam(name = "username", required = true) String username,
                              @Valid @RequestParam(name = "password", required = true) String password, HttpSession session, Model model) {
        UserAccount userAccount = new UserAccount();
        model.addAttribute("userAccount",userAccount);
        if(checkLogin(username,password)) {
            userAccount = userAccountRepository.findUserAccountByUsernameAndPassword(username,password);
            String permission = userAccount.getPermission();
            if(permission.equals("user")) {
                session.setAttribute("username", username);
                return "redirect:/account";
            } else if(permission.equals("admin")) {
                session.setAttribute("username", username);
                session.setAttribute("password", password);
                return "redirect:/admin";
            } else {
                userAccount.setPermission("user");
                userAccountRepository.save(userAccount);
                return "login";
            }
        }
        else {
            model.addAttribute("message","Tên đăng nhập hoặc mật khẩu không chính xác!");
            return "login";
        }
    }


    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("username");
        return "redirect:/";
    }
    @RequestMapping("/signup")
    public String signup(Model model) {
        UserAccount userAccount = new UserAccount();
        Customer customer = new Customer();
        model.addAttribute("userAccount", userAccount);
        model.addAttribute("customer", customer);
        return "registration";
    }

    @RequestMapping("/register")
    public RedirectView register(
            @Valid @RequestParam(name = "fullname", required = false, defaultValue = "") String fullname,
            @Valid @RequestParam(name = "username", required = false, defaultValue = "") String username,
            @Valid @RequestParam(name = "password", required = false, defaultValue = "") String password,
            @Valid @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @Valid @RequestParam(name = "mobilenumber", required = false, defaultValue = "") String mobilenumber,
            @Valid @RequestParam(name = "address", required = false, defaultValue = "") String address,
            @Valid @RequestParam(name = "pwrepeat", required = false, defaultValue = "") String pwrepeat,
            HttpSession session, Model model, HttpServletRequest request) {
        try {
            System.out.println("fullname : " + fullname + ", username: " + username + ", pass: " + password + ", email: " + email + ", mobile: " + mobilenumber);
            if(!userAccountRepository.existsUserAccountByUsernameOrMobilenumberOrEmail(username,mobilenumber,email)) {
                Customer customer = new Customer(fullname,mobilenumber,email,address);
                UserAccount userAccount = new UserAccount(username,password,mobilenumber, email, "user", address);
                customerRepository.save(customer);
                userAccountRepository.save(userAccount);
                System.out.println("Signup account success");
                session.setAttribute("username", username);
                return new RedirectView("account");
            } else  System.out.println("Username has been already"); return new RedirectView("signup");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new RedirectView("signup");
        }
    }

    public ModelAndView sendMessage(String message, String view, Model model) {
        model.addAttribute("message", message);
        return new ModelAndView(view);
    }
    public Customer getCustomerByUsernameSession(String username) {
        UserAccount userAccount = userAccountRepository.findUserAccountByUsername(username);
        Customer customer = customerRepository.findCustomerByMobilenumber(userAccount.getMobilenumber());
        return customer;
    }
    public boolean checkLogin(String username, String password) {
        if(userAccountRepository.findByUsernameAndPassword(username,password).isPresent()){
            return true;
        } else return false;
    }

}


