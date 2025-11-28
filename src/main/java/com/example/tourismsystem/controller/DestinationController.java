package com.example.tourismsystem.controller;

import com.example.tourismsystem.entity.Destination;
import com.example.tourismsystem.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController {

    @Autowired
    private DestinationRepository destinationRepository;

    // GET все направления
    @GetMapping
    public List<Destination> getAllDestinations() {
        return destinationRepository.findAll();
    }

    // GET направление по ID
    @GetMapping("/{id}")
    public ResponseEntity<Destination> getDestinationById(@PathVariable Long id) {
        Optional<Destination> destination = destinationRepository.findById(id);
        return destination.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST создать новое направление
    @PostMapping
    public Destination createDestination(@RequestBody Destination destination) {
        return destinationRepository.save(destination);
    }

    // PUT обновить направление
    @PutMapping("/{id}")
    public ResponseEntity<Destination> updateDestination(@PathVariable Long id,
                                                          @RequestBody Destination destinationDetails) {
        Optional<Destination> optionalDestination = destinationRepository.findById(id);
        if (optionalDestination.isPresent()) {
            Destination destination = optionalDestination.get();
            destination.setName(destinationDetails.getName());
            destination.setDescription(destinationDetails.getDescription());
            destination.setCountry(destinationDetails.getCountry());
            destination.setCity(destinationDetails.getCity());
            return ResponseEntity.ok(destinationRepository.save(destination));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE удалить направление
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestination(@PathVariable Long id) {
        if (destinationRepository.existsById(id)) {
            destinationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}