package com.autoclub_156.demo.services;

import com.autoclub_156.demo.interfaces.CarRepository;
import com.autoclub_156.demo.interfaces.RoleRepository;
import com.autoclub_156.demo.interfaces.UserRepository;
import com.autoclub_156.demo.model.Car;
import com.autoclub_156.demo.model.Role;
import com.autoclub_156.demo.model.User;
import com.autoclub_156.demo.security.CustomUserDetails;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Service
@Log
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(String login, String password) {
        User user = new User();
        Role userRole = roleRepository.findRoleByName("ROLE_USER");
        user.setRole(userRole);
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public Boolean isLoginUsed(String login) {
        System.out.println(userRepository.findByLogin(login));
        return userRepository.findByLogin(login) != null;
    }

    public User findByLogin(String login) {
        User user = userRepository.findByLogin(login);
        return user;
    }

    public User findByLoginAndPassword(String login, String password) {
        User user = findByLogin(login);
        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public Boolean bindCar(String vincode, String login) {
        User user = userRepository.findByLogin(login);
        Car car = carRepository.getCarByVincode(vincode);

        ArrayList<Car> cars = (user.getCars() == null) ? new ArrayList<Car>() : user.getCars();

        if (cars.contains(car)) {
            return false;
        }
        cars.add(car);

        user.setCars(cars);
        userRepository.save(user);
        return true;

    }

    public Boolean unbundCar(String vincode, String login) {
        User user = userRepository.findByLogin(login);
        for (int i = 0; i < user.getCars().size(); i++) {
            Car boundCar = user.getCars().get(i);

            System.out.println("boundCar.getVincode() == vincode");
            System.out.println(boundCar.getVincode() + " == " + vincode);

            if (boundCar.getVincode().equals(vincode)) {
                user.getCars().remove(boundCar);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Car> getCarsByLogin(String login) {
        return userRepository.findByLogin(login).getCars();
    }

    public Boolean isAccess(String login, String vincode) {
        ArrayList<Car> cars = new ArrayList<>();
        try {
            User user = userRepository.findByLogin(login);
            cars = user.getCars().isEmpty() ? cars : user.getCars();
        } catch (NullPointerException ex) {
            System.out.println(ex.getMessage());
            return false;
        }

        return cars.stream().anyMatch(x -> x.getVincode().equals(vincode)) || false;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String getLoginOfSender(HttpServletRequest request) {
        Authentication auth = (Authentication) request.getUserPrincipal();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        return customUserDetails.getUsername();
    }

    public void editName(String login, String newName) {
        User user = findByLogin(login);
        user.setName(newName);
        userRepository.save(user);
    }

    public boolean isSenderSameUser(HttpServletRequest request, String login) {
        String loginOfSender = getLoginOfSender(request);
        if (!login.equals(loginOfSender)) {
            return false;
        }
        return true;
    }

    public void editContactNumber(String login, String contactNumber) {
        User user = findByLogin(login);
        user.setContactNumber(contactNumber);
        userRepository.save(user);
    }

    public void editEmail(String login, String email) {
        User user = findByLogin(login);
        user.setContactNumber(email);
        userRepository.save(user);
    }

    public boolean isAdmin(HttpServletRequest request) {
        Authentication auth = (Authentication) request.getUserPrincipal();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        String userRole = customUserDetails.getRole().getName();
        if (userRole.equalsIgnoreCase("ROLE_ADMIN")) {
            return true;
        }
        return false;
    }

    public void addCar(String login, String vincode) {
        User user = userRepository.findByLogin(login);
        ArrayList<Car> cars = user.getCars();
        Car newCar = carRepository.getCarByVincode(vincode);
        cars.add(newCar);
        userRepository.save(user);
    }

    public void deleteCar(String login, String vincode) {
        User user = userRepository.findByLogin(login);
        ArrayList<Car> cars = user.getCars();
        Car deletingCar = carRepository.getCarByVincode(vincode);
        cars.add(deletingCar);
        userRepository.save(user);
    }

    public void deleteUser(String login) {
        User user = userRepository.findByLogin(login);
        userRepository.delete(user);
    }
}
