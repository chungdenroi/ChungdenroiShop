package chungdenroi.spring.controller.admin;

import chungdenroi.spring.controller.LoginController;
import chungdenroi.spring.model.Orders;
import chungdenroi.spring.repository.OrdersRepository;
import chungdenroi.spring.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {


    @Autowired
    LoginController loginController;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    UserAccountRepository userAccountRepository;


    @RequestMapping("/admin")
    public String index(HttpSession session, Model model) {
        if(checkLoginAdmin(session)) {
            orderSatistics(model);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
            String year = LocalDate.now().format(formatter);
            List<Double> monthRevenue = totalMoneyYear(year);
            model.addAttribute("monthRevenue",monthRevenue);
            model.addAttribute("year", year);

            return "admin/index";
        } else return "redirect:/login";
    }

    @RequestMapping("/admin/{year}")
    public String viewRevenue(
            @Valid @RequestParam(name = "year", required = false, defaultValue = "2022") String year
            ,Model model, HttpSession session) {
        if(checkLoginAdmin(session)) {
            List<Double> monthRevenue = totalMoneyYear(year);
            model.addAttribute("monthRevenue",monthRevenue);
            model.addAttribute("year", year);
            return "admin/index";
        } else return "redirect:/login";

    }

    public int countOrderByDate(String date) {
        return ordersRepository.countByOrderDateLike('%'+date+'%');
    }
    public void orderSatistics(Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String curentDate = LocalDate.now().format(formatter);
        model.addAttribute("totalOrder", countOrderByDate(curentDate));

        //total order canceled
        model.addAttribute("Canceled", ordersRepository.countAllByStatus("Canceled"));

        //total order paying
        model.addAttribute("Paying", ordersRepository.countAllByStatus("Paying"));

        //total order success
        model.addAttribute("Successed", ordersRepository.countAllByStatus("Successed"));

        //total order processing
        model.addAttribute("Processing", ordersRepository.countAllByStatus("Processing"));
    }
    public double totalMoneyByMonth(String month) {
        List<Orders> ordersList = ordersRepository.findOrdersByOrderDateLike("%"+month+"%");
        double monthAmount = 0;
        for (Orders orders :  ordersList) {
            if(orders.getStatus().equals("Successed")) {
                monthAmount += orders.getTotalPrice();
            }
        }
        return monthAmount;
    }
    public List<Double> totalMoneyYear(String year) {
        List<Double> amountList = new ArrayList<>();
        for(int i = 1; i<=12; i++) {
            if(i>9) {
                amountList.add(totalMoneyByMonth(year+"-"+i));
            } else {
                amountList.add(totalMoneyByMonth(year+"-0"+i));
            }
        }
        return amountList;

    }
    public boolean checkLoginAdmin(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        String role="admin";
        if(userAccountRepository.existsUserAccountByUsernameAndPasswordAndPermission(username,password,role)) {
            return true;
        } else return false;
    }
}
