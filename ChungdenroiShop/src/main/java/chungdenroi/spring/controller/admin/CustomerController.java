package chungdenroi.spring.controller.admin;

import chungdenroi.spring.model.Category;
import chungdenroi.spring.model.Customer;
import chungdenroi.spring.repository.CustomerRepository;
import groovy.transform.AutoImplement;
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
import java.util.List;

@Controller
@RequestMapping("/admin/customer/")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    DashboardController dashboardController;

    @RequestMapping("")
    public String getAllCustomer(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                 Model model, HttpSession session) {
        if(dashboardController.checkLoginAdmin(session)) {
            Pageable pageable = PageRequest.of(page,7);
            Page<Customer> pageTotal = customerRepository.findCustomersBy(pageable);
            int paging = pageTotal.getTotalPages();

            model.addAttribute("pageTotal", paging);
            model.addAttribute("customers", pageTotal);
            return "admin/customer/customerList";
        } else return "redirect:/login";

    }
    @RequestMapping("/{id}")
    public String getCustomerID(Model model,
                                  @PathVariable(value = "id") Long id) {
        Customer customer = customerRepository.getById(id);
        model.addAttribute("customers", customer);
        return "admin/customer/customerList";
    }

    @RequestMapping("/add")
    public String addCustomer (Model model) {
        Customer customer = new Customer();
        model.addAttribute("customer", customer);
        return "admin/customer/customerAdd";
    }

    @RequestMapping("/update/{id}")
    public String updateCustomer(
            @PathVariable (value = "id") Long id, Model model)  {
        Customer customer = customerRepository.getById(id);
        model.addAttribute("customer", customer);
        return "admin/customer/customerUpdate";
    }


    @RequestMapping("/saveAdd")
    public String saveCustomerAdd (
            @Valid Customer customer,
            BindingResult result) {
        System.out.println("Call save add method");
        if(result.hasErrors()) {
            return "admin/customer/customerAdd";
        }
        customerRepository.save(customer);
        System.out.println("Save success");
        return "redirect:/admin/customer/";
    }

    @RequestMapping("/saveUpdate")
    public String saveCustomerUpdate (
            @Valid Customer customer,
            @RequestParam(value = "id", required = false) Long id,
            BindingResult result) {
        System.out.println("Call save update method");
        if(result.hasErrors()) {
            return "admin/customer/customerUpdate";
        }
        customer.setId(id);
        customerRepository.save(customer);
        System.out.println("Save success");
        return "redirect:/admin/customer/";
    }

    @RequestMapping("/delete/{id}")
    public String deleteCustomer(
            @PathVariable(value = "id") Long id) {
        Customer customer = customerRepository.getById(id);
        customerRepository.delete(customer);
        return "redirect:/admin/customer/";
    }

    @RequestMapping("/search")
    public String searchCustomer(
            Model model,
            @RequestParam(value = "name") String fullname) {
        System.out.println("call search medthod");
        List<Customer> customers  = customerRepository.findCustomerByFullnameLike("%"+fullname+"%");
        model.addAttribute("customers", customers);
        System.out.println("Name search: " +  fullname);
        return "admin/customer/customerList";
    }
    @RequestMapping("/asc")
    public String sortByAsc(Model model) {
        List<Customer> customers = customerRepository.findAll(Sort.by(Sort.Direction.ASC,"fullname"));
        model.addAttribute("customers", customers);
        return "admin/customer/customerList";
    }
    @RequestMapping("/desc")
    public String sortByDESC(Model model) {
        List<Customer> customers = customerRepository.findAll(Sort.by(Sort.Direction.DESC,"fullname"));
        model.addAttribute("customers", customers);
        return "admin/customer/customerList";
    }

}
