package com.dailycodelearner.controller;

import com.dailycodelearner.model.Tutorial;
import com.dailycodelearner.repository.TutorialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TutorialControllerTest {

    //which service we want to test
    @InjectMocks
    private TutorialController tutorialController;

    //declare the dependencies
    @Mock
    private TutorialRepository tutorialRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSortDirection() {
        assertEquals(Sort.Direction.ASC, tutorialController.getSortDirection("asc"));
        assertEquals(Sort.Direction.DESC, tutorialController.getSortDirection("desc"));
    }

    @Test
    void shouldSuccessfullyExecuteGetSortedListByOrderAndTypeWithSatisfyingIfCondition() {
        //Given
        String[] input = new String[1];
        input[0] = ("title,desc");
        List<Tutorial> tutorial = new ArrayList<>();

        tutorial.add(new Tutorial("Spring Data Tut# 2", "Tut#2Description", true));
        tutorial.add(new Tutorial("Spring Boot Tut# 1", "Tut#1Description", false));
        tutorial.add(new Tutorial("Spring Cloud Tut# 5", "Tut#5Description", true));
        tutorial.add(new Tutorial("MongoDb Database Tut# 7", "Tut#7Description", true));
        tutorial.add(new Tutorial("Jpa Pagination Tut# 9", "Tut#9Description", false));
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "title"));

        //Mock the calls
        when(tutorialRepository.findAll(Sort.by(orders))).thenReturn(tutorial);

        //when
        ResponseEntity<List<Tutorial>> list = tutorialController.getSortedListByOrderAndType(input);

        //then
        assertEquals(Objects.requireNonNull(list.getBody()).size(), tutorial.size());
        assertEquals(list.getBody().get(0).getDescription(), tutorial.get(0).getDescription());
    }

    @Test
    void shouldSuccessfullyExecuteGetSortedListByOrderAndTypeWithNotSatisfyingIfCondition() {
        //Given
        String[] input = new String[2];
        input[0] = "title";
        input[1] = "asc";
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "title"));
        List<Tutorial> emptyTutorial = new ArrayList<>();

        //Mock the calls
        when(tutorialRepository.findAll(Sort.by(orders))).thenReturn(emptyTutorial);

        //when
        ResponseEntity<List<Tutorial>> list = tutorialController.getSortedListByOrderAndType(input);

        //then

        assertTrue(Objects.requireNonNull(list.getBody()).isEmpty());

    }

    @Test
    void shouldTestHandlingException() {

        //Given
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "title"));
        String[] input = new String[2];
        input[0] = "title";
        input[1] = "asc";

        //Mock The call
        when(tutorialRepository.findAll(Sort.by(orders))).thenThrow(new NullPointerException("this is test exception"));

        //when
        ResponseEntity<List<Tutorial>> list = tutorialController.getSortedListByOrderAndType(input);

//      then
        assertEquals(0, list.getBody().size());
        assertTrue(list.getBody().isEmpty());
    }

    @Test
    void executeTitleContainingGetAllTutorialPagesWithSatisfyingIfCondition() {

        //Given
        List<Tutorial> tutorial = new ArrayList<>();
        tutorial.add(new Tutorial("Spring Data Tut# 2", "Tut#2Description", true));
        tutorial.add(new Tutorial("Spring Boot Tut# 1", "Tut#1Description", false));
        tutorial.add(new Tutorial("Spring Cloud Tut# 5", "Tut#5Description", true));
        tutorial.add(new Tutorial("MongoDb Database Tut# 7", "Tut#7Description", true));
        tutorial.add(new Tutorial("Jpa Pagination Tut# 9", "Tut#9Description", false));
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "title"));
        Pageable pageable = PageRequest.of(0, 3, Sort.by(orders));
        Page<Tutorial> page = new PageImpl<>(tutorial, pageable, 2);

        //Mock the call
        when(tutorialRepository.findByTitleContaining(any(String.class), any(Pageable.class))).thenReturn(page);

        //when
        ResponseEntity<Map<String, Object>> spring = tutorialController.getAllTutorialsPages("spring", 0, 3, new String[]{"id,desc"});

        Map<String, Object> body = spring.getBody();
        //then

        assertNotNull(body);
        assertEquals(5L, body.get("totalItems"));
        assertEquals(2, body.get("totalPages"));
    }

    @Test
    void executeFindAllConditionForGetAllTutorialPagesWithOutSatisfyingIfCondition() {

        //Given
        List<Tutorial> tutorial = new ArrayList<>();
        tutorial.add(new Tutorial("Spring Data Tut# 2", "Tut#2Description", true));
        tutorial.add(new Tutorial("Spring Boot Tut# 1", "Tut#1Description", false));
        tutorial.add(new Tutorial("Spring Cloud Tut# 5", "Tut#5Description", true));
        tutorial.add(new Tutorial("MongoDb Database Tut# 7", "Tut#7Description", true));
        tutorial.add(new Tutorial("Jpa Pagination Tut# 9", "Tut#9Description", false));
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "title"));
        Pageable pageable = PageRequest.of(0, 3, Sort.by(orders));
        Page<Tutorial> page = new PageImpl<>(tutorial, pageable, 2);

        //Mock the call
        when(tutorialRepository.findAll(pageable)).thenReturn(page);

        //when
        ResponseEntity<Map<String, Object>> spring = tutorialController.getAllTutorialsPages(null, 0, 3, new String[]{"title", "desc"});

        Map<String, Object> body = spring.getBody();
        //then

        assertNotNull(body);
        assertEquals(5L, body.get("totalItems"));
        assertEquals(2, body.get("totalPages"));
    }

    @Test
    void executeFindAllConditionForGetAllTutorialPagesWithOutSatisfyingIfConditionIncludingException() {

        //Given
        List<Tutorial> tutorial = new ArrayList<>();
        tutorial.add(new Tutorial("Spring Data Tut# 2", "Tut#2Description", true));

        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "title"));
        //here we are preparing pageable object of title="title" only
        Pageable pageable = PageRequest.of(0, 3, Sort.by(orders));
        Page<Tutorial> page = new PageImpl<>(tutorial, pageable, 2);

        //Mock the call
        when(tutorialRepository.findAll(pageable)).thenReturn(page);

        //when
//        In controller layer we are preparing pagable object of title="id"
//        hence we are passing "id" but expecting "title" one repository call will not happen as expected passing object is different when compared to controller
//        Layer so repository will provide and nullpointerexception will be thrown while fetching data from repository obj in controller layer.

        ResponseEntity<Map<String, Object>> spring = tutorialController.getAllTutorialsPages(null, 0, 3, new String[]{"id", "desc"});

        //then

        assertNull(spring.getBody());
    }

    @Test
    void executeEmptyConditionForGetAllTuorialsPages() {
        //Given
        List<Tutorial> tutorial = new ArrayList<>();
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(0, 3, Sort.by(orders));
        Page<Tutorial> page = new PageImpl<>(tutorial);
        //Mock the call
        when(tutorialRepository.findAll(pageable)).thenReturn(page);

        //when
        ResponseEntity<Map<String, Object>> spring = tutorialController.getAllTutorialsPages(null, 0, 3, new String[]{"id", "desc"});
        List<Object> collect = spring.getBody().values().stream().toList();

        //then
        assertEquals(1, collect.size());
    }

    @Test
    void executeFindByPublishedWithSatisfyingIfCondition() {

        //Given
        List<Tutorial> tutorial = new ArrayList<>();
        tutorial.add(new Tutorial("Spring Data Tut# 2", "Tut#2Description", true));
        tutorial.add(new Tutorial("Spring Boot Tut# 1", "Tut#1Description", false));
        tutorial.add(new Tutorial("Spring Cloud Tut# 5", "Tut#5Description", true));
        tutorial.add(new Tutorial("MongoDb Database Tut# 7", "Tut#7Description", true));
        tutorial.add(new Tutorial("Jpa Pagination Tut# 9", "Tut#9Description", false));
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "title"));
        Pageable pageable = PageRequest.of(0, 3, Sort.by(orders));
        Page<Tutorial> page = new PageImpl<>(tutorial,pageable,2);

        //Mock the call
        when(tutorialRepository.findByPublished(any(boolean.class), any(Pageable.class))).thenReturn(page);

        //when
        ResponseEntity<Map<String, Object>> spring = tutorialController.findByPublished(0, 3, new String[]{"id,desc"});

        Map<String, Object> body = spring.getBody();
        //then

        assertNotNull(body);
        assertEquals(5L, body.get("totalItems"));
        assertEquals(2, body.get("totalPages"));
    }

    @Test
    void executeFindByPublishedWithOutSatisfyingIfCondition() {

        //Given
        List<Tutorial> tutorial = new ArrayList<>();
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "title"));
        Pageable pageable = PageRequest.of(0, 3, Sort.by(orders));
        Page<Tutorial> page = new PageImpl<>(tutorial, pageable, 2);

        //Mock the call
        when(tutorialRepository.findByPublished(any(boolean.class), any(Pageable.class))).thenReturn(page);

        //when
        ResponseEntity<Map<String, Object>> spring = tutorialController.findByPublished(0, 3, new String[]{"id","desc"});

        List<Object> collect = spring.getBody().values().stream().toList();
        //then

        assertEquals(1, collect.size());
    }

    @Test
    void shouldTestHandlingExceptionFindByPublished() {

        //Given
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "title"));
        String[] input = new String[2];
        input[0] = "title";
        input[1] = "asc";

        //Mock The call
        when(tutorialRepository.findByPublished(any(boolean.class), any(Pageable.class))).thenThrow(new NullPointerException("this is test exception"));

        //when
        ResponseEntity<Map<String, Object>> byPublished = tutorialController.findByPublished(0, 3, new String[]{"id", "desc"});


//      then
        assertNull(byPublished.getBody());

    }


    @Test
    void findByIdSuccess(){
        //Mock the call
        when(tutorialRepository.findById(1L)).thenReturn(Optional.of(new Tutorial("animal","ranveer",false)));

        //when
        ResponseEntity<Tutorial> id = tutorialController.findById(1);
        Tutorial tutorial=id.getBody();

        //then
        assertEquals("animal",tutorial.getTitle());
        assertEquals("ranveer",tutorial.getDescription());
        assertFalse(tutorial.isPublished());
    }

    @Test
    void findByIdException(){
        //Mock the call
        when(tutorialRepository.findById(1L)).thenReturn(Optional.of(new Tutorial()));

        //when
        ResponseEntity<Tutorial> id = tutorialController.findById(1);
        Tutorial tutorial=id.getBody();

        //then
       assertEquals(null,tutorial.getDescription());
    }

    @Test
    void createTutorial(){

        Tutorial t=new Tutorial("animal","ranveer",false);
        //Mock the Call
        when(tutorialRepository.save(t)).thenReturn(t);

        //when
        ResponseEntity<Tutorial> tutorial=tutorialController.createTutorial(t);

        //then
        assertNotNull(tutorial.getBody().getTitle());
        assertEquals("animal",tutorial.getBody().getTitle());

    }

    @Test
    void shouldExecuteSuccessfullUpdateTutorial(){
        //Given
        long id=1;
        Tutorial t=new Tutorial("animal","ranveer",false);

        //Mock the call
        when(tutorialRepository.findById(id)).thenReturn(Optional.of(t));
        when(tutorialRepository.save(t)).thenReturn(t);

        //when
        ResponseEntity<Tutorial> response=tutorialController.updateTutorial(id,t);

        //then

        assertNotNull(response.getBody());
        assertEquals("animal",response.getBody().getTitle());
    }

    @Test
    void shouldExecuteUpdateTutorialWhenObjectNotPresent(){
        //Given
        long id=1;
        Tutorial t=null;

        //Mock the call
        when(tutorialRepository.findById(id)).thenReturn(Optional.ofNullable(t));
        when(tutorialRepository.save(t)).thenReturn(t);

        //when
        ResponseEntity<Tutorial> response=tutorialController.updateTutorial(id,t);

        //then

        assertNull(response.getBody());
//        assertEquals("animal",response.getBody().getTitle());
    }
}