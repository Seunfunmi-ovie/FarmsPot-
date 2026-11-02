package ng.farmsPot.b2b.utils;

import ng.farmsPot.b2b.dtos.responses.BuyerResponse;
import ng.farmsPot.b2b.dtos.responses.ListingResponse;
import ng.farmsPot.b2b.dtos.responses.OrderResponse;
import ng.farmsPot.b2b.data.models.Buyer;
import ng.farmsPot.b2b.data.models.ExportListing;
import ng.farmsPot.b2b.data.models.ExportOrder;

public class B2bMapper {

    private B2bMapper() {
    }

    public static BuyerResponse toBuyer(Buyer buyer) {
        BuyerResponse response = new BuyerResponse();
        response.setBuyerId(buyer.getId());
        response.setCompanyName(buyer.getCompanyName());
        response.setContactName(buyer.getContactName());
        response.setEmail(buyer.getEmail());
        response.setPhoneNumber(buyer.getPhoneNumber());
        response.setCountry(buyer.getCountry());
        response.setBusinessRegNumber(buyer.getBusinessRegNumber());
        response.setStatus(buyer.getStatus());
        response.setVerificationNote(buyer.getVerificationNote());
        response.setCreatedAt(buyer.getCreatedAt());
        return response;
    }

    public static ListingResponse toListing(ExportListing listing) {
        ListingResponse response = new ListingResponse();
        response.setListingId(listing.getId());
        response.setFarmerId(listing.getFarmer().getId());
        response.setFarmerName(listing.getFarmer().getFullName());
        response.setCrop(listing.getCrop());
        response.setDescription(listing.getDescription());
        response.setOriginLocation(listing.getOriginLocation());
        response.setAvailableQuantityKg(listing.getAvailableQuantityKg());
        response.setMinOrderKg(listing.getMinOrderKg());
        response.setPricePerKgUsd(listing.getPricePerKgUsd());
        response.setAvailableFrom(listing.getAvailableFrom());
        response.setStatus(listing.getStatus());
        response.setReviewNote(listing.getReviewNote());
        response.setCreatedAt(listing.getCreatedAt());
        return response;
    }

    public static OrderResponse toOrder(ExportOrder order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setListingId(order.getListing().getId());
        response.setCrop(order.getListing().getCrop());
        response.setFarmerId(order.getListing().getFarmer().getId());
        response.setFarmerName(order.getListing().getFarmer().getFullName());
        response.setBuyerId(order.getBuyer().getId());
        response.setBuyerCompany(order.getBuyer().getCompanyName());
        response.setBuyerCountry(order.getBuyer().getCountry());
        response.setQuantityKg(order.getQuantityKg());
        response.setUnitPriceUsd(order.getUnitPriceUsd());
        response.setTotalPriceUsd(order.getTotalPriceUsd());
        response.setDestinationCountry(order.getDestinationCountry());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setNote(order.getNote());
        response.setStatus(order.getStatus());
        response.setPaid(order.isPaid());
        response.setStatusNote(order.getStatusNote());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }
}
