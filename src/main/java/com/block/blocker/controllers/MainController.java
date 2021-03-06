package com.block.blocker.controllers;


import com.block.blocker.models.User;
import com.block.blocker.repositories.UserReposiroty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jws.soap.SOAPBinding;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private UserReposiroty uRep;





    @GetMapping()
    public String userManager(@RequestParam(name = "event", required = false, defaultValue = "") String event,
                           @RequestParam(name = "id", required = false, defaultValue = "") String[] id,
                           Map<String, Object> model,
                           User user) {

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        User userFromDB;

        for (String oneOfChanged : id) {
                userFromDB = uRep.findById(Long.parseLong(oneOfChanged)).get();
            deleteAndChangeUserStatus(event, userFromDB, currentUser);
        }


        Iterable<User> users = uRep.findAll();
        users = uRep.findAll();
        model.put("currentUser", currentUser.getName());
        model.put("users", users);
        User qe;
        qe=uRep.findByUsername(currentUser.getName());


        if (!currentUser.isAuthenticated() || !qe.getBlock()) {
            return "redirect:/login?logout";
        }
        return "main";
    }


    @PostMapping()
    public String showUsers(Map<String, Object> model, User user) {
        Iterable<User> users = uRep.findAll();
        Authentication cures = SecurityContextHolder.getContext().getAuthentication();
        User qe;
        qe = uRep.findByUsername(cures.getName());

        if(qe.getBlock()){
            model.put("users", users);
            return "redirect:/users";
        }
        else {
            return "redirect:/login?logout";
        }


    }


    // Manager





    private void delete(User userFromDB) {
        uRep.delete(userFromDB);

    }

    private boolean currentUserIsDeleted(User userFromDB, Authentication currentUser){
        return currentUser.getName().equals(userFromDB.getUsername());
    }

    private void blockUnblock(User userFromDB) {
        if (userFromDB.getBlock()) {
            userFromDB.setBlock(false);
        } else {
            userFromDB.setBlock(true);
        }
        uRep.save(userFromDB);

    }

    private boolean currentUserIsBanned(User userFromDB, Authentication currentUser){
        return currentUser.getName().equals(userFromDB.getUsername());
    }

    private void blockUnblockUsers(User userFromDB, Authentication currentUser){
        blockUnblock(userFromDB);
        if (currentUserIsBanned(userFromDB, currentUser)){

            currentUser.setAuthenticated(false);
        }
    }

    private void deleteUsers(User userFromDB, Authentication currentUser){
        delete(userFromDB);
        if (currentUserIsDeleted(userFromDB, currentUser)){
            currentUser.setAuthenticated(false);
        }
    }

    private void deleteAndChangeUserStatus(String event, User userFromDB, Authentication currentUser){
        if (event.equals("Actеive/Disactive")) {
            blockUnblockUsers(userFromDB, currentUser);
        }
        else if (event.equals("Delete")) {
            deleteUsers(userFromDB, currentUser);
        }
    }

}

