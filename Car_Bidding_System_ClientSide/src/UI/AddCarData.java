package UI;

public class AddCarData {

    public String sellerId;
    public String title;
    public String brand;
    public String model;
    public String year;
    public String price;
    public String description;
    public String image;

    public AddCarData(String sellerId, String title, String brand,
                      String model, String year, String price,
                      String description, String image) {

        this.sellerId = sellerId;
        this.title = title;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.description = description;
        this.image = image;
    }
}