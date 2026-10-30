package ng.farmsPot.utils;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.dtos.requests.UpdateFarmerProfileRequest;
import ng.farmsPot.dtos.responses.UpdateFarmerProfileResponse;

public final class ProfileMapper {

    private ProfileMapper() {
    }

    /** Copies only the fields the caller actually sent; blank text and null numbers are ignored. */
    public static void applyUpdate(UpdateFarmerProfileRequest request, Farmer farmer) {
        if (hasText(request.getFullName())) farmer.setFullName(request.getFullName().trim());
        if (hasText(request.getEmail())) farmer.setEmail(request.getEmail().trim());
        if (hasText(request.getPhoneNumber())) farmer.setPhoneNumber(request.getPhoneNumber().trim());
        if (hasText(request.getFarmLocation())) farmer.setFarmLocation(request.getFarmLocation().trim());
        if (request.getFarmSizeHectares() != null) farmer.setFarmSizeHectares(request.getFarmSizeHectares());
        if (request.getHistoricAverageYieldKg() != null)
            farmer.setHistoricAverageYieldKg(request.getHistoricAverageYieldKg());
    }

    public static UpdateFarmerProfileResponse toResponse(Farmer farmer, String message) {
        UpdateFarmerProfileResponse response = new UpdateFarmerProfileResponse();
        response.setFarmerId(farmer.getId());
        response.setFullName(farmer.getFullName());
        response.setEmail(farmer.getEmail());
        response.setPhoneNumber(farmer.getPhoneNumber());
        response.setFarmLocation(farmer.getFarmLocation());
        response.setFarmSizeHectares(farmer.getFarmSizeHectares());
        response.setHistoricAverageYieldKg(farmer.getHistoricAverageYieldKg());
        response.setMessage(message);
        return response;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}