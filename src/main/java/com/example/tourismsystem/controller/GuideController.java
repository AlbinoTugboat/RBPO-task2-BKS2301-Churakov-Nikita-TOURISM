package com.example.tourismsystem.controller;

import com.example.tourismsystem.entity.Guide;
import com.example.tourismsystem.repository.GuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/guides")
public class GuideController {

    @Autowired
    private GuideRepository guideRepository;

    // GET все гиды
    @GetMapping
    public List<Guide> getAllGuides() {
        return guideRepository.findAll();
    }

    // GET гид по ID
    @GetMapping("/{id}")
    public ResponseEntity<Guide> getGuideById(@PathVariable Long id) {
        Optional<Guide> guide = guideRepository.findById(id);
        return guide.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST создать нового гида
    @PostMapping
    public Guide createGuide(@RequestBody Guide guide) {
        return guideRepository.save(guide);
    }

    // PUT обновить гида
    @PutMapping("/{id}")
    public ResponseEntity<Guide> updateGuide(@PathVariable Long id,
                                             @RequestBody Guide guideDetails) {
        Optional<Guide> optionalGuide = guideRepository.findById(id);
        if (optionalGuide.isPresent()) {
            Guide guide = optionalGuide.get();
            guide.setName(guideDetails.getName());
            guide.setEmail(guideDetails.getEmail());
            guide.setPhone(guideDetails.getPhone());
            guide.setSpecialization(guideDetails.getSpecialization());
            guide.setExperienceYears(guideDetails.getExperienceYears());
            return ResponseEntity.ok(guideRepository.save(guide));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE удалить гида
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuide(@PathVariable Long id) {
        if (guideRepository.existsById(id)) {
            guideRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}