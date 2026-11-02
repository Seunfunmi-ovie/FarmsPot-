package ng.farmsPot.b2b.services;

import ng.farmsPot.b2b.dtos.requests.CreateListingRequest;
import ng.farmsPot.b2b.dtos.requests.DecisionRequest;
import ng.farmsPot.b2b.dtos.responses.ListingResponse;
import ng.farmsPot.b2b.data.models.ExportListing;
import ng.farmsPot.b2b.data.models.ListingStatus;
import ng.farmsPot.b2b.data.repositories.ExportListingRepository;
import ng.farmsPot.b2b.utils.B2bMapper;
import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.repositories.FarmersRepository;
import ng.farmsPot.exceptions.ForbiddenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class ListingServiceImpl implements ListingService {

    private final ExportListingRepository listingRepository;
    private final FarmersRepository farmersRepository;

    public ListingServiceImpl(ExportListingRepository listingRepository, FarmersRepository farmersRepository) {
        this.listingRepository = listingRepository;
        this.farmersRepository = farmersRepository;
    }

    // ---- farmer side ----

    @Transactional
    @Override
    public ListingResponse create(int farmerId, CreateListingRequest request) {
        Farmer farmer = farmersRepository.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("No Farmer found with id " + farmerId));
        if (request.getMinOrderKg() > request.getAvailableQuantityKg()) {
            throw new IllegalArgumentException("Minimum order can't be more than the quantity you have available");
        }

        ExportListing listing = new ExportListing();
        listing.setFarmer(farmer);
        listing.setCrop(request.getCrop().trim());
        listing.setDescription(request.getDescription() == null || request.getDescription().isBlank()
                ? null : request.getDescription().trim());
        listing.setOriginLocation(request.getOriginLocation().trim());
        listing.setAvailableQuantityKg(request.getAvailableQuantityKg());
        listing.setMinOrderKg(request.getMinOrderKg());
        listing.setPricePerKgUsd(request.getPricePerKgUsd());
        listing.setAvailableFrom(request.getAvailableFrom());
        listing.setStatus(ListingStatus.PENDING_REVIEW);

        return B2bMapper.toListing(listingRepository.save(listing));
    }

    @Override
    public List<ListingResponse> forFarmer(int farmerId) {
        return listingRepository.findByFarmerId(farmerId).stream()
                .sorted(Comparator.comparingInt(ExportListing::getId).reversed())
                .map(B2bMapper::toListing)
                .toList();
    }

    @Transactional
    @Override
    public ListingResponse close(int farmerId, int listingId) {
        ExportListing listing = find(listingId);
        if (listing.getFarmer().getId() != farmerId) {
            throw new ForbiddenException("You can only manage your own listings.");
        }
        if (listing.getStatus() == ListingStatus.CLOSED) {
            throw new IllegalStateException("This listing is already closed.");
        }
        listing.setStatus(ListingStatus.CLOSED);
        return B2bMapper.toListing(listingRepository.save(listing));
    }

    // ---- buyer side ----

    @Override
    public List<ListingResponse> browse(String crop) {
        String term = crop == null ? "" : crop.trim().toLowerCase();
        return listingRepository.findByStatus(ListingStatus.ACTIVE).stream()
                .filter(listing -> listing.getAvailableQuantityKg() > 0)
                .filter(listing -> term.isEmpty() || listing.getCrop().toLowerCase().contains(term))
                .sorted(Comparator.comparingInt(ExportListing::getId).reversed())
                .map(B2bMapper::toListing)
                .toList();
    }

    @Override
    public ListingResponse getActive(int listingId) {
        ExportListing listing = find(listingId);
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new IllegalArgumentException("This listing is no longer available");
        }
        return B2bMapper.toListing(listing);
    }

    // ---- admin side ----

    @Override
    public List<ListingResponse> adminList(String status) {
        List<ExportListing> listings;
        if (status == null || status.isBlank() || status.equalsIgnoreCase("ALL")) {
            listings = listingRepository.findAll();
        } else {
            ListingStatus parsed;
            try {
                parsed = ListingStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Unknown listing status: " + status);
            }
            listings = listingRepository.findByStatus(parsed);
        }
        return listings.stream()
                .sorted(Comparator.comparingInt(ExportListing::getId).reversed())
                .map(B2bMapper::toListing)
                .toList();
    }

    @Transactional
    @Override
    public ListingResponse decide(int listingId, DecisionRequest request) {
        String decision = request.getDecision() == null ? "" : request.getDecision().trim().toUpperCase();
        String note = request.getNote() == null ? "" : request.getNote().trim();
        ExportListing listing = find(listingId);

        switch (decision) {
            case "APPROVE" -> {
                if (listing.getStatus() != ListingStatus.PENDING_REVIEW && listing.getStatus() != ListingStatus.REJECTED) {
                    throw new IllegalStateException("Only listings waiting for review can be approved.");
                }
                listing.setStatus(ListingStatus.ACTIVE);
                listing.setReviewNote(note.isEmpty() ? null : note);
            }
            case "REJECT" -> {
                if (note.isEmpty()) {
                    throw new IllegalArgumentException("Please give a reason for rejecting this listing.");
                }
                if (listing.getStatus() != ListingStatus.PENDING_REVIEW && listing.getStatus() != ListingStatus.ACTIVE) {
                    throw new IllegalStateException("Only pending or active listings can be rejected.");
                }
                listing.setStatus(ListingStatus.REJECTED);
                listing.setReviewNote(note);
            }
            default -> throw new IllegalArgumentException("Decision must be APPROVE or REJECT.");
        }
        return B2bMapper.toListing(listingRepository.save(listing));
    }

    private ExportListing find(int listingId) {
        return listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("No listing found with id " + listingId));
    }
}
