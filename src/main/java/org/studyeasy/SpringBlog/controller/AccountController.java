package org.studyeasy.SpringBlog.controller;


import javax.validation.Valid;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.studyeasy.SpringBlog.models.Account;
import org.studyeasy.SpringBlog.services.AccountService;
import org.studyeasy.SpringBlog.util.constants.AppUtil;

import java.nio.file.Files;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Value("${spring.mvc.static-path-pattern}")
    private String photo_prefix;

    @GetMapping("/register")
    public String register(Model model){
        Account account = new Account();
        model.addAttribute("account", account);
        return "account_views/register";
    }

    @PostMapping("/register")
    public String register_user(@Valid @ModelAttribute Account account, BindingResult result){
        if (result.hasErrors()){
            return "account_views/register";
        }
        
        accountService.save(account);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model){
        return "account_views/login";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model, Principal principal){
        String authUser="email";
        if(principal != null){
            authUser = principal.getName();
        }
        Optional<Account>optionalaccount=accountService.findOneByEmail(authUser);
        if(optionalaccount.isPresent()){
            Account account = optionalaccount.get();
            model.addAttribute("account", account);
            model.addAttribute("photo", account.getPhoto());
            return "account_views/profile";
        }else{
            return "redirect:/?error";
        }

    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String Post_profile(@Valid @ModelAttribute Account account, BindingResult result,Principal principal){
        if(result.hasErrors()){
            return "account_views/profile";
        }
        String authUser="email";
        if(principal != null){
            authUser = principal.getName();
        }
        Optional<Account> optionalAccount=accountService.findOneByEmail(authUser);
        if(optionalAccount.isPresent()){
         Account account_by_id=accountService.findOneById(account.getId()).get();
         //account_by_id.setPhoto(account.getPhoto());
         account_by_id.setDate_of_birth(account.getDate_of_birth());
         account_by_id.setAge(account.getAge());
         account_by_id.setGender(account.getGender());
         account_by_id.setFirstname(account.getFirstname());
         account_by_id.setLastname(account.getLastname());
         account_by_id.setPassword(account.getPassword());
         accountService.save(account_by_id);
         SecurityContextHolder.clearContext();
         return "redirect:/";
        }else{
            return "redirect:/?error";
        }

    }
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String update_photo(@RequestParam("file")MultipartFile file, RedirectAttributes attributes,Principal principal){
        if(file.isEmpty()){
             attributes.addAttribute("error","No file uploaded");
             return "redirect:/profile";
        }else{
            String fileName= StringUtils.cleanPath(file.getOriginalFilename());
            try{
                int length=10;
                boolean useLetters=true;
                boolean useNumbers=true;
                String generatedString = UUID.randomUUID().toString().substring(0, 10);

                String final_photo_name=generatedString+fileName;
                String absolutefileLocation= AppUtil.get_upload_path(final_photo_name);
                Path path= Paths.get(absolutefileLocation);
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                attributes.addFlashAttribute("message","File uploaded successfully");
                String authUser="email";
                if(principal != null){
                    authUser = principal.getName();
                }
                Optional<Account>optional_account=accountService.findOneByEmail(authUser);
                if(optional_account.isPresent()){
                    Account account=optional_account.get();
                    Account account_by_id=accountService.findOneById(account.getId()).get();
                    String relative_filelocation=photo_prefix.replace("**","uploads/+final_photo_name");
                    account_by_id.setPhoto(relative_filelocation);
                    accountService.save(account_by_id);
                }
                try{
                    TimeUnit.SECONDS.sleep(1);
                }catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                }
                return "redirect:/profile";
            }catch(Exception e){

            }
        }
        return "redirect:/profile?error";
    }


    
}
