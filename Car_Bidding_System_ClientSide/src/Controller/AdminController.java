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

	    String adminId = AdminSession.getAdminId();

	    view.onApprove(id -> {
	        try {
	            CarService.approveCar(id, adminId);
	            view.renderWithStatus(CarService.getPendingCars());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    view.onReject(id -> {
	        try {
	            CarService.rejectCar(id, adminId);
	            view.renderWithStatus(CarService.getPendingCars());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    view.onPermanentDelete(id -> {
	        try {
	            CarService.deleteCarPermanent(id, adminId);
	            view.renderWithStatus(CarService.getPendingCars());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    try {
	        view.renderWithStatus(CarService.getPendingCars());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public static void handleAuctions(AdminAuctionPanel view) {

	    view.onClose(id -> {
	        try {
	            AuctionService.stopAuction(id, AdminSession.getAdminId());
	            refreshAuctions(view);
	        } catch (Exception e) { e.printStackTrace(); }
	    });

	    view.onApprove(data -> {
	        try {
	            AuctionService.approveAuction(data[0], data[1], data[2], AdminSession.getAdminId());
	            refreshAuctions(view);
	        } catch (Exception e) { e.printStackTrace(); }
	    });

	    view.onConfirmWinner(id -> {
	        try {
	            AuctionService.confirmWinner(id, AdminSession.getAdminId());
	            refreshAuctions(view);
	        } catch (Exception e) { e.printStackTrace(); }
	    });

	    refreshAuctions(view);
	}

	private static void refreshAuctions(AdminAuctionPanel view) {
	    try {
	        view.render(AuctionService.getAuctionsDetailed());
	        view.renderPending(AuctionService.getPendingAuctions());
	    } catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void handleUsers(AdminUserPanel view) {

	    view.onBan(id -> {
	        try {
	            UserService.banUser(id, AdminSession.getAdminId());
	            view.render(UserService.getUsers());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    view.onMakeAdmin(id -> {
	        try {
	            UserService.makeAdmin(id, AdminSession.getAdminId());
	            view.render(UserService.getUsers());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    view.onApprove(id -> {
	        try {
	            UserService.approveUser(id, AdminSession.getAdminId());
	            view.render(UserService.getUsers());
	            view.renderPending(UserService.getPendingUsers());
	        } catch (Exception e) { e.printStackTrace(); }
	    });

	    view.onReject(id -> {
	        try {
	            UserService.deleteUser(id, AdminSession.getAdminId());
	            view.render(UserService.getUsers());
	            view.renderPending(UserService.getPendingUsers());
	        } catch (Exception e) { e.printStackTrace(); }
	    });

	    try {
	        view.render(UserService.getUsers());
	        view.renderPending(UserService.getPendingUsers());
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