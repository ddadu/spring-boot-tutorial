package ge.softgen.softlab.springboottutorial.controller;

import ge.softgen.softlab.springboottutorial.NotFoundException;
import ge.softgen.softlab.springboottutorial.entity.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CustomerController {
    private static int id = 0;

    private List<Customer> db = new ArrayList<>();

    @GetMapping("/customers")
    public List<Customer> getAll() {

        return db.stream().filter(c -> !c.getDelete()).toList();
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getBuyId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(getCustomer(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> add(@RequestBody Customer customer) {
        customer.setId(++id);
        customer.setDelete(false);
        db.add(customer);
        var location = UriComponentsBuilder.fromPath("./customers/" + id).build().toUri();
        return ResponseEntity.created(location).body(customer);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> update(@RequestBody Customer customer, @PathVariable int id) {
        try {
            var foundCustomer = getCustomer(id);
            foundCustomer.setFirstName(customer.getFirstName());
            foundCustomer.setLastName(customer.getLastName());
            foundCustomer.setBirthDate(customer.getBirthDate());
            return ResponseEntity.ok(foundCustomer);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }


    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Customer> delete(@PathVariable int id) {
        try {
            var foundCustomer = getCustomer(id);
            foundCustomer.setDelete(true);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }


    }

    private Customer getCustomer(int id) throws NotFoundException {
        var optional = db.stream().filter(c -> c.getId() == id).findFirst();
        if (optional.isEmpty()) {
            throw new NotFoundException("Customer not found");
        }
        return optional.get();
    }
}
