package com.example.vacationtourapp.model;

public class Places {
    String CurrentDate, CurrentTime, PlaceAbout, PlaceAddress, PlaceID, PlaceImage, PlaceName, PlacePrice, TopPlace;

    public Places() {
    }

    public Places(String currentDate, String currentTime, String placeAbout, String placeAddress, String placeID, String placeImage, String placeName, String placePrice, String topPlace) {
        CurrentDate = currentDate;
        CurrentTime = currentTime;
        PlaceAbout = placeAbout;
        PlaceAddress = placeAddress;
        PlaceID = placeID;
        PlaceImage = placeImage;
        PlaceName = placeName;
        PlacePrice = placePrice;
        TopPlace = topPlace;
    }

    public String getCurrentDate() {
        return CurrentDate;
    }

    public void setCurrentDate(String currentDate) {
        CurrentDate = currentDate;
    }

    public String getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(String currentTime) {
        CurrentTime = currentTime;
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

    public String getPlaceID() {
        return PlaceID;
    }

    public void setPlaceID(String placeID) {
        PlaceID = placeID;
    }

    public String getPlaceImage() {
        return PlaceImage;
    }

    public void setPlaceImage(String placeImage) {
        PlaceImage = placeImage;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public String getPlacePrice() {
        return PlacePrice;
    }

    public void setPlacePrice(String placePrice) {
        PlacePrice = placePrice;
    }

    public String getTopPlace() {
        return TopPlace;
    }

    public void setTopPlace(String topPlace) {
        TopPlace = topPlace;
    }
}