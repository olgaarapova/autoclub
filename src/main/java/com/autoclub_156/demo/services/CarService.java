package com.autoclub_156.demo.services;

import com.autoclub_156.demo.interfaces.CarRepository;
import com.autoclub_156.demo.model.Car;
import com.autoclub_156.demo.model.ServiceEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    public void saveCar(String vincode, String model, String transmission) {
        Car car = new Car(vincode, model, transmission);
        car.setVincode(vincode);
        car.setModel(model);
        car.setTransmission(transmission);
        carRepository.save(car);
    }

    public Boolean isCarExist(String vincode) {
        try {
            Car car = carRepository.getCarByVincode(vincode);
            if (!(car == null)) {
                return true;
            }
            return false;
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public void setTransmission(String vincode, String newValueOfTransmission) {
        Car car = carRepository.getCarByVincode(vincode);
        car.setTransmission(newValueOfTransmission);
    }

    public void deleteCar(String vincode) {
        carRepository.deleteCarByVincode(vincode);
    }

    public void enableReminderAboutMaintenance(String vincode) {
        Car car = carRepository.getCarByVincode(vincode);
        car.setMaintenance(true);
        carRepository.save(car);
    }

    public void disableReminderAboutMaintenance(String vincode) {
        Car car = carRepository.getCarByVincode(vincode);
        car.setMaintenance(false);
        carRepository.save(car);
    }

    public Car getCar(String vincode) {
        Car car = carRepository.getCarByVincode(vincode);
        return car;
    }

    public void setModel(String vincode, String model) {
        Car car = carRepository.getCarByVincode(vincode);
        car.setModel(model);
    }

    public void setVincode(String vincode, String newVincode) {
        Car car = carRepository.getCarByVincode(vincode);
        car.setVincode(newVincode);
    }
}
