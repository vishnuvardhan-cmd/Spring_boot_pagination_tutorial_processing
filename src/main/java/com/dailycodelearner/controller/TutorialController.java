package com.dailycodelearner.controller;

import com.dailycodelearner.model.Tutorial;
import com.dailycodelearner.repository.TutorialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/")
public class TutorialController {


    TutorialRepository tutorialRepository;

    public TutorialController(TutorialRepository tutorialRepository) {
        this.tutorialRepository = tutorialRepository;
    }

    public Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }

    @GetMapping("sortedtutorials")
    public ResponseEntity<List<Tutorial>> getSortedListByOrderAndType(@RequestParam(
            name = "sort",
            defaultValue = "id,desc"
    ) String[] sort) {
        try {
            List<Sort.Order> orders = new ArrayList<>();
            if (sort[0].contains(",")) {
                for (String s : sort) {
                    String[] _sort = s.split(",");
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }
            List<Tutorial> tutorials = tutorialRepository.findAll(Sort.by(orders));
            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(tutorials, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("tutorials")
    public ResponseEntity<Map<String, Object>> getAllTutorialsPages(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "title,asc") String[] sort) {

        try {

            List<Sort.Order> orders = new ArrayList<>();
            if (sort[0].contains(",")) {
                for (String s : sort) {
                    String[] split = s.split(",");
                    orders.add(new Sort.Order(getSortDirection(split[1]), split[0]));
                }
            } else {
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
            Page<Tutorial> pageTutorials;
            if (title == null) {
                pageTutorials = tutorialRepository.
                        findAll(pageable);
            } else {
                pageTutorials = tutorialRepository.findByTitleContaining(title, pageable);
            }

            List<Tutorial> tutorials = pageTutorials.getContent();
            if (tutorials.isEmpty()) {
                Map<String, Object> hs = new HashMap<>();
                hs.put("tutorial", tutorials);
                return new ResponseEntity<>(hs,
                        HttpStatus.NO_CONTENT);
            }
            Map<String, Object> pageWiseTutorials = new HashMap<>();
            pageWiseTutorials.put("totalPages", pageTutorials.getTotalPages());
            pageWiseTutorials.put("tutoria", tutorials);
            pageWiseTutorials.put("currentPage", pageTutorials.getNumber());
            pageWiseTutorials.put("totalItems", pageTutorials.getTotalElements());

            return new ResponseEntity<>(pageWiseTutorials, HttpStatus.OK);
        } catch (
                Exception e
        ) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("tutorials/published")
    public ResponseEntity<Map<String,Object>> findByPublished(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "3") int size,
                                                       @RequestParam(defaultValue = "id,asc") String[] sort){
        try{

            List<Sort.Order> orders=new ArrayList<>();
            if(sort[0].contains(",")){
                for(String s:sort){
                    String[] split = s.split(",");
                    orders.add(new Sort.Order(getSortDirection(split[1]),split[0]));
                }
            }else {
                orders.add(new Sort.Order(getSortDirection(sort[1]),sort[0]));
            }
            Pageable pageable=PageRequest.of(page,size,Sort.by(orders));
            Page<Tutorial> published = tutorialRepository.findByPublished(false, pageable);
            List<Tutorial> tutorials = published.getContent();
            if (tutorials.isEmpty()) {
                Map<String, Object> hs = new HashMap<>();
                hs.put("tutorials", tutorials);
                return new ResponseEntity<>(hs,
                        HttpStatus.NO_CONTENT);
            }
            Map<String, Object> pageWiseTutorials = new HashMap<>();
            pageWiseTutorials.put("totalPages", published.getTotalPages());
            pageWiseTutorials.put("tutorials", tutorials);
            pageWiseTutorials.put("currentPage", published.getNumber());
            pageWiseTutorials.put("totalItems", published.getTotalElements());

            return new ResponseEntity<>(pageWiseTutorials, HttpStatus.OK);
        }catch (Exception e){
            ResponseEntity<Map<String, Object>> mapResponseEntity = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            return mapResponseEntity;
        }
    }

    @GetMapping("tutorial/{id}")
    public ResponseEntity<Tutorial> findById(@PathVariable("id") long id){
        Optional<Tutorial> byId = tutorialRepository.findById(id);
        return byId.map(tutorial -> new ResponseEntity<>(tutorial, HttpStatus.OK)).
                orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NO_CONTENT));
    }

    @PostMapping("tutorial")
    public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial){
        Tutorial save = tutorialRepository.save(tutorial);
        return new ResponseEntity<>(save,HttpStatus.OK);
    }

    @PutMapping("tutorial/{id}")
    public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id,@RequestBody Tutorial tutorial){
        Optional<Tutorial> tutorial1 = tutorialRepository.findById(id);
        if(tutorial1.isPresent()){
            Tutorial tutorial2;
            tutorial2 = tutorial1.get();
            tutorial2.setTitle(tutorial.getTitle());
            tutorial2.setDescription(tutorial.getDescription());
            tutorial2.setPublished(tutorial.isPublished());
            return new ResponseEntity<>(tutorialRepository.save(tutorial2),HttpStatus.CREATED);
        }
        return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("tutorial/{id}")
    public ResponseEntity<Tutorial> deleteTutorial(@PathVariable("id") long id){
        Optional<Tutorial> tutorial1 = tutorialRepository.findById(id);
        if(tutorial1.isPresent()) {
            tutorialRepository.deleteById(id);
            return new ResponseEntity<>(tutorial1.get(),HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
    }

    @GetMapping("tutorials/sortedtitle")
    public ResponseEntity<List<Tutorial>> getByTitleContainingWithoutPagination(@RequestParam(required = false) String title
    ,@RequestParam(defaultValue = "id,desc") String[] sort){
        try {

            List<Sort.Order> orders = new ArrayList<>();
            if (sort[0].contains(",")) {
                for (String s : sort) {
                    String[] split = s.split(",");
                    orders.add(new Sort.Order(getSortDirection(split[1]), split[0]));
                }
            } else {
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }
            List<Tutorial> tutorials;
            if(title==null){
                tutorials=tutorialRepository.findAll(Sort.by(orders));
            }
            else{
                tutorials=tutorialRepository.findByTitleContaining(title,Sort.by(orders));
            }

            if(tutorials.isEmpty()){
                return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tutorials,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
