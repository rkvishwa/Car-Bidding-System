package Service;

import Client.ClientManager;

public class CarService {

    public static String addCar(String req) throws Exception {
        return ClientManager.send(req);
    }

    public static String getMyCars(String sellerId) throws Exception {
    	System.out.println("REQ: GET_MY_CARS:" + sellerId);
        return ClientManager.send("GET_MY_CARS:" + sellerId);
    }

    public static String getApprovedCars(String sellerId) throws Exception {
    	System.out.println("REQ: GET_APPROVED_CARS:" + sellerId);
        return ClientManager.send("GET_APPROVED_CARS:" + sellerId);
    }

    public static String getPendingCars() throws Exception {
        return ClientManager.send("GET_PENDING_CARS");
    }
    
    public static String approveCar(String carId, String adminId) throws Exception {
        return ClientManager.send("APPROVE_CAR:" + carId + ":" + adminId);
    }

    public static String rejectCar(String carId, String adminId) throws Exception {
        return ClientManager.send("REJECT_CAR:" + carId + ":" + adminId);
    }

    public static String softDeleteCar(String carId) throws Exception {
        return ClientManager.send("SOFT_DELETE_CAR:" + carId);
    }

    public static String deleteCarPermanent(String carId, String adminId) throws Exception {
        return ClientManager.send("DELETE_CAR_PERMANENT:" + carId + ":" + adminId);
    }
    
    public static String addCar(String sellerId, String title, String brand,
            String model, String year, String price,
            String desc, String image) throws Exception {

		return ClientManager.send(
		"ADD_CAR:" + sellerId + ":" + title + ":" + brand + ":"
		    + model + ":" + year + ":" + price + ":"
		    + desc + ":" + image
		);
    }
}