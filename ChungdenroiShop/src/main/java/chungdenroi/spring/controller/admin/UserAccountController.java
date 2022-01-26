package chungdenroi.spring.controller.admin;

import chungdenroi.spring.model.Customer;
import chungdenroi.spring.model.UserAccount;
import chungdenroi.spring.model.UserAccount;
import chungdenroi.spring.repository.CustomerRepository;
import chungdenroi.spring.repository.UserAccountRepository;
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

import javax.persistence.Column;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/user/")
public class UserAccountController {
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    DashboardController dashboardController;

    @RequestMapping("")
    public String getAllUser(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                             Model model, HttpSession session) {
        if(dashboardController.checkLoginAdmin(session)) {
            Pageable pageable = PageRequest.of(page,7);
            Page<UserAccount> pageTotal = userAccountRepository.findUserAccountsBy(pageable);
            int paging = pageTotal.getTotalPages();
            model.addAttribute("pageTotal", paging);

            model.addAttribute("users", pageTotal);
            return "admin/user/userList";
        } else return "redirect:/login";
    }
    @RequestMapping("/{id}")
    public String getUserID(Model model,
                                @PathVariable(value = "id") Long id) {
        UserAccount user = userAccountRepository.getById(id);
        model.addAttribute("users", user);
        return "admin/user/userList";
    }

    @RequestMapping("/add")
    public String addUser (Model model) {
        UserAccount user = new UserAccount();
        model.addAttribute("user", user);
        return "admin/user/userAdd";
    }

    @RequestMapping("/update/{id}")
    public String updateUser(
            @PathVariable (value = "id") Long id, Model model)  {
        UserAccount user = userAccountRepository.getById(id);
        model.addAttribute("user", user);
        return "admin/user/userUpdate";
    }


    @RequestMapping("/saveAdd")
    public String saveUserAdd (
            @Valid UserAccount user,
            BindingResult result) {
        System.out.println("Call save add method");
        if(result.hasErrors()) {
            System.out.println("error save add user");
            return "redirect:/admin/user/add";
        }
        userAccountRepository.save(user);
        System.out.println("Save success");
        return "redirect:/admin/user/";
    }

    @RequestMapping("/saveUpdate")
    public String saveUserUpdate (
            @Valid UserAccount user,
            @RequestParam(value = "id", required = false) Long id,
            BindingResult result) {
        System.out.println("Call save update method");
        if(result.hasErrors()) {
            return "redirect:/admin/user/update/"+id;
        }
        user.setId(id);
        userAccountRepository.save(user);
        System.out.println("Save success");
        return "redirect:/admin/user/";
    }

    @RequestMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable(value = "id") Long id) {
        UserAccount user = userAccountRepository.getById(id);
        userAccountRepository.delete(user);
        return "redirect:/admin/user/";
    }

    @RequestMapping("/search")
    public String searchUser(
            Model model,
            @RequestParam(value = "name") String username) {
        System.out.println("call search medthod");
        List<UserAccount> users  = userAccountRepository.findUserAccountByUsernameLike("%"+username+"%");
        model.addAttribute("users", users);
        return "admin/user/userList";
    }
    @RequestMapping("/asc")
    public String sortByAsc(Model model) {
        List<UserAccount> users = userAccountRepository.findAll(Sort.by(Sort.Direction.ASC,"username"));
        model.addAttribute("users", users);
        return "admin/user/userList";
    }
    @RequestMapping("/desc")
    public String sortByDESC(Model model) {
        List<UserAccount> users = userAccountRepository.findAll(Sort.by(Sort.Direction.DESC,"username"));
        model.addAttribute("users", users);
        return "admin/user/userList";
    }
}
