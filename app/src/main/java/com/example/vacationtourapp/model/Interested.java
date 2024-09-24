package com.example.vacationtourapp.model;

public class Interested {

    String PlaceName, PlaceAbout, PlaceAddress, PlacePrice, PlaceID, UserID, PlaceImage;

    public Interested() {
    }

    public Interested(String placeName, String placeAbout, String placeAddress, String placePrice, String placeID, String userID, String placeImage) {
        PlaceName = placeName;
        PlaceAbout = placeAbout;
        PlaceAddress = placeAddress;
        PlacePrice = placePrice;
        PlaceID = placeID;
        UserID = userID;
        PlaceImage = placeImage;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public String getPlaceAbout() {
        return PlaceAbout;
    }

    public void setPlaceAbout(String placeAbout) {
        PlaceAbout = placeAbout;
    }

    public String getPlaceAddress() {
        return PlaceAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        PlaceAddress = placeAddress;
    }

    public String getPlacePrice() {
        return PlacePrice;
    }

    public void setPlacePrice(String placePrice) {
        PlacePrice = placePrice;
    }

    public String getPlaceID() {
        return PlaceID;
    }

    public void setPlaceID(String placeID) {
        PlaceID = placeID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getPlaceImage() {
        return PlaceImage;
    }

    public void setPlaceImage(String placeImage) {
        PlaceImage = placeImage;
    }
}
