package Controller;

import Service.CarService;
import Service.UserService;
import Event.AdminSession;
import Service.AuctionService;
import Service.AuditService;
import UI.AdminPendingCarsPanel;
import UI.AdminUserPanel;
import UI.AdminAuctionPanel;
import UI.AdminAuditLogPanel;

public class AdminController {
	
	private String adminId;
	
	public AdminController(AdminUserPanel view, String adminId) {
	    this.adminId = adminId;
	}
	public static void handleCars(AdminPendingCarsPanel view) {

	    String adminId = AdminSession.getAdminName();

	    view.onApprove(id -> {
	        try {
	            CarService.approveCar(id, adminId);

	            view.render(CarService.getPendingCars());

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    view.onReject(id -> {
	        try {
	            CarService.rejectCar(id, adminId);

	            view.render(CarService.getPendingCars());

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    try {
	        view.render(CarService.getPendingCars());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public static void handleAuctions(AdminAuctionPanel view) {

	    view.onClose(id -> {
	        try {
	            AuctionService.stopAuction(id, AdminSession.getAdminName());

	            // Refresh after stop
	            String res = AuctionService.getAuctionsDetailed();
	            view.render(res);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    try {
	        String res = AuctionService.getAuctionsDetailed();
	        view.render(res);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static void handleUsers(AdminUserPanel view) {

	    view.onBan(id -> {
	        try {
	            UserService.banUser(id, AdminSession.getAdminName());
	            view.render(UserService.getUsers());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    view.onMakeAdmin(id -> {
	        try {
	            UserService.makeAdmin(id, AdminSession.getAdminName());
	            view.render(UserService.getUsers());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    try {
	        view.render(UserService.getUsers());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static void handleAuditLogs(AdminAuditLogPanel view) {

	    try {
	        String res = AuditService.getLogs();
	        view.render(res);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}